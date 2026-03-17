package com.jalvaviel.mixin.client;

import com.jalvaviel.MapMipMapModClient;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MapRenderer.MapInstance.class)
public abstract class MapRendererMixin {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;text(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"))
    private RenderType redirectMapRenderType(ResourceLocation location) {

        int mipmapValue = MapMipMapModClient.options().generalOptions.getMapmipmapLevels();

        if (mipmapValue <= 0 || MapMipMapModClient.OUTDATED_DRIVER) {
            return RenderType.text(location);
        }

//        MapMipMapModClient.LOG.info(">>> Applying Bilinear Filtering to the Map <<<");

        return RenderType.create(
                "map_mipmap_layer",
                com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
                com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS,
                786432,
                false,
                true,
                RenderType.CompositeState.builder()
                        .setShaderState(RenderStateShard.RENDERTYPE_TEXT_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(location, false, true))
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setLightmapState(RenderStateShard.LIGHTMAP)
                        .createCompositeState(false)
        );
    }
}