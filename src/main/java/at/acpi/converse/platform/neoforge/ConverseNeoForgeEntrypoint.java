package at.acpi.converse.platform.neoforge;

//? neoforge {

/*import at.acpi.converse.Converse;
import at.acpi.converse.config.ConverseConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(Converse.MOD_ID)
public class ConverseNeoForgeEntrypoint {
	public ConverseNeoForgeEntrypoint() {
		Converse.init();

		if (FMLEnvironment.getDist() != Dist.CLIENT)
			return;

		ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class,
				() -> (_container, parent) -> ConverseConfig.createScreen(parent));
	}
}
*///?}
