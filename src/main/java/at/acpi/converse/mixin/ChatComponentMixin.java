package at.acpi.converse.mixin;

import at.acpi.converse.config.ConverseConfig;
import at.acpi.converse.registry.hotkeys.ToggleChatHotkey;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

//? >=26.1 {
/*import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
 *///?} else {
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
//?}

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {
	@Shadow
	public abstract boolean isChatFocused();

	//? <=1.21.11 {
	@ModifyReturnValue(method = "isChatHidden", at = @At("RETURN"))
	private boolean modifyChatVisibilityEnum(boolean original) {
		if (!ConverseConfig.display().allowToggleHotkey || !ToggleChatHotkey.isChatHidden())
			return original;

		return !(ConverseConfig.display().showChatWhileTyping && isChatFocused());
	}

	//?} else {
	/*@ModifyExpressionValue(
			method = "extractRenderState(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/List;size()I"
			)
	)
	int converse$display$guardRendering(int original) {
		boolean shouldHide = ConverseConfig.display().allowToggleHotkey && ToggleChatHotkey.isChatHidden()
				&& !(ConverseConfig.display().showChatWhileTyping && isChatFocused());

		return shouldHide ? 0 : original;
	}
	*///?}
}
