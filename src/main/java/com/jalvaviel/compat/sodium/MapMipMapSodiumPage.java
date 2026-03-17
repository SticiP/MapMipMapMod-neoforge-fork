package com.jalvaviel.compat.sodium;

import com.google.common.collect.ImmutableList;
import com.jalvaviel.MapMipMapModClient;
import com.jalvaviel.config.MmmmGameOptions;
import net.caffeinemc.mods.sodium.client.gui.options.OptionGroup;
import net.caffeinemc.mods.sodium.client.gui.options.OptionImpl;
import net.caffeinemc.mods.sodium.client.gui.options.OptionPage;
import net.caffeinemc.mods.sodium.client.gui.options.control.ControlValueFormatter;
import net.caffeinemc.mods.sodium.client.gui.options.control.SliderControl;
import net.caffeinemc.mods.sodium.client.gui.options.storage.SodiumOptionsStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class MapMipMapSodiumPage {

    /**
     * Dummy storage for Sodium.
     * Since we handle data via our own config system, this serves as a bridge.
     */
    private static final SodiumOptionsStorage DUMMY_STORAGE = new SodiumOptionsStorage() {
        @Override
        public void save() {
            // Configuration is saved directly during binding.
        }
    };

    public static OptionPage createPage() {
        // 1. MipMap Levels Option (-1 to 8)
        var mipmapOption = OptionImpl.createBuilder(Integer.class, DUMMY_STORAGE)
                .setName(Component.translatable("entry.mapmipmapmod.map_mipmap_levels"))
                .setTooltip(Component.translatable("tooltip.mapmipmapmod.map_mipmap_levels"))
                .setControl(option -> new SliderControl(option, -1, 8, 1, value -> {
                    if (value <= -1) {
                        return Component.nullToEmpty(Component.translatable("entry.mapmipmapmod.auto").getString());
                    }
                    return Component.nullToEmpty(String.valueOf(value));
                }))
                .setBinding(
                        (options, value) -> {
                            MapMipMapModClient.options().generalOptions.setMapmipmapLevels(value);
                            MapMipMapModClient.LOG.info("Map MipMap level changed to: {}", value);

                            // Safely reset map data to apply changes instantly
                            Minecraft.getInstance().gameRenderer.getMapRenderer().resetData();
                        },
                        (options) -> MapMipMapModClient.options().generalOptions.getLiteralMapmipmapLevels()
                )
                .build();

        // 2. Locked Map Updates Option (Boolean Toggle)
        var mapUpdatesOption = OptionImpl.createBuilder(MmmmGameOptions.MapUpdateMode.class, DUMMY_STORAGE)
                .setName(Component.translatable("entry.mapmipmapmod.map_updates"))
                .setTooltip(Component.translatable("tooltip.mapmipmapmod.map_updates_only_unlocked")) // Tooltip generic
                .setControl(option -> new net.caffeinemc.mods.sodium.client.gui.options.control.CyclingControl<>(
                        option,
                        MmmmGameOptions.MapUpdateMode.class,
                        new Component[] {
                                Component.translatable("entry.mapmipmapmod.map_updates_all"),
                                Component.translatable("entry.mapmipmapmod.map_updates_only_unlocked"),
                                Component.translatable("entry.mapmipmapmod.map_updates_none")
                        }))
                .setBinding(
                        (options, value) -> MapMipMapModClient.options().generalOptions.setMapUpdates(value),
                        (options) -> MapMipMapModClient.options().generalOptions.getMapUpdates()
                )
                .build();

        var depthBiasOption = OptionImpl.createBuilder(Integer.class, DUMMY_STORAGE)
                .setName(Component.translatable("entry.mapmipmapmod.depth_bias"))
                .setTooltip(Component.translatable("tooltip.mapmipmapmod.depth_bias"))
                .setControl(option -> new SliderControl(option, 0, 8, 1, ControlValueFormatter.number()))
                .setBinding(
                        (options, value) -> MapMipMapModClient.options().generalOptions.setDepthBias(value),
                        (options) -> MapMipMapModClient.options().generalOptions.getDepthBias()
                )
                .build();

        var group = OptionGroup.createBuilder()
                .add(mipmapOption)
                .add(mapUpdatesOption)
                .add(depthBiasOption)
                .build();

        return new OptionPage(Component.translatable("tab.mapmipmapmod.general"), ImmutableList.of(group));
    }
}