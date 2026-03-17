package com.jalvaviel.mixin.client.compat.sodium;

import com.jalvaviel.compat.sodium.MapMipMapSodiumPage;
import net.caffeinemc.mods.sodium.client.gui.SodiumOptionsGUI;
import net.caffeinemc.mods.sodium.client.gui.options.OptionPage;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(SodiumOptionsGUI.class)
public class SodiumOptionsGUIMixin {

    @Mutable
    @Shadow(remap = false)
    @Final
    private List<OptionPage> pages;

    /**
     * Injects our custom option page into the Sodium Video Settings menu.
     * We use remap = false because Sodium's classes are not part of the Mojang mapped obfuscation.
     */
    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void onSodiumMenuInit(Screen prevScreen, CallbackInfo ci) {
        // List.copyOf returns an unmodifiable list, so we create a mutable copy first.
        List<OptionPage> mutablePages = new ArrayList<>(this.pages);
        mutablePages.add(MapMipMapSodiumPage.createPage());
        this.pages = List.copyOf(mutablePages);
    }
}