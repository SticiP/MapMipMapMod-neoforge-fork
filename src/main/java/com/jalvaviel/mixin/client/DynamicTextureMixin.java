package com.jalvaviel.mixin.client;

import com.jalvaviel.MapMipMapModClient;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DynamicTexture.class)
public abstract class DynamicTextureMixin {

    @Shadow public abstract NativeImage getPixels();

    /**
     * Intercepts the upload method of DynamicTexture to generate mipmaps for maps.
     * A try-catch block is used to prevent early-load crashes from mods like Iris Shaders
     * that initialize textures before our config is fully loaded.
     */
    @Inject(method = "upload", at = @At("HEAD"), cancellable = true)
    private void generateMipMapsOnUpload(CallbackInfo ci) {
        int mipmapValue;

        // Safely attempt to read the config.
        // If Iris/Sodium requests a texture too early, we gracefully exit.
        try {
            mipmapValue = MapMipMapModClient.options().generalOptions.getMapmipmapLevels();
        } catch (IllegalStateException e) {
            return;
        }

        // Proceed only if MipMap is enabled, drivers are valid, and the image has pixels
        if (mipmapValue > 0 && !MapMipMapModClient.OUTDATED_DRIVER && this.getPixels() != null) {

            // Minecraft map textures are strictly 128x128
            if (this.getPixels().getWidth() == 128 && this.getPixels().getHeight() == 128) {

                int textureId = ((AbstractTexture) (Object) this).getId();

                RenderSystem.assertOnRenderThreadOrInit();
                RenderSystem.bindTexture(textureId);

                // Allocate memory for mipmaps and upload the base image
                TextureUtil.prepareImage(textureId, mipmapValue, 128, 128);
                this.getPixels().upload(0, 0, 0, 0, 0, 128, 128, false, false, false, false);

                // Generate the mipmap levels using OpenGL
                GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

                // Cancel the original upload method since we handled it
                ci.cancel();
            }
        }
    }
}