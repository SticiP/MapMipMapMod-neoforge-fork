package com.jalvaviel.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jalvaviel.MapMipMapModClient;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import net.neoforged.fml.loading.FMLPaths;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class MmmmGameOptions {
    private static final String DEFAULT_FILE_NAME = "mapmipmapmod-options.json";

    // GSON configured to write variables like "mapmipmapLevels" as "mapmipmap_levels" in the JSON
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();

    public final GeneralOptions generalOptions = new GeneralOptions();
    private boolean readOnly;

    private MmmmGameOptions() {}

    @Contract(" -> new")
    public static @NotNull MmmmGameOptions defaults() {
        return new MmmmGameOptions();
    }

    /**
     * Loads the configuration from the disk. If it doesn't exist, it creates a new one.
     */
    public static MmmmGameOptions loadFromDisk() {
        Path path = FMLPaths.CONFIGDIR.get().resolve(DEFAULT_FILE_NAME);
        MmmmGameOptions config;

        if (Files.exists(path)) {
            try (FileReader reader = new FileReader(path.toFile())) {
                config = GSON.fromJson(reader, MmmmGameOptions.class);
                MapMipMapModClient.LOG.info("Configuration successfully loaded from disk.");
            } catch (IOException e) {
                MapMipMapModClient.LOG.error("Could not read the configuration file!", e);
                throw new RuntimeException("Could not parse config", e);
            }
        } else {
            MapMipMapModClient.LOG.info("Configuration file not found. Creating a new one with default settings.");
            config = new MmmmGameOptions();
        }

        // Save the config back to disk to ensure any new missing fields are written
        try {
            writeToDisk(config);
            return config;
        } catch (IOException e) {
            throw new RuntimeException("Couldn't update config file", e);
        }
    }

    /**
     * Safely writes the configuration to the disk using a temporary file.
     */
    public static void writeToDisk(@NotNull MmmmGameOptions config) throws IOException {
        if (config.isReadOnly()) {
            throw new IllegalStateException("Config file is read-only");
        }

        Path path = FMLPaths.CONFIGDIR.get().resolve(DEFAULT_FILE_NAME);
        Path dir = path.getParent();

        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        } else if (!Files.isDirectory(dir)) {
            throw new IOException("Not a directory: " + dir);
        }

        // Write to a temporary file first, then move it to prevent corruption on crash
        Path tempPath = path.resolveSibling(path.getFileName() + ".tmp");
        Files.writeString(tempPath, GSON.toJson(config));
        Files.move(tempPath, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public void setReadOnly() {
        this.readOnly = true;
    }

    public enum MapUpdateMode {
        ALL, ONLY_UNLOCKED, NONE
    }

    /**
     * Inner class holding the actual settings values.
     */
    public static class GeneralOptions {
        private int mapmipmapLevels = -1;
        private MapUpdateMode mapUpdates = MapUpdateMode.ONLY_UNLOCKED;
        private int depthBias = 0;

        public GeneralOptions() {}

        /**
         * Returns the effective MipMap level based on user settings and driver support.
         * If set to -1 (Auto), it falls back to the game's default MipMap level.
         */
        public int getMapmipmapLevels() {
            if (MapMipMapModClient.OUTDATED_DRIVER) {
                return 0;
            }
            return (this.mapmipmapLevels <= -1) ? Minecraft.getInstance().options.mipmapLevels().get() : this.mapmipmapLevels;
        }

        /**
         * Returns the literal value selected by the user in the config (-1 to 8).
         */
        public int getLiteralMapmipmapLevels() {
            return this.mapmipmapLevels;
        }

        public MapUpdateMode getMapUpdates() { return this.mapUpdates; }

        public int getDepthBias() { return this.depthBias; }

        public void setMapmipmapLevels(int mapmipmapLevels) {
            this.mapmipmapLevels = mapmipmapLevels;
        }

        public void setMapUpdates(MapUpdateMode mapUpdates) { this.mapUpdates = mapUpdates; }

        public void setDepthBias(int depthBias) { this.depthBias = depthBias; }
    }
}