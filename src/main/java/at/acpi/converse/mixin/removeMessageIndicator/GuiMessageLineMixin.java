package at.acpi.converse.mixin.removeMessageIndicator;

import at.acpi.converse.config.ConverseConfig;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;

@Mixin(GuiMessage.Line.class)
public class GuiMessageLineMixin {
	@Inject(method = "tag", at = @At("HEAD"), cancellable = true)
	private void converse$appearance$removeMessageIndicator(CallbackInfoReturnable<GuiMessageTag> cir) {
		if (ConverseConfig.appearance().removeMessageIndicator) {
			cir.setReturnValue(null);
		}
	}
}
