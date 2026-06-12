package at.acpi.converse.rendering.image;

import at.acpi.converse.config.ConverseConfig;
import at.acpi.converse.rendering.image.domain.ActiveChatImage;
import at.acpi.converse.rendering.image.domain.ChatImageRenderingState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public final class ActiveChatImageRenderer {
	private ActiveChatImageRenderer() {
	}

	public static void render(GuiGraphics graphics, ActiveChatImage image, int x, int y, int maxWidth, int maxHeight) {
		if (image.getState() != ChatImageRenderingState.LOADED) return;

		Identifier textureId = image.getData().resourceIdentifier();
		if (textureId == null) return;

		image.touch();

		int imageWidth = image.getData().width();
		int imageHeight = image.getData().height();

		float widthRatio = (maxWidth > 0) ? (float) imageWidth / maxWidth : 1.0f;
		float heightRatio = (maxHeight > 0) ? (float) imageWidth / maxHeight : 1.0f;
		float dominantRatio = Math.max(widthRatio, heightRatio);

		int width = Math.max(1, (int) (imageWidth / dominantRatio));
		int height = Math.max(1, (int) (imageHeight / dominantRatio));

		graphics.blit(RenderPipelines.GUI_TEXTURED, textureId, x, y, 0, 0, width, height, width, height);
	}

	public static void renderInChat(GuiGraphics graphics, ActiveChatImage image, int x, int y) {
		int maxWidth = ConverseConfig.image().maxWidth;
		int maxHeight = ConverseConfig.image().maxHeight;

		Minecraft mc = Minecraft.getInstance();
		ChatComponent chat = mc.gui.getChat();
		Double unfocusedHeight = mc.options.chatHeightUnfocused().get();

		double chatScale = chat.getScale();
		int maxChatWidth = (int) ((float) chat.getWidth() / chatScale) - 8;
		int maxChatHeight = (int) ((float) ChatComponent.getHeight(unfocusedHeight) / chatScale) - 8;

		render(graphics, image, x, y, Math.min(maxChatWidth, maxWidth), Math.min(maxChatHeight, maxHeight));
	}
}
