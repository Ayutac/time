package org.abos.fabricmc.time.items;

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;
import org.abos.fabricmc.time.Time;
import org.abos.fabricmc.time.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * 7. Add conversions via shardForBlock for blocks and mixins for everything else.
     */

    public static final AmethymeShard BEETROOT = new AmethymeShard();
    public static final AmethymeShard BLAZE = new AmethymeShard();
    public static final AmethymeShard CARROT = new AmethymeShard();
    public static final AmethymeShard CAVE_SPIDER = new AmethymeShard();
    public static final AmethymeShard CHICKEN = new AmethymeShard();
    public static final AmethymeShard COW = new AmethymeShard();
    public static final AmethymeShard CREEPER = new AmethymeShard();
    public static final AmethymeShard ENDERMAN = new AmethymeShard();
    public static final AmethymeShard GHAST = new AmethymeShard();
    public static final AmethymeShard MAGMA_SLIME = new AmethymeShard();
    public static final AmethymeShard MELON = new AmethymeShard();
    public static final AmethymeShard OVERWORLD_HOSTILES = new AmethymeShard();
    public static final AmethymeShard PIG = new AmethymeShard();
    public static final AmethymeShard POTATO = new AmethymeShard();
    public static final AmethymeShard PUMPKIN = new AmethymeShard();
    public static final AmethymeShard SHEEP = new AmethymeShard();
    public static final AmethymeShard SILVERFISH = new AmethymeShard();
    public static final AmethymeShard SKELETON = new AmethymeShard();
    public static final AmethymeShard SLIME = new AmethymeShard();
    public static final AmethymeShard SPIDER = new AmethymeShard();
    public static final AmethymeShard WHEAT = new AmethymeShard();
    public static final AmethymeShard WITCH = new AmethymeShard();
    public static final AmethymeShard ZOMBIE = new AmethymeShard();
    public static final AmethymeShard ZOMBIFIED_PIGLIN = new AmethymeShard();

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

    /**
     * Creates an essence shard item.
     */
    public AmethymeShard() {
        super(Time.getTimeItemSettings());
    }

    public boolean isUnbound() {
        return this == Time.AMETHYME_SHARD;
    }

    @Nullable
    public static ItemStack getUnboundStack(@NotNull PlayerEntity player) {
        Utils.requireNonNull(player, "player");
        if (player.getMainHandStack().isOf(Time.AMETHYME_SHARD))
            return player.getMainHandStack();
        else if (player.getOffHandStack().isOf(Time.AMETHYME_SHARD))
            return player.getOffHandStack();
        return null;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        if (player == null)
            return ActionResult.PASS;
        ItemStack unboundStack = getUnboundStack(player);
        if (unboundStack == null)
            return ActionResult.PASS;
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState blockState = world.getBlockState(pos);
        AmethymeShard shardResult = shardForBlock(blockState);
        if (shardResult == null && blockState.isOf(Blocks.SPAWNER)) {
            MobSpawnerBlockEntity entity = (MobSpawnerBlockEntity)(world.getBlockEntity(pos));
            shardResult = shardForSpawner(entity.getLogic().getRenderedEntity(entity.getWorld()).getType());
        }
        if (shardResult != null) {
            unboundStack.decrement(1);
            world.breakBlock(pos, false, player);
            player.getInventory().offerOrDrop(new ItemStack(shardResult));
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    // use this method or shardForSpawner for mixin injections if needed, not useOnBlock
    @Nullable
    public AmethymeShard shardForBlock(BlockState blockState) {
        if (blockState.isOf(Blocks.HAY_BLOCK))
            return WHEAT;
        return null;
    }

    // use this method or shardForBlock for mixin injections if needed, not useOnBlock
    @Nullable
    public AmethymeShard shardForSpawner(EntityType<?> type) {
        if (type == EntityType.ZOMBIE) {
            return ZOMBIE;
        }
        if (type == EntityType.SKELETON) {
            return SKELETON;
        }
        if (type == EntityType.SPIDER) {
            return SPIDER;
        }
        if (type == EntityType.CAVE_SPIDER) {
            return CAVE_SPIDER;
        }
        if (type == EntityType.SILVERFISH) {
            return SILVERFISH;
        }
        if (type == EntityType.BLAZE) {
            return BLAZE;
        }
        if (type == EntityType.MAGMA_CUBE) {
            return MAGMA_SLIME;
        }
        return null;
    }
}
