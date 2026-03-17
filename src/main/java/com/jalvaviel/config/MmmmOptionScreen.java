package com.jalvaviel.config;

import com.jalvaviel.MapMipMapModClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class MmmmOptionScreen extends OptionsSubScreen {

    private static final MmmmOptionsStorage mmmmOpts = new MmmmOptionsStorage();

    public MmmmOptionScreen(Screen parent, Options gameOptions) {
        super(parent, gameOptions, Component.translatable("tab.mapmipmapmod.general"));
    }

    @Override
    protected void addOptions() {
        // Setare Mipmap Levels
        OptionInstance<Integer> mapmipmapLevels = new OptionInstance<>(
                "entry.mapmipmapmod.map_mipmap_levels",
                OptionInstance.cachedConstantTooltip(Component.translatable("tooltip.mapmipmapmod.map_mipmap_levels")),
                (optionText, value) -> {
                    Component textValue = value <= -1 ?
                            Component.translatable("entry.mapmipmapmod.auto") :
                            Component.literal(Integer.toString(value));
                    return Component.translatable("entry.mapmipmapmod.map_mipmap_levels").append(Component.literal(": ")).append(textValue);
                },
                new OptionInstance.IntRange(-1, 8),
                mmmmOpts.getData().generalOptions.getLiteralMapmipmapLevels(),
                (value) -> {
                    mmmmOpts.getData().generalOptions.setMapmipmapLevels(value);
                    MapMipMapModClient.LOG.info("Setare MipMap modificata la: " + value); // LOG AICI
                    Minecraft.getInstance().gameRenderer.getMapRenderer().resetData();
                });

        // Setare Locked Maps
        OptionInstance<Boolean> lockedMapUpdates = OptionInstance.createBoolean(
                "entry.mapmipmapmod.locked_map_updates",
                OptionInstance.cachedConstantTooltip(Component.translatable("tooltip.mapmipmapmod.locked_map_updates")),
                mmmmOpts.getData().generalOptions.isLockedMapUpdates(),
                (value) -> {
                    mmmmOpts.getData().generalOptions.setLockedMapUpdates(value);
                    MapMipMapModClient.LOG.info("Actualizari harti blocate setat pe: " + value); // LOG AICI
                });

        if (this.list != null) {
            this.list.addBig(mapmipmapLevels);
            this.list.addBig(lockedMapUpdates);
        }
    }

    @Override
    public void onClose() {
        mmmmOpts.save();
        MapMipMapModClient.LOG.info("Meniu inchis. Setarile au fost salvate."); // LOG AICI
        Minecraft.getInstance().gameRenderer.getMapRenderer().resetData();
        super.onClose();
    }
}