package at.acpi.converse.mixin.smartChatBackground;

import at.acpi.converse.config.ConverseConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

//? <=1.21.11 {
import net.minecraft.client.GuiMessage;
//?} else {
/*import net.minecraft.client.multiplayer.chat.GuiMessage;
*///?}

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
	//? <=1.21.11 {
	@WrapOperation(
			method = "method_75802",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;fill(IIIII)V"
			)
	)
	//?} else {
	/*@WrapOperation(
			method = "lambda$extractRenderState$1",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;fill(IIIII)V"
			)
	)
			*///?}
	private static void converse$appearance$adjustBackgroundWidth(
			ChatComponent.ChatGraphicsAccess instance,
			int left, int top, int right, int bottom, int color, Operation<Void> original,
			@Local(argsOnly = true) GuiMessage.Line line
	) {
		if (!ConverseConfig.appearance().smartWidth) {
			original.call(instance, left, top, right, bottom, color);
			return;
		}

		var font = Minecraft.getInstance().font;

		int textWidth = font.width(line.content());
		int dynamicRight = textWidth + 4;

		right = Math.min(dynamicRight, right);
		original.call(instance, left, top, right, bottom, color);
	}
}
