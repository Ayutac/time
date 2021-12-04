package org.abos.fabricmc.time.blocks;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.abos.fabricmc.time.Time;
import org.jetbrains.annotations.Nullable;

public class TimeExtractorBlock extends BlockWithEntity {

    protected VoxelShape SHAPE_BOTTOM = Block.createCuboidShape(0d,0d, 0d, 16d, 3d, 16d);
    protected VoxelShape SHAPE_COLUMN = Block.createCuboidShape(1d,3d, 1d, 15d, 16d, 15d);
    protected VoxelShape SHAPE = VoxelShapes.combineAndSimplify(SHAPE_BOTTOM,SHAPE_COLUMN, BooleanBiFunction.OR);

    protected TimeExtractorBlock(Settings settings) {
        super(settings);
    }

    public TimeExtractorBlock() {
        this(Time.getTimeBlockSettings().nonOpaque());
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
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
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
        return checkType(type, Time.TIME_EXTRACTOR_ENTITY, TimeExtractorBlockEntity::tick);
    }

}
