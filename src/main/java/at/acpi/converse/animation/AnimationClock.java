package at.acpi.converse.animation;

public class AnimationClock {
	private long startTime = 0L;

	public void restart() {
		this.startTime = System.currentTimeMillis();
	}

	public float getElapsedPercent(float durationMs) {
		if (startTime == 0L) return 1.0f;
		long elapsed = System.currentTimeMillis() - startTime;
		return Math.min((float) elapsed / durationMs, 1.0f);
	}
}
