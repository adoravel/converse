package at.acpi.converse.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.network.chat.Component;

public class ConverseDisplayConfig {
	static final ConfigClassHandler<ConverseDisplayConfig> HANDLER =
			ConverseConfig.create("display", ConverseDisplayConfig.class);

	public static ConverseDisplayConfig get() {
		return HANDLER.instance();
	}

	@SerialEntry
	public boolean allowToggleHotkey = true;

	@SerialEntry
	public boolean notifyOnToggle = true;

	@SerialEntry
	public boolean showChatWhileTyping = true;

	public static ConfigCategory category() {
		return ConfigCategory.createBuilder()
				.name(Component.translatable("text.converse.config.display.title"))
				.option(Option.<Boolean>createBuilder()
						.name(Component.translatable("text.converse.config.display.option.toggeable"))
						.description(OptionDescription.createBuilder()
								.text(Component.translatable("text.converse.config.display.option.toggeable.desc"))
								.build())
						.binding(true, () -> get().allowToggleHotkey, v -> get().allowToggleHotkey = v)
						.controller(BooleanControllerBuilder::create)
						.build())
				.option(Option.<Boolean>createBuilder()
						.name(Component.translatable("text.converse.config.display.option.notify"))
						.description(OptionDescription.createBuilder()
								.text(Component.translatable("text.converse.config.display.option.notify.desc"))
								.build())
						.binding(true, () -> get().notifyOnToggle, v -> get().notifyOnToggle = v)
						.controller(BooleanControllerBuilder::create)
						.build())
				.option(Option.<Boolean>createBuilder()
						.name(Component.translatable("text.converse.config.display.option.typing"))
						.description(OptionDescription.createBuilder()
								.text(Component.translatable("text.converse.config.display.option.typing.desc"))
								.build())
						.binding(true, () -> get().showChatWhileTyping, v -> get().showChatWhileTyping = v)
						.controller(BooleanControllerBuilder::create)
						.build())
				.build();
	}
}
