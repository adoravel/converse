package at.acpi.converse.rendering.image.hosting;

import com.google.common.collect.ImmutableList;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ImageHostingRegistry {
	private final List<ImageHostingService> services;

	private ImageHostingRegistry(List<ImageHostingService> services) {
		this.services = List.copyOf(services);
	}

	public Optional<ImageHostingService> findServiceFor(@Nullable URI uri) {
		if (uri == null) {
			return Optional.empty();
		}

		for (ImageHostingService service : this.services) {
			if (service.isEligible(uri)) return Optional.of(service);
		}
		return Optional.empty();
	}

	public ImmutableList<ImageHostingService> services() {
		return ImmutableList.copyOf(this.services);
	}

	public static class Builder {
		private final List<ImageHostingService> buildingServices = new ArrayList<>();

		public Builder register(ImageHostingService service) {
			if (service != null) {
				this.buildingServices.add(service);
			}
			return this;
		}

		public ImageHostingRegistry build() {
			return new ImageHostingRegistry(this.buildingServices);
		}
	}
}
