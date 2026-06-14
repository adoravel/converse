package at.acpi.converse.rendering.image.component.tooltip;

import at.acpi.converse.rendering.image.domain.ActiveChatImage;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.jspecify.annotations.NonNull;

public class ImageTooltipComponent implements ClientTooltipComponent {
	private final ActiveChatImage image;
	private final float alpha;

	public ImageTooltipComponent(ActiveChatImage image, float alpha) {
		this.image = image;
		this.alpha = alpha;
	}

	@Override
	public int getWidth(@NonNull Font font) {
		return image.data().width();
	}

	@Override
	public int getHeight(@NonNull Font font) {
		return image.data().height();
	}

	@Override //? >=26.1 {
	/*public void extractImage(@NonNull Font font, int x, int y, int w, int h, @NonNull GuiGraphics graphics) {
		*///?} else {
		public void renderImage(@NonNull Font font, int x, int y, int w, int h, @NonNull GuiGraphics graphics) {
		 //?}
		var resourceId = image.data().resourceIdentifier();
		if (resourceId == null) return;

		image.getImageFormat().render(
				graphics, resourceId, x, y, getWidth(font), getHeight(font), alpha
		);
	}
}
