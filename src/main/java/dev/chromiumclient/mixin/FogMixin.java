package dev.chromiumclient.mixin;

import dev.chromiumclient.ChromiumClient;
import dev.chromiumclient.util.Reflect;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Pushes every vanilla fog range to the render-distance edge. */
@Mixin(FogRenderer.class)
public abstract class FogMixin {
    @Inject(method = "setupFog", at = @At("RETURN"), require = 0)
    private void chromium$noFog(Camera camera, int renderDistanceChunks, DeltaTracker delta, float partialTick,
                                ClientLevel level, CallbackInfoReturnable<FogData> cir) {
        if (!ChromiumClient.MODULES.on("No Fog")) return;
        FogData fog = cir.getReturnValue();
        if (fog == null) return;
        float far = Math.max(512.0F, renderDistanceChunks * 16.0F + 32.0F);
        Reflect.setField(fog, far, "environmentalStart");
        Reflect.setField(fog, far + 1.0F, "environmentalEnd");
        Reflect.setField(fog, far, "skyEnd");
        Reflect.setField(fog, far, "cloudEnd");
        Reflect.setField(fog, far - 1.0F, "renderDistanceStart");
        Reflect.setField(fog, far, "renderDistanceEnd");
    }
}
