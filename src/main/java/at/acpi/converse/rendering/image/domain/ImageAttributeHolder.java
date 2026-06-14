package at.acpi.converse.rendering.image.domain;

import org.jspecify.annotations.Nullable;

import java.net.URI;

public interface ImageAttributeHolder {
@Nullable URI converse$getImageUri();

	void converse$setImageUri(@Nullable URI url);

	default boolean converse$isImagePlaceholder() {
		return converse$getImageUri() != null && converse$getImagePlaceholderIndex() >= 0;
	}

	default boolean converse$isTooltipAnchor() {
		return converse$getImageUri() != null && converse$getImagePlaceholderIndex() < 0;
	}

	void converse$setImagePlaceholderIndex(int index);

	int converse$getImagePlaceholderIndex();
}
