package org.abos.fabricmc.time.blocks;

import dev.onyxstudios.cca.api.v3.component.ComponentProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.abos.fabricmc.time.Time;
import org.abos.fabricmc.time.components.CounterComponent;
import org.abos.fabricmc.time.components.TimeComponents;

public class TimeCollectorBlock extends Block {

    protected VoxelShape SHAPE_LAYER_1 = Block.createCuboidShape(1d,0d, 1d, 15d, 2d, 15d);
    protected VoxelShape SHAPE_LAYER_2 = Block.createCuboidShape(2d,2d, 2d, 14d, 3d, 14d);
    protected VoxelShape SHAPE_LAYER_3 = Block.createCuboidShape(6d,3d, 6d, 10d, 4d, 10d);
    protected VoxelShape SHAPE_LAYER_4 = Block.createCuboidShape(7d,4d, 7d, 9d, 6d, 9d);
    protected VoxelShape SHAPE = VoxelShapes.combineAndSimplify(SHAPE_LAYER_1,
            VoxelShapes.combineAndSimplify(SHAPE_LAYER_2,
                    VoxelShapes.combineAndSimplify(SHAPE_LAYER_3, SHAPE_LAYER_4,
                            BooleanBiFunction.OR), BooleanBiFunction.OR), BooleanBiFunction.OR);

    public TimeCollectorBlock(Settings settings) {
        super(settings);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Time.LOGGER.trace("Entered {}#onUse(...)",TimeCollectorBlock.class.getName());
        if (!world.isClient) {
            ComponentProvider provider = ComponentProvider.fromWorld(world);
            CounterComponent passedTime = TimeComponents.PASSED_TIME.get(provider);
            int amount;
            // load a crystal
            if (player.getStackInHand(hand).isOf(Items.AMETHYST_SHARD) && !passedTime.isZero()) {
                amount = hit.getSide().equals(Direction.UP) ?
                        Math.min(passedTime.getValue(),player.getStackInHand(hand).getCount()) : 1;
                player.getStackInHand(hand).decrement(amount);
                passedTime.decrement(amount);
                player.getInventory().offerOrDrop(new ItemStack(Time.AMETHYME_SHARD, amount));
                TimeComponents.PASSED_TIME.sync(provider);
            }
        }
        Time.LOGGER.trace("Leaving {}#onUse(...)",TimeCollectorBlock.class.getName());
        return ActionResult.SUCCESS;
    }

}
