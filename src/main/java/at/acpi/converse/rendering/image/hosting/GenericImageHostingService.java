package at.acpi.converse.rendering.image.hosting;

import at.acpi.converse.config.ConverseConfig;
import at.acpi.converse.rendering.image.domain.ChatImageData;
import at.acpi.converse.rendering.image.format.StaticFormat;
import at.acpi.converse.rendering.image.pipeline.ImageProcessingError;
import at.acpi.converse.rendering.image.pipeline.ImageProcessingResult;
import org.apache.commons.io.FilenameUtils;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;

public class GenericImageHostingService implements ImageHostingService {
	protected static final Set<String> ACCEPTED_IMAGE_EXTENSIONS = StaticFormat.EXTENSIONS;

	private static final Set<String> IMMUTABLE_CORE_HOSTS = Set.of(
			"x.kyu.re",
			"kyu.re",
			"github.com",
			"codeberg.org",
			"raw.githubusercontent.com",
			"i.imgur.com",
			"discord.com",
			"ooye.stay.rip",
			"media.tenor.com",
			"media1.tenor.com",
			"c.tenor.com",
			"media.giphy.com",
			"media0.giphy.com",
			"media1.giphy.com",
			"media2.giphy.com",
			"media3.giphy.com",
			"media4.giphy.com"
	);

	public static @Nullable String extractExtension(URI uri) {
		if (uri == null || uri.getPath() == null) {
			return null;
		}
		return FilenameUtils.getExtension(uri.getPath()).toLowerCase(Locale.getDefault());
	}

	/**
	 * Inspects structural route variables or path elements for safety filters 🤓☝️
	 */
	public boolean isSupportedHost(@Nullable URI uri) {
		if (uri == null || uri.getHost() == null) {
			return false;
		}

		Collection<String> whitelist = ConverseConfig.image().domainWhitelist;
		if (whitelist == null || whitelist.isEmpty())
			return true;

		String host = uri.getHost().toLowerCase(Locale.getDefault());
		return IMMUTABLE_CORE_HOSTS.contains(host) || whitelist.contains(host);
	}

	@Override
	public boolean isEligible(URI uri) {
		if (!isSupportedHost(uri)) return false;

		if (!ConverseConfig.image().requireImageExtension)
			return true;

		String extension = extractExtension(uri);
		return extension != null && ACCEPTED_IMAGE_EXTENSIONS.contains(extension);
	}

	@Override
	public ImageProcessingResult compile(URI uri) {
		if (!isEligible(uri))
			return new ImageProcessingResult.Failure(ImageProcessingError.UNSUPPORTED_PROTOCOL);

		if (uri == null) {
			return new ImageProcessingResult.Failure(ImageProcessingError.INVALID_URI);
		}

		String scheme = uri.getScheme();
		if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
			return new ImageProcessingResult.Failure(ImageProcessingError.UNSUPPORTED_PROTOCOL);
		}

		ChatImageData data = new ChatImageData(uri, 0, 0);
		return new ImageProcessingResult.Success(data);
	}
}
