package at.acpi.converse.rendering.image.pipeline;

import at.acpi.converse.rendering.image.domain.ChatImageData;

public sealed interface ImageProcessingResult {
	record Success(ChatImageData data) implements ImageProcessingResult {
	}

	record Failure(ImageProcessingError error) implements ImageProcessingResult {
	}
}
