package at.acpi.converse.registry;

import at.acpi.converse.Converse;
import at.acpi.converse.config.ConverseConfig;
import at.acpi.converse.hotkey.Hotkey;
import at.acpi.converse.hotkey.KeybindData;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class ConverseHotkeys {
	private ConverseHotkeys() {}

	private static KeyMapping.Category category;
	private static Hotkey TOGGLE_CHAT;

	private static void initialiseCategory() {
		category = new KeyMapping.Category(Converse.of("keymap"));
	}

	public static void registerHotkeys() {
		initialiseCategory();

		if (ConverseConfig.display().allowToggleHotkey) {
			var data = new KeybindData("key.converse.toggle_chat", GLFW.GLFW_KEY_PERIOD, category);
			TOGGLE_CHAT = Hotkey.create(data);
			TOGGLE_CHAT.register();
		}
	}

	public static Hotkey getToggleChatHotkey() {
		return TOGGLE_CHAT;
	}
}
