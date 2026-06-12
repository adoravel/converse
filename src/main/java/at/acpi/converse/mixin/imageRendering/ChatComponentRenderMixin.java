package at.acpi.converse.mixin.imageRendering;

import at.acpi.converse.Converse;
import at.acpi.converse.config.ConverseConfig;
import at.acpi.converse.rendering.image.ActiveChatImageRenderer;
import at.acpi.converse.rendering.image.domain.ActiveChatImage;
import at.acpi.converse.rendering.image.domain.ChatImageRenderingState;
import at.acpi.converse.rendering.image.domain.ImageAttributeHolder;
import at.acpi.converse.rendering.image.hosting.ImageHostingService;
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
public class ChatComponentRenderMixin {
	@Unique
	private static void converse$image$renderImage(
			ChatComponent.ChatGraphicsAccess graphicsAccess, ActiveChatImage image, int x, int y
	) {
		if (graphicsAccess instanceof ChatComponent.DrawingFocusedGraphicsAccess access) {
			ActiveChatImageRenderer.renderInChat(access.graphics, image, x, y);
		} else if (graphicsAccess instanceof ChatComponent.DrawingBackgroundGraphicsAccess access) {
			ActiveChatImageRenderer.renderInChat(access.graphics, image, x, y);
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
			@Local(argsOnly = true) GuiMessage.Line line
	) {
		var config = ConverseConfig.image();
		if (!config.enableImages)
			return original.call(access, y, alpha, content);

		URI uri = ((ImageAttributeHolder) (Object) line).converse$getImageUri();
		if (uri == null)
			return original.call(access, y, alpha, content);

		Optional<ImageHostingService> service = Converse.imageLoadingOrchestrator()
				.hostingRegistry()
				.findServiceFor(uri);

		if (service.isEmpty())
			return original.call(access, y, alpha, content);

		Optional<ActiveChatImage> maybeImage = Converse.imageLoadingOrchestrator()
				.requestImage(service.get(), uri);
		if (maybeImage.isEmpty()) {
			return original.call(access, y, alpha, content);
		}

		var image = maybeImage.get();
		if (config.replaceUrlWithImage && image.getState() == ChatImageRenderingState.LOADED) {
			int imageY = y + 9 + 2;
			converse$image$renderImage(access, image, 0, imageY);
		}

		return original.call(access, y, alpha, content);
	}
	//?} else {

	//?}
}
