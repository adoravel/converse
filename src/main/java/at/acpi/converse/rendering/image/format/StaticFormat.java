package at.acpi.converse.rendering.image.format;

import at.acpi.converse.rendering.image.pipeline.FormatProcessingError;
import at.acpi.converse.rendering.image.pipeline.FormatProcessingResult;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Set;
import java.util.function.Function;

import static at.acpi.converse.rendering.image.hosting.GenericImageHostingService.extractExtension;

public final class StaticFormat implements ImageFormat {
	public static final Set<String> EXTENSIONS = Set.of("png", "jpg", "jpeg", "webp", "gif", "ico", "icns");

	private static final Logger LOGGER = LoggerFactory.getLogger("converse/images/static");

	private static boolean hasPNGHeader(byte[] bytes) {
		if (bytes.length < 16) return false;

		// dumb 8-byte signature \x89 P N G \r \n \x1a \n
		return (bytes[0] & 0xFF) == 137 &&
				bytes[1] == 'P' &&
				bytes[2] == 'N' &&
				bytes[3] == 'G' &&
				bytes[4] == '\r' &&
				bytes[5] == '\n' &&
				bytes[6] == '\u001A' &&
				bytes[7] == '\n' &&
				// ihdr len = 13
				bytes[8] == 0 &&
				bytes[9] == 0 &&
				bytes[10] == 0 &&
				bytes[11] == 13 &&
				// self-explanatory
				bytes[12] == 'I' &&
				bytes[13] == 'H' &&
				bytes[14] == 'D' &&
				bytes[15] == 'R';
	}

	private static boolean hasJPEGHeader(byte[] bytes) {
		if (bytes == null || bytes.length < 3) return false;

		// FF D8 FF
		return (bytes[0] & 0xFF) == 0xFF &&
				(bytes[1] & 0xFF) == 0xD8 &&
				(bytes[2] & 0xFF) == 0xFF;
	}

	private static boolean hasGIFHeader(byte[] bytes) {
		if (bytes == null || bytes.length < 6) return false;

		return bytes[0] == 'G' &&
				bytes[1] == 'I' &&
				bytes[2] == 'F' &&
				bytes[3] == '8' &&
				(bytes[4] == '7' || bytes[4] == '9') &&
				bytes[5] == 'a';
	}

	private static <R> R withSafeBuffer(byte[] bytes, Function<ByteBuffer, R> processor) {
		@SuppressWarnings("resource")
		MemoryStack stack = MemoryStack.stackGet();

		if (stack.getPointer() < bytes.length) {
			ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length);
			try {
				buffer.put(bytes).rewind();
				return processor.apply(buffer);
			} finally {
				MemoryUtil.memFree(buffer);
			}
		} else {
			try (MemoryStack frame = MemoryStack.stackPush()) {
				ByteBuffer buffer = frame.malloc(bytes.length);
				buffer.put(bytes).rewind();
				return processor.apply(buffer);
			}
		}
	}

	private static FormatProcessingResult decodeViaSTB(byte[] bytes) {
		return withSafeBuffer(bytes, textureBuffer -> {
			try (MemoryStack stack = MemoryStack.stackPush()) {
				IntBuffer width = stack.mallocInt(1);
				IntBuffer height = stack.mallocInt(1);
				IntBuffer channels = stack.mallocInt(1);

				ByteBuffer data = STBImage.stbi_load_from_memory(textureBuffer, width, height, channels, 4);
				if (data == null) {
					String reason = STBImage.stbi_failure_reason();
					return new FormatProcessingResult.Failure(FormatProcessingError.INTERNAL_ERROR, reason);
				}

				long address = MemoryUtil.memAddress(data);
				return FormatProcessingResult.Success.staticImage(
						new NativeImage(NativeImage.Format.RGBA, width.get(0), height.get(0), true, address)
				);
			} catch (Exception e) {
				return new FormatProcessingResult.Failure(FormatProcessingError.CORRUPT_DATA, e.getMessage());
			}
		});
	}

	private static FormatProcessingResult toNativeImage(BufferedImage src) {
		int width = src.getWidth();
		int height = src.getHeight();
		NativeImage data = new NativeImage(NativeImage.Format.RGBA, width, height, false);

		// nativeimage expects abgr little-endian it's so stupid
		int[] pixels = src.getRGB(0, 0, width, height, null, 0, width);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = pixels[y * width + x];

				int a = (pixel >> 24) & 0xFF;
				int r = (pixel >> 16) & 0xFF;
				int g = (pixel >> 8) & 0xFF;
				int b = pixel & 0xFF;
				int abgr = (a << 24) | (b << 16) | (g << 8) | r;

				data.setPixelABGR(x, y, abgr);
			}
		}

		return FormatProcessingResult.Success.staticImage(data);
	}

	private static FormatProcessingResult decodeViaImageIO(byte[] bytes) {
		try (ByteArrayInputStream stream = new ByteArrayInputStream(bytes)) {
			BufferedImage buffered = ImageIO.read(stream);
			if (buffered == null) {
				return new FormatProcessingResult.Failure(FormatProcessingError.UNSUPPORTED_FORMAT);
			}

			return toNativeImage(buffered);
		} catch (IOException e) {
			LOGGER.warn("failed to decode static image via imageio: {}", e.getMessage());
			return new FormatProcessingResult.Failure(FormatProcessingError.INTERNAL_ERROR);
		}
	}

	public FormatProcessingResult decode(byte[] data) {
		if (hasPNGHeader(data)) {
			try {
				NativeImage nativeImage = NativeImage.read(data);
				return FormatProcessingResult.Success.staticImage(nativeImage);
			} catch (IOException e) {
				return new FormatProcessingResult.Failure(FormatProcessingError.CORRUPT_DATA);
			}
		}

		if (hasJPEGHeader(data) || hasGIFHeader(data)) {
			return decodeViaSTB(data);
		}

		LOGGER.warn("attempting dúzia de macacos 🙂‍↕️");
		return decodeViaImageIO(data);
	}

	@Override
	public boolean isEligible(URI uri) {
		String ext = extractExtension(uri);
		return ext != null && EXTENSIONS.contains(ext);
	}

	@Override
	public Decoder decoder() {
		return this::decode;
	}

	@Override
	public void render(GuiGraphics graphics, Identifier textureId, int x, int y, int width, int height, float alpha) {
		graphics.blit(
				RenderPipelines.GUI_TEXTURED, textureId,
				x, y, 0, 0, width, height, width, height,
				ARGB.white(alpha)
		);
	}
}
