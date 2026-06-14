package at.acpi.converse.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionEventListener;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConverseImageConfig {
	static final ConfigClassHandler<ConverseImageConfig> HANDLER =
			ConverseConfig.create("image", ConverseImageConfig.class);

	public boolean enableImages = true;
	public boolean replaceUrlWithImage = true;

	public int maxWidth = 160;
	public int maxHeight = 160;

	public int maxCacheEntries = 32;
	public int cacheExpiryDays = 5;

	public Set<String> domainWhitelist = Set.of("i.imgur.com");
	public boolean requireImageExtension = true;

	public static ConverseImageConfig get() {
		return HANDLER.instance();
	}

	private static <T> void rebuild(Option<T> option, OptionEventListener.Event type) {
		if (type == OptionEventListener.Event.STATE_CHANGE)
			Minecraft.getInstance().gui.getChat().rescaleChat();
	}

	public static ConfigCategory category() {
		return ConfigCategory.createBuilder()
				.name(Component.translatable("text.converse.config.images.title"))
				.option(Option.<Boolean>createBuilder()
						.name(Component.translatable("text.converse.config.images.option.enable"))
						.description(OptionDescription.createBuilder()
								.text(Component.translatable("text.converse.config.images.option.enable.desc"))
								.build())
						.binding(true, () -> get().enableImages, v -> get().enableImages = v)
						.controller(BooleanControllerBuilder::create)
						.addListener(ConverseImageConfig::rebuild)
						.build())
				.option(Option.<Boolean>createBuilder()
						.name(Component.translatable("text.converse.config.images.option.replaceUrl"))
						.description(OptionDescription.createBuilder()
								.text(Component.translatable("text.converse.config.images.option.replaceUrl.desc"))
								.build())
						.binding(true, () -> get().replaceUrlWithImage, v -> get().replaceUrlWithImage = v)
						.controller(BooleanControllerBuilder::create)
						.addListener(ConverseImageConfig::rebuild)
						.build())
				.group(OptionGroup.createBuilder()
						.name(Component.translatable("text.converse.config.images.group.size"))
						.option(Option.<Integer>createBuilder()
								.name(Component.translatable("text.converse.config.images.option.maxWidth"))
								.description(OptionDescription.createBuilder()
										.text(Component.translatable("text.converse.config.images.option.maxWidth.desc"))
										.build())
								.binding(160, () -> get().maxWidth, v -> get().maxWidth = v)
								.controller(opt -> IntegerSliderControllerBuilder.create(opt)
										.range(32, 480).step(8))
								.addListener(ConverseImageConfig::rebuild)
								.build())
						.option(Option.<Integer>createBuilder()
								.name(Component.translatable("text.converse.config.images.option.maxHeight"))
								.description(OptionDescription.createBuilder()
										.text(Component.translatable("text.converse.config.images.option.maxHeight.desc"))
										.build())
								.binding(160, () -> get().maxHeight, v -> get().maxHeight = v)
								.controller(opt -> IntegerSliderControllerBuilder.create(opt)
										.range(32, 480).step(8))
								.addListener(ConverseImageConfig::rebuild)
								.build())
						.build())
				.group(OptionGroup.createBuilder()
						.name(Component.translatable("text.converse.config.images.group.cache"))
						.option(Option.<Integer>createBuilder()
								.name(Component.translatable("text.converse.config.images.option.maxCacheEntries"))
								.description(OptionDescription.createBuilder()
										.text(Component.translatable("text.converse.config.images.option.maxCacheEntries.desc"))
										.build())
								.binding(32, () -> get().maxCacheEntries, v -> get().maxCacheEntries = v)
								.controller(opt -> IntegerSliderControllerBuilder.create(opt)
										.range(16, 160).step(8))
								.build())
						.option(Option.<Integer>createBuilder()
								.name(Component.translatable("text.converse.config.images.option.cacheExpiryDays"))
								.description(OptionDescription.createBuilder()
										.text(Component.translatable("text.converse.config.images.option.cacheExpiryDays.desc"))
										.build())
								.binding(5, () -> get().cacheExpiryDays, v -> get().cacheExpiryDays = v)
								.controller(opt -> IntegerSliderControllerBuilder.create(opt)
										.range(1, 90).step(1))
								.build())
						.build())
				.group(OptionGroup.createBuilder()
						.name(Component.translatable("text.converse.config.images.group.filter"))
						.option(Option.<Boolean>createBuilder()
								.name(Component.translatable("text.converse.config.images.option.requireExtension"))
								.description(OptionDescription.createBuilder()
										.text(Component.translatable("text.converse.config.images.option.requireExtension.desc"))
										.build())
								.binding(true, () -> get().requireImageExtension, v -> get().requireImageExtension = v)
								.controller(BooleanControllerBuilder::create)
								.build())
						.build())
				.group(ListOption.<String>createBuilder()
						.name(Component.translatable("text.converse.config.images.option.domainWhitelist"))
						.description(OptionDescription.createBuilder()
								.text(Component.translatable("text.converse.config.images.option.domainWhitelist.desc"))
								.build())
						.binding(List.of("i.imgur.com"), () -> new ArrayList<>(get().domainWhitelist),
								v -> get().domainWhitelist = new HashSet<>(v))
						.controller(StringControllerBuilder::create)
						.initial("cdn.myverycoolsite.com")
						.build())
				.build();
	}
}
