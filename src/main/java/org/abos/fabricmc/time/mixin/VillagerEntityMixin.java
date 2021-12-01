package org.abos.fabricmc.time.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.abos.fabricmc.time.Time;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerEntity.class)
public class VillagerEntityMixin {

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (player != null && (player.getMainHandStack().isOf(Time.TIMEY_WAND) || player.getMainHandStack().isOf(Time.TIME_WAND)))
            cir.setReturnValue(player.getMainHandStack().getItem().useOnEntity(player.getMainHandStack(), player, (LivingEntity)(Object)this, hand));
        // this will disable trading while holding a time(y) wand in the right hand
        // this is intended
    }

}
