package at.acpi.converse.mixin.imageRendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.function.Consumer;

@Mixin(OptionInstance.class)
public class LineScalingListenerMixin {
	@Shadow
	@Final
	public Component caption;

	@Inject(
			method = "set",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"
			)
	)
	private void converse$images$recomputeWhenLineHeightChanges(Object value, CallbackInfo ci) {
		if (!(this.caption.getContents() instanceof TranslatableContents contents)) {
			return;
		}
		if (!"options.chat.line_spacing".equals(contents.getKey()))
			return;

		Minecraft.getInstance().gui.getChat().rescaleChat();
	}
}
