package org.abos.fabricmc.time.items;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.gen.feature.StructureFeature;
import org.abos.fabricmc.time.Time;
import org.abos.fabricmc.time.Utils;

public class TimeWand extends TimeyWand {

    protected TimeWand(Item.Settings settings) {
        super(settings);
    }

    public TimeWand() {
        this(Time.getTimeItemSettings().fireproof().maxDamage(AmethymeToolMaterial.INSTANCE.getDurability()*2));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        // TODO Bricks to Cracked Bricks
        // TODO make coral grow
        // TODO slime blocks to mini slimes
        ActionResult result = super.useOnBlock(context);
        // only testing stuff here
        /*if (result == ActionResult.FAIL) {
            if (context.getWorld() instanceof ServerWorld world && context.getPlayer() != null) {
                if (Utils.isStructure(world, ChunkSectionPos.from(context.getBlockPos()), StructureFeature.FORTRESS))
                    context.getPlayer().sendMessage(new LiteralText("Fortress detected!"),false);
                else
                    context.getPlayer().sendMessage(new LiteralText("No fortress detected..."),false);
            }
            return ActionResult.FAIL;
        }*/
        return result;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity playerEntity, LivingEntity entity, Hand hand) {
        if (entity instanceof VillagerEntity villager && !villager.isBaby()) { // only convert adults
            if (villager.world instanceof ServerWorld world) {
                Vec3d oldPos = villager.getPos();
                playSound(world, playerEntity, oldPos);
                BlockPos oldBlockPos = villager.getBlockPos();
                if (Utils.isStructure(world, ChunkSectionPos.from(villager), StructureFeature.FORTRESS) &&
                        Utils.standsOn(world, villager, Blocks.NETHER_BRICKS)) { // turn into wither skeleton
                    Utils.convertTo(world, villager, EntityType.WITHER_SKELETON);
                } else if (world.getRegistryKey().equals(World.END)) { // turn into ender man
                    // if that dimension lookup doesn't work (only checks ==), the values of the keys must be compared
                    Utils.convertTo(world, villager, EntityType.ENDERMAN);
                } else if (world.getDimension().hasFixedTime()) { // turn into skeleton
                    Utils.convertTo(world, villager, EntityType.SKELETON);
                } else if (world.isThundering()) { // turn into witch
                    Utils.convertTo(world, villager, EntityType.WITCH);
                } else { // turn into zombie villager
                    Utils.convertTo(world, villager, EntityType.ZOMBIE_VILLAGER);
                }
                world.emitGameEvent(playerEntity, GameEvent.MOB_INTERACT, villager.getBlockPos());
                createParticles(world, oldBlockPos);
                if (playerEntity != null) {
                    stack.damage(1, playerEntity, p -> p.sendToolBreakStatus(hand));
                }
                return ActionResult.success(world.isClient());
            }
        }
        // TODO baby villagers to ghasts -> Archivement: We all suspected this...
        // TODO slimes to bigger slimes
        // TODO slimes in nether-like to magma slimes
        // TODO non magic pillagers aging to zombie/skeleton/wither skeleton
        // TODO any pillagers in End to endermen -> Archivement: Happy End
        // TODO adult piglins and brutes in End to endermen -> if Brute -> Archivement: You came all the way here...
        // TODO adult piglins in bastions to brutes -> + killing it -> Archivement: Faster Stronger
        // TODO spiders (both) in nether-like to striders
        // TODO adult turtles in End to shulkers
        // TODO silverfish in End to endermites
        // TODO Guardian to Elder Guardian -> + killing it -> Archivement: Harder Better
        return super.useOnEntity(stack, playerEntity, entity, hand);
    }

}
