package at.acpi.converse.mixin.imageRendering;

import at.acpi.converse.Converse;
import at.acpi.converse.config.ConverseConfig;
import at.acpi.converse.rendering.image.ActiveChatImageRenderer;
import at.acpi.converse.rendering.image.component.ChatLinkRewriter;
import at.acpi.converse.rendering.image.domain.ChatImageRenderingState;
import at.acpi.converse.rendering.image.domain.ImageAttributeHolder;
import at.acpi.converse.rendering.image.hosting.ImageUrlDetector;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static at.acpi.converse.rendering.image.ActiveChatImageRenderer.computeImageWidth;

//? >=26.1 {
/*import net.minecraft.client.GuiMessageSource;
*///?}

@SuppressWarnings({"NameDoesntMatchTargetClass", "RedundantSuppression"})
@Mixin(ChatComponent.class)
public abstract class ChatComponentLayoutMixin {
	@Unique
	private static Set<URI> converse$filterLinks(GuiMessage message) {
		var fromText = ImageUrlDetector.findUrls(message.content().getString());
		Set<URI> found = new HashSet<>(fromText);

		//noinspection unused
		message.content().visit((style, content) -> {
			String insertion = style.getInsertion();
			if (insertion != null) {
				try {
					found.add(new URI(insertion));
				} catch (URISyntaxException ignored) {
				}
			}
			return Optional.empty();
		}, Style.EMPTY);

		return found;
	}

	@Shadow
	public abstract double getScale();

	@Unique
	@SuppressWarnings("DataFlowIssue")
	private void converse$image$injectImageMetadata(URI uri, List<GuiMessage.Line> buffer, GuiMessage.Line line) {
		Converse.imageLoadingOrchestrator().requestImage(uri).ifPresent(image -> {
			if (image.getState() != ChatImageRenderingState.LOADED) return;

			if (!ConverseConfig.image().replaceUrlWithImage) {
				ImageAttributeHolder anchor = (ImageAttributeHolder) (Object) line;
				anchor.converse$setImageUri(uri);
				anchor.converse$setImagePlaceholderIndex(-1);
				return;
			}

			int imageHeight = ActiveChatImageRenderer.computeImageHeight(image.data());
			int lineCount = ActiveChatImageRenderer.computeImageLineCount(imageHeight);

			final int width = computeImageWidth(getScale(), image.data());
			int characterCount = ActiveChatImageRenderer.computeImageLineWidth(width, ' ');
			Style placeholderStyle = Style.EMPTY.withClickEvent(new ClickEvent.OpenUrl(uri));
			var stubContent = FormattedCharSequence.forward(" ".repeat(characterCount), placeholderStyle);

			for (int i = 0; i < lineCount; i++) {
				//? <= 1.21.11 {
				GuiMessage.Line stub = new GuiMessage.Line(line.addedTime(), stubContent, null, true);
				 //?} else {
				/*var message = new GuiMessage(line.addedTime(), Component.empty(), null, GuiMessageSource.SYSTEM_CLIENT, null);
				GuiMessage.Line stub = new GuiMessage.Line(message, stubContent, true);
				*///?}
				ImageAttributeHolder holder = (ImageAttributeHolder) (Object) stub;
				holder.converse$setImageUri(uri);
				holder.converse$setImagePlaceholderIndex(i);
				buffer.addFirst(stub);
			}
		});
	}

	@ModifyVariable(
			//? <=1.21.11 {
			method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V",
			//?} else {
			/*method = "addMessage",
			name = "contents",
			*///?}
			at = @At("HEAD"),
			argsOnly = true
	)
	private Component converse$rewriteLinks(Component message) {
		if (!ConverseConfig.image().enableImages) return message;
		return ChatLinkRewriter.rewrite(message, Converse.imageLoadingOrchestrator().hostingRegistry());
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
			//? <=1.21.11 {
			@Local(ordinal = 1) boolean endOfEntry,
			 //?} else {
			/*@Local(name = "endOfEntry") boolean endOfEntry,
			*///?}
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
