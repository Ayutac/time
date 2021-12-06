package org.abos.fabricmc.time.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.abos.fabricmc.time.Time;
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
        boolean isBinding = false;
        // if crop/gourd block
        AmethymeShard shardResult = AmethymeShard.shardForCrop(state);
        if (shardResult == null)
            shardResult = AmethymeShard.shardForGourd(state);
        // (crops and gourds share the same drop rate)
        if (shardResult != null)
            isBinding = world.getRandom().nextInt(100) < world.getGameRules().getInt(Time.CONFIG.getPercentageShardCropRule());
        // if storage block
        else
            shardResult = AmethymeShard.shardForStorageBlock(state);
        if (shardResult != null)
            isBinding = world.getRandom().nextInt(100) < world.getGameRules().getInt(Time.CONFIG.getPercentageShardStorageRule());
        // if spawner block
        else if (state.isOf(Blocks.SPAWNER))
            shardResult = AmethymeShard.shardForSpawner((MobSpawnerBlockEntity)blockEntity);
        if (shardResult != null)
            isBinding = world.getRandom().nextInt(100) < world.getGameRules().getInt(Time.CONFIG.getPercentageShardSpawnerRule());
        // if target and probability succeeded
        if (shardResult != null && isBinding) {
            unboundStack.decrement(1);
            player.getInventory().offerOrDrop(new ItemStack(shardResult));
            ci.cancel();
        }
    }

}
