package com.jalvaviel.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

import static com.jalvaviel.MapMipMapModClient.MAP_SIZE;

/** <h1>MmmmOptionScreen class</h1>
 * Ecranul de optiuni pentru MapMipMapMod fara Sodium.
 */
public class MmmmOptionScreen extends OptionsSubScreen {

    private static final MmmmOptionsStorage mmmmOpts = new MmmmOptionsStorage();

    /**
     * The option screen constructor.
     * @param parent The parent screen.
     * @param gameOptions The game options.
     */
    public MmmmOptionScreen(Screen parent, Options gameOptions) {
        super(parent, gameOptions, Component.translatable("tab.mapmipmapmod.general"));
    }

    /**
     * Adauga optiunile folosind constructorii OptionInstance (echivalentul SimpleOption).
     */
    @Override
    protected void addOptions() {
        // Map Mipmap Levels Option
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
                    Minecraft.getInstance().gameRenderer.getMapRenderer().resetData();
                });

        // Atlas Size Option
        OptionInstance<Integer> atlasSize = new OptionInstance<>(
                "entry.mapmipmapmod.atlas_size",
                OptionInstance.cachedConstantTooltip(Component.translatable("tooltip.mapmipmapmod.atlas_size")),
                (optionText, value) -> {
                    Component textValue = value <= 0 ?
                            Component.translatable("entry.mapmipmapmod.auto") :
                            Component.literal(value + "x" + value + " (" + (value * MAP_SIZE) + "x" + (value * MAP_SIZE) + "px)");
                    return Component.translatable("entry.mapmipmapmod.atlas_size").append(Component.literal(": ")).append(textValue);
                },
                new OptionInstance.IntRange(0, 32),
                mmmmOpts.getData().generalOptions.getLiteralAtlasSize(),
                (value) -> {
                    mmmmOpts.getData().generalOptions.setAtlasSize(value);
                    Minecraft.getInstance().gameRenderer.getMapRenderer().resetData();
                });

        // Locked Map Updates Option
        OptionInstance<Boolean> lockedMapUpdates = OptionInstance.createBoolean(
                "entry.mapmipmapmod.locked_map_updates",
                OptionInstance.cachedConstantTooltip(Component.translatable("tooltip.mapmipmapmod.locked_map_updates")),
                mmmmOpts.getData().generalOptions.isLockedMapUpdates(),
                (value) -> mmmmOpts.getData().generalOptions.setLockedMapUpdates(value));

        // In NeoForge/MojMap, lista de butoane se numeste "list" iar butoanele mari (full width) folosesc addBig
        if (this.list != null) {
            this.list.addBig(mapmipmapLevels);
            this.list.addBig(atlasSize);
            this.list.addBig(lockedMapUpdates);
        }
    }

    /**
     * In MojMap, metoda close() se numeste onClose() si este apelata la apasarea butonului ESC sau Done.
     */
    @Override
    public void onClose() {
        mmmmOpts.save();
        Minecraft.getInstance().gameRenderer.getMapRenderer().resetData();
        super.onClose();
    }
}