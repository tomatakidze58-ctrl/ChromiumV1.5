package dev.chromiumclient.mixin;

import dev.chromiumclient.ChromiumClient;
import net.minecraft.client.renderer.Lightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Minecraft 26.2 replaced the old LightTexture class with Lightmap.
 * Returning 1.0F here makes every vanilla light level use maximum
 * brightness while Chromium Fullbright is enabled.
 */
@Mixin(Lightmap.class)
public abstract class LightTextureMixin {
    @Inject(
        method = "getBrightness(Lnet/minecraft/world/level/dimension/DimensionType;I)F",
        at = @At("HEAD"),
        cancellable = true,
        require = 0
    )
    private static void chromium$fullbright(CallbackInfoReturnable<Float> cir) {
        if (ChromiumClient.MODULES.on("Fullbright") || ChromiumClient.MODULES.on("Night Vision")) {
            cir.setReturnValue(1.0F);
        }
    }
}
