package org.abos.fabricmc.time.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.abos.fabricmc.time.Time;
import org.abos.fabricmc.time.Utils;
import org.abos.fabricmc.time.blocks.TimeExtractorBlockEntity;
import org.jetbrains.annotations.Contract;

public class TimeExtractorRecipe implements Recipe<TimeExtractorBlockEntity> {

    // This will be the "type" field in the JSON
    public static final Identifier ID = new Identifier(Time.MOD_ID,Time.TIME_EXTRACTOR_STR);

    public static class Type implements RecipeType<TimeExtractorRecipe> {
        private Type() {} // should never be called from the outside
        public static final Type INSTANCE = new Type();
        public static final String ID = Time.TIME_EXTRACTOR_STR+"_recipe";
    }

    // if this is changed, change recipes as well
    // but since other mods may expect old value for their recipes, best not to change at all
    public static final int MIN_EXTRACTION_TU = 1;

    protected Identifier id;

    protected Ingredient input;

    protected ItemStack output;

    protected int extractedTU;

    /**
     * Creates an extractor recipe from the given parameters
     * @param id the name of the recipe
     * @param input the extraction material
     * @param output the extracted material
     * @param extractedTU The amount of TUs unleashed. If less than {@link #MIN_EXTRACTION_TU}, will default to that.
     * @throws NullPointerException If any parameter refers to {@code null}.
     */
    @Contract("null,_,_,_->fail; _,null,_,_->fail; _,_,null,_->fail")
    public TimeExtractorRecipe(Identifier id, Ingredient input, ItemStack output, int extractedTU) {
        Utils.requireNonNull(id, "id");
        Utils.requireNonNull(input, "input");
        Utils.requireNonNull(output, "result");
        if (extractedTU < MIN_EXTRACTION_TU) {
            Time.LOGGER.warn("Illegal TU extraction amount {} detected, will be defaulted to {}", extractedTU, MIN_EXTRACTION_TU);
            extractedTU = MIN_EXTRACTION_TU;
        }
        this.id = id;
        this.input = input;
        this.output = output;
        this.extractedTU = extractedTU;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    public Ingredient getInput() {
        return input;
    }

    @Override
    public ItemStack getOutput() {
        return output;
    }

    public int getExtractedTU() {
        return extractedTU;
    }

    @Override
    public boolean matches(TimeExtractorBlockEntity inventory, World world) {
        return input.test(inventory.getStack(0));
    }

    @Override
    public ItemStack craft(TimeExtractorBlockEntity inventory) {
        return getOutput().copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(Time.TIME_EXTRACTOR.asItem());
    }
}
