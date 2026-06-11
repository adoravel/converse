package at.acpi.converse.config;

import dev.isxander.yacl3.api.YetAnotherConfigLib;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConverseConfig {
	public static ConverseAppearanceConfig appearance() {
		return ConverseAppearanceConfig.HANDLER.instance();
	}

	public static ConverseDisplayConfig display() {
		return ConverseDisplayConfig.HANDLER.instance();
	}

	public static void loadConfig() {
		ConverseAppearanceConfig.HANDLER.load();
		ConverseDisplayConfig.HANDLER.load();
	}

	public static void save() {
		ConverseAppearanceConfig.HANDLER.save();
		ConverseDisplayConfig.HANDLER.save();
	}

	public static Screen createScreen(Screen parent) {
		return YetAnotherConfigLib.createBuilder()
				.title(Component.translatable("text.converse.config.title"))
				.category(ConverseDisplayConfig.category())
				.category(ConverseAppearanceConfig.category())
				.save(ConverseConfig::save)
				.build()
				.generateScreen(parent);
	}
}
