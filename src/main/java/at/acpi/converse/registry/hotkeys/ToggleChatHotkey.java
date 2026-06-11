package at.acpi.converse.registry.hotkeys;

import at.acpi.converse.config.ConverseDisplayConfig;
import at.acpi.converse.hotkey.Hotkey;
import at.acpi.converse.hotkey.KeybindData;
import at.acpi.converse.registry.ConverseHotkeys;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public final class ToggleChatHotkey {
	private ToggleChatHotkey() {
	}

	private static Hotkey STATE;

	public static void registerHotkey() {
		get().register();
	}

	private static void onToggle(Minecraft client, boolean disabled) {
		if (client.player == null || !ConverseDisplayConfig.get().notifyOnToggle) return;

		String message = disabled ? "text.converse.toggle.invisible" : "text.converse.toggle.visible";
		Component component = Component.translatable(message);

		//? <=1.21.11 {
		client.player.displayClientMessage(component, true);
		//?} else {
		/*client.player.sendOverlayMessage(component);
		*///?}
	}

	public static Hotkey get() {
		if (STATE == null) {
			var data = new KeybindData(
					"key.converse.toggle_chat",
					GLFW.GLFW_KEY_PERIOD,
					ConverseHotkeys.category(),
					ToggleChatHotkey::onToggle
			);
			STATE = Hotkey.create(data);
		}
		return STATE;
	}

	public static boolean isChatHidden() {
		return STATE != null && STATE.isActive();
	}
}
