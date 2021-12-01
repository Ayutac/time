package org.abos.fabricmc.time.blocks;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.abos.fabricmc.time.Time;
import org.jetbrains.annotations.Nullable;

public class TimeSyphonBlock extends BlockWithEntity {

    public TimeSyphonBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TimeSyphonBlockEntity(pos, state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Time.LOGGER.trace("Entering {}#onUse(...)",TimeSyphonBlock.class.getName());
        if (!world.isClient) {
            TimeSyphonBlockEntity syphon = (TimeSyphonBlockEntity) world.getBlockEntity(pos);
            ItemStack stack = player.getStackInHand(hand);
            // store item into syphon
            if (!stack.isEmpty() && syphon.isValid(0,stack) && syphon.getStack(0).isEmpty()) {
                syphon.setStack(0, new ItemStack(stack.getItem(), 1));
                stack.decrement(1);
                syphon.markDirty(); // for game-related updates in neighboring blocks
            }
            // take item out of syphon
            else if (stack.isEmpty() && !syphon.getStack(0).isEmpty()) {
                player.getInventory().offerOrDrop(syphon.removeStack(0));
                syphon.resetTickCounter();
                syphon.markDirty(); // for game-related updates in neighboring blocks
            }
            Time.LOGGER.trace("Leaving {}#onUse(...)", TimeSyphonBlock.class.getName());
        }
        return ActionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof TimeSyphonBlockEntity) {
                ItemScatterer.spawn(world, pos, (TimeSyphonBlockEntity)blockEntity);
                world.updateComparators(pos,this); // TODO why this (method copied from tutorial)
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    // as required by tutorial
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, Time.TIME_SYPHON_ENTITY, (world1, pos, state1, be) -> TimeSyphonBlockEntity.tick(world1, pos, state1, be));
    }

}
