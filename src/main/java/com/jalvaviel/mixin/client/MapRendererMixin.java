package com.jalvaviel.mixin.client;

import com.jalvaviel.MapMipMapModClient;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MapRenderer.MapInstance.class)
public abstract class MapRendererMixin {

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
}