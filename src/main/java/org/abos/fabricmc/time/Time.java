package org.abos.fabricmc.time;

import dev.onyxstudios.cca.api.v3.component.ComponentProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.*;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.abos.fabricmc.time.blocks.*;
import org.abos.fabricmc.time.components.BooleanComponent;
import org.abos.fabricmc.time.components.CounterComponent;
import org.abos.fabricmc.time.components.TimeComponents;
import org.abos.fabricmc.time.gui.TimeExtractorScreenHandler;
import org.abos.fabricmc.time.items.*;
import org.abos.fabricmc.time.recipes.TimeExtractorRecipe;
import org.abos.fabricmc.time.recipes.TimeExtractorRecipeSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Time implements ModInitializer, ServerTickEvents.EndWorldTick {

	// if changed, update fabric.mod.json and assets as well
	// don't forget to look up use of this variable for even more changes...
	public static final String MOD_ID = "time";

	public static final String MOD_NAME = FabricLoader.getInstance().getModContainer(MOD_ID).get().getMetadata().getName();
	public static final String MOD_VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).get().getMetadata().getVersion().getFriendlyString();

	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
	public static final Config CONFIG = new Config();

	// if any name is changed, change the corresponding tag data files accordingly
	public static final Tag<Item> SWORDS = TagFactory.ITEM.create(new Identifier("c", "swords"));
	public static final Tag<Item> WANDS = TagFactory.ITEM.create(new Identifier("c", "wands"));
	public static final Tag<Block> TARDIS_BLOCKS = TagFactory.BLOCK.create(new Identifier(MOD_ID, "tardis_blocks"));
	public static final Tag<Item> AMETHYME_SHARDS = TagFactory.ITEM.create(new Identifier(MOD_ID, AmethymeShard.ID+"s"));

	public static final ItemGroup TIME_GROUP = FabricItemGroupBuilder.build(
			new Identifier(MOD_ID, "general"), // if changed, update language assets as well
			() -> new ItemStack(Items.CLOCK));
	public static Item.Settings getTimeItemSettings() {return new FabricItemSettings().group(Time.TIME_GROUP);}
	public static AbstractBlock.Settings getTimeBlockSettings() {return FabricBlockSettings.of(Material.AMETHYST).strength(2.5f);}

	public static final Item TIMEY_WIMEY = new Item(getTimeItemSettings());
	public static final AmethymeShard AMETHYME_SHARD = new AmethymeShard();

	public static ToolItem TIME_SWORD = new SwordItem(AmethymeToolMaterial.INSTANCE, 25, 5f,
			getTimeItemSettings().maxDamage(3));
	public static ToolItem TIMEY_WAND = new TimeyWand();
	public static ToolItem TIME_WAND = new TimeWand();
	public static ToolItem TIME_STAFF = new TimeStaff();

	public static final String TIME_COLLECTOR_STR = "time_collector"; // if changed, update the assets as well
	public static final Block TIME_COLLECTOR = new TimeCollectorBlock();

	public static final String TIME_SYPHON_STR = "time_syphon"; // if changed, update the assets as well
	public static final Block TIME_SYPHON = new TimeSyphonBlock();
	public static BlockEntityType<TimeSyphonBlockEntity> TIME_SYPHON_ENTITY;

	// if name is changed, update the assets as well, especially the recipes using this as a recipe type
	public static final String TIME_EXTRACTOR_STR = "time_extractor";
	public static final Block TIME_EXTRACTOR = new TimeExtractorBlock();
	public static BlockEntityType<TimeExtractorBlockEntity> TIME_EXTRACTOR_ENTITY;
	public static ScreenHandlerType<TimeExtractorScreenHandler> TIME_EXTRACTOR_SCREEN_HANDLER;

	@Override
	public void onInitialize() {
		// if any name is changed, update the assets as well
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "timey_wimey"), TIMEY_WIMEY);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, AmethymeShard.ID), AMETHYME_SHARD);
		AmethymeShard.register(); // registers all essence shards except for the (empty) amethyme shard, which comes first
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "time_sword"), TIME_SWORD);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "timey_wand"), TIMEY_WAND);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "time_wand"), TIME_WAND);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "time_staff"), TIME_STAFF);

		Identifier id;

		id = new Identifier(MOD_ID, TIME_COLLECTOR_STR);
		Registry.register(Registry.BLOCK, id, TIME_COLLECTOR);
		Registry.register(Registry.ITEM, id, new BlockItem(TIME_COLLECTOR, getTimeItemSettings()));

		id = new Identifier(MOD_ID, TIME_SYPHON_STR);
		Registry.register(Registry.BLOCK, id, TIME_SYPHON);
		Registry.register(Registry.ITEM, id, new BlockItem(TIME_SYPHON, getTimeItemSettings()));
		TIME_SYPHON_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.create(TimeSyphonBlockEntity::new, TIME_SYPHON).build(null));

		id = new Identifier(MOD_ID, TIME_EXTRACTOR_STR);
		Registry.register(Registry.BLOCK, id, TIME_EXTRACTOR);
		Registry.register(Registry.ITEM, id, new BlockItem(TIME_EXTRACTOR, getTimeItemSettings()));
		TIME_EXTRACTOR_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.create(TimeExtractorBlockEntity::new, TIME_EXTRACTOR).build(null));
		TIME_EXTRACTOR_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(id, TimeExtractorScreenHandler::new);
		Registry.register(Registry.RECIPE_SERIALIZER, TimeExtractorRecipe.ID, TimeExtractorRecipeSerializer.INSTANCE);

		ServerTickEvents.END_WORLD_TICK.register(this);

		LOGGER.info("Basic {} registrations complete.", MOD_NAME);
	}

	@Override
	public void onEndTick(ServerWorld world) {
		if (world == null || world.getDimension().hasFixedTime() || !world.getDimension().hasSkyLight())
			return;
		ComponentProvider provider = ComponentProvider.fromWorld(world);
		BooleanComponent nightCheck = TimeComponents.NIGHT_CHECK.get(provider);
		if (world.isDay()) {
			if (nightCheck.isTrue()) {
				CounterComponent passedTime = TimeComponents.PASSED_TIME.get(provider);
				passedTime.increment(world.getGameRules().getInt(CONFIG.getTUIncreaseRule()));
				TimeComponents.PASSED_TIME.sync(provider);
			}
			nightCheck.setValue(false);
		}
		else {
			nightCheck.setValue(true);
		}
		TimeComponents.NIGHT_CHECK.sync(provider);
	}
}
