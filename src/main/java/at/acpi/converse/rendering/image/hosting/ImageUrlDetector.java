package at.acpi.converse.rendering.image.hosting;

import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ImageUrlDetector {
	private ImageUrlDetector() {
	}

	public static List<URI> findUrls(final String text) {
		if (StringUtils.isBlank(text))
			return Collections.emptyList();

		final List<URI> foundUris = new ArrayList<>();
		final Matcher matcher = WEB_URL.matcher(text);

		while (matcher.find())
			processCandidate(matcher.group(), foundUris);

		return foundUris.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(foundUris);
	}

	private static void processCandidate(final String candidate, final List<URI> destinationList) {
		try {
			final URI uri = new URI(candidate);
			destinationList.add(uri);
		} catch (URISyntaxException ignored) {
		}
	}


	// thank you android slop
	// https://github.com/aosp-mirror/platform_frameworks_base/blob/master/core/java/android/util/Patterns.java
	private static final String PROTOCOL = "(?i:https?)://";

	private static final String WORD_BOUNDARY = "\\b|$|^";

	private static final String USER_INFO = "(?:[a-zA-Z0-9$\\-_.+!*'()"
			+ ",;?&=]|%[a-fA-F0-9]{2}){1,64}(?::(?:[a-zA-Z0-9$\\-"
			+ ".+!*'(),;?&=]|%[a-fA-F0-9]{2}){1,25})?";

	private static final String UCS_CHAR = "[" +
			"\u00A0-\uD7FF" +
			"豈-﷏" +
			"ﷰ-\uFFEF" +
			"\uD800\uDC00-\uD83F\uDFFD" +
			"\uD840\uDC00-\uD87F\uDFFD" +
			"\uD880\uDC00-\uD8BF\uDFFD" +
			"\uD8C0\uDC00-\uD8FF\uDFFD" +
			"\uD900\uDC00-\uD93F\uDFFD" +
			"\uD940\uDC00-\uD97F\uDFFD" +
			"\uD980\uDC00-\uD9BF\uDFFD" +
			"\uD9C0\uDC00-\uD9FF\uDFFD" +
			"\uDA00\uDC00-\uDA3F\uDFFD" +
			"\uDA40\uDC00-\uDA7F\uDFFD" +
			"\uDA80\uDC00-\uDABF\uDFFD" +
			"\uDAC0\uDC00-\uDAFF\uDFFD" +
			"\uDB00\uDC00-\uDB3F\uDFFD" +
			"\uDB44\uDC00-\uDB7F\uDFFD" +
			"&&[^\u00A0[\u2000-\u200A]\u2028\u2029\u202F\u3000]]";

	private static final String PORT_NUMBER = ":\\d{1,5}";

	private static final String LABEL_CHAR = "a-zA-Z0-9" + UCS_CHAR;

	private static final String TLD_CHAR = "a-zA-Z" + UCS_CHAR;

	@SuppressWarnings("RegExpUnnecessaryNonCapturingGroup")
	private static final String PATH_AND_QUERY = "[/?](?:(?:[" + LABEL_CHAR
			+ ";/?:@&=#~"  // plus optional query params
			+ "\\-.+!*'(),_$])|%[a-fA-F0-9]{2})*";

	private static final String IRI_LABEL =
			"[" + LABEL_CHAR + "](?:[" + LABEL_CHAR + "_\\-]{0,61}[" + LABEL_CHAR + "])?";

	private static final String PUNYCODE_TLD = "xn--[\\w\\-]{0,58}\\w";
	private static final String TLD = "(" + PUNYCODE_TLD + "|" + "[" + TLD_CHAR + "]{2,63}" + ")";
	private static final String HOST_NAME = "(" + IRI_LABEL + "\\.)+" + TLD;
	private static final String IP_ADDRESS_STRING =
			"((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
					+ "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
					+ "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
					+ "|[1-9][0-9]|[0-9]))";

	private static final String DOMAIN_NAME_STR = "(" + HOST_NAME + "|" + IP_ADDRESS_STRING + ")";

	@SuppressWarnings("RegExpUnnecessaryNonCapturingGroup")
	private static final Pattern WEB_URL = Pattern.compile("("
			+ "("
			+ "(?:" + PROTOCOL + "(?:" + USER_INFO + ")?" + ")?"
			+ "(?:" + DOMAIN_NAME_STR + ")"
			+ "(?:" + PORT_NUMBER + ")?"
			+ ")"
			+ "(" + PATH_AND_QUERY + ")?"
			+ WORD_BOUNDARY
			+ ")");
}
