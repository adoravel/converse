package at.acpi.converse.animation;

public class MessageSlideController implements AnimationController {
	private final AnimationClock clock;
	private final float durationMs, maxDisplacement;

	public MessageSlideController(AnimationClock clock, float durationMs, float maxDisplacement) {
		this.clock = clock;
		this.durationMs = durationMs;
		this.maxDisplacement = maxDisplacement;
		this.clock.restart();
	}

	public AnimationClock getClock() {
		return this.clock;
	}

	@Override
	public float getProgress() {
		return AnimationMath.easeOutExpo(clock.getElapsedPercent(durationMs));
	}

	public float getDisplacement() {
		return maxDisplacement * (1.0f - getProgress());
	}
}
