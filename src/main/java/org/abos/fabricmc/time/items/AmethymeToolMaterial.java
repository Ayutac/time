package org.abos.fabricmc.time.items;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import org.abos.fabricmc.time.Time;

public class AmethymeToolMaterial implements ToolMaterial {

    public static final ToolMaterial INSTANCE = new AmethymeToolMaterial();

    private AmethymeToolMaterial() {}

    @Override
    public int getDurability() {
        return 16;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 12;
    }

    @Override
    public float getAttackDamage() {
        return 0f;
    }

    @Override
    public int getMiningLevel() {
        return 4;
    }

    @Override
    public int getEnchantability() {
        return 4;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(Time.AMETHYME_SHARD);
    }
}
