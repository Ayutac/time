package org.abos.fabricmc.time.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.abos.fabricmc.time.items.AmethymeShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    // not cancellable because the drop cannot be avoided without more mixin
    // tail because we don't want to call this method if we returned early (in client)
    @Inject(method = "onKilledBy", at = @At("TAIL"))
    protected void onKilledBy(LivingEntity adversary, CallbackInfo ci) {
        if (!(adversary instanceof PlayerEntity player))
            return;
        ItemStack unboundStack = AmethymeShard.getUnboundStack(player);
        if (unboundStack == null)
            return;
        AmethymeShard shardResult = AmethymeShard.shardForMob(getType());
        if (shardResult != null) {
            unboundStack.decrement(1);
            player.getInventory().offerOrDrop(new ItemStack(shardResult));
        }
    }

}
