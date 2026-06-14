package at.acpi.converse.platform.fabric;

//? fabric {

import at.acpi.converse.config.ConverseConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ConverseModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return ConverseConfig::createScreen;
	}
}//?}

