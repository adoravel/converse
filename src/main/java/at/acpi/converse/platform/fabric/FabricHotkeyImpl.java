package at.acpi.converse.platform.fabric;

//? fabric {

import at.acpi.converse.hotkey.Hotkey;
import at.acpi.converse.hotkey.KeybindData;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public final class FabricHotkeyImpl implements Hotkey {
	private final KeybindData data;
	private KeyMapping keyMapping;
	private boolean active;

	public FabricHotkeyImpl(KeybindData data) {
		this.data = data;
	}

	@Override
	public boolean isActive() {
		return this.active;
	}

	@Override
	public void toggle() {
		this.active = !this.active;
		this.data.callback().onUpdate(Minecraft.getInstance(), this.active);
	}

	@Override
	public void register() {
		this.keyMapping = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				data.description(),
				InputConstants.Type.KEYSYM,
				data.defaultKey(),
				data.category()
		));

		ClientTickEvents.END_CLIENT_TICK.register(_ -> {
			while (this.keyMapping.consumeClick()) {
				this.toggle();
			}
		});
	}
}
//?}
