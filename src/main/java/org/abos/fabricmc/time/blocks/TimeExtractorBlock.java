package org.abos.fabricmc.time.blocks;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.abos.fabricmc.time.Time;
import org.jetbrains.annotations.Nullable;

public class TimeExtractorBlock extends BlockWithEntity {

    public TimeExtractorBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TimeExtractorBlockEntity(pos, state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Time.LOGGER.trace("Entering {}#onUse(...)", TimeExtractorBlock.class.getName());
        if (!world.isClient) {
            // access and open the inventory
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        Time.LOGGER.trace("Leaving {}#onUse(...)", TimeExtractorBlock.class.getName());
        return ActionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof TimeExtractorBlockEntity) {
                ItemScatterer.spawn(world, pos, (TimeExtractorBlockEntity)blockEntity);
                world.updateComparators(pos,this); // TODO why this (method copied from tutorial)
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    // as required by tutorial
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, Time.TIME_EXTRACTOR_ENTITY, (world1, pos, state1, be) -> TimeExtractorBlockEntity.tick(world1, pos, state1, be));
    }

}
