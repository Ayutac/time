package org.abos.fabricmc.time;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.StructureFeature;
import org.abos.fabricmc.time.mixin.VillagerEntityEnvoker;
import org.jetbrains.annotations.Contract;

import java.util.LinkedList;
import java.util.List;

public final class Utils {

    private Utils() {
        assert false; // should never be called
    }

    public static void requireNonNull(Object param, String name) {
        if (param == null)
            throw new NullPointerException(name + " must not be null!");
    }

    public static boolean isStructure(ServerWorld world, ChunkSectionPos pos, StructureFeature<?> feature) {
        return world.getStructures(pos, feature).findAny().isPresent();
    }

    public static boolean standsOn(World world, Entity entity, Block block) {
        return world.getBlockState(entity.getBlockPos().down()).isOf(block);
    }

    public static <T extends MobEntity> void convertTo(ServerWorld world, MobEntity oldEntity, EntityType<T> newEntityType) {
        if (oldEntity instanceof VillagerEntity villager && !villager.isBaby() && newEntityType == EntityType.ZOMBIE_VILLAGER) {
            convertVillagerToZombieVillager(world, villager);
            return;
        }
        // mainly copied from VillagerEntity#onStruckByLightning
        T entity = newEntityType.create(world);
        entity.refreshPositionAndAngles(oldEntity.getX(), oldEntity.getY(), oldEntity.getZ(), oldEntity.getYaw(), oldEntity.getPitch());
        entity.initialize(world, world.getLocalDifficulty(entity.getBlockPos()), SpawnReason.CONVERSION, null, null);
        entity.setAiDisabled(oldEntity.isAiDisabled());
        if (oldEntity.hasCustomName()) {
            entity.setCustomName(oldEntity.getCustomName());
            entity.setCustomNameVisible(oldEntity.isCustomNameVisible());
        }
        entity.setPersistent();
        world.spawnEntityAndPassengers(entity);
        if (oldEntity instanceof VillagerEntity villager)
            ((VillagerEntityEnvoker)villager).invokeReleaseAllTickets();
        oldEntity.discard();
    }

    public static void convertVillagerToZombieVillager(ServerWorld world, VillagerEntity villager) {
        // mainly copied from ZombieEntity#onKilledOther
        ZombieVillagerEntity zombieVillagerEntity = villager.convertTo(EntityType.ZOMBIE_VILLAGER, false);
        zombieVillagerEntity.initialize(world, world.getLocalDifficulty(zombieVillagerEntity.getBlockPos()), SpawnReason.CONVERSION, new ZombieEntity.ZombieData(false, false), null);
        zombieVillagerEntity.setVillagerData(villager.getVillagerData());
        zombieVillagerEntity.setGossipData(villager.getGossip().serialize(NbtOps.INSTANCE).getValue());
        zombieVillagerEntity.setOfferData(villager.getOffers().toNbt());
        zombieVillagerEntity.setXp(villager.getExperience());
    }

    //----------------------------------------------------------
    // loot methods
    //----------------------------------------------------------

    // TODO doc null returned if not found
    @Contract("null,_,_->fail; _,null,_->fail")
    public static ItemStack getFirstNonFullSlotOf(ItemStack item, Inventory inventory, boolean checkValid) {
        requireNonNull(item,"item");
        requireNonNull(inventory,"inventory");
        ItemStack slot;
        for (int index = 0; index < inventory.size(); index++) {
            slot = inventory.getStack(index);
            if (slot.isOf(item.getItem()) && slot.getCount() < slot.getMaxCount()  && (!checkValid || (inventory.isValid(index,item))))
                return slot;
        }
        return null;
    }

    // TODO doc null returned if not found
    @Contract("null,_->fail; _,null->fail")
    public static ItemStack getFirstNonFullSlotOf(ItemStack item, Inventory inventory) {
        return getFirstNonFullSlotOf(item, inventory, false);
    }

    // TODO doc -1 returned if not found
    @Contract("null,_,_->fail")
    public static int getFirstFreeSlotOf(Inventory inventory, ItemStack whatFor, boolean checkValid) {
        requireNonNull(inventory,"inventory");
        for (int index = 0; index < inventory.size(); index++) {
            if (inventory.getStack(index).isEmpty() && (!checkValid || (whatFor != null && inventory.isValid(index,whatFor))))
                return index;
        }
        return -1;
    }

    // TODO doc -1 returned if not found
    @Contract("null->fail")
    public static int getFirstFreeSlotOf(Inventory inventory) {
        return getFirstFreeSlotOf(inventory, null, false);
    }

    public static boolean addToInventory(ItemStack stack, Inventory inventory, boolean checkValid) {
        ItemStack inventorySlot;
        int inventoryIndex, exchange;
        // find non-full stack
        if ((inventorySlot = getFirstNonFullSlotOf(stack,inventory,checkValid)) != null) {
            exchange = Math.min(inventorySlot.getMaxCount()-inventorySlot.getCount(), stack.getCount());
            inventorySlot.increment(exchange);
            stack.decrement(exchange);
            return true;
        }
        // find free stack
        if ((inventoryIndex = getFirstFreeSlotOf(inventory, stack, checkValid)) != -1) {
            inventorySlot = inventory.getStack(inventoryIndex);
            exchange = Math.min(inventorySlot.getMaxCount(), stack.getCount());
            inventory.setStack(inventoryIndex, stack.copy()); // without this damage and enchantments are lost
            inventory.getStack(inventoryIndex).setCount(exchange);
            stack.decrement(exchange);
            return true;
        }
        return false;
    }

    @Contract("null,_,_->fail; _,null,_->fail; _,_,null->fail")
    public static List<ItemStack> fillWithLoot(ServerWorld world, BlockPos pos, LootTable table) {
        requireNonNull(world,"world");
        requireNonNull(pos,"pos");
        requireNonNull(table,"table");
        LootContext context = new LootContext.Builder(world).parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos)).random(world.getRandom()).build(LootContextTypes.COMMAND);
        List<ItemStack> loot = table.generateLoot(context);
        if (!(world.getBlockEntity(pos) instanceof Inventory inventory))
            return loot;
        List<ItemStack> lootRemainder = new LinkedList<>();
        ItemStack lootEntry;
        while (!loot.isEmpty()) {
            // skip empty entries
            lootEntry = loot.get(0);
            if (lootEntry == null || lootEntry.isEmpty()) {
                loot.remove(0);
                continue;
            }
            if (addToInventory(lootEntry, inventory, true)) {
                if (lootEntry.isEmpty())
                    loot.remove(0);
                continue;
            }
            // nothing changed, return to remainder
            lootRemainder.add(lootEntry);
            loot.remove(0);
        }
        return lootRemainder;
    }

}
