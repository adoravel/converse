package at.acpi.converse.registry;

import at.acpi.converse.Converse;
import at.acpi.converse.config.ConverseConfig;
import at.acpi.converse.hotkey.Hotkey;
import at.acpi.converse.registry.hotkeys.ToggleChatHotkey;
import net.minecraft.client.KeyMapping;

public class ConverseHotkeys {
	private ConverseHotkeys() {}

	private static KeyMapping.Category category;

	public static KeyMapping.Category category() {
		if (category == null) {
			category = new KeyMapping.Category(Converse.of("keymap"));
		}
		return category;
	}

	public static void registerHotkeys() {
		if (ConverseConfig.display().allowToggleHotkey) {
			ToggleChatHotkey.registerHotkey();
		}
	}
}
