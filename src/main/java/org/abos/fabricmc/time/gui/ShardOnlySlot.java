package org.abos.fabricmc.time.gui;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.abos.fabricmc.time.Time;

public class ShardOnlySlot extends Slot {

    public ShardOnlySlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    // if changed, also change valid method of entities using this slot
    @Override
    public boolean canInsert(ItemStack stack) {
        if (stack == null)
            return false;
        return stack.isIn(Time.AMETHYME_SHARDS);
    }
}
