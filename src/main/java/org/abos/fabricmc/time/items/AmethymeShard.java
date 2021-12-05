package org.abos.fabricmc.time.items;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.abos.fabricmc.time.Time;

public class AmethymeShard extends Item {

    // if changed, change the assets accordingly
    // don't forget to look up use of this variable for even more changes...
    public static String ID = "amethyme_shard";

    public static AmethymeShard UNBOUND = Time.AMETHYME_SHARD;

    /*
     * How to add new shard:
     * 1. Add public static variable.
     * 2. Add to static register method.
     * 3. Add to language assets.
     * 4. Add to tag asset.
     * 5. Add model.
     * 6. Add texture.
     */

    public static AmethymeShard BEETROOT = new AmethymeShard();
    public static AmethymeShard BLAZE = new AmethymeShard();
    public static AmethymeShard CARROT = new AmethymeShard();
    public static AmethymeShard CAVE_SPIDER = new AmethymeShard();
    public static AmethymeShard CHICKEN = new AmethymeShard();
    public static AmethymeShard COW = new AmethymeShard();
    public static AmethymeShard CREEPER = new AmethymeShard();
    public static AmethymeShard ENDERMAN = new AmethymeShard();
    public static AmethymeShard GHAST = new AmethymeShard();
    public static AmethymeShard MAGMA_SLIME = new AmethymeShard();
    public static AmethymeShard MELON = new AmethymeShard();
    public static AmethymeShard OVERWORLD_HOSTILES = new AmethymeShard();
    public static AmethymeShard PIG = new AmethymeShard();
    public static AmethymeShard POTATO = new AmethymeShard();
    public static AmethymeShard PUMPKIN = new AmethymeShard();
    public static AmethymeShard SHEEP = new AmethymeShard();
    public static AmethymeShard SILVERFISH = new AmethymeShard();
    public static AmethymeShard SKELETON = new AmethymeShard();
    public static AmethymeShard SLIME = new AmethymeShard();
    public static AmethymeShard SPIDER = new AmethymeShard();
    public static AmethymeShard WHEAT = new AmethymeShard();
    public static AmethymeShard WITCH = new AmethymeShard();
    public static AmethymeShard ZOMBIE = new AmethymeShard();
    public static AmethymeShard ZOMBIFIED_PIGLIN = new AmethymeShard();

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

}
