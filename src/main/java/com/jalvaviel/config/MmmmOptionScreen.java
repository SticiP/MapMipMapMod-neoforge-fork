package com.jalvaviel.config;

import com.jalvaviel.MapMipMapModClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class MmmmOptionScreen extends OptionsSubScreen {

    private static final MmmmOptionsStorage OPTIONS_STORAGE = new MmmmOptionsStorage();

    public MmmmOptionScreen(Screen parent, Options gameOptions) {
        super(parent, gameOptions, Component.translatable("tab.mapmipmapmod.general"));
    }

    @Override
    protected void addOptions() {
        // MipMap Levels Setting (-1 for Auto, 0-8 for manual)
        OptionInstance<Integer> mapmipmapLevels = new OptionInstance<>(
                "entry.mapmipmapmod.map_mipmap_levels",
                OptionInstance.cachedConstantTooltip(Component.translatable("tooltip.mapmipmapmod.map_mipmap_levels")),
                (optionText, value) -> {
                    Component textValue = value <= -1 ?
                            Component.translatable("entry.mapmipmapmod.auto") :
                            Component.literal(Integer.toString(value));
                    return Component.translatable("entry.mapmipmapmod.map_mipmap_levels")
                            .append(Component.literal(": "))
                            .append(textValue);
                },
                new OptionInstance.IntRange(-1, 8),
                OPTIONS_STORAGE.getData().generalOptions.getLiteralMapmipmapLevels(),
                (value) -> {
                    OPTIONS_STORAGE.getData().generalOptions.setMapmipmapLevels(value);
                    MapMipMapModClient.LOG.info("MipMap setting changed to: " + value);

                    // Safely reset map data to apply changes instantly
                    Minecraft.getInstance().gameRenderer.getMapRenderer().resetData();
                });

        // Locked Map Updates Setting (Boolean Toggle)
        OptionInstance<Boolean> lockedMapUpdates = OptionInstance.createBoolean(
                "entry.mapmipmapmod.locked_map_updates",
                OptionInstance.cachedConstantTooltip(Component.translatable("tooltip.mapmipmapmod.locked_map_updates")),
                OPTIONS_STORAGE.getData().generalOptions.isLockedMapUpdates(),
                (value) -> {
                    OPTIONS_STORAGE.getData().generalOptions.setLockedMapUpdates(value);
                    MapMipMapModClient.LOG.info("Locked map updates set to: " + value);
                });

        // Add the configured options to the UI list
        if (this.list != null) {
            this.list.addBig(mapmipmapLevels);
            this.list.addBig(lockedMapUpdates);
        }
    }

    @Override
    public void onClose() {
        OPTIONS_STORAGE.save();
        MapMipMapModClient.LOG.info("Menu closed. Settings saved.");

        // Safely reset map data upon closing the menu
        Minecraft.getInstance().gameRenderer.getMapRenderer().resetData();

        super.onClose();
    }
}