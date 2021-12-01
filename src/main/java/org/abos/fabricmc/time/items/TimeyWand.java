package org.abos.fabricmc.time.items;

import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.abos.fabricmc.time.Time;
import org.abos.fabricmc.time.mixin.HoglinEntityEnvoker;
import org.abos.fabricmc.time.mixin.PiglinEntityEnvoker;

public class TimeyWand extends ToolItem implements Vanishable {

    protected TimeyWand(Item.Settings settings) {
        super(AmethymeToolMaterial.INSTANCE, settings);
    }

    public TimeyWand() {
        this(Time.getTimeItemSettings());
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity playerEntity = context.getPlayer();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        // grow crops if they are not all grown up
        // TODO if from first stage -> Archivement: The Greenest Thumb
        if (block instanceof CropBlock crop) {
            int maxAge = crop.getMaxAge();
            if (blockState.get(crop.getAgeProperty()) < maxAge) {
                playSound(world, playerEntity, pos);
                world.setBlockState(pos, crop.withAge(maxAge), Block.NOTIFY_LISTENERS);
                world.emitGameEvent(playerEntity, GameEvent.BLOCK_CHANGE, pos);
                createParticles(world, pos);
                if (playerEntity != null) {
                    context.getStack().damage(1, playerEntity, p -> p.sendToolBreakStatus(context.getHand()));
                }
                return ActionResult.success(world.isClient);
            }
        }
        // grow sugar cane if it is not all grown up
        // TODO Archivement: Finally...
        else if (block instanceof SugarCaneBlock sugarCane) {
            int i = 0;
            // find bottom sugar cane
            do {
                i++;
            } while (world.getBlockState(pos.down(i)).isOf(sugarCane));
            i--;
            //BlockState sugarBottom = (SugarCaneBlock)world.getBlockState(pos.down(i));
            boolean growthGuaranteed = false;
            if (i == 0) { // i.e. bottom sugar cane block
                if (world.getBlockState(pos.up()).isOf(Blocks.AIR)) { // grow second sugar cane
                    growthGuaranteed = true;
                    world.setBlockState(pos.up(), sugarCane.getDefaultState());
                }
                if (world.getBlockState(pos.up()).isOf(sugarCane) &&
                        world.getBlockState(pos.up(2)).isOf(Blocks.AIR)) { // grow third sugar cane
                    growthGuaranteed = true;
                    world.setBlockState(pos.up(2), sugarCane.getDefaultState());
                }
            }
            else if (i == 1) { // i.e. player hit the second from bottom sugar cane block and there are no more
                if (world.getBlockState(pos.up()).isOf(Blocks.AIR)) { // grow second sugar cane
                    growthGuaranteed = true;
                    world.setBlockState(pos.up(), sugarCane.getDefaultState());
                }
            }
            // in any other case there are at least 3 sugar cane blocks, so we do nothing
            if (growthGuaranteed) { // player effects
                playSound(world, playerEntity, pos);
                world.setBlockState(pos, blockState.with(SugarCaneBlock.AGE, 0), Block.NO_REDRAW);
                createParticles(world, pos);
                if (playerEntity != null) {
                    context.getStack().damage(1, playerEntity, p -> p.sendToolBreakStatus(context.getHand()));
                }
                return ActionResult.success(world.isClient);
            }
        }
        // TODO make saplins grow, including nether/end variants and mushrooms
        // TODO make pumpkin/melon grow
        // TODO make two-block flowers multiply (as drops, say x8)
        // TODO make one-block flowers multiply (as blocks in the surrounding area)
        // TODO make turtle eggs hatch -> Archivement: Done Waiting Forever
        // TODO make grass and sea grass grow
        // TODO make bambus and kelp grow
        // TODO make sea pickles grow
        // TODO make anvil more used
        return ActionResult.FAIL;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity playerEntity, LivingEntity entity, Hand hand) {
        // TODO baby-grow-up Archivement: Best Nanny
        // grow up babies (but zombify hoglins first if applicable)
        if (entity instanceof PassiveEntity mob) {
            World world = mob.getEntityWorld();
            Vec3d pos = mob.getPos();
            if (mob instanceof HoglinEntity hoglin && world instanceof ServerWorld) {
                if (hoglin.canConvert()) {
                    playSound(world, playerEntity, pos);
                    ((HoglinEntityEnvoker)hoglin).invokeZombify((ServerWorld)world);
                    world.emitGameEvent(playerEntity, GameEvent.MOB_INTERACT, hoglin.getBlockPos());
                    createParticles(world, hoglin.getBlockPos());
                    if (playerEntity != null) {
                        stack.damage(1, playerEntity, p -> p.sendToolBreakStatus(hand));
                    }
                    return ActionResult.success(world.isClient());
                }
            }
            if (mob.isBaby()) { // must enter this part if mob was hoglin but couldn't be converted (in which case we haven't returned already)
                playSound(world, playerEntity, pos);
                mob.setBreedingAge(1); // ages the mob up
                world.emitGameEvent(playerEntity, GameEvent.MOB_INTERACT, mob.getBlockPos());
                createParticles(world, mob.getBlockPos());
                if (playerEntity != null) {
                    stack.damage(1, playerEntity, p -> p.sendToolBreakStatus(hand));
                }
                return ActionResult.success(world.isClient());
            }
        }
        // zombify piglins or grow up piglin babies (in that order)
        else if (entity instanceof PiglinEntity piglin) {
            World world = piglin.getEntityWorld();
            Vec3d pos = piglin.getPos();
            boolean zombify = piglin.shouldZombify() && world instanceof ServerWorld;
            if (piglin.isBaby() || zombify) {
                playSound(world, playerEntity, pos);
                if (zombify) { // zombifies the piglin
                    ((PiglinEntityEnvoker) piglin).invokeZombify((ServerWorld) world);
                }
                else { // ages the piglin up
                    piglin.setBaby(false);
                }
                world.emitGameEvent(playerEntity, GameEvent.MOB_INTERACT, piglin.getBlockPos());
                createParticles(world, piglin.getBlockPos());
                if (playerEntity != null) {
                    stack.damage(1, playerEntity, p -> p.sendToolBreakStatus(hand));
                }
                return ActionResult.success(world.isClient());
            }
        }
        // TODO submerged zombie to drowned
        // TODO zombie in desert to husk
        // TODO skeleton in snowy biomes to stray
        return ActionResult.FAIL;
    }

    public void createParticles(WorldAccess world, BlockPos pos) {
        BoneMealItem.createParticles(world, pos, 0); // 0 defaults
    }

    public void playSound(World world, PlayerEntity player, BlockPos pos) {
        // the coordinates are supposed to be changed just as in World#playSound(PlayerEntity, BlockPos, ...)
        playSound(world, player, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5);
    }

    public void playSound(World world, PlayerEntity player, Vec3d pos) {
        world.playSound(player, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 1.0f, world.getRandom().nextFloat() * 0.4f + 0.8f);
    }

    public void playSound(World world, PlayerEntity player, double posX, double posY, double posZ) {
        world.playSound(player, posX, posY, posZ, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 1.0f, world.getRandom().nextFloat() * 0.4f + 0.8f);
    }

}
