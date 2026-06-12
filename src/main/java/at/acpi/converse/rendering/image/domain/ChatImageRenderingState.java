package at.acpi.converse.rendering.image.domain;

public enum ChatImageRenderingState {
	/**
	 * The image has been registered or placed into a processing queue.
	 * It is waiting for an available worker thread to pick it up.
	 */
	PENDING,

	/**
	 * The image data is actively being fetched from the network, read from
	 * disk, or undergoing initial binary decoding.
	 */
	LOADING,

	/**
	 * The image has been successfully downloaded, fully decoded, and cached.
	 * It is entirely ready to be rendered :3
	 */
	LOADED,

	/**
	 * The network download timed out, the file was missing, or the data stream
	 * was corrupted.
	 */
	FAILED
}
