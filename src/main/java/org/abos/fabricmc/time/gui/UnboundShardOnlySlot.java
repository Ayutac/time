package org.abos.fabricmc.time.gui;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.abos.fabricmc.time.Time;

public class UnboundShardOnlySlot extends ShardOnlySlot  {

    public UnboundShardOnlySlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    // if changed, also change valid method of entities using this slot
    @Override
    public boolean canInsert(ItemStack stack) {
        if (!super.canInsert(stack)) // ensures stack != null
            return false;
        return stack.isOf(Time.AMETHYME_SHARD);
    }
}
