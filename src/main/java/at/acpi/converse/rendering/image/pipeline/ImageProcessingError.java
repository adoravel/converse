package at.acpi.converse.rendering.image.pipeline;

/**
 * Expresses specific structural and operational reasons why an image
 * compilation task could not complete successfully.
 */
public enum ImageProcessingError {
	INVALID_URI,
	CANT_DECODE,

	/**
	 * The URL protocol is unsupported (e.g., file:// or ftp:// instead of http/https).
	 */
	UNSUPPORTED_PROTOCOL,

	/**
	 * The file extension extracted from the URL is missing or not allowed
	 */
	UNSUPPORTED_EXTENSION,

	/**
	 * The host domain was unreachable or failed structural validation
	 */
	UNREACHABLE
}
