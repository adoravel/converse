package at.acpi.converse.mixin.chatAnimation;

import at.acpi.converse.animation.AnimationStateProvider;
import at.acpi.converse.config.ConverseAppearanceConfig;
import at.acpi.converse.config.ConverseConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenRenderMixin {
	@Inject(method = "init", at = @At("HEAD"))
	private void converse$appearance$init(CallbackInfo ci) {
		if (ConverseConfig.appearance().enableTextFieldAnimation)
			((AnimationStateProvider) this).converse$resetMessageController();
	}

	//? <=1.21.11 {
	@WrapOperation(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"
			)
	) //?} else {
	/*@WrapOperation(
            method = "extractRenderState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V")
    )*///?}
	private void converse$appearance$displaceChatScreen(
			GuiGraphics graphics, int minX, int minY, int maxX, int maxY, int color, Operation<Void> original
	) {
		if (!ConverseAppearanceConfig.get().enableTextFieldAnimation) {
			original.call(graphics, minX, minY, maxX, maxY, color);
			return;
		}

		float displacement = ((AnimationStateProvider) this).converse$getMessageController().getDisplacement();
		graphics.pose().pushMatrix();
		graphics.pose().translate(0, displacement);
		original.call(graphics, minX, minY, maxX, maxY, color);
		graphics.pose().popMatrix();
	}
}

