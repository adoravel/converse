package at.acpi.converse.config;

import at.acpi.converse.Converse;
import at.acpi.converse.ConversePlatform;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;

public class ConverseAppearanceConfig {
	public static final ConfigClassHandler<ConverseAppearanceConfig> HANDLER = ConfigClassHandler.createBuilder(ConverseAppearanceConfig.class)
			.id(Converse.of("appearance_config"))
			.serializer(config -> GsonConfigSerializerBuilder.create(config)
					.setPath(ConversePlatform.PLATFORM.getConfigFolder().resolve("converse").resolve("appearance.json"))
					.build())
			.build();

	public static ConfigCategory category() {
		return ConfigCategory.createBuilder().build();
	}
}
