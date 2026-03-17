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

    // Storage fals - noi salvam datele direct cand modificam controlul
    private static final SodiumOptionsStorage DUMMY_STORAGE = new SodiumOptionsStorage() {
        @Override
        public void save() { }
    };

    public static OptionPage createPage() {

        // 1. Optiunea pentru MipMap Levels (-1 la 8)
        var mipmapOption = OptionImpl.createBuilder(Integer.class, DUMMY_STORAGE)
                .setName(Component.translatable("entry.mapmipmapmod.map_mipmap_levels"))
                .setTooltip(Component.translatable("tooltip.mapmipmapmod.map_mipmap_levels"))
                .setControl(option -> new SliderControl(option, -1, 8, 1, value -> {
                    // Daca valoarea este -1, returnam textul "Auto", altfel returnam numarul
                    if (value <= -1) {
                        return Component.nullToEmpty(Component.translatable("entry.mapmipmapmod.auto").getString());
                    }
                    return Component.nullToEmpty(String.valueOf(value));
                }))
                .setBinding(
                        // La modificare: salvam valoarea si resetam harta (exact ca in meniul tau original)
                        (options, value) -> {
                            MapMipMapModClient.options().generalOptions.setMapmipmapLevels(value);
                            MapMipMapModClient.LOG.info("Setare MipMap modificata la: " + value);

                            Minecraft.getInstance().gameRenderer.getMapRenderer().resetData();
                        },
                        // La citire: luam valoarea curenta
                        (options) -> MapMipMapModClient.options().generalOptions.getLiteralMapmipmapLevels()
                )
                .build();

        // 2. Optiunea pentru Locked Map Updates (Bifa / Toggle)
        var lockedMapUpdatesOption = OptionImpl.createBuilder(Boolean.class, DUMMY_STORAGE)
                .setName(Component.translatable("entry.mapmipmapmod.locked_map_updates"))
                .setTooltip(Component.translatable("tooltip.mapmipmapmod.locked_map_updates"))
                .setControl(TickBoxControl::new)
                .setBinding(
                        (options, value) -> {
                            MapMipMapModClient.options().generalOptions.setLockedMapUpdates(value);
                            MapMipMapModClient.LOG.info("Actualizari harti blocate setat pe: " + value);
                        },
                        (options) -> MapMipMapModClient.options().generalOptions.isLockedMapUpdates()
                )
                .build();

        // Adaugam ambele optiuni in acelasi grup vizual
        var group = OptionGroup.createBuilder()
                .add(mipmapOption)
                .add(lockedMapUpdatesOption)
                .build();

        // Cream tab-ul folosind titlul tradus
        return new OptionPage(Component.translatable("tab.mapmipmapmod.general"), ImmutableList.of(group));
    }
}