package com.tsuo1951.gtceumorethings.common.data;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipeSerializer;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import com.tsuo1951.gtceumorethings.GTCEuMoreThings;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeType;

public class MoreThingsRecipeTypes {

    public static GTRecipeType PLASMA_SYNTHESIS_RECIPES;

    public static void init() {
        PLASMA_SYNTHESIS_RECIPES = register("plasma_synthesis", GTRecipeTypes.MULTIBLOCK)
                .setMaxIOSize(4, 2, 2, 2)
                .setEUIO(IO.IN)
                .setProgressBar(GuiTextures.PROGRESS_BAR_FUSION, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
                .setSound(GTSoundEntries.ARC)
                .setMaxTooltips(4)
                .addDataInfo(data -> {
                    int mode = data.getInt("mode");
                    return LocalizationUtils.format("gtceumorethings.recipe.plasma_mode." + mode);
                })
                .addDataInfo(data -> {
                    int minTemp = data.getInt("min_temp");
                    int maxTemp = data.getInt("max_temp");
                    if (maxTemp > 0) {
                        return LocalizationUtils.format("gtceumorethings.recipe.plasma_temp_range", minTemp, maxTemp);
                    }
                    return LocalizationUtils.format("gtceumorethings.recipe.plasma_temp_min", minTemp);
                })
                .addDataInfo(data -> {
                    int minDensity = data.getInt("min_density");
                    int minConfinement = data.getInt("min_confinement");
                    return LocalizationUtils.format("gtceumorethings.recipe.plasma_density_confinement",
                            minDensity, minConfinement);
                });
    }

    public static GTRecipeType register(String name, String group, RecipeType<?>... proxyRecipes) {
        GTRecipeType recipeType = new GTRecipeType(GTCEuMoreThings.id(name), group, proxyRecipes);
        GTRegistries.register(BuiltInRegistries.RECIPE_TYPE, recipeType.registryName, recipeType);
        GTRegistries.register(BuiltInRegistries.RECIPE_SERIALIZER, recipeType.registryName, new GTRecipeSerializer());
        GTRegistries.RECIPE_TYPES.register(recipeType.registryName, recipeType);
        return recipeType;
    }
}
