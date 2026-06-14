package at.acpi.converse.rendering.image.component;

import at.acpi.converse.rendering.image.hosting.ImageHostingRegistry;
import at.acpi.converse.rendering.image.hosting.ImageUrlDetector;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.net.URI;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class ChatLinkRewriter {
	public static final Style TOOLTIP_LINK_STYLE = Style.EMPTY
			.withColor(TextColor.fromRgb(0x5c8ff2));

	/**
	 * An empty-string literal component is skipped entirely by {@link Component#visit}.
	 * I learned this in the dumbest way. </3
	 */
	private static final String ANCHOR_TEXT = " ";

	private ChatLinkRewriter() {
	}

	public static Component rewrite(Component message, ImageHostingRegistry registry) {
		String fullText = message.getString();

		Set<URI> eligible = ImageUrlDetector.findUrls(fullText).stream()
				.filter(uri -> registry.findServiceFor(uri).isPresent())
				.collect(Collectors.toSet());

		if (eligible.isEmpty()) return message;

		MutableComponent output = Component.empty();
		message.visit((style, segment) -> {
			processSegment(output, segment, style, eligible);
			return Optional.empty();
		}, Style.EMPTY);
		return output;
	}

	private static void processSegment(
			MutableComponent output, String segment, Style style,
			Set<URI> eligible
	) {
		Set<URI> segmentUrls = ImageUrlDetector.findUrls(segment);
		if (segmentUrls.isEmpty()) {
			if (!segment.isEmpty())
				output.append(Component.literal(segment).withStyle(style));
			return;
		}

		int last = 0;
		for (URI uri : segmentUrls) {
			if (!eligible.contains(uri)) continue;

			String raw = uri.toString();
			int idx = segment.indexOf(raw, last);
			if (idx < 0)
				continue;

			String before = segment.substring(last, idx);
			if (!before.isEmpty()) {
				output.append(Component.literal(before).withStyle(style));
			}

			output.append(Component.literal("[Image]")
					.withStyle(TOOLTIP_LINK_STYLE
							.withInsertion(raw)
							.withClickEvent(new ClickEvent.OpenUrl(uri))));

			last = idx + raw.length();
		}

		if (last < segment.length()) {
			String after = segment.substring(last);
			if (!after.isEmpty())
				output.append(Component.literal(after).withStyle(style));
		}
	}
}
