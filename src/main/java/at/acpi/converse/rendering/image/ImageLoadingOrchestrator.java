package at.acpi.converse.rendering.image;


import at.acpi.converse.Converse;
import at.acpi.converse.rendering.image.domain.ActiveChatImage;
import at.acpi.converse.rendering.image.domain.ChatImageData;
import at.acpi.converse.rendering.image.domain.ChatImageRenderingState;
import at.acpi.converse.rendering.image.hosting.ImageHostingRegistry;
import at.acpi.converse.rendering.image.network.ImageFetchResult;
import at.acpi.converse.rendering.image.network.RemoteImageFetcher;
import at.acpi.converse.rendering.image.pipeline.ImageProcessingResult;
import at.acpi.converse.rendering.image.storage.ImageCachePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Optional;

public final class ImageLoadingOrchestrator {
	private static final Logger LOGGER = LoggerFactory.getLogger("converse/images");

	private final ImageHostingRegistry hostingRegistry;
	private final ImageCachePool cachePool;
	private final ChatImageTextureManager textureManager;
	private final RemoteImageFetcher imageFetcher;

	public ImageLoadingOrchestrator(ImageHostingRegistry hostingRegistry,
	                                ImageCachePool cachePool,
	                                ChatImageTextureManager textureManager,
	                                RemoteImageFetcher imageFetcher) {
		this.hostingRegistry = hostingRegistry;
		this.cachePool = cachePool;
		this.textureManager = textureManager;
		this.imageFetcher = imageFetcher;
	}

	public ImageHostingRegistry hostingRegistry() {
		return this.hostingRegistry;
	}

	public Optional<ActiveChatImage> requestCachedImage(URI uri) {
		return cachePool.lookup(uri.toString());
	}

	public Optional<ActiveChatImage> requestImage(URI uri) {
		Optional<ActiveChatImage> maybeImage = requestCachedImage(uri);
		if (maybeImage.isPresent()) return maybeImage;

		return requestCachedImage(uri)
				.or(() -> Converse.imageLoadingOrchestrator()
						.hostingRegistry()
						.findServiceFor(uri)
						.flatMap(service ->
								service.compile(uri) instanceof ImageProcessingResult.Success(ChatImageData data)
										? Optional.of(cachePool.getOrCreate(uri.toString(), data))
										: Optional.empty()))
				.map(image -> {
					if (image.getState() != ChatImageRenderingState.PENDING)
						image.touch();
					else if (image.compareAndSetState(ChatImageRenderingState.PENDING, ChatImageRenderingState.LOADING))
						fetchBytesAsync(image);
					return image;
				});
	}

	private void fetchBytesAsync(ActiveChatImage image) {
		imageFetcher.fetchAsync(image.getData().uri()).thenAccept(fetchResult -> {
			switch (fetchResult) {
				case ImageFetchResult.Success(byte[] bytes) ->
						textureManager.decodeAndScheduleUploadAsync(image, bytes);
				case ImageFetchResult.Failure(Exception cause) -> {
					LOGGER.warn("😿 failed to fetch image from {}: {}", image.getData().uri(), cause.getMessage());
					image.setState(ChatImageRenderingState.FAILED);
				}
			}
		});
	}
}
