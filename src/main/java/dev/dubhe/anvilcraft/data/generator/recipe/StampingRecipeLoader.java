package dev.dubhe.anvilcraft.data.generator.recipe;

import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.recipe.StampingRecipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;

public class StampingRecipeLoader {
    public static void init(RegistrateRecipeProvider provider) {
        StampingRecipe.builder()
                .requires(Items.IRON_INGOT)
                .result(new ItemStack(Items.HEAVY_WEIGHTED_PRESSURE_PLATE))
                .save(provider, AnvilCraft.of("stamping/heavy_weighted_pressure_plate"));
    }
}
