package org.abos.fabricmc.time.items;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.GourdBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.abos.fabricmc.time.Time;
import org.abos.fabricmc.time.Utils;
import org.abos.fabricmc.time.gui.BoundShardOnlySlot;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class AmethymeShard extends Item {

    // if changed, change the assets accordingly
    // don't forget to look up use of this variable for even more changes...
    public static final String ID = "amethyme_shard";

    /*
     * How to add new shard:
     * 1. Add public static variable.
     * 2. Add to static register method.
     * 3. Add to language assets.
     * 4. Add to tag asset.
     * 5. Add model.
     * 6. Add texture.
     * 7. Add conversions via static shardFor methods.
     */

    public static final AmethymeShard BEETROOT = new AmethymeShard("compact_farm/beetroot");
    public static final AmethymeShard BLAZE = new AmethymeShard("compact_farm/blaze");
    public static final AmethymeShard CARROT = new AmethymeShard("compact_farm/carrot");
    public static final AmethymeShard CAVE_SPIDER = new AmethymeShard("compact_farm/cave_spider");
    public static final AmethymeShard CHICKEN = new AmethymeShard("compact_farm/chicken");
    public static final AmethymeShard COW = new AmethymeShard("compact_farm/cow");
    public static final AmethymeShard CREEPER = new AmethymeShard("compact_farm/creeper");
    public static final AmethymeShard ENDERMAN = new AmethymeShard("compact_farm/enderman");
    public static final AmethymeShard GHAST = new AmethymeShard("compact_farm/ghast");
    public static final AmethymeShard IRON_GOLEM = new AmethymeShard("compact_farm/iron_golem");
    public static final AmethymeShard MAGMA_SLIME = new AmethymeShard("compact_farm/magma_slime");
    public static final AmethymeShard MELON = new AmethymeShard("compact_farm/melon");
    public static final AmethymeShard OVERWORLD_HOSTILES = new AmethymeShard("compact_farm/overworld_hostiles");
    public static final AmethymeShard PIG = new AmethymeShard("compact_farm/pig");
    public static final AmethymeShard POTATO = new AmethymeShard("compact_farm/potato");
    public static final AmethymeShard PUMPKIN = new AmethymeShard("compact_farm/pumpkin");
    public static final AmethymeShard SHEEP = new AmethymeShard("compact_farm/sheep");
    public static final AmethymeShard SILVERFISH = new AmethymeShard("compact_farm/silverfish");
    public static final AmethymeShard SKELETON = new AmethymeShard("compact_farm/skeleton");
    public static final AmethymeShard SLIME = new AmethymeShard("compact_farm/slime");
    public static final AmethymeShard SPIDER = new AmethymeShard("compact_farm/spider");
    public static final AmethymeShard WHEAT = new AmethymeShard("compact_farm/wheat");
    public static final AmethymeShard WITCH = new AmethymeShard("compact_farm/witch");
    public static final AmethymeShard ZOMBIE = new AmethymeShard("compact_farm/zombie");
    public static final AmethymeShard ZOMBIFIED_PIGLIN = new AmethymeShard("compact_farm/zombified_piglin");

    public static void register() {
        // if any name is changed, change the variable name too, as well as the corresponding assets
        Registry.register(Registry.ITEM, new Identifier(Time.MOD_ID, ID+"s/beetroot"), BEETROOT);
        Registry.register(Registry.ITEM, new Identifier(Time.MOD_ID, ID+"s/blaze"), BLAZE);
        Registry.register(Registry.ITEM, new Identifier(Time.MOD_ID, ID+"s/carrot"), CARROT);
        Registry.register(Registry.ITEM, new Identifier(Time.MOD_ID, ID+"s/cave_spider"), CAVE_SPIDER);
        Registry.register(Registry.ITEM, new Identifier(Time.MOD_ID, ID+"s/chicken"), CHICKEN);
        Registry.register(Registry.ITEM, new Identifier(Time.MOD_ID, ID+"s/cow"), COW);
        Registry.register(Registry.ITEM, new Identifier(Time.MOD_ID, ID+"s/creeper"), CREEPER);
        Registry.register(Registry.ITEM, new Identifier(Time.MOD_ID, ID+"s/enderman"), ENDERMAN);
        Registry.register(Registry.ITEM, new Identifier(Time.MOD_ID, ID+"s/ghast"), GHAST);
        Registry.register(Registry.ITEM, new Identifier(Time.MOD_ID, ID+"s/iron_golem"), IRON_GOLEM);
        Registry.register(Registry.ITEM, new Identifier(Time.MOD_ID, ID+"s/magma_slime"), MAGMA_SLIME);
        Registry.register(Registry.ITEM, new Identifier(Time.MOD_ID, ID+"s/melon"), MELON);
        Registry.register(Registry.ITEM, new Identifier(Time.MOD_ID, ID+"s/overworld_hostiles"), OVERWORLD_HOSTILES);
        Registry.register(Registry.ITEM, new Identifier(Time.MOD_ID, ID+"s/pig"), PIG);
        Registry.register(Registry.ITEM, new Identifier(Time.MOD_ID, ID+"s/potato"), POTATO);
        Registry.register(Registry.ITEM, new Identifier(Time.MOD_ID, ID+"s/pumpkin"), PUMPKIN);
        Registry.register(Registry.ITEM, new Identifier(Time.MOD_ID, ID+"s/sheep"), SHEEP);
        Registry.register(Registry.ITEM, new Identifier(Time.MOD_ID, ID+"s/silverfish"), SILVERFISH);
        Registry.register(Registry.ITEM, new Identifier(Time.MOD_ID, ID+"s/skeleton"), SKELETON);
        Registry.register(Registry.ITEM, new Identifier(Time.MOD_ID, ID+"s/slime"), SLIME);
        Registry.register(Registry.ITEM, new Identifier(Time.MOD_ID, ID+"s/spider"), SPIDER);
        Registry.register(Registry.ITEM, new Identifier(Time.MOD_ID, ID+"s/wheat"), WHEAT);
        Registry.register(Registry.ITEM, new Identifier(Time.MOD_ID, ID+"s/witch"), WITCH);
        Registry.register(Registry.ITEM, new Identifier(Time.MOD_ID, ID+"s/zombie"), ZOMBIE);
        Registry.register(Registry.ITEM, new Identifier(Time.MOD_ID, ID+"s/zombified_piglin"), ZOMBIFIED_PIGLIN);
    }

    public String lootPath;

    /**
     * Creates an amethyme shard item with the specified loot table path
     * @param lootPath the relative path to the loot table
     */
    public AmethymeShard(@Nullable String lootPath) {
        super(Time.getTimeItemSettings());
        this.lootPath = lootPath;
    }

    /**
     * Creates an amethyme shard item.
     */
    public AmethymeShard() {
        this(null);
    }

    public boolean isUnbound() {
        return this == Time.AMETHYME_SHARD;
    }

    public String getLootPath() {
        return lootPath;
    }

    public String getLevelledLootPath(int level) {
        if (lootPath == null)
            return Integer.toString(level);
        return lootPath+level;
    }

    public boolean hasLootTable() {
        return lootPath != null;
    }

    @Nullable
    public LootTable getLootTable(@Nullable World world, int level) {
        if (world == null || world.getServer() == null || lootPath == null || level < 1 || level > BoundShardOnlySlot.MAX_AMOUNT)
            return null;
        return world.getServer().getLootManager().getTable(new Identifier(Time.MOD_ID,lootPath+level));
    }

    @Nullable
    public LootTable getLootTable(@Nullable World world) {
        return getLootTable(world, 1);
    }

    public static @Nullable ItemStack getUnboundStack(@NotNull PlayerEntity player) {
        Utils.requireNonNull(player, "player");
        if (player.getMainHandStack().isOf(Time.AMETHYME_SHARD))
            return player.getMainHandStack();
        else if (player.getOffHandStack().isOf(Time.AMETHYME_SHARD))
            return player.getOffHandStack();
        return null;
    }

    @Contract(pure = true)
    public static @Nullable AmethymeShard shardForCrop(@Nullable BlockState blockState) {
        if (blockState == null)
            return null;
        if (blockState.isOf(Blocks.BEETROOTS) && ((CropBlock)Blocks.BEETROOTS).isMature(blockState))
            return BEETROOT;
        if (blockState.isOf(Blocks.CARROTS) && ((CropBlock)Blocks.CARROTS).isMature(blockState))
            return CARROT;
        if (blockState.isOf(Blocks.POTATOES) && ((CropBlock)Blocks.POTATOES).isMature(blockState))
            return POTATO;
        if (blockState.isOf(Blocks.WHEAT) && ((CropBlock)Blocks.WHEAT).isMature(blockState))
            return WHEAT;
        return null;
    }

    @Contract(pure = true)
    public static @Nullable AmethymeShard shardForGourd(@Nullable BlockState blockState) {
        if (blockState == null)
            return null;
        if (blockState.isOf(Blocks.PUMPKIN)) // TODO add stem check
            return PUMPKIN;
        if (blockState.isOf(Blocks.MELON)) // TODO add stem check
            return MELON;
        return null;
    }

    @Contract(pure = true)
    public @Nullable static AmethymeShard shardForStorageBlock(@Nullable  BlockState blockState) {
        if (blockState == null)
            return null;
        if (blockState.isOf(Blocks.HAY_BLOCK))
            return WHEAT;
        return null;
    }

    @Contract(pure = true)
    public static @Nullable AmethymeShard shardForMob(@Nullable EntityType<?> type) {
        if (type == EntityType.ZOMBIE || type == EntityType.SKELETON || type == EntityType.SPIDER || type == EntityType.CREEPER)
            return OVERWORLD_HOSTILES;
        if (type == EntityType.WITCH)
            return WITCH;
        if (type == EntityType.CAVE_SPIDER)
            return CAVE_SPIDER;
        if (type == EntityType.SILVERFISH)
            return SILVERFISH;
        if (type == EntityType.SLIME)
            return SLIME;
        if (type == EntityType.IRON_GOLEM)
            return IRON_GOLEM;
        if (type == EntityType.ENDERMAN)
            return ENDERMAN;
        // nether mobs
        if (type == EntityType.BLAZE)
            return BLAZE;
        if (type == EntityType.GHAST)
            return GHAST;
        if (type == EntityType.MAGMA_CUBE)
            return MAGMA_SLIME;
        if (type == EntityType.ZOMBIFIED_PIGLIN)
            return WITCH;
        // friendly mobs
        if (type == EntityType.CHICKEN)
            return CHICKEN;
        if (type == EntityType.COW)
            return COW;
        if (type == EntityType.PIG)
            return PIG;
        if (type == EntityType.SHEEP)
            return SHEEP;
        return null;
    }

    @Contract(pure = true)
    public static boolean isCattle(@Nullable EntityType<?> type) {
        return type == EntityType.CHICKEN || type == EntityType.COW || type == EntityType.PIG || type == EntityType.SHEEP;
    }

    @Contract(pure = true)
    public static @Nullable AmethymeShard shardForSpawner(@Nullable EntityType<?> type) {
        if (type == EntityType.BLAZE)
            return BLAZE;
        if (type == EntityType.CAVE_SPIDER)
            return CAVE_SPIDER;
        if (type == EntityType.MAGMA_CUBE)
            return MAGMA_SLIME;
        if (type == EntityType.SILVERFISH)
            return SILVERFISH;
        if (type == EntityType.SKELETON)
            return SKELETON;
        if (type == EntityType.SPIDER)
            return SPIDER;
        if (type == EntityType.ZOMBIE)
            return ZOMBIE;
        return null;
    }

    @Contract(pure = true)
    public static @Nullable AmethymeShard shardForSpawner(@Nullable MobSpawnerBlockEntity spawnerEntity) {
        if (spawnerEntity == null)
            return null;
        return shardForSpawner(spawnerEntity.getLogic().getRenderedEntity(spawnerEntity.getWorld()).getType());
    }

    public static ItemStack eggForShard(AmethymeShard shard, Random random) {
        if (shard != null) {
            if (random == null)
                random = new Random();
            if (shard == BLAZE)
                return new ItemStack(Items.BLAZE_SPAWN_EGG);
            if (shard == CAVE_SPIDER)
                return new ItemStack(Items.CAVE_SPIDER_SPAWN_EGG);
            if (shard == CHICKEN)
                return new ItemStack(Items.CHICKEN_SPAWN_EGG);
            if (shard == COW)
                return new ItemStack(Items.COW_SPAWN_EGG);
            if (shard == CREEPER)
                return new ItemStack(Items.CREEPER_SPAWN_EGG);
            if (shard == ENDERMAN)
                return new ItemStack(Items.ENDERMAN_SPAWN_EGG);
            if (shard == GHAST)
                return new ItemStack(Items.GHAST_SPAWN_EGG);
            // Items.IRON_GOLEM_SPAWN_EGG does not exist
            if (shard == MAGMA_SLIME)
                return new ItemStack(Items.MAGMA_CUBE_SPAWN_EGG);
            if (shard == OVERWORLD_HOSTILES)
                switch (random.nextInt(4)) {
                    case 0: return new ItemStack(Items.CREEPER_SPAWN_EGG);
                    case 1: return new ItemStack(Items.SPIDER_SPAWN_EGG);
                    case 2: return new ItemStack(Items.SKELETON_SPAWN_EGG);
                    case 3: return new ItemStack(Items.ZOMBIE_SPAWN_EGG);
                }
            if (shard == PIG)
                return new ItemStack(Items.PIG_SPAWN_EGG);
            if (shard == SHEEP)
                return new ItemStack(Items.SHEEP_SPAWN_EGG);
            if (shard == SILVERFISH)
                return new ItemStack(Items.SILVERFISH_SPAWN_EGG);
            if (shard == SKELETON)
                return new ItemStack(Items.SKELETON_SPAWN_EGG);
            if (shard == SLIME)
                return new ItemStack(Items.SLIME_SPAWN_EGG);
            if (shard == SPIDER)
                return new ItemStack(Items.SPIDER_SPAWN_EGG);
            if (shard == WITCH)
                return new ItemStack(Items.WITCH_SPAWN_EGG);
            if (shard == ZOMBIE)
                return new ItemStack(Items.ZOMBIE_SPAWN_EGG);
            if (shard == ZOMBIFIED_PIGLIN)
                return new ItemStack(Items.ZOMBIFIED_PIGLIN_SPAWN_EGG);
        }
        return new ItemStack(Items.EGG);
    }
}
