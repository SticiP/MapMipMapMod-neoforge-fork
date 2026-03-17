package com.jalvaviel.mixin.client;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Plugin to handle conditional Mixin loading.
 * It prevents the mod from crashing if Sodium is not installed.
 */
public class MapMipMapMixinPlugin implements IMixinConfigPlugin {
    private boolean isSodiumLoaded = false;

    @Override
    public void onLoad(String mixinPackage) {
        try {
            // Check for the presence of Sodium's main GUI class.
            // Note: Package includes ".mods." as per Sodium 0.6+ structure.
            Class.forName("net.caffeinemc.mods.sodium.client.gui.SodiumOptionsGUI", false, this.getClass().getClassLoader());
            isSodiumLoaded = true;
        } catch (ClassNotFoundException e) {
            isSodiumLoaded = false;
        }
    }

    @Override
    public String getRefMapperConfig() { return null; }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // Load compatibility mixins only if Sodium is detected.
        if (mixinClassName.contains("compat.sodium")) {
            return isSodiumLoaded;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() { return null; }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}