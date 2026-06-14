package at.acpi.converse.mixin.chatAnimation;

import at.acpi.converse.rendering.animation.AnimationStateProvider;
import at.acpi.converse.config.ConverseAppearanceConfig;
import at.acpi.converse.config.ConverseConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class ChatComponentRenderMixin {
	@Shadow
	private int chatScrollbarPos;

	@Inject(
			method = "addMessageToDisplayQueue",
			at = @At("TAIL")
	)
	private void onAddMessage(CallbackInfo ci) {
		if (ConverseConfig.appearance().enableMessageAnimation)
			((AnimationStateProvider) this).converse$resetMessageController();
	}

	//? <=1.21.11 {
	@WrapOperation(
			method = "render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;IIIZZ)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IIZ)V"
			)
	)
	private void converse$appearance$displaceNewMessage(
			ChatComponent instance, ChatComponent.ChatGraphicsAccess graphics,
			int screenHeight, int ticks, boolean displayMode, Operation<Void> original,
			@Local(argsOnly = true) GuiGraphics context
	)
	//?} else {
	/*@WrapOperation(
		method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/ChatComponent;extractRenderState(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V"
		)
	)
	private void converse$appearance$displaceNewMessage(
			ChatComponent instance, ChatComponent.ChatGraphicsAccess graphics, int screenHeight, int ticks,
			ChatComponent.DisplayMode displayMode, Operation<Void> original,
			@Local(argsOnly = true) GuiGraphics context
	) *//*?}*/ {
		if (!ConverseAppearanceConfig.get().enableMessageAnimation || this.chatScrollbarPos != 0) {
			original.call(instance, graphics, screenHeight, ticks, displayMode);
			return;
		}

		float displacement = ((AnimationStateProvider) this).converse$getMessageController().getDisplacement();

		if (displacement > 0.01f) {
			context.pose().pushMatrix();
			context.pose().translate(0, displacement);
			original.call(instance, graphics, screenHeight, ticks, displayMode);
			context.pose().popMatrix();
		} else {
			original.call(instance, graphics, screenHeight, ticks, displayMode);
		}
	}
}
