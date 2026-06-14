package at.acpi.converse.mixin.imageRendering;

import at.acpi.converse.rendering.image.domain.ImageAttributeHolder;
import net.minecraft.client.GuiMessage;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.net.URI;

@Mixin(GuiMessage.Line.class)
public class GuiMessageLineImageHolderMixin implements ImageAttributeHolder {
	@Unique
	private URI converse$imageUri;

	@Unique
	private int converse$placeholderIndex;

	@Override
	public @Nullable URI converse$getImageUri() {
		return converse$imageUri;
	}

	@Override
	public void converse$setImageUri(@Nullable URI uri) {
		converse$imageUri = uri;
	}

	@Override
	public int converse$getImagePlaceholderIndex() {
		return converse$placeholderIndex;
	}

	@Override
	public void converse$setImagePlaceholderIndex(int packed) {
		converse$placeholderIndex = packed;
	}
}
