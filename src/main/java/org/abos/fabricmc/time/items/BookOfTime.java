package org.abos.fabricmc.time.items;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.abos.fabricmc.time.Time;
import org.abos.fabricmc.time.Utils;
import vazkii.patchouli.api.PatchouliAPI;

public final class BookOfTime extends BookItem {

    public BookOfTime() {
        super(Time.getTimeItemSettings());
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        Utils.requireNonNull(world,"world");
        Utils.requireNonNull(user,"player");
        Utils.requireNonNull(hand,"hand");
        if (user instanceof ServerPlayerEntity player) {
            if (FabricLoader.getInstance().isModLoaded(Time.PATCHOULI_ID)) {
                PatchouliAPI.get().openBookGUI(player, Registry.ITEM.getId(this));
            }
            else {
                // if key is changed, change language asset as well
                player.sendMessage(new TranslatableText("time.patchouli.missing"), false);
            }
            return TypedActionResult.success(user.getStackInHand(hand));
        }
        return super.use(world, user, hand); // which does pass
    }
}
