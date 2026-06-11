package at.acpi.converse.neoforge;

//? neoforge {
/*import at.acpi.converse.Converse;
import at.acpi.converse.hotkey.Hotkey;
import at.acpi.converse.hotkey.KeybindData;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = Converse.MOD_ID, value = Dist.CLIENT)
public final class NeoForgeHotkeyImpl implements Hotkey {
	private static final List<NeoForgeHotkeyImpl> REGISTRY = new ArrayList<>();

	private final KeybindData data;
	private final Lazy<KeyMapping> keyMapping;
	private boolean active;

	public NeoForgeHotkeyImpl(KeybindData data) {
		this.data = data;
		this.keyMapping = Lazy.of(() -> new KeyMapping(
				data.description(),
				InputConstants.Type.KEYSYM,
				data.defaultKey(),
				data.category()
		));
	}

	@Override
	public boolean isActive() {
		return this.active;
	}

	@Override
	public void toggle() {
		this.active = !this.active;
		this.data.callback().onUpdate(Minecraft.getInstance(), this.active);
	}

	@Override
	public void register() {
		REGISTRY.add(this);
	}

	@SubscribeEvent
	public static void onKeyRegister(RegisterKeyMappingsEvent event) {
		for (NeoForgeHotkeyImpl hotkey : REGISTRY) {
			event.register(hotkey.keyMapping.get());
		}
	}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Post event) {
		for (NeoForgeHotkeyImpl hotkey : REGISTRY) {
			while (hotkey.keyMapping != null && hotkey.keyMapping.get().consumeClick()) {
				hotkey.toggle();
			}
		}
	}
}
*///?}
