package org.abos.fabricmc.time.gui;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.abos.fabricmc.time.Time;

public class BoundShardOnlySlot extends ShardOnlySlot {

    public static final int MAX_AMOUNT = 3;

    public BoundShardOnlySlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    // if changed, also change valid method of entities using this slot
    @Override
    public boolean canInsert(ItemStack stack) {
        if (!super.canInsert(stack)) // ensures stack != null
            return false;
        return stack.isIn(Time.BOUND_AMETHYME_SHARDS);
    }

    @Override
    public int getMaxItemCount() {
        return MAX_AMOUNT;
    }
}
