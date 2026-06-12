package at.acpi.converse.rendering.image.domain;

import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.net.URI;

public class ChatImageData {
	private final URI uri;

	private volatile @Nullable Identifier resourceIdentifier;
	private volatile int width;
	private volatile int height;

	public ChatImageData(URI uri, int width, int height) {
		this.uri = uri;
		this.width = width;
		this.height = height;
		this.resourceIdentifier = null;
	}

	public URI uri() {
		return this.uri;
	}

	public @Nullable Identifier resourceIdentifier() {
		return this.resourceIdentifier;
	}

	public int width() {
		return this.width;
	}

	public int height() {
		return this.height;
	}

	public void updateTextureData(Identifier id, int width, int height) {
		this.resourceIdentifier = id;
		this.width = width;
		this.height = height;
	}
}
