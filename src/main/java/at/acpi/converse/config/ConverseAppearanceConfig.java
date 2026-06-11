package at.acpi.converse.config;

import at.acpi.converse.Converse;
import at.acpi.converse.ConversePlatform;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.minecraft.network.chat.Component;

public class ConverseAppearanceConfig {
	static final ConfigClassHandler<ConverseAppearanceConfig> HANDLER = ConfigClassHandler.createBuilder(ConverseAppearanceConfig.class)
			.id(Converse.of("appearance_config"))
			.serializer(config -> GsonConfigSerializerBuilder.create(config)
					.setPath(ConversePlatform.PLATFORM.getConfigFolder().resolve("converse").resolve("appearance.json"))
					.build())
			.build();

	public static ConverseAppearanceConfig get() {
		return HANDLER.instance();
	}

	@SerialEntry(comment = "Automatically tweak chat background width accordingly to each line's content")
	public boolean smartWidth = false;

	@SerialEntry(comment = "Removes the chat message indicator icon")
	public boolean removeMessageIndicator = false;

	public static ConfigCategory category() {
		return ConfigCategory.createBuilder()
				.name(Component.translatable("text.converse.config.appearance.title"))
				.option(Option.<Boolean>createBuilder()
						.name(Component.translatable("text.converse.config.appearance.option.smartWidth"))
						.binding(false, () -> get().smartWidth, v -> get().smartWidth = v)
						.controller(BooleanControllerBuilder::create)
						.build())
				.option(Option.<Boolean>createBuilder()
						.name(Component.translatable("text.converse.config.appearance.option.removeMessageIndicator"))
						.binding(false, () -> get().removeMessageIndicator, v -> get().removeMessageIndicator = v)
						.controller(BooleanControllerBuilder::create)
						.build())
				.build();
	}
}
