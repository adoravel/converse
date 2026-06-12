package at.acpi.converse.mixin.imageRendering;

import at.acpi.converse.Converse;
import at.acpi.converse.config.ConverseConfig;
import at.acpi.converse.rendering.image.domain.ImageAttributeHolder;
import at.acpi.converse.rendering.image.hosting.ImageHostingRegistry;
import at.acpi.converse.rendering.image.hosting.ImageUrlDetector;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URI;
import java.util.List;

@Mixin(ChatComponent.class)
public abstract class ChatComponentScanMixin {
	@Final
	@Shadow
	private List<GuiMessage.Line> trimmedMessages;

	@Inject(method = "addMessageToDisplayQueue", at = @At("TAIL"))
	void converse$image$tagLastLine(GuiMessage message, CallbackInfo ci) {
		if (!ConverseConfig.image().enableImages) return;
		if (trimmedMessages.isEmpty()) return;

		String fullText = message.content().getString();
		List<URI> urls = ImageUrlDetector.findUrls(fullText);
		if (urls.isEmpty()) return;

		ImageHostingRegistry registry = Converse.imageLoadingOrchestrator().hostingRegistry();
		for (URI uri : urls) {
			if (registry.findServiceFor(uri).isPresent()) {
				GuiMessage.Line lastLine = trimmedMessages.getFirst();
				((ImageAttributeHolder) (Object) lastLine).converse$setImageUri(uri);
				break;
			}
		}
	}
}
