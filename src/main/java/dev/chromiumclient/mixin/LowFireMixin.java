package dev.chromiumclient.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.chromiumclient.ChromiumClient;
import dev.chromiumclient.util.ChromiumSettings;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public abstract class LowFireMixin {
    @Inject(method = "submitFire", at = @At("HEAD"), require = 0)
    private static void chromium$lowFire(PoseStack pose, SubmitNodeCollector collector, TextureAtlasSprite sprite, CallbackInfo ci) {
        if (ChromiumClient.MODULES.on("Low Fire")) pose.translate(0.0F, ChromiumSettings.lowFireOffset, 0.0F);
    }
}
