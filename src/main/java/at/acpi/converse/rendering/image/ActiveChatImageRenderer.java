package at.acpi.converse.rendering.image;

import at.acpi.converse.config.ConverseConfig;
import at.acpi.converse.rendering.image.domain.ActiveChatImage;
import at.acpi.converse.rendering.image.domain.ChatImageData;
import at.acpi.converse.rendering.image.domain.ChatImageRenderingState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public final class ActiveChatImageRenderer {
	private ActiveChatImageRenderer() {
	}

	private static void render(
			GuiGraphics graphics, ActiveChatImage image, int x, int y, int width, int height, float alpha
	) {
		if (image.getState() != ChatImageRenderingState.LOADED) return;

		Identifier textureId = image.getData().resourceIdentifier();
		if (textureId == null) return;

		image.getImageFormat().render(graphics, textureId, x, y, width, height, alpha);
	}

	public static int computeImageWidth(double scale) {
		final int maxWidth = ConverseConfig.image().maxWidth;
		final int maxChatWidth = (int) ((float) Minecraft.getInstance().gui.getChat().getWidth() / scale) - 8;

		return Math.min(maxWidth, maxChatWidth);
	}

	public static int computeImageHeight(double scale, ChatImageData data) {
		final int maxWidth = computeImageWidth(scale);

		final int maxHeight = ConverseConfig.image().maxHeight;
		final int maxChatHeight = ChatComponent.getHeight(Minecraft.getInstance().options.chatHeightUnfocused().get());
		final int targetHeight = Math.min(maxHeight, maxChatHeight);

		float wRatio = (maxWidth > 0) ? (float) data.width() / maxWidth : 1.0f;
		float hRatio = (targetHeight > 0) ? (float) data.height() / targetHeight : 1.0f;
		float dominant = Math.max(wRatio, hRatio);

		return Math.max(1, (int) (data.height() / dominant));
	}

	public static int computeImageHeight(ChatImageData imageData) {
		return computeImageHeight(Minecraft.getInstance().gui.getChat().getScale(), imageData);
	}

	public static int computeImageLineWidth(int width, char input) {
		var font = Minecraft.getInstance().font;
		var glyph = font.getGlyphSource(Style.EMPTY.getFont()).getGlyph(input);
		var glyphWidth = Mth.ceil(glyph.info().getAdvance());
		return width / glyphWidth - 1;
	}

	public static int computeImageLineCount(int height) {
		final int lineHeight = Minecraft.getInstance().gui.getChat().getLineHeight();
		return Mth.ceil((float) height / lineHeight);
	}

	public static void renderInChat(GuiGraphics graphics, ActiveChatImage image, int x, int y, float alpha) {
		final double scale = Minecraft.getInstance().gui.getChat().getScale();
		final int width = computeImageWidth(scale),
				height = computeImageHeight(scale, image.getData());

		render(graphics, image, x, y, width, height, alpha);
	}
}
