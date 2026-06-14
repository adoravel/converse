package at.acpi.converse.rendering.animation;

public class AnimationMath {
	private static float OVERSHOOT = 1.70158f;

	public static float easeOutExpo(float x) {
		return x == 1.0f ? 1.0f : 1.0f - (float) Math.pow(2, -10 * x);
	}

	public static float easeOutBack(float x) {
		float c1 = OVERSHOOT;
		float c3 = c1 + 1f;
		return 1.0f + c3 * (float) Math.pow(x - 1.0f, 3) + c1 * (float) Math.pow(x - 1.0f, 2);
	}
}
