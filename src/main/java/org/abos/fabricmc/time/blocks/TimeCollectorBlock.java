package org.abos.fabricmc.time.blocks;

import dev.onyxstudios.cca.api.v3.component.ComponentProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.abos.fabricmc.time.Time;
import org.abos.fabricmc.time.components.CounterComponent;
import org.abos.fabricmc.time.components.TimeComponents;

public class TimeCollectorBlock extends Block {

    public TimeCollectorBlock(Settings settings) {
        super(settings);
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
            }
            // unload a crystal
            else if (player.getStackInHand(hand).isOf(Time.AMETHYME_SHARD)) {
                amount = hit.getSide().equals(Direction.UP) ?
                        player.getStackInHand(hand).getCount() : 1;
                player.getStackInHand(hand).decrement(amount);
                passedTime.increment(amount);
                player.getInventory().offerOrDrop(new ItemStack(Items.AMETHYST_SHARD, amount));
            }
            TimeComponents.PASSED_TIME.sync(provider);
        }
        Time.LOGGER.trace("Leaving {}#onUse(...)",TimeCollectorBlock.class.getName());
        return ActionResult.SUCCESS;
    }

}
