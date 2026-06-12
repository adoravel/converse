package at.acpi.converse;

import at.acpi.converse.config.ConverseConfig;
import at.acpi.converse.hotkey.ConverseHotkeys;
import at.acpi.converse.rendering.image.ChatImageTextureManager;
import at.acpi.converse.rendering.image.ImageLoadingOrchestrator;
import at.acpi.converse.rendering.image.hosting.DiscordImageHostingService;
import at.acpi.converse.rendering.image.hosting.GenericImageHostingService;
import at.acpi.converse.rendering.image.hosting.ImageHostingRegistry;
import at.acpi.converse.rendering.image.network.RemoteImageFetcher;
import at.acpi.converse.rendering.image.storage.ImageCachePool;
import at.acpi.converse.rendering.image.storage.ImageFilesystemCache;
import net.minecraft.resources.Identifier;

public final class Converse {
	public static final String MOD_ID = "converse";

	private static volatile ImageLoadingOrchestrator imageLoadingOrchestrator;

	public static void init() {
		ConverseConfig.loadConfig();
		ConverseHotkeys.registerHotkeys();
	}

	public static Identifier of(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public static ImageLoadingOrchestrator imageLoadingOrchestrator() {
		ImageLoadingOrchestrator localRef = imageLoadingOrchestrator;
		if (localRef == null) {
			synchronized (Converse.class) {
				localRef = imageLoadingOrchestrator;
				if (localRef == null) {
					imageLoadingOrchestrator = localRef = createImageLoadingOrchestrator();
				}
			}
		}
		return localRef;
	}

	private static ImageLoadingOrchestrator createImageLoadingOrchestrator() {
		var fileCache = new ImageFilesystemCache(
				ConversePlatform.PLATFORM.getConfigFolder().resolve("converse").resolve("image-cache")
		);
		fileCache.pruneFilesystemCacheAsync();

		var fetcher = new RemoteImageFetcher(fileCache);
		var textureManager = new ChatImageTextureManager();
		var cachePool = new ImageCachePool(textureManager);

		var hostingRegistry = new ImageHostingRegistry.Builder()
				.register(new DiscordImageHostingService())
				.register(new GenericImageHostingService())
				.build();

		return new ImageLoadingOrchestrator(hostingRegistry, cachePool, textureManager, fetcher);
	}
}
