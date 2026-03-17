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

        OptionInstance<MmmmGameOptions.MapUpdateMode> mapUpdates = new OptionInstance<>(
                "entry.mapmipmapmod.map_updates",
                OptionInstance.noTooltip(),
                (optionText, value) -> switch (value) {
                    case ALL -> Component.translatable("entry.mapmipmapmod.map_updates_all");
                    case ONLY_UNLOCKED -> Component.translatable("entry.mapmipmapmod.map_updates_only_unlocked");
                    case NONE -> Component.translatable("entry.mapmipmapmod.map_updates_none");
                },
                new OptionInstance.Enum<>(
                        java.util.List.of(MmmmGameOptions.MapUpdateMode.values()),
                        com.mojang.serialization.Codec.INT.xmap(
                                i -> MmmmGameOptions.MapUpdateMode.values()[i],
                                Enum::ordinal
                        )
                ),
                OPTIONS_STORAGE.getData().generalOptions.getMapUpdates(),
                value -> OPTIONS_STORAGE.getData().generalOptions.setMapUpdates(value)
        );

        OptionInstance<Integer> depthBias = new OptionInstance<>(
                "entry.mapmipmapmod.depth_bias",
                OptionInstance.cachedConstantTooltip(Component.translatable("tooltip.mapmipmapmod.depth_bias")),
                (optionText, value) -> Component.translatable("entry.mapmipmapmod.depth_bias")
                        .append(": ")
                        .append(Component.literal(String.valueOf(value))),
                new OptionInstance.IntRange(0, 8),
                OPTIONS_STORAGE.getData().generalOptions.getDepthBias(),
                value -> OPTIONS_STORAGE.getData().generalOptions.setDepthBias(value)
        );

        // Add the configured options to the UI list
        if (this.list != null) {
            this.list.addBig(mapmipmapLevels);
            this.list.addBig(mapUpdates);
            this.list.addBig(depthBias);
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