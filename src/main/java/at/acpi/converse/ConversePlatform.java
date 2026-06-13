package at.acpi.converse;

import java.nio.file.Path;

public interface ConversePlatform {

	//? fabric {
	ConversePlatform PLATFORM = new at.acpi.converse.platform.fabric.ConverseFabricPlatform();
	 //? } else neoforge {
	/*ConversePlatform PLATFORM = new at.acpi.converse.platform.neoforge.ConverseNeoForgePlatform();
	*///? }

	Path getConfigFolder();

	String loader();
}
