package org.abos.fabricmc.time.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.abos.fabricmc.time.items.AmethymeShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {

    // at head because this way we can omit the drop
    @Inject(method = "afterBreak", at = @At("HEAD"), cancellable = true)
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack, CallbackInfo ci) {
        if (world.isClient() || player == null)
            return;
        ItemStack unboundStack = AmethymeShard.getUnboundStack(player);
        if (unboundStack == null)
            return;
        AmethymeShard shardResult = AmethymeShard.shardForCrop(state);
        if (shardResult != null) {
            unboundStack.decrement(1);
            player.getInventory().offerOrDrop(new ItemStack(shardResult));
            ci.cancel();
        }
    }

}
