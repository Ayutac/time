package org.abos.fabricmc.time.blocks;

import dev.onyxstudios.cca.api.v3.component.ComponentProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.abos.fabricmc.time.Time;
import org.abos.fabricmc.time.components.Counter;
import org.abos.fabricmc.time.components.CounterComponent;
import org.abos.fabricmc.time.components.CounterImpl;
import org.abos.fabricmc.time.components.TimeComponents;

public class TimeSyphonBlockEntity extends BlockEntity implements DefaultedInventory {

    public static final String TICK_COUNTER_KEY = "tickCounter";

    public static final String CTU_COUNTER_NAME = "ctu";

    /**
     * Inventory of this instance. Should be able to hold exactly 1 valid item.
     * @see #isValid(int, ItemStack)
     */
    protected DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);

    // if you want to change the factor, you MUST change the hard-coded numbers in #tick
    protected Counter ctuCounter = new CounterImpl(); // centi Time Unit, a hundredth of 1 Time Unit

    protected Counter tickCounter = new CounterImpl();

    public TimeSyphonBlockEntity(BlockPos pos, BlockState state) {
        super(Time.TIME_SYPHON_ENTITY, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    public void resetTickCounter() {
        tickCounter.reset();
    }

    /**
     * Checks if an item is valid to be syphoned on. Right now, only the dragon egg can be syphoned.
     * @param slot the slot to check
     * @param stack the stack to check
     * @return {@code true} if the slot is {@code 0} and the item is valid.
     */
    @Override
    // if changed, don't forget the doc above!
    public boolean isValid(int slot, ItemStack stack) {
        return slot == 0 && stack.isOf(Items.DRAGON_EGG);
    }

    public static void tick(World world, BlockPos pos, BlockState state, TimeSyphonBlockEntity syphon) {
        if (world instanceof ServerWorld) {
            syphon.tickCounter.increment();
            int ticksNeeded = world.getGameRules().getInt(Time.CONFIG.getDragonEggSyphonTicksRule());
            if (syphon.tickCounter.getValue() >= ticksNeeded) {
                syphon.ctuCounter.increment();
                syphon.tickCounter.decrement(ticksNeeded);
                if (syphon.ctuCounter.getValue() >= 100) {
                    ComponentProvider provider = ComponentProvider.fromWorld(world);
                    CounterComponent passedTime = TimeComponents.PASSED_TIME.get(provider);
                    passedTime.increment(world.getGameRules().getInt(Time.CONFIG.getTUIncreaseRule()));
                    TimeComponents.PASSED_TIME.sync(provider);
                    syphon.ctuCounter.decrement(100);
                }
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, items);
        // read tick counter
        int counter = nbt.getInt(TICK_COUNTER_KEY);
        if (Counter.isCounterValue(counter))
            tickCounter.setValue(counter);
        else {
            Time.LOGGER.warn("Illegal value {} for counter detected, will default to 0 instead.", counter);
            tickCounter.setValue(0);
        }
        // read ctu counter
        counter = nbt.getInt(CTU_COUNTER_NAME);
        if (Counter.isCounterValue(counter))
            ctuCounter.setValue(counter);
        else {
            Time.LOGGER.warn("Illegal value {} for counter detected, will default to 0 instead.", counter);
            ctuCounter.setValue(0);
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt(CTU_COUNTER_NAME, ctuCounter.getValue());
        nbt.putInt(TICK_COUNTER_KEY, tickCounter.getValue());
        Inventories.writeNbt(nbt, items);
        return super.writeNbt(nbt);
    }
}
