package at.acpi.converse.hotkey;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public record KeybindData(String description, int defaultKey, KeyMapping.Category category, Callback callback) {
	public interface Callback {
		void onUpdate(Minecraft client, boolean current);
	}
}
