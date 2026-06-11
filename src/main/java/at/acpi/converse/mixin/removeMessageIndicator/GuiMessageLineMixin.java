package at.acpi.converse.mixin.removeMessageIndicator;

import at.acpi.converse.config.ConverseConfig;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? <=1.21.11 {
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
//?} else {
/*import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
 *///?}

@Mixin(GuiMessage.Line.class)
public class GuiMessageLineMixin {
	@Inject(method = "tag", at = @At("HEAD"), cancellable = true)
	private void converse$appearance$removeMessageIndicator(CallbackInfoReturnable<GuiMessageTag> cir) {
		if (ConverseConfig.appearance().removeMessageIndicator) {
			cir.setReturnValue(null);
		}
	}
}
