package dev.chromiumclient.mixin;

import dev.chromiumclient.ChromiumClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Cancels client particle insertion while No Particles is enabled. */
@Mixin(ParticleEngine.class)
public abstract class NoParticlesMixin {
    @Inject(method = "add(Lnet/minecraft/client/particle/Particle;)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void chromium$noParticles(Particle particle, CallbackInfo ci) {
        if (ChromiumClient.MODULES.on("No Particles")) ci.cancel();
    }
}
