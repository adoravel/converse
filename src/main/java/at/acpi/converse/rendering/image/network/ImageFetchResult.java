package at.acpi.converse.rendering.image.network;

public sealed interface ImageFetchResult {
	record Success(byte[] bytes) implements ImageFetchResult {
	}

	record Failure(Exception cause) implements ImageFetchResult {
	}
}
