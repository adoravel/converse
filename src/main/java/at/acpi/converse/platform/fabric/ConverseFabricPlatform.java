package at.acpi.converse.platform.fabric;

//? fabric {
import at.acpi.converse.ConversePlatform;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class ConverseFabricPlatform implements ConversePlatform {
	@Override
	public Path getConfigFolder() {
		return FabricLoader.getInstance().getConfigDir();
	}

    @Override
    public String loader() {
        return "fabric";
    }
}
//?}
