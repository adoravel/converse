package at.acpi.converse.rendering.image;

import at.acpi.converse.Converse;
import at.acpi.converse.config.ConverseConfig;
import at.acpi.converse.rendering.image.domain.ActiveChatImage;
import at.acpi.converse.rendering.image.domain.ChatImageData;
import at.acpi.converse.rendering.image.domain.ChatImageRenderingState;
import at.acpi.converse.rendering.image.format.ImageFormat;
import at.acpi.converse.rendering.image.pipeline.FormatProcessingResult;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ChatImageTextureManager {
	private static final Logger LOGGER = LoggerFactory.getLogger("converse/images");

	private final ExecutorService executor;

	public ChatImageTextureManager() {
		this.executor = Executors.newFixedThreadPool(2, r -> {
			Thread t = new Thread(r, "converse-image-loader");
			t.setDaemon(true);
			return t;
		});
	}

	/**
	 * Schedules image decoding and GL uploading.
	 */
	public void decodeAndScheduleUploadAsync(ActiveChatImage image, byte[] bytes, ImageFormat format) {
		executor.submit(() -> decodeAndScheduleUpload(image, bytes, format));
	}

	/**
	 * Releases a GL texture from the Render thread.
	 */
	public void releaseTexture(ChatImageData data) {
		if (data.resourceIdentifier() != null) {
			Minecraft.getInstance().execute(() ->
					Minecraft.getInstance().getTextureManager()
							.release(Objects.requireNonNull(data.resourceIdentifier())));
		}
	}

	@SuppressWarnings("resource")
	private void decodeAndScheduleUpload(ActiveChatImage image, byte[] bytes, ImageFormat format) {
		FormatProcessingResult result = format.decoder().decode(bytes);
		if (result instanceof FormatProcessingResult.Failure failure) {
			LOGGER.warn("😿 failed to decode image from {}: {}", image.getData().uri(), failure.message());
			image.setState(ChatImageRenderingState.FAILED);
			return;
		}
		try {
			var nativeImage = ((FormatProcessingResult.Success) result).data();
			Minecraft.getInstance().execute(() -> uploadTexture(image, nativeImage));
		} catch (Exception e) {
			LOGGER.warn("😿 failed to upload image from {}: {}", image.getData().uri(), e.getMessage());
			image.setState(ChatImageRenderingState.FAILED);
		}
	}

	private void uploadTexture(ActiveChatImage image, NativeImage nativeImage) {
		try (nativeImage) {
			int srcW = nativeImage.getWidth();
			int srcH = nativeImage.getHeight();

			int maxWidth = ConverseConfig.image().maxWidth;
			int maxHeight = ConverseConfig.image().maxHeight;

			float scale = Math.min((float) maxWidth / srcW, (float) maxHeight / srcH);
			if (scale > 1f) scale = 1f;

			int renderedWidth = Math.max(1, (int) (srcW * scale));
			int renderedHeight = Math.max(1, (int) (srcH * scale));

			String label = "converse:image/" + hash(image.getData().uri().toString());
			DynamicTexture texture = new DynamicTexture(() -> label, nativeImage);

			String path = "image/" + hash(image.getData().uri().toString());
			Identifier id = Converse.of(path);

			Minecraft.getInstance().getTextureManager().register(id, texture);

			if (image.compareAndSetState(ChatImageRenderingState.LOADING, ChatImageRenderingState.LOADED)) {
				image.getData().updateTextureData(id, renderedWidth, renderedHeight);
				Minecraft.getInstance().gui.getChat().rescaleChat();
			}
		} catch (Exception e) {
			LOGGER.warn("😿 failed to upload image for {}: {}", image.getData().uri(), e.getMessage());
			image.setState(ChatImageRenderingState.FAILED);
		}
	}

	public static String hash(String url) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(url.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(hashBytes);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("jvm somehow missing standard sha-256 provider???", e);
		}
	}
}
