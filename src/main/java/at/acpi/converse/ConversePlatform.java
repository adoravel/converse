package at.acpi.converse;

//? fabric {
import at.acpi.converse.fabric.ConverseFabricPlatform;


//?} else neoforge {

/*import at.acpi.converse.neoforge.ConverseNeoForgePlatform;
*///? }

import java.nio.file.Path;

public interface ConversePlatform {

	//? fabric {
	ConversePlatform PLATFORM = new ConverseFabricPlatform();
	 //? } else neoforge {
	/*ConversePlatform PLATFORM = new ConverseNeoForgePlatform();
	*///? }

	Path getConfigFolder();

	String loader();
}
