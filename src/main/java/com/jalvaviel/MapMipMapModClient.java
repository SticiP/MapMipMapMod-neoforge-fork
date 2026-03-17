package com.jalvaviel;

import com.jalvaviel.config.MmmmGameOptions;
import com.jalvaviel.config.MmmmOptionScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The @Mod annotation tells NeoForge that this is the entry point.
// The value "mapmipmapmod" must match the modId in the neoforge.mods.toml file.
@Mod(value = "mapmipmapmod", dist = Dist.CLIENT)
public class MapMipMapModClient {
	public static final Logger LOG = LogManager.getLogger("MapMipMapMod");

	private static MmmmGameOptions config;
	public static boolean OUTDATED_DRIVER = false;
	public static final int MAP_SIZE = 128;

	public MapMipMapModClient(IEventBus modEventBus, ModContainer modContainer) {
		// 1. Load the configuration
		config = loadConfig();

		// 2. Register the configuration screen factory
		modContainer.registerExtensionPoint(IConfigScreenFactory.class,
				(container, parentScreen) -> new MmmmOptionScreen(parentScreen, Minecraft.getInstance().options)
		);
	}

	public static MmmmGameOptions options() {
		if (config == null) {
			throw new IllegalStateException("Config not yet available.");
		}
		return config;
	}

	private static MmmmGameOptions loadConfig() {
		try {
			return MmmmGameOptions.loadFromDisk();
		} catch (Exception e) {
			LOG.error("Failed to load configuration file", e);
			LOG.error("Using default configuration file in read-only mode");

			MmmmGameOptions fallbackConfig = MmmmGameOptions.defaults();
			fallbackConfig.setReadOnly();
			return fallbackConfig;
		}
	}
}