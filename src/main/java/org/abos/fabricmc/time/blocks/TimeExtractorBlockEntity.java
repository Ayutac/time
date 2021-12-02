package org.abos.fabricmc.time.blocks;

import dev.onyxstudios.cca.api.v3.component.ComponentProvider;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.abos.fabricmc.time.Time;
import org.abos.fabricmc.time.Utils;
import org.abos.fabricmc.time.components.Counter;
import org.abos.fabricmc.time.components.CounterComponent;
import org.abos.fabricmc.time.components.CounterImpl;
import org.abos.fabricmc.time.components.TimeComponents;
import org.abos.fabricmc.time.gui.TimeExtractorScreenHandler;
import org.abos.fabricmc.time.recipes.TimeExtractorRecipe;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// if the inventory parts of this class are changed, make sure to change org.abos.fabricmc.time.recipes.TimeExtractorRecipe accordingly
public class TimeExtractorBlockEntity extends LockableContainerBlockEntity
        implements DefaultedInventory, SidedInventory, RecipeUnlocker, RecipeInputProvider {

    public static final String CONTAINER_NAME = "container." + Time.TIME_EXTRACTOR_STR;

    // NBT keys
    public static final String TICK_COUNTER_KEY = "tickCounter";
    public static final String REMAINING_EXTRACTION_TU_KEY = "remainingExtractionTU";
    public static final String CURRENT_EXTRACTION_FORMULA_KEY = "currentExtractionFormula";
    public static final String CURRENT_EXTRACTION_INPUT_KEY = "currentExtractionFormula";
    public static final String CURRENT_EXTRACTION_OUTPUT_KEY = "currentExtractionFormula";
    public static final String POTENTIAL_TU_KEY = "potentialTU";
    public static final String RECIPES_USED_KEY = "recipesUsed";

    public static final int INVENTORY_SIZE = 2;
    public static final int PROPERTY_DELEGATE_SIZE = 3;

    public static final String ONLY_OUTPUT_ITEM_LOST = "This shouldn't happen! Extraction result is lost, but the TUs have been extracted successfully.";

    public static final String ONLY_OUTPUT_ITEM_LOST_PART = "This shouldn't happen! Extraction result may be lost in parts or completely, but the TUs have been extracted successfully.";

    // slot orientations: 0 is the item input (up), 1 is the item output (down)
    private static final int[] TOP_SLOTS = new int[]{0};
    private static final int[] SIDE_SLOTS = new int[]{0};
    private static final int[] BOTTOM_SLOTS = new int[]{1};

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

    protected Counter remainingExtractionTU = new CounterImpl();

    protected int potentialTU;

    protected DefaultedList<ItemStack> currentExtraction = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);

    /**
     * Counts how often individual recipes have been used.
     */
    private final Object2IntOpenHashMap<Identifier> recipesUsed = new Object2IntOpenHashMap<>();

    //----------------------------------------------------------
    // the property delegate field needed for the animation
    //----------------------------------------------------------

    private final PropertyDelegate propertyDelegate = new PropertyDelegate(){

        @Override
        public int get(int index) {
            switch (index) {
                case 0: {
                    return TimeExtractorBlockEntity.this.tickCounter.getValue();
                }
                case 1: {
                    return TimeExtractorBlockEntity.this.remainingExtractionTU.getValue();
                }
                case 2: {
                    return TimeExtractorBlockEntity.this.potentialTU;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    TimeExtractorBlockEntity.this.tickCounter.setValue(value);
                    break;
                }
                case 1: {
                    TimeExtractorBlockEntity.this.remainingExtractionTU.setValue(value);
                    break;
                }
                case 2: {
                    TimeExtractorBlockEntity.this.potentialTU = value;
                    break;
                }
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

    public TimeExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(Time.TIME_EXTRACTOR_ENTITY, pos, state);
    }

    //----------------------------------------------------------
    // Tick method
    //----------------------------------------------------------

    public static void tick(World world, BlockPos pos, BlockState state, TimeExtractorBlockEntity extractor) {
        if (world instanceof ServerWorld) {
            if (extractor.isExtracting()) {
                // do one tick of extracting
                extractor.getTickCounter().increment();
                // extract 1 TU maybe
                ticksNeeded = world.getGameRules().getInt(Time.CONFIG.getTicksPerExtractedTURule());
                if (extractor.getTickCounter().getValue() >= ticksNeeded) {
                    ComponentProvider provider = ComponentProvider.fromWorld(world);
                    CounterComponent passedTime = TimeComponents.PASSED_TIME.get(provider);
                    passedTime.increment(world.getGameRules().getInt(Time.CONFIG.getTUIncreaseRule()));
                    extractor.getRemainingExtractionTU().decrement();
                    TimeComponents.PASSED_TIME.sync(provider);
                    extractor.getTickCounter().decrement(ticksNeeded);
                }
                // if last TU has been extracted, add output item
                if (!extractor.isExtracting()) {
                    ItemStack outputStack = extractor.getExtractedMaterial();
                    ItemStack resultStack = extractor.getCurrentExtractedMaterial();
                    if (resultStack.isEmpty()) {
                        Time.LOGGER.warn("Extraction output vanished! {}", ONLY_OUTPUT_ITEM_LOST);
                    } else if (!outputStack.isEmpty() && !outputStack.isItemEqualIgnoreDamage(resultStack)){
                        Time.LOGGER.warn("Output slot is filled with {} instead of {}! {}",
                                outputStack.getTranslationKey(), resultStack.getTranslationKey(), ONLY_OUTPUT_ITEM_LOST);
                    } else if (outputStack.isEmpty()) {
                        extractor.setExtractedMaterial(resultStack.copy());
                        extractor.resetCurrentExtractionProcess();
                        extractor.markDirty(world, pos, state);
                    } else { // items in the output slot of the same type as recipe output
                        int maxCount = Math.min(extractor.getMaxCountPerStack(), outputStack.getMaxCount());
                        if (outputStack.getCount() + resultStack.getCount() > maxCount) {
                            Time.LOGGER.warn("Output slot is too full with {}! {}",
                                    outputStack.getTranslationKey(), ONLY_OUTPUT_ITEM_LOST_PART);
                            outputStack.setCount(maxCount);
                        }
                        else
                            outputStack.increment(resultStack.getCount());
                        extractor.resetCurrentExtractionProcess();
                        extractor.markDirty(world, pos, state);
                    }
                } // -> if last TU has been extracted
            } // -> if extraction was in progress

            // start extracting maybe
            if (!extractor.isExtracting() && !extractor.getExtractionMaterial().isEmpty()) {
                Recipe<?> r = getRecipe(world, extractor.getRecipeType(), extractor);
                if (r instanceof TimeExtractorRecipe recipe) {
                    ItemStack outputStack = extractor.getExtractedMaterial();
                    // check if the output has space and if so, start extracting
                    if (outputStack.isEmpty() || (outputStack.isItemEqualIgnoreDamage(recipe.getOutput()) &&
                            outputStack.getCount() + recipe.getOutput().getCount()
                                    <= Math.min(extractor.getMaxCountPerStack(), outputStack.getMaxCount()))) {
                        if (extractor.getExtractionMaterial().isEmpty()) { // shouldn't happen
                            Time.LOGGER.warn("Despite finding the recipe {} the input slot is empty?! Extraction not started.", recipe.getId());
                        }
                        else {
                            extractor.startExtractionProcess(extractor.getExtractionMaterial(),recipe.getOutput(), recipe.getExtractedTU());
                            extractor.setLastRecipe(recipe);
                            extractor.markDirty(world, pos, state);
                        }
                    }
                }
                else if (r != null) { // shouldn't happen
                    Time.LOGGER.warn("Found a wrongly registered recipe {}, will be ignored.", r.getId());
                }
            }
        }
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

    protected Counter getRemainingExtractionTU() {
        return remainingExtractionTU;
    }

    @NotNull
    protected ItemStack getExtractionMaterial() {
        if (inventory.get(0) == null)
            throw new IllegalStateException("Input can be empty, but not null!");
        return inventory.get(0);
    }

    // TODO doc
    protected void startExtractionProcess(ItemStack concreteInput, ItemStack recipeOutput, int expectedTU) {
        Utils.requireNonNull(concreteInput, "concreteInput");
        Utils.requireNonNull(recipeOutput, "recipeOutput");
        resetCurrentExtractionProcess();
        currentExtraction.set(0, new ItemStack(concreteInput.getItem(),1));
        concreteInput.decrement(1);
        currentExtraction.set(1, recipeOutput);
        remainingExtractionTU.setValue(expectedTU); // throws IAE
        potentialTU = expectedTU;
    }

    protected ItemStack getCurrentExtractionMaterial() {
        return currentExtraction.get(0);
    }

    protected ItemStack getCurrentExtractedMaterial() {
        return currentExtraction.get(1);
    }

    protected void resetCurrentExtractionProcess() {
        tickCounter.reset();
        remainingExtractionTU.reset();
        currentExtraction.set(0, ItemStack.EMPTY);
        currentExtraction.set(1, ItemStack.EMPTY);
    }

    @NotNull
    protected ItemStack getExtractedMaterial() {
        if (inventory.get(1) == null)
            throw new IllegalStateException("Output can be empty, but not null!");
        return inventory.get(1);
    }

    protected void setExtractedMaterial(ItemStack result) {
        inventory.set(1, result);
    }

    public RecipeType<? extends TimeExtractorRecipe>getRecipeType() {
        return TimeExtractorRecipe.Type.INSTANCE;
    }

    /*
     * It's possible we need to give <code>Inventory inventory</code> instead, in that case
     * also change the generic in {@link TimeExtractorRecipe}, correct the corresponding methods
     * (especially {@link TimeExtractorRecipe#matches(...)}). Hopefully that will fix any issues that might occur.
     */
    @Contract(pure = true)
    public static Recipe<?> getRecipe(World world, RecipeType<? extends TimeExtractorRecipe> recipeType, TimeExtractorBlockEntity inventory) {
        return world.getRecipeManager().getFirstMatch(recipeType, inventory, world).orElse(null);
    }

    public boolean isExtracting() {
        return !remainingExtractionTU.isZero();
    }

    //----------------------------------------------------------
    // RecipeUnlocker methods
    // (copied from AbstractFurnaceBlockEntity)
    //----------------------------------------------------------

    @Override
    public void setLastRecipe(@Nullable Recipe<?> recipe) {
        if (recipe != null) {
            Identifier identifier = recipe.getId();
            recipesUsed.addTo(identifier, 1);
        }
    }

    @Nullable
    @Override
    public Recipe<?> getLastRecipe() {
        return null;
    }

    //----------------------------------------------------------
    // RecipeInputProvider method
    // (copied from AbstractFurnaceBlockEntity)
    //----------------------------------------------------------

    @Override
    public void provideRecipeInputs(RecipeMatcher finder) {
        for (ItemStack itemStack : inventory) {
            finder.addInput(itemStack);
        }
    }

    //----------------------------------------------------------
    // SidedInventory methods
    // (mostly copied from AbstractFurnaceBlockEntity)
    //----------------------------------------------------------

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) {
            return BOTTOM_SLOTS;
        }
        if (side == Direction.UP) {
            return TOP_SLOTS;
        }
        return SIDE_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.isValid(slot, stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    //----------------------------------------------------------
    // other Inventory methods
    // (mostly copied from AbstractFurnaceBlockEntity or
    //  defaulting to DefaultInventory)
    //----------------------------------------------------------

    /**
     * Checks if an item is valid for extraction or be put in the output slot.
     * Right now, anything can be put in for extraction (no guarantee it works),
     * but nothing can be put into the output slot.
     * @param slot the slot to check
     * @param stack the stack to check
     * @return {@code true} if the slot is {@code 0} and the item is valid.
     */
    @Override
    // if changed, don't forget the doc above!
    public boolean isValid(int slot, ItemStack stack) {
        if (slot == 0) // we can put anything in slot 0, but maybe nothing happens then
            return true;
        return false; // don't put anything in the output slot 1 and the other slots are illegal anyway
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText(CONTAINER_NAME);
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new TimeExtractorScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
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
        // read inventory
        inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbt, inventory);
        // read current extraction formula
        currentExtraction = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        NbtCompound currentFormula = nbt.getCompound(CURRENT_EXTRACTION_FORMULA_KEY);
        currentExtraction.set(0,ItemStack.fromNbt(currentFormula.getCompound(CURRENT_EXTRACTION_INPUT_KEY)));
        currentExtraction.set(1,ItemStack.fromNbt(currentFormula.getCompound(CURRENT_EXTRACTION_OUTPUT_KEY)));
        // read tick counter
        int tickCounter = nbt.getInt(TICK_COUNTER_KEY);
        if (Counter.isCounterValue(tickCounter))
            this.tickCounter.setValue(tickCounter);
        else {
            Time.LOGGER.warn("Illegal value {} for tick counter detected, will default to 0 instead.", tickCounter);
            this.tickCounter.setValue(0);
        }
        // read TU that remain to be extracted
        int remainingExtractionTU = nbt.getInt(REMAINING_EXTRACTION_TU_KEY);
        if (Counter.isCounterValue(remainingExtractionTU))
            this.remainingExtractionTU.setValue(remainingExtractionTU);
        else {
            Time.LOGGER.warn("Illegal value {} for remaining TU counter detected, will default to 0 instead.", remainingExtractionTU);
            this.remainingExtractionTU.setValue(0);
        }
        // read potential TU
        int potentialTU = nbt.getInt(POTENTIAL_TU_KEY);
        if (potentialTU >= this.remainingExtractionTU.getValue())
            this.potentialTU = potentialTU;
        else
        {
            Time.LOGGER.warn("Illegal value {} for potential detected, will default to {} instead.", potentialTU, this.remainingExtractionTU.getValue());
            this.potentialTU = this.remainingExtractionTU.getValue();
        }
        // read used recipes
        NbtCompound nbtCompound = nbt.getCompound(RECIPES_USED_KEY);
        for (String string : nbtCompound.getKeys()) {
            this.recipesUsed.put(new Identifier(string), nbtCompound.getInt(string));
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        // write inventory
        Inventories.writeNbt(nbt, this.inventory);
        // write current extraction formula
        NbtCompound currentFormula = new NbtCompound();
        currentFormula.put(CURRENT_EXTRACTION_INPUT_KEY, getCurrentExtractionMaterial().writeNbt(new NbtCompound()));
        currentFormula.put(CURRENT_EXTRACTION_OUTPUT_KEY, getCurrentExtractedMaterial().writeNbt(new NbtCompound()));
        nbt.put(CURRENT_EXTRACTION_FORMULA_KEY, currentFormula);
        // write tick counter
        nbt.putInt(TICK_COUNTER_KEY, tickCounter.getValue());
        // write TU that remain to be extracted
        nbt.putInt(REMAINING_EXTRACTION_TU_KEY, remainingExtractionTU.getValue());
        // write potential TU
        nbt.putInt(POTENTIAL_TU_KEY, potentialTU);
        // write used recipes
        NbtCompound nbtCompound = new NbtCompound();
        this.recipesUsed.forEach((identifier, integer) -> nbtCompound.putInt(identifier.toString(), integer));
        nbt.put(RECIPES_USED_KEY, nbtCompound);
        return super.writeNbt(nbt);
    }
}
