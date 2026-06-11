package at.acpi.converse.fabric;

//? fabric {

import at.acpi.converse.Converse;
import net.fabricmc.api.ClientModInitializer;

public class ConverseFabricClientEntrypoint implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Converse.init();
	}
}
//?}
