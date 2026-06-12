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
	private @Nullable URI converse$imageUri;

	@Override
	public @Nullable URI converse$getImageUri() {
		return this.converse$imageUri;
	}

	@Override
	public void converse$setImageUri(@Nullable URI uri) {
		this.converse$imageUri = uri;
	}
}
