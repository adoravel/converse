package at.acpi.converse.rendering.image;

import at.acpi.converse.config.ConverseConfig;
import at.acpi.converse.rendering.image.domain.ActiveChatImage;
import at.acpi.converse.rendering.image.domain.ChatImageData;
import at.acpi.converse.rendering.image.domain.ChatImageRenderingState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public final class ActiveChatImageRenderer {
	private ActiveChatImageRenderer() {
	}

	public static void render(
			GuiGraphics graphics, ActiveChatImage image, int x, int y, int maxWidth, int maxHeight, float alpha
	) {
		if (image.getState() != ChatImageRenderingState.LOADED) return;

		Identifier textureId = image.getData().resourceIdentifier();
		if (textureId == null) return;

		image.touch();

		final int imageWidth = image.getData().width();
		final int imageHeight = image.getData().height();

		final float widthRatio = (maxWidth > 0) ? (float) imageWidth / maxWidth : 1.0f;
		final float heightRatio = (maxHeight > 0) ? (float) imageWidth / maxHeight : 1.0f;
		final float dominantRatio = Math.max(widthRatio, heightRatio);

		final int color = ARGB.white(alpha);

		final int width = Math.max(1, (int) (imageWidth / dominantRatio));
		final int height = Math.max(1, (int) (imageHeight / dominantRatio));

		y -= height;
		graphics.blit(RenderPipelines.GUI_TEXTURED, textureId, x, y, 0, 0, width, height, width, height, color);
	}

	public static int computeImageWidth(double scale) {
		final int maxWidth = ConverseConfig.image().maxWidth;
		final int maxChatWidth = (int) ((float) Minecraft.getInstance().gui.getChat().getWidth() / scale) - 8;

		return Math.min(maxWidth, maxChatWidth);
	}

	public static int computeImageHeight(double scale, ChatImageData data) {
		int maxW = computeImageWidth(scale);
		int maxH = ConverseConfig.image().maxHeight;

		float wRatio = (maxW > 0) ? (float) data.width() / maxW : 1.0f;
		float hRatio = (maxH > 0) ? (float) data.height() / maxH : 1.0f;
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
		return width / glyphWidth;
	}

	public static int computeImageLineCount(int height) {
		final int lineHeight = Minecraft.getInstance().gui.getChat().getLineHeight();
		return Mth.ceil((float) height / lineHeight) + 1;
	}

	public static void renderInChat(GuiGraphics graphics, ActiveChatImage image, int x, int y, float alpha) {
		final double scale = Minecraft.getInstance().gui.getChat().getScale();
		final int width = computeImageWidth(scale),
				height = computeImageHeight(scale, image.getData());

		render(graphics, image, x, y, width, height, alpha);
	}
}
