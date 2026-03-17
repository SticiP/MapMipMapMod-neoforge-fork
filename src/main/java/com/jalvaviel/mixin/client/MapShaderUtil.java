package com.jalvaviel.mixin.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;
import java.util.function.Function;

public class MapShaderUtil {
    // Am mutat codul "periculos" aici, unde NeoForge nu il va citi prea devreme!
    public static final Function<ResourceLocation, RenderType> MAP_MIPMAP_LAYER = Util.memoize(texture -> RenderType.create("map_mipmap_layer",
            DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
            VertexFormat.Mode.QUADS,
            786432,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_TEXT_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(texture, false, true))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .createCompositeState(true)));
}