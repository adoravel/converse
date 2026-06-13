package at.acpi.converse.mixin.imageRendering;

import at.acpi.converse.Converse;
import at.acpi.converse.config.ConverseConfig;
import at.acpi.converse.rendering.image.ActiveChatImageRenderer;
import at.acpi.converse.rendering.image.domain.ImageAttributeHolder;
import at.acpi.converse.rendering.image.hosting.ImageHostingRegistry;
import at.acpi.converse.rendering.image.hosting.ImageUrlDetector;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.net.URI;
import java.util.List;

@Mixin(ChatComponent.class)
public abstract class ChatComponentLayoutMixin {

	@Unique
	private static boolean converse$image$tagLastLine(GuiMessage.Line line, GuiMessage message) {
		final String content = message.content().getString();
		List<URI> urls = ImageUrlDetector.findUrls(content);
		if (urls.isEmpty()) return false;

		final ImageHostingRegistry registry = Converse.imageLoadingOrchestrator().hostingRegistry();
		for (final URI uri : urls) {
			if (registry.findServiceFor(uri).isPresent()) {
				((ImageAttributeHolder) (Object) line).converse$setImageUri(uri);
				return true;
			}
		}
		return false;
	}

	@Unique
	private static void converse$image$injectImageDimensions(List<GuiMessage.Line> buffer, GuiMessage.Line line) {
		final URI uri = ((ImageAttributeHolder) (Object) line).converse$getImageUri();
		if (uri == null) return;

		Converse.imageLoadingOrchestrator().requestImage(uri).ifPresent(image -> {
			int imageHeight = ActiveChatImageRenderer.computeImageHeight(image.getData());
			int lineCount = ActiveChatImageRenderer.computeImageLineCount(imageHeight);

			int characterCount = ActiveChatImageRenderer.computeImageLineWidth(image.getData().width(), ' ');
			Component component = Component.literal(" ".repeat(characterCount));

			FormattedCharSequence visualOrderText = component.getVisualOrderText();
			for (int i = 0; i < lineCount; i++) {
				buffer.addFirst(new GuiMessage.Line(line.addedTime(), visualOrderText, null, true));
			}
		});
	}

	@WrapOperation(
			method = "addMessageToDisplayQueue",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/List;addFirst(Ljava/lang/Object;)V"
			)
	)
	private void converse$image$scan(
			List<GuiMessage.Line> trimmedMessages, Object line,
			Operation<Void> original,
			@Local(ordinal = 1) boolean endOfEntry,
			@Local(argsOnly = true) GuiMessage message
	) {
		original.call(trimmedMessages, line);
		if (endOfEntry && ConverseConfig.image().enableImages) {
			final var l = (GuiMessage.Line) line;
			if (converse$image$tagLastLine(l, message))
				converse$image$injectImageDimensions(trimmedMessages, l);
		}
	}
}
