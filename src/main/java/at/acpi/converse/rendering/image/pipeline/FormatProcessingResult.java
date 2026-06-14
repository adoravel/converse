package at.acpi.converse.rendering.image.pipeline;

import com.mojang.blaze3d.platform.NativeImage;
import org.jspecify.annotations.Nullable;

public sealed interface FormatProcessingResult {
	record Success(NativeImage data, int frameDurationMs) implements FormatProcessingResult {
		public static final int STATIC_DURATION = -1;

		public static Success staticImage(NativeImage data) {
			return new Success(data, STATIC_DURATION);
		}
	}

	record Failure(FormatProcessingError error, @Nullable String message) implements FormatProcessingResult {
		public Failure(FormatProcessingError error) {
			this(error, null);
		}
	}
}
