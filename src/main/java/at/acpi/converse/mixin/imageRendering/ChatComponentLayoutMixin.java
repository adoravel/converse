package at.acpi.converse.mixin.imageRendering;

import at.acpi.converse.Converse;
import at.acpi.converse.config.ConverseConfig;
import at.acpi.converse.rendering.image.ActiveChatImageRenderer;
import at.acpi.converse.rendering.image.domain.ChatImageRenderingState;
import at.acpi.converse.rendering.image.domain.ImageAttributeHolder;
import at.acpi.converse.rendering.image.hosting.ImageUrlDetector;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.net.URI;
import java.util.List;

import static at.acpi.converse.rendering.image.ActiveChatImageRenderer.computeImageWidth;

@Mixin(ChatComponent.class)
public abstract class ChatComponentLayoutMixin {

	@Shadow
	public abstract double getScale();

	@Unique
	private static List<URI> converse$filterLinks(GuiMessage message) {
		final String content = message.content().getString();
		return ImageUrlDetector.findUrls(content);
	}

	@Unique
	@SuppressWarnings("DataFlowIssue")
	private void converse$image$injectImageMetadata(URI uri, List<GuiMessage.Line> buffer, GuiMessage.Line line) {
		Converse.imageLoadingOrchestrator().requestImage(uri).ifPresent(image -> {
			if (image.getState() != ChatImageRenderingState.LOADED) return;
			if (!ConverseConfig.image().replaceUrlWithImage) {
				ImageAttributeHolder anchor = (ImageAttributeHolder)(Object) line;
				anchor.converse$setImageUri(uri);
				anchor.converse$setImagePlaceholderIndex(-1);
				return;
			}

			int imageHeight = ActiveChatImageRenderer.computeImageHeight(image.getData());
			int lineCount = ActiveChatImageRenderer.computeImageLineCount(imageHeight);

			final int width = computeImageWidth(getScale(), image.getData());
			int characterCount = ActiveChatImageRenderer.computeImageLineWidth(width, ' ');
			FormattedCharSequence stubContent = FormattedCharSequence.forward(" ".repeat(characterCount), Style.EMPTY);

			for (int i = 0; i < lineCount; i++) {
				GuiMessage.Line stub = new GuiMessage.Line(line.addedTime(), stubContent, null, true);
				ImageAttributeHolder holder = (ImageAttributeHolder) (Object) stub;
				holder.converse$setImageUri(uri);
				holder.converse$setImagePlaceholderIndex(i);
				buffer.addFirst(stub);
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

		if (!endOfEntry || !ConverseConfig.image().enableImages)
			return;

		var uris = converse$filterLinks(message);
		if (uris.isEmpty()) return;

		var registry = Converse.imageLoadingOrchestrator().hostingRegistry();
		for (final URI uri : uris) {
			if (registry.findServiceFor(uri).isPresent()) {
				converse$image$injectImageMetadata(uri, trimmedMessages, (GuiMessage.Line) line);
				break;
			}
		}
	}
}
