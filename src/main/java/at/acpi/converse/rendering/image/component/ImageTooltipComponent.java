package at.acpi.converse.rendering.image.component;

import at.acpi.converse.rendering.image.ActiveChatImageRenderer;
import at.acpi.converse.rendering.image.domain.ActiveChatImage;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.jspecify.annotations.NonNull;

public record ImageTooltipComponent(ActiveChatImage image, float alpha) implements ClientTooltipComponent {
	@Override
	public int getHeight(@NonNull Font font) {
		return ActiveChatImageRenderer.computeImageHeight(image.getData());
	}

	@Override
	public int getWidth(@NonNull Font font) {
		return ActiveChatImageRenderer.computeImageWidth(image.getData());
	}

	@Override
	public void renderImage(
			@NonNull Font font, int x, int y, int width, int height, @NonNull GuiGraphics graphics
	) {
		var textureId = image.getData().resourceIdentifier();
		if (textureId == null) return;

		image.getImageFormat().render(graphics, textureId, x, y, getWidth(font), getHeight(font), alpha);
	}
}
