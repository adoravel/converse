package at.acpi.converse.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ConverseImageConfig {
	static final ConfigClassHandler<ConverseImageConfig> HANDLER =
			ConverseConfig.create("image", ConverseImageConfig.class);

	public static ConverseImageConfig get() {
		return HANDLER.instance();
	}

	@SerialEntry(comment = "Render images from URLs found in chat messages")
	public boolean enableImages = true;

	@SerialEntry(comment = "Replace the URL text with the image inline. If false, the URL remains and image shows only on hover")
	public boolean replaceUrlWithImage = true;

	@SerialEntry(comment = "Maximum rendered image width in pixels (before GUI scaling)")
	public int maxWidth = 256;

	@SerialEntry(comment = "Maximum rendered image height in pixels (before GUI scaling)")
	public int maxHeight = 256;

	@SerialEntry(comment = "Maximum number of images to keep in the filesystem cache before evicting the oldest")
	public int maxCacheEntries = 128;

	@SerialEntry(comment = "Number of days an unused cached image is kept before being deleted")
	public int cacheExpiryDays = 5;

	@SerialEntry(comment = "Only load images from these domains (one per entry). Empty = allow all domains that pass the extension check")
	public HashSet<String> domainWhitelist = new HashSet<>();

	@SerialEntry(comment = "Also check URL path ends with a known image extension (.png .jpg .jpeg .gif .webp)")
	public boolean requireImageExtension = true;

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
						.build())
				.option(Option.<Boolean>createBuilder()
						.name(Component.translatable("text.converse.config.images.option.replaceUrl"))
						.description(OptionDescription.createBuilder()
								.text(Component.translatable("text.converse.config.images.option.replaceUrl.desc"))
								.build())
						.binding(true, () -> get().replaceUrlWithImage, v -> get().replaceUrlWithImage = v)
						.controller(BooleanControllerBuilder::create)
						.build())
				.group(OptionGroup.createBuilder()
						.name(Component.translatable("text.converse.config.images.group.size"))
						.option(Option.<Integer>createBuilder()
								.name(Component.translatable("text.converse.config.images.option.maxWidth"))
								.binding(200, () -> get().maxWidth, v -> get().maxWidth = v)
								.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(32, 400).step(8))
								.build())
						.option(Option.<Integer>createBuilder()
								.name(Component.translatable("text.converse.config.images.option.maxHeight"))
								.binding(100, () -> get().maxHeight, v -> get().maxHeight = v)
								.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(16, 300).step(8))
								.build())
						.build())
				.group(OptionGroup.createBuilder()
						.name(Component.translatable("text.converse.config.images.group.cache"))
						.option(Option.<Integer>createBuilder()
								.name(Component.translatable("text.converse.config.images.option.maxCacheEntries"))
								.binding(200, () -> get().maxCacheEntries, v -> get().maxCacheEntries = v)
								.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(10, 1000).step(10))
								.build())
						.option(Option.<Integer>createBuilder()
								.name(Component.translatable("text.converse.config.images.option.cacheExpiryDays"))
								.binding(7, () -> get().cacheExpiryDays, v -> get().cacheExpiryDays = v)
								.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 90).step(1))
								.build())
						.build())
				.group(OptionGroup.createBuilder()
						.name(Component.translatable("text.converse.config.images.group.filter"))
						.option(Option.<Boolean>createBuilder()
								.name(Component.translatable("text.converse.config.images.option.requireExtension"))
								.binding(true, () -> get().requireImageExtension, v -> get().requireImageExtension = v)
								.controller(BooleanControllerBuilder::create)
								.build())
						.build())
				.build();
	}
}
