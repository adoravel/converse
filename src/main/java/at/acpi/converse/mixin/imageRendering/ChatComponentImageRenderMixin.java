package at.acpi.converse.mixin.imageRendering;

import at.acpi.converse.Converse;
import at.acpi.converse.config.ConverseConfig;
import at.acpi.converse.rendering.image.ActiveChatImageRenderer;
import at.acpi.converse.rendering.image.domain.ActiveChatImage;
import at.acpi.converse.rendering.image.domain.ChatImageRenderingState;
import at.acpi.converse.rendering.image.domain.ImageAttributeHolder;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.net.URI;
import java.util.Optional;

@Mixin(targets = "net.minecraft.client.gui.components.ChatComponent$1")
public class ChatComponentImageRenderMixin {
	@Unique
	private static void converse$image$renderImage(
			ChatComponent.ChatGraphicsAccess graphicsAccess, ActiveChatImage image, int y, float alpha
	) {
		if (graphicsAccess instanceof ChatComponent.DrawingFocusedGraphicsAccess access) {
			ActiveChatImageRenderer.renderInChat(access.graphics, image, 0, y, alpha);
		} else if (graphicsAccess instanceof ChatComponent.DrawingBackgroundGraphicsAccess access) {
			ActiveChatImageRenderer.renderInChat(access.graphics, image, 0, y, alpha);
		}
	}

	//? <=1.21.11 {
	@WrapOperation(
			method = "accept",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;handleMessage(IFLnet/minecraft/util/FormattedCharSequence;)Z"
			)
	)
	private boolean converse$image$renderImageAtPivot(
			ChatComponent.ChatGraphicsAccess access,
			int y, float alpha, FormattedCharSequence content,
			Operation<Boolean> original,
			@SuppressWarnings("UnresolvedLocalCapture")
			@Local(argsOnly = true) GuiMessage.Line line
	) {
		boolean hovered = original.call(access, y, alpha, content);

		var config = ConverseConfig.image();
		if (!config.enableImages || !config.replaceUrlWithImage)
			return original.call(access, y, alpha, content);

		URI uri = ((ImageAttributeHolder) (Object) line).converse$getImageUri();
		if (uri == null)
			return original.call(access, y, alpha, content);

		Converse.imageLoadingOrchestrator()
				.requestCachedImage(uri)
				.ifPresent(image -> {
					if (image.getState() == ChatImageRenderingState.LOADED)
						converse$image$renderImage(access, image, y, alpha);
				});

		return hovered;
	}
	//?} else {

	//?}
}
