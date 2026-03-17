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

    @Inject(method = "upload", at = @At("HEAD"), cancellable = true)
    private void generateMipMapsOnUpload(CallbackInfo ci) {

        int mipmapValue = MapMipMapModClient.options().generalOptions.getMapmipmapLevels();

        // Daca e setat MipMap si textura are pixeli
        if (mipmapValue > 0 && !MapMipMapModClient.OUTDATED_DRIVER && this.getPixels() != null) {

            // Doar texturile de 128x128 sunt harti
            if (this.getPixels().getWidth() == 128 && this.getPixels().getHeight() == 128) {

                // Facem cast la AbstractTexture ca sa putem apela getId()
                int textureId = ((AbstractTexture) (Object) this).getId();

                RenderSystem.assertOnRenderThreadOrInit();
                RenderSystem.bindTexture(textureId);

                // 1. Alocam spatiu si incarcam textura originala
                TextureUtil.prepareImage(textureId, mipmapValue, 128, 128);

                // Ultimul parametru trebuie sa fie FALSE (sa nu elibereze memoria imaginii)
                this.getPixels().upload(0, 0, 0, 0, 0, 128, 128, false, false, false, false);

                // 2. Generam restul nivelurilor de mipmap pe GPU
                GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

                // 3. Oprim metoda Vanilla sa mai faca upload
                ci.cancel();
            }
        }
    }
}