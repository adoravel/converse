package at.acpi.converse.rendering.image;

import at.acpi.converse.rendering.image.domain.ActiveChatImage;
import net.minecraft.client.gui.GuiGraphics;
import at.acpi.converse.rendering.image.domain.ChatImageRenderingState;
import net.minecraft.resources.Identifier;

public final class ActiveChatImageRenderer {
	private ActiveChatImageRenderer() {
	}

	public static void render(GuiGraphics graphics, ActiveChatImage image, int x, int y) {
		if (image.getState() != ChatImageRenderingState.LOADED) return;

		Identifier textureId = image.getData().resourceIdentifier();
		if (textureId == null) return;

		image.touch();

		int width = image.getData().width();
		int height = image.getData().height();

		//? <=1.21.11 {
		graphics.blit(
				textureId,
				x, y, 0, 0,
				width, height,
				width, height
		);
		//?} else {
        /*graphics.blit(
				net.minecraft.client.renderer.RenderPipelines.GUI,
                textureId,
                x, y,
                width, height,
                0, 0,
                width, height,
                width, height
        );
        *///?}
	}

	public static void renderTooltip(
			//? <=1.21.11 {
			GuiGraphics graphics,
			//?} else {
			/*GuiGraphics graphics,
			 *///?}
			ActiveChatImage image, int x, int y, int maxTooltipWidth
	) {
		if (image.getState() != ChatImageRenderingState.LOADED) return;

		Identifier textureId = image.getData().resourceIdentifier();
		if (textureId == null) return;

		int textureW = image.getData().width();
		int textureH = image.getData().height();

		int renderW = textureW;
		int renderH = textureH;

		if (textureW > maxTooltipWidth) {
			float scale = (float) maxTooltipWidth / textureW;
			renderW = maxTooltipWidth;
			renderH = Math.max(1, (int) (textureH * scale));
		}

		//? <=1.21.11 {
		graphics.blit(
				textureId,
				x, y, 0, 0,
				renderW, renderH,
				textureW, textureH
		);
		//?} else {
        /*graphics.blit(
				net.minecraft.client.renderer.RenderPipelines.GUI,
                textureId,
                x, y,
                renderW, renderH,
                0, 0,
                textureW, textureH,
                textureW, textureH
        );
        *///?}
	}
}
