package at.acpi.converse.rendering.image.hosting;

import at.acpi.converse.rendering.image.domain.ChatImageData;
import at.acpi.converse.rendering.image.pipeline.ImageProcessingResult;

import java.net.URI;

public interface ImageHostingService {
	/**
	 * Determines whether this service is capable of handling and processing the given URL.
	 *
	 * @param uri The URI of the image or the hosting page to validate.
	 * @return {@code true} if this service can process the URL; {@code false} otherwise.
	 */
	boolean isEligible(URI uri);

	/**
	 * Extracts, downloads, or transforms the provided URL into a standardized
	 * {@link ChatImageData} object containing metadata and image data.
	 *
	 * @param uri The source URI of the image to compile.
	 * @return A compiled {@code ChatImageData} object containing the processed image details.
	 */
	ImageProcessingResult compile(URI uri);
}
