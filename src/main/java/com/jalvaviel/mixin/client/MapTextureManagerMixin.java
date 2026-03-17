package com.jalvaviel.mixin.client;

import com.jalvaviel.MapMipMapModClient;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.gui.MapRenderer$MapInstance", priority = 1200)
public abstract class MapTextureManagerMixin {

    // Shadow ne permite sa accesam campul privat "texture" din clasa MapInstance
    @Shadow @Final private DynamicTexture texture;

    @Inject(method = "updateTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;upload()V"), cancellable = true)
    private void preUploadMipmap(CallbackInfo ci) {
        if (!MapMipMapModClient.OUTDATED_DRIVER) {
            NativeImage image = this.texture.getPixels();
            if (image != null) {
                int mipmapValue = MapMipMapModClient.options().generalOptions.getMapmipmapLevels();

                if (mipmapValue > 0) {
                    // 1. Alocam memorie pentru nivelele de mipmap
                    TextureUtil.prepareImage(this.texture.getId(), mipmapValue, image.getWidth(), image.getHeight());

                    // 2. Facem upload la textura de baza (Level 0)
                    this.texture.upload();

                    // 3. Generam restul nivelelor pe GPU
                    GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D);

                    // 4. IMPORTANT: Anulam apelul original de upload() ca sa nu se faca de doua ori
                    ci.cancel();
                }
            }
        }
    }
}