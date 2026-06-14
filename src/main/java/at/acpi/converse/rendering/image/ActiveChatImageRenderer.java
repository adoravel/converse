package at.acpi.converse.rendering.image;

import at.acpi.converse.config.ConverseConfig;
import at.acpi.converse.rendering.image.component.tooltip.ImageTooltipComponent;
import at.acpi.converse.rendering.image.component.tooltip.ImageTooltipPositioner;
import at.acpi.converse.rendering.image.domain.ActiveChatImage;
import at.acpi.converse.rendering.image.domain.ChatImageData;
import at.acpi.converse.rendering.image.domain.ImageAttributeHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.List;

public final class ActiveChatImageRenderer {
	private ActiveChatImageRenderer() {
	}

	public static int computeMaxBoundingWidth(double scale) {
		final int maxWidth = ConverseConfig.image().maxWidth;
		final int maxChatWidth = (int) ((float) Minecraft.getInstance().gui.getChat().getWidth() / scale) - 8;

		return Math.min(maxWidth, maxChatWidth);
	}

	private static float calculateScaleRatio(double scale, ChatImageData data) {
		final int maxWidth = computeMaxBoundingWidth(scale);
		final int maxHeight = ConverseConfig.image().maxHeight;

		final int maxChatHeight = ChatComponent.getHeight(Minecraft.getInstance().options.chatHeightUnfocused().get());
		final int targetHeight = Math.min(maxHeight, maxChatHeight);

		float wRatio = (maxWidth > 0) ? (float) data.width() / maxWidth : 1.0f;
		float hRatio = (targetHeight > 0) ? (float) data.height() / targetHeight : 1.0f;

		return Math.max(wRatio, hRatio);
	}

	public static int computeImageWidth(double scale, ChatImageData data) {
		float ratio = Math.max(1f, calculateScaleRatio(scale, data));
		return Math.max(1, (int) (data.width() / ratio));
	}

	public static int computeImageHeight(double scale, ChatImageData data) {
		float ratio = Math.max(1f, calculateScaleRatio(scale, data));
		return Math.max(1, (int) (data.height() / ratio));
	}

	public static int computeImageWidth(ChatImageData imageData) {
		return computeImageWidth(Minecraft.getInstance().gui.getChat().getScale(), imageData);
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
		return height / Minecraft.getInstance().gui.getChat().getLineHeight() + 1;
	}

	/**
	 * Renders an image inline in the chat, replacing a URL.
	 */
	public static void renderInChat(
			GuiGraphics graphics, ImageAttributeHolder metadata,
			ActiveChatImage image, int x, int y, float alpha
	) {
		Identifier textureId = image.data().resourceIdentifier();
		if (textureId == null) return;

		var chat = Minecraft.getInstance().gui.getChat();
		final double scale = chat.getScale();
		final int width = computeImageWidth(scale, image.data()),
				height = computeImageHeight(scale, image.data());

		final int idx = metadata.converse$getImagePlaceholderIndex();

		int renderY = y - idx * chat.getLineHeight();
		int screenBottomBound = Mth.floor((float) (graphics.guiHeight() - 40) / scale);
		int screenRightBound = width + x;

		graphics.pose().pushMatrix();
		graphics.enableScissor(x, y, screenRightBound, screenBottomBound);
		image.getImageFormat().render(graphics, textureId, x, renderY, width, height, alpha);
		graphics.disableScissor();
		graphics.pose().popMatrix();
	}

	/**
	 * Renders a small preview of the image as a tooltip near the mouse cursor,
	 * used when {@code replaceUrlWithImage = false} and the user hovers the
	 * {@code [Image]} anchor
	 */
	public static void renderTooltip(
			GuiGraphics graphics, Font font, ActiveChatImage image, int mouseX, int mouseY, float alpha
	) {
		Identifier textureId = image.data().resourceIdentifier();
		if (textureId == null) return;

		var tooltip = new ImageTooltipComponent(image, alpha);
		var positioner = new ImageTooltipPositioner(image);

		graphics.nextStratum();
		//? <=1.21.11 {
		graphics.renderTooltip(
		 //?} else {
		/*graphics.tooltip(
				*///?}
				font, List.of(tooltip),
				mouseX, mouseY,
				positioner,
				null
		);
	}
}
