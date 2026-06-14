package at.acpi.converse.config;

import at.acpi.converse.Converse;
import at.acpi.converse.ConversePlatform;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConverseConfig {
	public static ConverseAppearanceConfig appearance() {
		return ConverseAppearanceConfig.get();
	}

	public static ConverseDisplayConfig display() {
		return ConverseDisplayConfig.get();
	}

	public static ConverseImageConfig image() {
		return ConverseImageConfig.get();
	}

	public static <T> ConfigClassHandler<T> create(String name, Class<T> clazz) {
		return ConfigClassHandler.createBuilder(clazz)
				.id(Converse.of(name + "_config"))
				.serializer(config -> GsonConfigSerializerBuilder.create(config)
						.setPath(ConversePlatform.PLATFORM.getConfigFolder().resolve("converse")
								.resolve(name + ".json"))
						.build())
				.build();
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
				.category(ConverseImageConfig.category())
				.save(ConverseConfig::save)
				.build()
				.generateScreen(parent);
	}
}
