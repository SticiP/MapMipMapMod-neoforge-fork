package com.jalvaviel.mixin.client;

import com.jalvaviel.MapMipMapModClient;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MapRenderer.class, priority = 1200)
public abstract class MapRendererMixin {
    /**
     * Anuleaza actualizarile inutile pentru hartile blocate.
     */
    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    public void setNeedsUpdate(MapId mapId, MapItemSavedData mapState, CallbackInfo ci){
        if (mapState.locked && MapMipMapModClient.options().generalOptions.isLockedMapUpdates()) {
            ci.cancel();
        }
    }
}