package org.abos.fabricmc.time.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.abos.fabricmc.time.Time;
import org.abos.fabricmc.time.components.Counter;
import org.abos.fabricmc.time.components.CounterImpl;
import org.abos.fabricmc.time.gui.CompactFarmScreenHandler;
import org.abos.fabricmc.time.gui.TimeExtractorScreenHandler;

public class CompactFarmBlockEntity extends LockableContainerBlockEntity implements DefaultedInventory {

    public static final String CONTAINER_NAME = "container." + Time.COMPACT_FARM_STR;

    public static final String TICK_COUNTER_NAME = "tickCounter";

    public static final int INVENTORY_SIZE = 32; // 3x 9 rows + shard input + 3 shard battery + egg input
    public static final int PROPERTY_DELEGATE_SIZE = 1;

    private static int ticksNeeded;
    /**
     * Ticks needed per TU extracted. Is read out from game rules every tick.
     */
    public static int getTicksNeeded() {return ticksNeeded;}

    //----------------------------------------------------------
    // Fields (need to be read/written via nbt)
    //----------------------------------------------------------

    protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);

    protected Counter tickCounter = new CounterImpl();

    //----------------------------------------------------------
    // the property delegate field needed for the animation
    //----------------------------------------------------------

    private final PropertyDelegate propertyDelegate = new PropertyDelegate(){

        @Override
        public int get(int index) {
            if (index == 0) {
                return CompactFarmBlockEntity.this.tickCounter.getValue();
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                CompactFarmBlockEntity.this.tickCounter.setValue(value);
            }
        }

        @Override
        public int size() {
            return PROPERTY_DELEGATE_SIZE;
        }
    };

    //----------------------------------------------------------
    // Constructor
    //----------------------------------------------------------

    public CompactFarmBlockEntity(BlockPos pos, BlockState state) {
        super(Time.COMPACT_FARM_ENTITY, pos, state);
    }

    //----------------------------------------------------------
    // Tick method
    //----------------------------------------------------------

    public static void tick(World world, BlockPos pos, BlockState state, CompactFarmBlockEntity syphon) {
        if (world instanceof ServerWorld) {
            syphon.tickCounter.increment();
            ticksNeeded = world.getGameRules().getInt(Time.CONFIG.getCompactFarmTicksRule());
            if (syphon.tickCounter.getValue() >= ticksNeeded) {
                syphon.tickCounter.decrement(ticksNeeded);
                // TODO action
            }
        }
    }

    //----------------------------------------------------------
    // Miscellaneous methods
    //----------------------------------------------------------

    // getter/setter methods are protected because the extraction process shouldn't be disturbed from the outside

    public void resetTickCounter() {
        tickCounter.reset();
    }

    //----------------------------------------------------------
    // other Inventory methods
    // (mostly copied from AbstractFurnaceBlockEntity or
    //  defaulting to DefaultInventory)
    //----------------------------------------------------------

    @Override
    protected Text getContainerName() {
        return new TranslatableText(CONTAINER_NAME);
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new CompactFarmScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        }
        // reachable from 8 blocks afar (8*8=64)
        return player.squaredDistanceTo((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    //----------------------------------------------------------
    // NBT I/O
    //----------------------------------------------------------

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        // read tick counter
        int counter = nbt.getInt(TICK_COUNTER_NAME);
        if (Counter.isCounterValue(counter))
            tickCounter.setValue(counter);
        else {
            Time.LOGGER.warn("Illegal value {} for counter detected, will default to 0 instead.", counter);
            tickCounter.setValue(0);
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt(TICK_COUNTER_NAME, tickCounter.getValue());
        Inventories.writeNbt(nbt, inventory);
        return super.writeNbt(nbt);
    }
}
