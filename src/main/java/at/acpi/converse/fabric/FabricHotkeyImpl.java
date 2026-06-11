package at.acpi.converse.fabric;

//? fabric {
import at.acpi.converse.hotkey.Hotkey;
import at.acpi.converse.hotkey.KeybindData;
import at.acpi.converse.registry.ConverseHotkeys;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

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
	}

	@Override
	public void register() {
		this.keyMapping = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				data.description(),
				InputConstants.Type.KEYSYM,
				data.defaultKey(),
				data.category()
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (this.keyMapping.consumeClick()) {
				this.toggle();
			}
		});
	}
}
//?}
