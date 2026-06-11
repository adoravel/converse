package at.acpi.converse.config;

import at.acpi.converse.Converse;
import at.acpi.converse.ConversePlatform;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
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

	@SerialEntry
	public boolean smartWidth = false;

	@SerialEntry
	public boolean removeMessageIndicator = true;

	@SerialEntry
	public boolean enableMessageAnimation = true;

	@SerialEntry
	public int messageAnimationDuration = 500;

	@SerialEntry
	public boolean enableTextFieldAnimation = true;

	@SerialEntry
	public int textFieldAnimationDuration = 300;

	public static ConfigCategory category() {
		return ConfigCategory.createBuilder()
				.name(Component.translatable("text.converse.config.appearance.title"))
				.option(Option.<Boolean>createBuilder()
						.name(Component.translatable("text.converse.config.appearance.option.smartWidth"))
						.description(OptionDescription.createBuilder()
								.text(Component.translatable("text.converse.config.appearance.option.smartWidth.desc"))
								.build())
						.binding(false, () -> get().smartWidth, v -> get().smartWidth = v)
						.controller(BooleanControllerBuilder::create)
						.build())
				.option(Option.<Boolean>createBuilder()
						.name(Component.translatable("text.converse.config.appearance.option.removeMessageIndicator"))
						.description(OptionDescription.createBuilder()
								.text(Component.translatable("text.converse.config.appearance.option.removeMessageIndicator.desc"))
								.build())
						.binding(true, () -> get().removeMessageIndicator, v -> get().removeMessageIndicator = v)
						.controller(BooleanControllerBuilder::create)
						.build())
				.group(OptionGroup.createBuilder()
						.name(Component.translatable("text.converse.config.appearance.group.animations"))
						.option(Option.<Boolean>createBuilder()
								.name(Component.translatable("text.converse.config.appearance.option.enableMessageAnimation"))
								.description(OptionDescription.createBuilder()
										.text(Component.translatable("text.converse.config.appearance.option.enableMessageAnimation.desc"))
										.build())
								.binding(true, () -> get().enableMessageAnimation, v -> get().enableMessageAnimation = v)
								.controller(BooleanControllerBuilder::create)
								.build())
						.option(Option.<Integer>createBuilder()
								.name(Component.translatable("text.converse.config.appearance.option.messageDuration"))
								.description(OptionDescription.createBuilder()
										.text(Component.translatable("text.converse.config.appearance.option.messageDuration.desc"))
										.build())
								.binding(500, () -> get().messageAnimationDuration, v -> get().messageAnimationDuration = v)
								.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(100, 2000).step(50))
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Component.translatable("text.converse.config.appearance.option.enableTextFieldAnimation"))
								.description(OptionDescription.createBuilder()
										.text(Component.translatable("text.converse.config.appearance.option.enableTextFieldAnimation.desc"))
										.build())
								.binding(true, () -> get().enableTextFieldAnimation, v -> get().enableTextFieldAnimation = v)
								.controller(BooleanControllerBuilder::create)
								.build())
						.option(Option.<Integer>createBuilder()
								.name(Component.translatable("text.converse.config.appearance.option.textFieldDuration"))
								.description(OptionDescription.createBuilder()
										.text(Component.translatable("text.converse.config.appearance.option.textFieldDuration.desc"))
										.build())
								.binding(300, () -> get().textFieldAnimationDuration, v -> get().textFieldAnimationDuration = v)
								.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(100, 1000).step(50))
								.build())
						.build())
				.build();
	}
}
