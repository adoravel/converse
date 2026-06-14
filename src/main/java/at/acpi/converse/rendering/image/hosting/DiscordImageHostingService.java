package at.acpi.converse.rendering.image.hosting;

import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.util.Locale;

public class DiscordImageHostingService extends GenericImageHostingService {
	@Override
	public boolean isSupportedHost(@Nullable URI uri) {
		if (uri == null || uri.getHost() == null) {
			return false;
		}
		String host = uri.getHost().toLowerCase(Locale.getDefault());
		return "cdn.discordapp.com".equals(host) || "media.discordapp.net".equals(host);
	}

	@Override
	public boolean isEligible(URI uri) {
		if (!super.isEligible(uri)) return false;

		String path = uri.getPath();
		if (path == null) {
			return false;
		}

		boolean isAttachment = path.regionMatches(true, 0, "/attachments/", 0, 13)
				|| path.regionMatches(true, 0, "/ephemeral-attachments/", 0, 23);

		if (isAttachment) {
			String query = uri.getQuery();
			return query != null && query.contains("ex=") && query.contains("is=") && query.contains("hm=");
		}
		return true;
	}
}
