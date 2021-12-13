package org.abos.fabricmc.time.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
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
import org.abos.fabricmc.time.Utils;
import org.abos.fabricmc.time.components.Counter;
import org.abos.fabricmc.time.components.CounterImpl;
import org.abos.fabricmc.time.gui.BoundShardOnlySlot;
import org.abos.fabricmc.time.gui.CompactFarmScreenHandler;
import org.abos.fabricmc.time.items.AmethymeShard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CompactFarmBlockEntity extends LockableContainerBlockEntity implements DefaultedInventory {

    public static final String CONTAINER_NAME = "container." + Time.COMPACT_FARM_STR;

    public static final String TICK_COUNTER_KEY = "tickCounter";
    public static final String CURRENT_SHARD_KEY = "levelCounter";
    public static final String CURRENTLY_EGG_KEY = "currentlyEgg";

    public static final int INVENTORY_SIZE = 30; // 3x 9 rows + shard input + bound shard + egg input
    public static final int PROPERTY_DELEGATE_SIZE = 2;

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

    protected ItemStack currentShard = ItemStack.EMPTY;

    protected boolean currentlyEgg = false;

    //----------------------------------------------------------
    // the property delegate field needed for the animation
    //----------------------------------------------------------

    private final PropertyDelegate propertyDelegate = new PropertyDelegate(){

        @Override
        public int get(int index) {
            if (index == 0) {
                return CompactFarmBlockEntity.this.tickCounter.getValue();
            }
            if (index == 1) {
                return CompactFarmBlockEntity.this.currentShard.getCount();
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                CompactFarmBlockEntity.this.tickCounter.setValue(value);
            }
            if (index == 0) {
                CompactFarmBlockEntity.this.currentShard.setCount(value);
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

    public static void tick(World world, BlockPos pos, BlockState state, CompactFarmBlockEntity compactFarm) {
        if (world instanceof ServerWorld) {
            if (compactFarm.isFarming()) {
                // do one tick of extracting
                compactFarm.getTickCounter().increment();
                // farm once maybe
                ticksNeeded = world.getGameRules().getInt(Time.CONFIG.getCompactFarmTicksRule());
                if (compactFarm.getTickCounter().getValue() >= ticksNeeded) {
                    compactFarm.resetTickCounter();
                    ItemStack currentShard = compactFarm.getCurrentShard();
                    if (!(currentShard.getItem() instanceof AmethymeShard)) {
                        Time.LOGGER.warn("Used shard couldn't be determined, farm loot is lost!");
                    }
                    else {
                        LootTable table = ((AmethymeShard)currentShard.getItem()).getLootTable(world, currentShard.getCount());
                        if (table == null) {
                            Time.LOGGER.warn("Loot table {} couldn't be found, farm loot is lost!",
                                    ((AmethymeShard)currentShard.getItem()).getLevelledLootPath(currentShard.getCount()));
                        }
                        else if (compactFarm.isCurrentlyEgg() && Time.CONFIG.allowsCompactFarmEggs()) {
                            Utils.addToInventory(AmethymeShard.eggForShard((AmethymeShard)currentShard.getItem(),world.getRandom()),compactFarm,true);
                            CompactFarmBlockEntity.markDirty(world,pos,state);
                        }
                        else {
                            // fill farm normally
                            if (compactFarm.isCurrentlyEgg() && !Time.CONFIG.allowsCompactFarmEggs()) {
                                Time.LOGGER.warn("Egg production has been disabled, normal loot will be generated!");
                            }
                            List<ItemStack> remainder = Utils.fillWithLoot((ServerWorld) world, pos, table);
                            CompactFarmBlockEntity.markDirty(world,pos,state);
                            int size = 0;
                            for (ItemStack slot : remainder)
                                size += slot != null ? slot.getCount() : 0;
                            if (size != 0)
                                Time.LOGGER.warn("{} items couldn't be created due to full farm!", size);
                        } // -> if loot table was accessible
                    } // -> if shard was recognizable AmethymeShard
                    compactFarm.setCurrentShard(ItemStack.EMPTY);
                    compactFarm.setCurrentlyEgg(false);
                } // -> if ticks were full: it was time to farm
            } // -> if farming was in progress

            // start farming maybe
            if (!compactFarm.isFarming() && !compactFarm.getFarmingShard().isEmpty() && compactFarm.getShardToBeUsedUp().isOf(Time.AMETHYME_SHARD)) {
                compactFarm.setCurrentShard(compactFarm.getFarmingShard());
                compactFarm.getShardToBeUsedUp().decrement(1);
                if (compactFarm.getEgg().isOf(Items.EGG) && Time.CONFIG.allowsCompactFarmEggs()) {
                    compactFarm.getEgg().decrement(1);
                    compactFarm.setCurrentlyEgg(true);
                }
                CompactFarmBlockEntity.markDirty(world,pos,state);
            }
        } // -> if server world
    }

    //----------------------------------------------------------
    // Miscellaneous methods
    //----------------------------------------------------------

    // getter/setter methods are protected because the extraction process shouldn't be disturbed from the outside

    protected Counter getTickCounter() {
        return tickCounter;
    }

    public void resetTickCounter() {
        tickCounter.reset();
    }

    protected ItemStack getCurrentShard() {
        return currentShard;
    }

    // TODO doc reference will not be saved
    protected void setCurrentShard(@NotNull ItemStack currentShard) {
        Utils.requireNonNull(currentShard,"currentShard");
        if (currentShard.getCount() > BoundShardOnlySlot.MAX_AMOUNT)
            Time.LOGGER.warn("Counts greater than {} will be reduced to {}.", BoundShardOnlySlot.MAX_AMOUNT, BoundShardOnlySlot.MAX_AMOUNT);
        this.currentShard = new ItemStack(currentShard.getItem(), Math.min(currentShard.getCount(), BoundShardOnlySlot.MAX_AMOUNT));
    }

    public boolean isCurrentlyEgg() {
        return currentlyEgg;
    }

    public void setCurrentlyEgg(boolean currentlyEgg) {
        this.currentlyEgg = currentlyEgg;
    }

    public ItemStack getShardToBeUsedUp() {
        if (inventory.get(0) == null)
            throw new IllegalStateException("Slot for shards to be used can be empty, but not null!");
        return inventory.get(0);
    }

    public ItemStack getEgg() {
        if (inventory.get(1) == null)
            throw new IllegalStateException("Egg slot can be empty, but not null!");
        return inventory.get(1);
    }

    public boolean eggSlotUsed() {
        return !getEgg().isEmpty();
    }

    public void ejectEggSlot(@Nullable PlayerEntity player) {
        if (player == null || !eggSlotUsed())
            return;
        player.getInventory().offerOrDrop(inventory.get(1));
        inventory.set(1, ItemStack.EMPTY);
    }

    protected ItemStack getFarmingShard() {
        if (inventory.get(2) == null)
            throw new IllegalStateException("Farming shard slot can be empty, but not null!");
        return inventory.get(2);
    }

    public boolean isFarming() {
        return !(tickCounter.isZero() && currentShard.isEmpty());
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
        if (world == null || world.getBlockEntity(this.pos) != this) {
            return false;
        }
        // reachable from 8 blocks afar (8*8=64)
        return player.squaredDistanceTo((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (stack == null)
            return false;
        // if changed, also change the slot classes for the ScreenHandler
        if (slot == 0)
            return stack.isOf(Time.AMETHYME_SHARD) || stack.isEmpty();
        if (slot == 1) // emptying the egg slot is always allowed
            return (stack.isOf(Items.EGG) && Time.CONFIG.allowsCompactFarmEggs()) || stack.isEmpty();
        if (slot == 2)
            return stack.isIn(Time.BOUND_AMETHYME_SHARDS) || stack.isEmpty();
        return 0 <= slot && slot < INVENTORY_SIZE;
    }

    //----------------------------------------------------------
    // NBT I/O
    //----------------------------------------------------------

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        // read inventory
        Inventories.readNbt(nbt, inventory);
        // read currently egg
        currentlyEgg = nbt.getBoolean(CURRENTLY_EGG_KEY);
        // read current shard
        currentShard = ItemStack.fromNbt(nbt.getCompound(CURRENT_SHARD_KEY));
        // read tick counter
        int counter = nbt.getInt(TICK_COUNTER_KEY);
        if (Counter.isCounterValue(counter))
            tickCounter.setValue(counter);
        else {
            Time.LOGGER.warn("Illegal value {} for counter detected, will default to 0 instead.", counter);
            tickCounter.setValue(0);
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        // write tick counter
        nbt.putInt(TICK_COUNTER_KEY, tickCounter.getValue());
        // write current shard
        nbt.put(CURRENT_SHARD_KEY, currentShard.writeNbt(new NbtCompound()));
        // write currently egg
        nbt.putBoolean(CURRENTLY_EGG_KEY, currentlyEgg);
        // write inventory
        Inventories.writeNbt(nbt, inventory);
        return super.writeNbt(nbt);
    }
}
