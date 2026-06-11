package at.acpi.converse.hotkey;

public interface Hotkey {
	/**
	 * Checks if the feature is currently active.
	 */
	boolean isActive();

	/**
	 * Toggles the state manually.
	 */
	void toggle();

	/**
	 * Registers the keybind. This signature is clean and identical across loaders.
	 */
	void register();

	/**
	 * Factory method to create the correct loader-specific implementation.
	 */
	static Hotkey create(KeybindData data) {
		//? fabric {
		return new at.acpi.converse.fabric.FabricHotkeyImpl(data);
		 //? } else neoforge {
		/*return new at.acpi.converse.neoforge.NeoForgeHotkeyImpl(data);
		*///? }
	}
}
