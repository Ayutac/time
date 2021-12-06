package org.abos.fabricmc.time.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.abos.fabricmc.time.Time;
import org.abos.fabricmc.time.items.AmethymeShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow public abstract boolean isBaby();

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    /*@Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    protected void drop(DamageSource damageSource, CallbackInfo ci) {
        if (isBaby() || !(damageSource.getAttacker() instanceof PlayerEntity player))
            return;
        ItemStack unboundStack = AmethymeShard.getUnboundStack(player);
        if (unboundStack == null)
            return;
        AmethymeShard shardResult = AmethymeShard.shardForMob(getType());
        boolean isBinding = false;
        // if mob was peaceful
        if (AmethymeShard.isCattle(getType()))
            isBinding = getEntityWorld().getRandom().nextInt(100) < getEntityWorld().getGameRules().getInt(Time.CONFIG.getPercentageShardCattleRule());
        else // if mob wasn't so peaceful
            isBinding = getEntityWorld().getRandom().nextInt(100) < getEntityWorld().getGameRules().getInt(Time.CONFIG.getPercentageShardMobRule());
        if (shardResult != null && isBinding) {
            unboundStack.decrement(1);
            player.getInventory().offerOrDrop(new ItemStack(shardResult));
            ci.cancel();
        }
    }*/

}
