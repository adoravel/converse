package at.acpi.converse.rendering.image.storage;

import at.acpi.converse.config.ConverseConfig;
import at.acpi.converse.rendering.image.ChatImageTextureManager;
import at.acpi.converse.rendering.image.domain.ActiveChatImage;
import at.acpi.converse.rendering.image.domain.ChatImageData;
import at.acpi.converse.rendering.image.format.ImageFormat;
import at.acpi.converse.rendering.image.pipeline.ImageProcessingResult;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ImageCachePool {
	private final Map<String, ActiveChatImage> cache;
	private final ChatImageTextureManager textureManager;

	public ImageCachePool(ChatImageTextureManager textureManager) {
		this.textureManager = textureManager;

		this.cache = new LinkedHashMap<>(16, 0.75f, true) {
			@Override
			protected boolean removeEldestEntry(Map.Entry<String, ActiveChatImage> eldest) {
				if (size() > ConverseConfig.image().maxCacheEntries) {
					textureManager.releaseTexture(eldest.getValue().getData());
					return true;
				}
				return false;
			}
		};
	}

	public synchronized Optional<ActiveChatImage> getOrCreate(
			String key, ImageFormat imageFormat, Supplier<ImageProcessingResult> fallback
	) {
		ActiveChatImage image = cache.computeIfAbsent(key, k -> {
			if (fallback.get() instanceof ImageProcessingResult.Success(ChatImageData data)) {
				return new ActiveChatImage(data, imageFormat);
			}
			return null;
		});
		return Optional.ofNullable(image);
	}

	public synchronized Optional<ActiveChatImage> lookup(String id) {
		ActiveChatImage image = cache.get(id);
		if (image != null) {
			image.touch();
			return Optional.of(image);
		}
		return Optional.empty();
	}

	public synchronized void evict(String id) {
		ActiveChatImage image = cache.remove(id);
		if (image != null) {
			textureManager.releaseTexture(image.getData());
		}
	}
}
