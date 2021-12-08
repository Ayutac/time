package org.abos.fabricmc.time.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.abos.fabricmc.time.Time;
import org.abos.fabricmc.time.blocks.TimeExtractorBlockEntity;
import org.abos.fabricmc.time.recipes.TimeExtractorRecipe;

/**
 * {@link ScreenHandler} for the {@link TimeExtractorBlockEntity}.
 *
 * Mostly copied from <a href="https://fabricmc.net/wiki/tutorial:screenhandler">this</a>
 * and <a href="https://fabricmc.net/wiki/tutorial:propertydelegates">this tutorial</a>.
 */
public class TimeExtractorScreenHandler extends ScreenHandler {

    private final Inventory inventory;

    private final PropertyDelegate propertyDelegate;

    /*
     * This constructor gets called on the client when the server wants it to open the screenHandler,
     * The client will call the other constructor with an empty Inventory and the screenHandler will automatically
     * sync this empty inventory with the inventory on the server.
     */
    public TimeExtractorScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(TimeExtractorBlockEntity.INVENTORY_SIZE),new ArrayPropertyDelegate(TimeExtractorBlockEntity.PROPERTY_DELEGATE_SIZE));
    }

    /*
     * This constructor gets called from the BlockEntity on the server without calling the other constructor first,
     * the server knows the inventory of the container and can therefore directly provide it as an argument.
     * This inventory will then be synced to the client.
     */
    public TimeExtractorScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(Time.TIME_EXTRACTOR_SCREEN_HANDLER, syncId);
        checkSize(inventory, TimeExtractorBlockEntity.INVENTORY_SIZE);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        // some inventories do custom logic when a player opens it.
        inventory.onOpen(playerInventory.player);
        // register properties so they are synced
        if (this.propertyDelegate != null)
            addProperties(this.propertyDelegate);
        else
            Time.LOGGER.warn("Property delegate for {} missing, this shouldn't happen. Animations will not occur.",TimeExtractorScreenHandler.class.getName());
        // This will not render the background of the slots however, this is the Screens job
        int m;
        int l;
        // extractor inventory
        this.addSlot(new Slot(inventory, 0, 80, 17)); // input slot
        this.addSlot(new OutputOnlySlot(inventory, 1, 80, 55)); // output slot
        // player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, l * 18 + 8, m * 18 + 84));
            }
        }
        // player hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, m * 18 + 8, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    // Shift + Player Inv Slot
    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    public boolean isExtracting() {
        return this.propertyDelegate.get(1) > 0; // remaining TU
    }

    public int getExtractionProgress() {
        int tickCounter = this.propertyDelegate.get(0);
        int remainingTU = this.propertyDelegate.get(1);
        int potentialTU = this.propertyDelegate.get(2);
        int ticksPerTU = TimeExtractorBlockEntity.getTicksNeeded();
        int currentProgress = (potentialTU-remainingTU)*ticksPerTU + tickCounter;
        int pixelHeight = 18;
        return currentProgress * pixelHeight / (potentialTU*ticksPerTU);
    }
}
