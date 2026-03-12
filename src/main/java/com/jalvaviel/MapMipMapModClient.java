package com.jalvaviel;

import com.jalvaviel.config.MmmmGameOptions;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Adnotarea @Mod spune NeoForge-ului că acesta este punctul de intrare.
// "mapmipmapmod" trebuie să fie identic cu cel din neoforge.mods.toml
@Mod(value = "mapmipmapmod", dist = Dist.CLIENT)
public class MapMipMapModClient {
	public static final Logger LOG = LogManager.getLogger("MapMipMapMod");

	private static MmmmGameOptions CONFIG;
	public static boolean OUTDATED_DRIVER = false;
	public static int MAP_SIZE = 128;

	// Constructorul înlocuiește onInitializeClient() din Fabric
	public MapMipMapModClient(IEventBus modEventBus, ModContainer modContainer) {
		// 1. Încărcăm configurația
		CONFIG = loadConfig();

		// 2. Spunem NeoForge-ului ce ecran să deschidă când apeși pe butonul "Config" din lista de moduri
		modContainer.registerExtensionPoint(IConfigScreenFactory.class,
				(minecraft, parentScreen) -> new com.jalvaviel.config.MmmmOptionScreen(parentScreen, minecraft.options)
		);
	}

	public static MmmmGameOptions options() {
		if (CONFIG == null) {
			throw new IllegalStateException("Config not yet available.");
		} else {
			return CONFIG;
		}
	}

	private static MmmmGameOptions loadConfig() {
		try {
			return MmmmGameOptions.loadFromDisk();
		} catch (Exception e) {
			LOG.error("Failed to load configuration file", e);
			LOG.error("Using default configuration file in read-only mode");
			MmmmGameOptions config = MmmmGameOptions.defaults();
			config.setReadOnly();
			return config;
		}
	}
}