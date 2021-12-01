package org.abos.fabricmc.time;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.StructureFeature;
import org.abos.fabricmc.time.mixin.VillagerEntityEnvoker;

public final class Utils {

    private Utils() {
        assert false; // should never be called
    }

    public static void requireNonNull(Object param, String name) {
        if (param == null)
            throw new NullPointerException(name + " must not be null!");
    }

    public static boolean isStructure(ServerWorld world, ChunkSectionPos pos, StructureFeature<?> feature) {
        return world.getStructures(pos, StructureFeature.FORTRESS).findAny().isPresent();
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

}
