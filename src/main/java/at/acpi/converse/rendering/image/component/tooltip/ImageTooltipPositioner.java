package at.acpi.converse.rendering.image.component.tooltip;

import at.acpi.converse.rendering.image.domain.ActiveChatImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.jspecify.annotations.NonNull;

import static at.acpi.converse.rendering.image.ActiveChatImageRenderer.computeImageHeight;
import static at.acpi.converse.rendering.image.ActiveChatImageRenderer.computeImageWidth;

public class ImageTooltipPositioner implements ClientTooltipPositioner {
	private final ActiveChatImage image;

	public ImageTooltipPositioner(ActiveChatImage image) {
		this.image = image;
	}

	@Override
	public @NonNull Vector2ic positionTooltip(
			int screenWidth, int screenHeight, int mouseX, int mouseY, int tooltipWidth, int tooltipHeight
	) {
		final double scale = Minecraft.getInstance().gui.getChat().getScale();
		final int width = Math.min(tooltipWidth, computeImageWidth(scale, image.data()));
		final int height = Math.min(tooltipHeight, computeImageHeight(scale, image.data()));

		int x = mouseX + 8;
		int y = Math.max(mouseY - height - 8, 16);

		if (x + width > screenWidth) x = screenWidth - width;
		if (y + height > screenHeight) y = screenHeight - height;

		return new Vector2i(x, y);
	}
}
