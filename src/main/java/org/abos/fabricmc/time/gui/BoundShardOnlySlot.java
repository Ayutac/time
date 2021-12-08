package org.abos.fabricmc.time.gui;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.abos.fabricmc.time.Time;

public class BoundShardOnlySlot extends ShardOnlySlot {

    public BoundShardOnlySlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        if (!super.canInsert(stack)) // ensures stack != null
            return false;
        return !stack.isOf(Time.AMETHYME_SHARD);
    }

    @Override
    public int getMaxItemCount() {
        return 3;
    }
}
