package at.acpi.converse.rendering.image.storage;

import at.acpi.converse.config.ConverseConfig;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static at.acpi.converse.rendering.image.ChatImageTextureManager.hash;

public class ImageFilesystemCache {
	private static final Logger LOGGER = LoggerFactory.getLogger("converse/images");
	private static final Executor IO_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

	private final Path cacheDir;

	public ImageFilesystemCache(Path cacheDir) {
		this.cacheDir = cacheDir;
		try {
			Files.createDirectories(this.cacheDir);
		} catch (IOException e) {
			throw new IllegalStateException("ummm failed to create image cache directory: {}" + e.getMessage());
		}
	}

	private record Entry(Path datFile, Path timeFile, long lastAccess) {
	}

	public byte @Nullable [] readFromCache(String url) throws IOException {
		Path file = getPathFor(url);
		try {
			byte[] bytes = Files.readAllBytes(file);
			touchCacheEntry(url);
			return bytes;
		} catch (NoSuchFileException e) {
			return null;
		}
	}

	public void writeToCache(String url, byte[] bytes) throws IOException {
		Path file = getPathFor(url);
		Files.createDirectories(file.getParent());
		Files.write(file, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		touchCacheEntry(url);
	}

	public void pruneFilesystemCacheAsync() {
		CompletableFuture.runAsync(this::pruneCacheSafely, IO_EXECUTOR)
				.exceptionally(e -> {
					LOGGER.warn("cache pruning failed: {}", e.getMessage());
					return null;
				});
	}

	private void pruneCacheSafely() {
		try {
			if (!Files.exists(cacheDir)) return;

			var config = ConverseConfig.image();
			List<Entry> entries = loadCacheEntries();

			long expiryMs = (long) config.cacheExpiryDays * 24L * 60 * 60 * 1000L;
			long cutoffTime = System.currentTimeMillis() - expiryMs;

			List<Entry> activeEntries = evictExpiredEntries(entries, cutoffTime);
			evictOverCapacityEntries(activeEntries, config.maxCacheEntries);
		} catch (IOException e) {
			LOGGER.warn("Image cache eviction failed: {}", e.getMessage());
		}
	}

	private List<Entry> loadCacheEntries() throws IOException {
		List<Entry> entries = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(cacheDir, "*.dat")) {
			for (Path dat : stream) {
				Path timePath = resolveCompanionPath(dat);
				long lastAccess = readLastAccessTime(timePath);
				entries.add(new Entry(dat, timePath, lastAccess));
			}
		}
		return entries;
	}

	private List<Entry> evictExpiredEntries(List<Entry> entries, long cutoffTime) {
		List<Entry> activeEntries = new ArrayList<>();
		for (Entry entry : entries) {
			if (entry.lastAccess() < cutoffTime) {
				deleteEntryFiles(entry);
			} else {
				activeEntries.add(entry);
			}
		}
		return activeEntries;
	}

	private void evictOverCapacityEntries(List<Entry> entries, int maxEntries) {
		if (entries.size() <= maxEntries) return;

		entries.sort(Comparator.comparingLong(Entry::lastAccess));
		int toRemove = entries.size() - maxEntries;

		for (int i = 0; i < toRemove; i++) {
			deleteEntryFiles(entries.get(i));
		}
	}

	private void touchCacheEntry(String url) {
		CompletableFuture.runAsync(() -> {
			try {
				Path datFile = getPathFor(url);
				Path timeFile = resolveCompanionPath(datFile);
				String timestamp = String.valueOf(System.currentTimeMillis());
				Files.writeString(timeFile, timestamp, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			} catch (IOException ignored) {
			}
		}, IO_EXECUTOR);
	}

	private long readLastAccessTime(Path timePath) {
		try {
			String content = Files.readString(timePath);
			return Long.parseLong(content.trim());
		} catch (IOException | NumberFormatException e) {
			return 0L;
		}
	}

	private Path getPathFor(String input) {
		return cacheDir.resolve(hash(input) + ".dat");
	}

	private Path resolveCompanionPath(Path datPath) {
		String filename = datPath.getFileName().toString();
		String time = filename.substring(0, filename.lastIndexOf('.')) + ".time";
		return datPath.getParent().resolve(time);
	}

	private void deleteEntryFiles(Entry entry) {
		purge(entry.datFile());
		purge(entry.timeFile());
	}

	private void purge(Path path) {
		try {
			Files.deleteIfExists(path);
		} catch (IOException ignored) {
		}
	}
}
