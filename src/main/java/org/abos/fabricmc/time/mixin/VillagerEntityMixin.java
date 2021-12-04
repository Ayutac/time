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
        if (player != null && (player.getMainHandStack().isIn(Time.WANDS)))
            cir.setReturnValue(player.getMainHandStack().getItem().useOnEntity(player.getMainHandStack(), player, (LivingEntity)(Object)this, hand));
        // this will disable trading while holding a wand in the right hand, even if it has no effect on the villager
        // this is intended, so players are careful with using wands in combinations with trades
    }

}
