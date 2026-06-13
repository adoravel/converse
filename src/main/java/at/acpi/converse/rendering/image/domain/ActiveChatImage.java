package at.acpi.converse.rendering.image.domain;

import at.acpi.converse.rendering.image.format.ImageFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Tracks the runtime status and state transitions of a chat image asset.
 */
public class ActiveChatImage {
	private static final Logger LOGGER = LoggerFactory.getLogger("converse/images");

	private final ChatImageData data;
	private final ImageFormat imageFormat;

	private final AtomicReference<ChatImageRenderingState> state =
			new AtomicReference<>(ChatImageRenderingState.PENDING);
	private volatile long lastAccessMs = System.currentTimeMillis();

	public ActiveChatImage(ChatImageData data, ImageFormat imageFormat) {
		this.data = data;
		this.imageFormat = imageFormat;
	}

	public ChatImageRenderingState getState() {
		return this.state.get();
	}

	public void setState(ChatImageRenderingState newState) {
		this.state.set(newState);
		this.touch();
	}

	public long getLastAccessMs() {
		return this.lastAccessMs;
	}

	public ChatImageData getData() {
		return this.data;
	}

	public ImageFormat getImageFormat() {
		return imageFormat;
	}

	public boolean compareAndSetState(ChatImageRenderingState expectedState, ChatImageRenderingState newState) {
		boolean success = this.state.compareAndSet(expectedState, newState);
		if (success) {
			this.touch();
		} else {
			LOGGER.warn("state transition race condition detected for url '{}'. expected: {}, found: {} 💔🍅",
					data.uri(), expectedState, this.state.get());
		}
		return success;
	}

	public void touch() {
		this.lastAccessMs = System.currentTimeMillis();
	}
}
