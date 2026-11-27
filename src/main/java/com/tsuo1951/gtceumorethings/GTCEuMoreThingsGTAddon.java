package com.tsuo1951.gtceumorethings;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

@GTAddon
public final class GTCEuMoreThingsGTAddon implements IGTAddon {

    @Override
    public GTRegistrate getRegistrate() {
        return GTCEuMoreThings.REGISTRATE;
    }

    @Override
    public void initializeAddon() {
      
    }

    @Override
    public String addonModId() {
        return GTCEuMoreThings.MOD_ID;
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
      
    }

    @Override
    public void removeRecipes(Consumer<ResourceLocation> consumer) {
      
    }
}
