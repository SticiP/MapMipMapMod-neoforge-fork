package com.jalvaviel.compat.sodium;

import com.google.common.collect.ImmutableList;
import com.jalvaviel.MapMipMapModClient;
import net.caffeinemc.mods.sodium.client.gui.options.OptionGroup;
import net.caffeinemc.mods.sodium.client.gui.options.OptionImpl;
import net.caffeinemc.mods.sodium.client.gui.options.OptionPage;
import net.caffeinemc.mods.sodium.client.gui.options.control.SliderControl;
import net.caffeinemc.mods.sodium.client.gui.options.control.TickBoxControl;
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
                            if (Minecraft.getInstance().gameRenderer != null && Minecraft.getInstance().gameRenderer.getMapRenderer() != null) {
                                Minecraft.getInstance().gameRenderer.getMapRenderer().resetData();
                            }
                        },
                        (options) -> MapMipMapModClient.options().generalOptions.getLiteralMapmipmapLevels()
                )
                .build();

        // 2. Locked Map Updates Option (Boolean Toggle)
        var lockedMapUpdatesOption = OptionImpl.createBuilder(Boolean.class, DUMMY_STORAGE)
                .setName(Component.translatable("entry.mapmipmapmod.locked_map_updates"))
                .setTooltip(Component.translatable("tooltip.mapmipmapmod.locked_map_updates"))
                .setControl(TickBoxControl::new)
                .setBinding(
                        (options, value) -> {
                            MapMipMapModClient.options().generalOptions.setLockedMapUpdates(value);
                            MapMipMapModClient.LOG.info("Locked map updates set to: {}", value);
                        },
                        (options) -> MapMipMapModClient.options().generalOptions.isLockedMapUpdates()
                )
                .build();

        var group = OptionGroup.createBuilder()
                .add(mipmapOption)
                .add(lockedMapUpdatesOption)
                .build();

        return new OptionPage(Component.translatable("tab.mapmipmapmod.general"), ImmutableList.of(group));
    }
}