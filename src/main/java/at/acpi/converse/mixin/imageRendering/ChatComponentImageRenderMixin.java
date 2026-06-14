package at.acpi.converse.mixin.imageRendering;

import at.acpi.converse.Converse;
import at.acpi.converse.config.ConverseConfig;
import at.acpi.converse.rendering.image.ActiveChatImageRenderer;
import at.acpi.converse.rendering.image.domain.ChatImageRenderingState;
import at.acpi.converse.rendering.image.domain.ImageAttributeHolder;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.net.URI;

@Mixin(targets = "net.minecraft.client.gui.components.ChatComponent$1")
public class ChatComponentImageRenderMixin {
	@WrapOperation(
			method = "accept",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;handleMessage(IFLnet/minecraft/util/FormattedCharSequence;)Z"
			)
	)
	private boolean converse$image$render(
			ChatComponent.ChatGraphicsAccess access,
			int y, float alpha, FormattedCharSequence content,
			Operation<Boolean> original,
			@SuppressWarnings({"UnresolvedLocalCapture", "RedundantSuppression"})
			@Local(argsOnly = true) GuiMessage.Line line
	) {
		boolean hovered = original.call(access, y, alpha, content);
		if (line == null) return hovered;

		var config = ConverseConfig.image();
		if (!config.enableImages)
			return hovered;

		//noinspection ConstantValue
		if (!((Object) line instanceof ImageAttributeHolder holder)) return hovered;

		URI uri = holder.converse$getImageUri();
		if (uri == null) return hovered;

		Converse.imageLoadingOrchestrator()
				.requestCachedImage(uri)
				.ifPresent(image -> {
					if (image.getState() != ChatImageRenderingState.LOADED) return;

					if (config.replaceUrlWithImage && holder.converse$isImagePlaceholder()) {
						var graphics =
								access instanceof ChatComponent.DrawingBackgroundGraphicsAccess f
										? f.graphics
										: access instanceof ChatComponent.DrawingFocusedGraphicsAccess f
										  ? f.graphics
										  : null;
						if (graphics != null)
							ActiveChatImageRenderer.renderInChat(graphics, holder, image, 0, y, alpha);
					} else if (holder.converse$isTooltipAnchor()
							&& access instanceof ChatComponent.DrawingFocusedGraphicsAccess f
							&& f.hoveredStyle != null
							&& uri.toString().equals(f.hoveredStyle.getInsertion())) {
						ActiveChatImageRenderer.renderTooltip(
								f.graphics, f.font, image, f.globalMouseX, f.globalMouseY, alpha
						);
					}
				});

		return hovered;
	}
}
