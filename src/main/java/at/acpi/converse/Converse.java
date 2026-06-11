package at.acpi.converse;

import at.acpi.converse.config.ConverseConfig;
import at.acpi.converse.registry.ConverseHotkeys;
import net.minecraft.resources.Identifier;

public final class Converse {
	public static final String MOD_ID = "converse";

	public static void init() {
		ConverseConfig.loadConfig();
		ConverseHotkeys.registerHotkeys();
	}

	public static Identifier of(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
