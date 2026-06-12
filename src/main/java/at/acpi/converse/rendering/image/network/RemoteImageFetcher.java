package at.acpi.converse.rendering.image.network;

import at.acpi.converse.rendering.image.storage.ImageFilesystemCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class RemoteImageFetcher {
	private static final Logger LOGGER = LoggerFactory.getLogger("converse/images");

	private final HttpClient http;
	private final ImageFilesystemCache filesystem;

	public RemoteImageFetcher(HttpClient http, ImageFilesystemCache filesystem) {
		this.http = http;
		this.filesystem = filesystem;
	}

	public RemoteImageFetcher(ImageFilesystemCache filesystem) {
		this.http = HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(30))
				.followRedirects(HttpClient.Redirect.ALWAYS)
				.build();
		this.filesystem = filesystem;
	}

	/**
	 * @param uri The URI to download from.
	 */
	public CompletableFuture<ImageFetchResult> fetchAsync(URI uri) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				byte[] cached = filesystem.readFromCache(uri.toString());
				if (cached != null) {
					return new ImageFetchResult.Success(cached);
				}
				return new ImageFetchResult.Success(fetchRemoteResource(uri));
			} catch (Exception e) {
				return new ImageFetchResult.Failure(e);
			}
		});
	}

	private byte[] fetchRemoteResource(URI uri) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(uri)
				.header("User-Agent", "converse/1.0")
				.header("Accept-Encoding", "gzip")
				.timeout(Duration.ofSeconds(10))
				.GET()
				.build();

		HttpResponse<byte[]> response = http.send(request, HttpResponse.BodyHandlers.ofByteArray());

		int status = response.statusCode();
		if (status < 200 || status >= 300) {
			throw new IOException("HTTP " + response.statusCode() + " for " + uri);
		}

		byte[] bytes = response.body();
		try {
			filesystem.writeToCache(uri.toString(), bytes);
		} catch (IOException e) {
			LOGGER.warn("failed to write image to disk cache: {}", e.getMessage());
		}
		return bytes;
	}

}
