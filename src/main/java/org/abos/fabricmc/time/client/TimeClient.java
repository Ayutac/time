package org.abos.fabricmc.time.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import org.abos.fabricmc.time.Time;

@Environment(EnvType.CLIENT)
public class TimeClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(Time.TIME_EXTRACTOR_SCREEN_HANDLER, TimeExtractorScreen::new);
    }

}
