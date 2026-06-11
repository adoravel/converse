package at.acpi.converse.hotkey;

import net.minecraft.client.KeyMapping;

public record KeybindData(String description, int defaultKey, KeyMapping.Category category) {
}
