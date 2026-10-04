package dev.chromiumclient.mixin;

import dev.chromiumclient.ChromiumClient;
import dev.chromiumclient.util.ChromiumSettings;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Low Shield affects the shield only, never the other held item. */
@Mixin(ItemInHandRenderer.class)
public abstract class FirstPersonItemMixin {
    @Inject(
        method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
        at = @At("HEAD"), require = 0
    )
    private void chromium$lowShield(LivingEntity entity, ItemStack stack, ItemDisplayContext context,
                                    PoseStack pose, SubmitNodeCollector collector, int light, CallbackInfo ci) {
        if (stack != null && !stack.isEmpty() && stack.getItem() == Items.SHIELD && ChromiumClient.MODULES.on("Low Shield")) {
            pose.translate(0.0F, -ChromiumSettings.lowShieldOffset, 0.0F);
        }
    }
}
