package com.jalvaviel.config;

import com.jalvaviel.MapMipMapModClient;

import java.io.IOException;

/**
 * A lightweight storage wrapper that provides an interface to communicate with
 * the MmmmGameOptions instance, allowing easy data retrieval and saving to disk.
 */
public class MmmmOptionsStorage implements OptionStorage<MmmmGameOptions> {

    private final MmmmGameOptions options;

    public MmmmOptionsStorage() {
        // Initialize with the current running configuration
        this.options = MapMipMapModClient.options();
    }

    /**
     * Gets the current running configuration data.
     * * @return The MmmmGameOptions singleton reference.
     */
    @Override
    public MmmmGameOptions getData() {
        return this.options;
    }

    /**
     * Saves the current running configuration to the disk.
     * Throws a RuntimeException if the file cannot be written.
     */
    @Override
    public void save() {
        try {
            MmmmGameOptions.writeToDisk(this.options);
        } catch (IOException e) {
            // Log the error nicely in the console before crashing
            MapMipMapModClient.LOG.error("Failed to save configuration changes to disk!", e);
            throw new RuntimeException("Couldn't save configuration changes", e);
        }
    }
}