package org.abos.fabricmc.time.recipes;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.abos.fabricmc.time.Time;

import java.util.Optional;

/**
 * The serializer for {@link TimeExtractorRecipe}.
 * Mostly copied from <a href="https://github.com/natanfudge/fabric-docs/blob/master/newdocs/Modding-Tutorials/Crafting-Recipes/defining-custom-crafting-recipes.md">this tutorial</a>
 */
public class TimeExtractorRecipeSerializer implements RecipeSerializer<TimeExtractorRecipe> {

    class RecipeJsonParsed {
        JsonObject input;
        String outputItem;
        int outputItemAmount;
        int extractedTU;
    }

    public static final TimeExtractorRecipeSerializer INSTANCE = new TimeExtractorRecipeSerializer();

    private TimeExtractorRecipeSerializer() {} // should never be called from the outside

    // TODO finish doc
    /**
     *
     * @param id
     * @param json
     * @throws NullPointerException If any parameter refers to {@code null}.
     * @throws com.google.gson.JsonSyntaxException If the specified JSON is not {@code null} but syntactically invalid.
     * @throws JsonParseException If anything in the specified JSON is not available at call time or illegal
     * quantities of items or TU were specified.
     */
    @Override
    public TimeExtractorRecipe read(Identifier id, JsonObject json) {
        RecipeJsonParsed parsedRecipe = new Gson().fromJson(json, RecipeJsonParsed.class);
        Ingredient input = Ingredient.fromJson(parsedRecipe.input);
        Optional<Item> outputItem = Registry.ITEM.getOrEmpty(new Identifier(parsedRecipe.outputItem));
        if (!outputItem.isPresent()) {
            throw new JsonParseException("Unknown item '" + parsedRecipe.outputItem + "'!");
        }
        if (parsedRecipe.outputItemAmount < 0) {
            throw new JsonParseException("Items cannot occur in negative quantities!");
        }
        else if (parsedRecipe.outputItemAmount == 0) { // default if not given
            final int defaultOutputItemAmount = 1;
            parsedRecipe.outputItemAmount = defaultOutputItemAmount;
            Time.LOGGER.debug("Output Amount for {} {} was defaulted to {}", TimeExtractorRecipe.ID, id, defaultOutputItemAmount);
        }
        ItemStack output = new ItemStack(outputItem.get(), parsedRecipe.outputItemAmount);
        if (parsedRecipe.extractedTU == 0) { // default if not given
            parsedRecipe.extractedTU = TimeExtractorRecipe.MIN_EXTRACTION_TU;
            Time.LOGGER.debug("Extracted TU amount for {} {} was defaulted to {}", TimeExtractorRecipe.ID, id, TimeExtractorRecipe.MIN_EXTRACTION_TU);
        }
        else if (parsedRecipe.extractedTU < TimeExtractorRecipe.MIN_EXTRACTION_TU) {
            throw new JsonParseException("At least "+TimeExtractorRecipe.MIN_EXTRACTION_TU+" TU must be extracted!");
        }
        return new TimeExtractorRecipe(id, input, output, parsedRecipe.extractedTU);
    }

    @Override
    public void write(PacketByteBuf buf, TimeExtractorRecipe recipe) {
        recipe.getInput().write(buf);
        buf.writeItemStack(recipe.getOutput());
        buf.writeInt(recipe.getExtractedTU());
    }

    @Override
    public TimeExtractorRecipe read(Identifier id, PacketByteBuf buf) {
        return new TimeExtractorRecipe(id, Ingredient.fromPacket(buf), buf.readItemStack(), buf.readInt());
    }
}
