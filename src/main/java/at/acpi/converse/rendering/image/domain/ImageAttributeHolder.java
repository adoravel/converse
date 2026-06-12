package at.acpi.converse.rendering.image.domain;

import org.jspecify.annotations.Nullable;

import java.net.URI;

public interface ImageAttributeHolder {
	@Nullable URI converse$getImageUri();

	void converse$setImageUri(@Nullable URI url);
}
