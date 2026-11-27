package com.tsuo1951.gtceumorethings.common.data;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet;

import com.tsuo1951.gtceumorethings.GTCEuMoreThings;

import net.minecraft.resources.ResourceLocation;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;

public class MoreThingsMaterials {

    public static Material TitaniumAluminumCarbideMixture;
    public static Material TitaniumAluminumCarbide;
    public static Material AluminiumFluoride;
    public static Material EtchedTitaniumCarbideSlurry;
    public static Material MultilayerTitaniumCarbideMXene;
    public static Material IntercalatedTitaniumCarbideMXene;
    public static Material ColloidalTitaniumCarbideMXene;
    public static Material TitaniumCarbideMXene;

    private static ResourceLocation id(String path) {
        return new ResourceLocation(GTCEuMoreThings.MOD_ID, path);
    }

    public static void register() {
        TitaniumAluminumCarbideMixture = new Material.Builder(id("titanium_aluminum_carbide_mixture"))
                .dust()
                .color(0x6C737E)
                .iconSet(MaterialIconSet.FINE)
                .components(TitaniumCarbide, 2, Titanium, 1, Aluminium, 1)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        TitaniumAluminumCarbide = new Material.Builder(id("titanium_aluminum_carbide"))
                .ingot()
                .color(0x525866)
                .iconSet(MaterialIconSet.METALLIC)
                .components(Titanium, 3, Aluminium, 1, Carbon, 2)
                .flags(NO_SMELTING, DISABLE_DECOMPOSITION)
                .buildAndRegister();

        AluminiumFluoride = new Material.Builder(id("aluminium_fluoride"))
                .dust()
                .color(0xD8E2DC)
                .iconSet(MaterialIconSet.DULL)
                .components(Aluminium, 1, Fluorine, 3)
                .flags(DECOMPOSITION_BY_ELECTROLYZING)
                .buildAndRegister();

        EtchedTitaniumCarbideSlurry = new Material.Builder(id("etched_titanium_carbide_slurry"))
                .fluid()
                .color(0x2A322D)
                .buildAndRegister();

        MultilayerTitaniumCarbideMXene = new Material.Builder(id("multilayer_titanium_carbide_mxene"))
                .dust()
                .color(0x252B29)
                .iconSet(MaterialIconSet.ROUGH)
                .components(Titanium, 3, Carbon, 2)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        IntercalatedTitaniumCarbideMXene = new Material.Builder(id("intercalated_titanium_carbide_mxene"))
                .fluid()
                .color(0x1E2623)
                .buildAndRegister();

        ColloidalTitaniumCarbideMXene = new Material.Builder(id("colloidal_titanium_carbide_mxene"))
                .fluid()
                .color(0x11241E)
                .buildAndRegister();

        TitaniumCarbideMXene = new Material.Builder(id("titanium_carbide_mxene"))
                .dust()
                .color(0x1D2228)
                .secondaryColor(0x0E1114)
                .iconSet(MaterialIconSet.SHINY)
                .components(Titanium, 3, Carbon, 2)
                .formula("Ti3C2Tx", true)
                .flags(GENERATE_PLATE, GENERATE_FOIL, DISABLE_DECOMPOSITION)
                .buildAndRegister();
    }
}
