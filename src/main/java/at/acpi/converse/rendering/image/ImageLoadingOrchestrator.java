package at.acpi.converse.rendering.image;

import at.acpi.converse.Converse;
import at.acpi.converse.rendering.image.domain.ActiveChatImage;
import at.acpi.converse.rendering.image.domain.ChatImageRenderingState;
import at.acpi.converse.rendering.image.format.ImageFormatRegistry;
import at.acpi.converse.rendering.image.hosting.ImageHostingRegistry;
import at.acpi.converse.rendering.image.network.ImageFetchResult;
import at.acpi.converse.rendering.image.network.RemoteImageFetcher;
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
	private final ImageFormatRegistry formatRegistry;

	public ImageLoadingOrchestrator(ImageHostingRegistry hostingRegistry,
	                                ImageCachePool cachePool,
	                                ImageFormatRegistry formatRegistry,
	                                ChatImageTextureManager textureManager,
	                                RemoteImageFetcher imageFetcher) {
		this.hostingRegistry = hostingRegistry;
		this.cachePool = cachePool;
		this.formatRegistry = formatRegistry;
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
		return this.formatRegistry.findFor(uri).flatMap(imageFormat -> requestCachedImage(uri)
				.or(() -> Converse.imageLoadingOrchestrator()
						.hostingRegistry()
						.findServiceFor(uri)
						.flatMap(service ->
								cachePool.getOrCreate(uri.toString(), imageFormat, () -> service.compile(uri))))
				.map(image -> {
					if (image.getState() == ChatImageRenderingState.LOADED)
						image.touch();
					else if (image.compareAndSetState(ChatImageRenderingState.PENDING, ChatImageRenderingState.LOADING))
						fetchBytesAsync(image);
					return image;
				}));

	}

	private void fetchBytesAsync(ActiveChatImage image) {
		imageFetcher.fetchAsync(image.data().uri()).thenAccept(fetchResult -> {
			switch (fetchResult) {
				case ImageFetchResult.Success(byte[] bytes) ->
						textureManager.decodeAndScheduleUploadAsync(image, bytes, image.getImageFormat());
				case ImageFetchResult.Failure(Exception cause) -> {
					if (cause instanceof UnsupportedOperationException) return;
					LOGGER.warn("😿 failed to fetch image from {}: {}", image.data().uri(), cause.getMessage());
					image.setState(ChatImageRenderingState.FAILED);
				}
			}
		});
	}
}
