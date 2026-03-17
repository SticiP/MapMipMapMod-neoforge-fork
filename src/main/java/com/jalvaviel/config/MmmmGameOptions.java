package com.jalvaviel.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import net.neoforged.fml.loading.FMLPaths;

import static com.jalvaviel.MapMipMapModClient.*;

public class MmmmGameOptions {
    private static final String DEFAULT_FILE_NAME = "mapmipmapmod-options.json";

    public final GeneralOptions generalOptions = new GeneralOptions();

    private boolean readOnly;
    private static final Gson GSON;

    private MmmmGameOptions() {}

    @Contract(" -> new")
    public static @NotNull MmmmGameOptions defaults() {
        return new MmmmGameOptions();
    }

    public static MmmmGameOptions loadFromDisk() {
        Path path = FMLPaths.CONFIGDIR.get().resolve(DEFAULT_FILE_NAME);
        MmmmGameOptions config;
        if (Files.exists(path)) {
            try (FileReader reader = new FileReader(path.toFile())) {
                config = GSON.fromJson(reader, MmmmGameOptions.class);
                LOG.info("Configuratia a fost incarcata cu succes de pe disc.");
            } catch (IOException e) {
                LOG.error("Nu am putut citi configuratia!", e);
                throw new RuntimeException("Could not parse config", e);
            }
        } else {
            LOG.info("Fisierul de configuratie nu exista. Se creeaza unul nou cu setari default.");
            config = new MmmmGameOptions();
        }

        try {
            writeToDisk(config);
            return config;
        } catch (IOException e) {
            throw new RuntimeException("Couldn't update config file", e);
        }
    }

    public static void writeToDisk(@NotNull MmmmGameOptions config) throws IOException {
        if (config.isReadOnly()) {
            throw new IllegalStateException("Config file is read-only");
        } else {
            Path path = FMLPaths.CONFIGDIR.get().resolve(DEFAULT_FILE_NAME);
            Path dir = path.getParent();
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            } else if (!Files.isDirectory(dir)) {
                throw new IOException("Not a directory: " + dir);
            }

            Path tempPath = path.resolveSibling(path.getFileName() + ".tmp");
            Files.writeString(tempPath, GSON.toJson(config));
            Files.move(tempPath, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public void setReadOnly() {
        this.readOnly = true;
    }

    static {
        GSON = (new GsonBuilder()).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();
    }

    public static class GeneralOptions {
        private int mapmipmapLevels = -1;
        private boolean lockedMapUpdates = true;

        public GeneralOptions() {}

        public int getMapmipmapLevels() {
            return OUTDATED_DRIVER ? 0 : (this.mapmipmapLevels <= -1 ? Minecraft.getInstance().options.mipmapLevels().get() : this.mapmipmapLevels);
        }

        public int getLiteralMapmipmapLevels() {
            return this.mapmipmapLevels;
        }

        public boolean isLockedMapUpdates() {
            return this.lockedMapUpdates;
        }

        public void setMapmipmapLevels(int mapmipmapLevels) {
            this.mapmipmapLevels = mapmipmapLevels;
        }

        public void setLockedMapUpdates(boolean lockedMapUpdates) {
            this.lockedMapUpdates = lockedMapUpdates;
        }
    }
}