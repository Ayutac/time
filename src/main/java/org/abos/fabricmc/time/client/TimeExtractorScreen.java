package org.abos.fabricmc.time.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.abos.fabricmc.time.blocks.TimeExtractorBlockEntity;
import org.abos.fabricmc.time.gui.TimeExtractorScreenHandler;

/**
 * {@link HandledScreen} for the {@link TimeExtractorBlockEntity}.
 *
 * Mostly copied from <a href="https://fabricmc.net/wiki/tutorial:screenhandler">this tutorial</a>
 */
public class TimeExtractorScreen extends HandledScreen<ScreenHandler> {

    public TimeExtractorScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        playerInventoryTitleY += 1; // 1px lower
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, new Identifier("time", "textures/gui/container/time_extractor.png"));
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
        if (((TimeExtractorScreenHandler)this.handler).isExtracting()) {
            int height = ((TimeExtractorScreenHandler)this.handler).getExtractionProgress();
            this.drawTexture(matrices, x+62 + 1 * 18 + 1, y+17 + 1 * 18, 176, 0, 15, height + 1);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        // center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }

}
