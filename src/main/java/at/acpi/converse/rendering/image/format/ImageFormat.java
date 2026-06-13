package at.acpi.converse.rendering.image.format;

import at.acpi.converse.rendering.image.pipeline.FormatProcessingResult;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

import java.net.URI;

/**
 * Represents a specific image format and its decoding/rendering rules.
 */
public sealed interface ImageFormat permits StaticFormat {
	/**
	 * Determines if this format can handle the given URL based on its structure/extension.
	 */
	boolean isEligible(URI uri);

	/**
	 * Creates a new decoder instance for this format.
	 * Decoders hold temporary state (like frame counters for GIFs) and are NOT thread-safe.
	 * A new decoder must be created for each image load.
	 */
	Decoder decoder();

	/**
	 * Renders the decoded image to the screen.
	 *
	 * @param textureId The texture identifier
	 * @param width     Rendered width
	 * @param height    Rendered height
	 */
	void render(GuiGraphics graphics, Identifier textureId, int x, int y, int width, int height, float alpha);

	/**
	 * Interface for decoding image bytes into usable pixel data.
	 * Implementations are stateful and operate on a background thread.
	 */
	@FunctionalInterface
	interface Decoder {
		FormatProcessingResult decode(byte[] bytes);
	}
}
