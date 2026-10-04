package dev.chromiumclient.mixin;

import dev.chromiumclient.camera.FreelookState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Captures the normal mouse turn, feeds it into the freelook camera, then restores player rotation. */
@Mixin(MouseHandler.class)
public abstract class FreelookMouseMixin {
    @Unique private float chromium$oldYaw;
    @Unique private float chromium$oldPitch;
    @Unique private boolean chromium$capturing;

    @Inject(method = "turnPlayer", at = @At("HEAD"), require = 0)
    private void chromium$beforeTurn(double movementTime, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        chromium$capturing = FreelookState.active() && mc.player != null;
        if (chromium$capturing) {
            chromium$oldYaw = mc.player.getYRot();
            chromium$oldPitch = mc.player.getXRot();
        }
    }

    @Inject(method = "turnPlayer", at = @At("TAIL"), require = 0)
    private void chromium$afterTurn(double movementTime, CallbackInfo ci) {
        if (!chromium$capturing) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        float yawDelta = mc.player.getYRot() - chromium$oldYaw;
        float pitchDelta = mc.player.getXRot() - chromium$oldPitch;
        FreelookState.addTurn(yawDelta, pitchDelta);
        mc.player.setYRot(chromium$oldYaw);
        mc.player.setXRot(chromium$oldPitch);
        chromium$capturing = false;
    }
}
