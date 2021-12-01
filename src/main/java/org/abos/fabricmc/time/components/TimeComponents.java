package org.abos.fabricmc.time.components;

import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.minecraft.util.Identifier;
import org.abos.fabricmc.time.Time;

public class TimeComponents implements BlockComponentInitializer, WorldComponentInitializer {

    public static final String PASSED_TIME_STR = "passed_time"; // if changed, update fabric.mod.json as well

    public static final String NIGHT_CHECK_STR = "night_check"; // if changed, update fabric.mod.json as well

    public static final ComponentKey<CounterComponent> PASSED_TIME =
            ComponentRegistry.getOrCreate(new Identifier(Time.MOD_ID, PASSED_TIME_STR), CounterComponent.class);

    public static final ComponentKey<BooleanComponent> NIGHT_CHECK =
            ComponentRegistry.getOrCreate(new Identifier(Time.MOD_ID, NIGHT_CHECK_STR), BooleanComponent.class);

    @Override
    public void registerBlockComponentFactories(BlockComponentFactoryRegistry registry) {
        //registry.registerFor(TimeCollectorBlockEntity.class, PASSED_TIME, new PassedTimeCounterFactory<>()); // TODO not needed?
        Time.LOGGER.info("{} Block Components registered.",Time.MOD_NAME);
    }

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(PASSED_TIME, (Object) -> new PassedTimeCounter());
        registry.register(NIGHT_CHECK, (Object) -> new NightCheck());
        Time.LOGGER.info("{} World Components registered.",Time.MOD_NAME);
    }

}
