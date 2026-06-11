package at.acpi.converse.mixin.chatAnimation;

import at.acpi.converse.animation.AnimationClock;
import at.acpi.converse.animation.AnimationStateProvider;
import at.acpi.converse.animation.MessageSlideController;
import at.acpi.converse.config.ConverseAppearanceConfig;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChatComponent.class)
public abstract class ChatComponentAnimationStateProviderMixin implements AnimationStateProvider {
	@Shadow
	protected abstract int getLineHeight();

	@Unique
	private MessageSlideController converse$messageController;

	@Override
	public MessageSlideController converse$getMessageController() {
		if (converse$messageController == null) {
			return converse$resetMessageController();
		}
		return converse$messageController;
	}

	@Override
	public MessageSlideController converse$resetMessageController() {
		converse$messageController = new MessageSlideController(
				new AnimationClock(),
				(float) ConverseAppearanceConfig.get().messageAnimationDuration,
				this.getLineHeight() * 0.8f
		);
		return converse$messageController;
	}
}
