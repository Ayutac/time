package org.abos.fabricmc.time.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.render.RenderLayer;
import org.abos.fabricmc.time.Time;

@Environment(EnvType.CLIENT)
public class TimeClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(Time.TIME_EXTRACTOR_SCREEN_HANDLER, TimeExtractorScreen::new);
        ScreenRegistry.register(Time.COMPACT_FARM_SCREEN_HANDLER, CompactFarmScreen::new);
        BlockRenderLayerMap.INSTANCE.putBlock(Time.TIME_COLLECTOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Time.TIME_SYPHON, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Time.TIME_EXTRACTOR, RenderLayer.getCutout());
    }

}
