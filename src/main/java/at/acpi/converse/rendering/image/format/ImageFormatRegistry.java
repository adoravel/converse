package at.acpi.converse.rendering.image.format;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Ordered registry of {@link ImageFormat} implementations.
 * The first format whose {@link ImageFormat#isEligible} returns true is used.
 */
public final class ImageFormatRegistry {
	private final List<ImageFormat> formats;

	private ImageFormatRegistry(List<ImageFormat> formats) {
		this.formats = List.copyOf(formats);
	}

	/**
	 * Returns the first format eligible for this URI, or empty if none matches.
	 */
	public Optional<ImageFormat> findFor(URI uri) {
		for (ImageFormat format : formats) {
			if (format.isEligible(uri)) return Optional.of(format);
		}
		return Optional.empty();
	}

	public List<ImageFormat> formats() {
		return List.copyOf(this.formats);
	}

	public static class Builder {
		private final List<ImageFormat> buildingFormats = new ArrayList<>();

		public ImageFormatRegistry.Builder register(ImageFormat service) {
			if (service != null) {
				this.buildingFormats.add(service);
			}
			return this;
		}

		public ImageFormatRegistry build() {
			return new ImageFormatRegistry(this.buildingFormats);
		}
	}
}
