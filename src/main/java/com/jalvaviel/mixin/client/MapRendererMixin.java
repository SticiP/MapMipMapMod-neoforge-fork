package com.jalvaviel.mixin.client;

import com.jalvaviel.MapMipMapModClient;
import com.jalvaviel.config.MmmmGameOptions;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapRenderer.MapInstance.class)
public abstract class MapRendererMixin {

    @Shadow
    private MapItemSavedData data;

    /**
     * Redirects the default Map RenderType creation.
     * If MipMap is enabled, it swaps the standard text RenderType with a custom one
     * that applies bilinear filtering (blur) for distant maps.
     */
    @Redirect(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;text(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;")
    )
    private RenderType redirectMapRenderType(ResourceLocation location) {

        int mipmapValue = MapMipMapModClient.options().generalOptions.getMapmipmapLevels();

        // Fallback to vanilla rendering if MipMap is disabled or drivers are unsupported
        if (mipmapValue <= 0 || MapMipMapModClient.OUTDATED_DRIVER) {
            return RenderType.text(location);
        }

        // Create and return the custom RenderType with texture blurring (blur = true)
        return RenderType.create(
                "map_mipmap_layer",
                DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
                VertexFormat.Mode.QUADS,
                786432,
                false,
                true,
                RenderType.CompositeState.builder()
                        .setShaderState(RenderStateShard.RENDERTYPE_TEXT_SHADER)
                        // The third boolean 'true' enables the bilinear filtering (blur)
                        .setTextureState(new RenderStateShard.TextureStateShard(location, false, true))
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setLightmapState(RenderStateShard.LIGHTMAP)
                        .createCompositeState(false)
        );
    }


    @Inject(method = "updateTexture", at = @At("HEAD"), cancellable = true)
    private void onUpdateTexture(CallbackInfo ci) {
        MmmmGameOptions.MapUpdateMode mode = MapMipMapModClient.options().generalOptions.getMapUpdates();

        if (mode == MmmmGameOptions.MapUpdateMode.ALL) {
            return;
        } else if (mode == MmmmGameOptions.MapUpdateMode.NONE) {
            ci.cancel();
            return;
        } else if (mode == MmmmGameOptions.MapUpdateMode.ONLY_UNLOCKED) {
            if (this.data != null && this.data.locked) {
                ci.cancel();
            }
        }
    }

    /**
     * Logic for "Depth Bias" (Z-Fighting Fix).
     * We wrap the entire draw call in a push/pop pose to safely translate the map forward.
     */
    @Inject(method = "draw", at = @At("HEAD"))
    private void preDraw(PoseStack poseStack, MultiBufferSource bufferSource, boolean active, int packedLight, CallbackInfo ci) {
        int biasLevel = MapMipMapModClient.options().generalOptions.getDepthBias();
        if (biasLevel > 0) {
            poseStack.pushPose();
            // Move the map slightly towards the camera (-Z)
            float offset = biasLevel * 0.001f;
            poseStack.translate(0.0f, 0.0f, -offset);
        }
    }

    @Inject(method = "draw", at = @At("RETURN"))
    private void postDraw(PoseStack poseStack, MultiBufferSource bufferSource, boolean active, int packedLight, CallbackInfo ci) {
        int biasLevel = MapMipMapModClient.options().generalOptions.getDepthBias();
        if (biasLevel > 0) {
            poseStack.popPose();
        }
    }
}