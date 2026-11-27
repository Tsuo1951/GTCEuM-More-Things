package com.tsuo1951.gtceumorethings.common.data;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet;
import com.tsuo1951.gtceumorethings.GTCEuMoreThings;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;

public class MoreThingsMaterials {

    public static Material TitaniumAluminumCarbide;
    public static Material TitaniumCarbideMXene;

    public static void init() {
        TitaniumAluminumCarbide = new Material.Builder(GTCEuMoreThings.id("titanium_aluminum_carbide"))
                .dust()
                .color(0x525866)
                .iconSet(MaterialIconSet.METALLIC)
                .components(Titanium, 3, Aluminium, 1, Carbon, 2)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();
      
        TitaniumCarbideMXene = new Material.Builder(GTCEuMoreThings.id("titanium_carbide_mxene"))
                .ingot()
                .color(0x2B303A)
                .secondaryColor(0x161920)
                .iconSet(MaterialIconSet.SHINY)
                .components(Titanium, 3, Carbon, 2)
                .flags(
                        GENERATE_PLATE,
                        GENERATE_FOIL,
                        GENERATE_FINE_WIRE,
                        DISABLE_DECOMPOSITION
                )
                .buildAndRegister();
    }
}
