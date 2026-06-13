package at.acpi.converse.rendering.image.pipeline;

/**
 * Expresses specific structural and operational reasons why an image
 * format decoding task could not complete successfully.
 */
public enum FormatProcessingError {
	/**
	 * The format requires a decoder that is not supported by the current environment (e.g., missing TwelveMonkeys...
	 * why would you do that to the monkeys :/).
	 */
	UNSUPPORTED_FORMAT,

	/**
	 * The image data is corrupted, truncated, or violates the format specification.
	 */
	CORRUPT_DATA,

	/**
	 * The image dimensions or length exceeds safe limits.
	 */
	OVERFLOW_RISK,

	/**
	 * An unexpected error occurred during the I/O or decoding process.
	 */
	INTERNAL_ERROR
}
