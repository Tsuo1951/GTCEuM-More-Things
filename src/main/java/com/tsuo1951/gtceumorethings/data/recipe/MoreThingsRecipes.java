package com.tsuo1951.gtceumorethings.data.recipe;

import net.minecraft.data.recipes.FinishedRecipe;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.data.recipe.CustomTags;

import com.tsuo1951.gtceumorethings.common.data.MoreThingsMachines;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;
import static com.tsuo1951.gtceumorethings.common.data.MoreThingsMaterials.*;

public class MoreThingsRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        BLAST_RECIPES.recipeBuilder("titanium_carbide_from_rutile")
                .inputItems(dust, Rutile, 1)
                .inputItems(dust, Carbon, 3)
                .outputItems(dust, TitaniumCarbide, 1)
                .outputFluids(CarbonMonoxide.getFluid(2000))
                .blastFurnaceTemp(2000)
                .duration(400)
                .EUt(VA[EV])
                .save(provider);

        BLAST_RECIPES.recipeBuilder("titanium_carbide_direct")
                .inputItems(dust, Titanium, 1)
                .inputItems(dust, Carbon, 1)
                .outputItems(dust, TitaniumCarbide, 1)
                .blastFurnaceTemp(1800)
                .duration(240)
                .EUt(VA[EV])
                .save(provider);

        MIXER_RECIPES.recipeBuilder("titanium_aluminum_carbide_mixture")
                .inputItems(dust, TitaniumCarbide, 2)
                .inputItems(dust, Titanium, 1)
                .inputItems(dust, Aluminium, 1)
                .circuitMeta(4)
                .outputItems(dust, TitaniumAluminumCarbideMixture, 1)
                .duration(200)
                .EUt(VA[HV])
                .save(provider);

        BLAST_RECIPES.recipeBuilder("titanium_aluminum_carbide_sintering")
                .inputItems(dust, TitaniumAluminumCarbideMixture, 1)
                .inputFluids(Argon.getFluid(1000))
                .outputItems(ingot, TitaniumAluminumCarbide, 1)
                .blastFurnaceTemp(1700)
                .duration(600)
                .EUt(VA[EV])
                .save(provider);

        CHEMICAL_RECIPES.recipeBuilder("titanium_aluminum_carbide_hf_etching")
                .inputItems(dust, TitaniumAluminumCarbide, 2)
                .inputFluids(HydrofluoricAcid.getFluid(6000))
                .inputFluids(DistilledWater.getFluid(2000))
                .outputFluids(EtchedTitaniumCarbideSlurry.getFluid(2000))
                .outputFluids(Hydrogen.getFluid(3000))
                .duration(480)
                .EUt(VA[EV])
                .save(provider);

        CENTRIFUGE_RECIPES.recipeBuilder("etched_mxene_slurry_washing")
                .inputFluids(EtchedTitaniumCarbideSlurry.getFluid(2000))
                .inputFluids(DistilledWater.getFluid(4000))
                .outputItems(dust, MultilayerTitaniumCarbideMXene, 2)
                .outputItems(dust, AluminiumFluoride, 2)
                .outputFluids(Water.getFluid(4000))
                .duration(320)
                .EUt(VA[HV])
                .save(provider);

        CHEMICAL_RECIPES.recipeBuilder("lithium_chloride_synthesis")
                .inputItems(dust, Lithium, 2)
                .inputFluids(HydrochloricAcid.getFluid(2000))
                .outputItems(dust, LithiumChloride, 2)
                .outputFluids(Hydrogen.getFluid(1000))
                .duration(160)
                .EUt(VA[HV])
                .save(provider);

        CHEMICAL_RECIPES.recipeBuilder("mxene_lithium_intercalation")
                .inputItems(dust, MultilayerTitaniumCarbideMXene, 1)
                .inputItems(dust, LithiumChloride, 1)
                .inputFluids(DistilledWater.getFluid(2000))
                .inputFluids(Argon.getFluid(500))
                .outputFluids(IntercalatedTitaniumCarbideMXene.getFluid(2000))
                .duration(400)
                .EUt(VA[EV])
                .save(provider);

        CENTRIFUGE_RECIPES.recipeBuilder("mxene_delamination_centrifuging")
                .inputFluids(IntercalatedTitaniumCarbideMXene.getFluid(2000))
                .outputItems(dust, LithiumChloride, 1)
                .outputFluids(ColloidalTitaniumCarbideMXene.getFluid(1000))
                .outputFluids(Water.getFluid(1000))
                .duration(360)
                .EUt(VA[EV])
                .save(provider);

        DISTILLERY_RECIPES.recipeBuilder("mxene_vacuum_filtration")
                .inputFluids(ColloidalTitaniumCarbideMXene.getFluid(1000))
                .circuitMeta(1)
                .outputItems(dust, TitaniumCarbideMXene, 1)
                .outputFluids(DistilledWater.getFluid(1000))
                .duration(300)
                .EUt(VA[EV])
                .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder("processing_array")
                .inputItems(GTMachines.HULL[IV].asStack())
                .inputItems(GTItems.ROBOT_ARM_IV.asStack(4))
                .inputItems(CustomTags.IV_CIRCUITS, 4)
                .inputItems(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.asStack(4))
                .inputItems(plate, TitaniumCarbideMXene, 4)
                .outputItems(MoreThingsMachines.PROCESSING_ARRAY.asStack())
                .duration(600)
                .EUt(VA[IV])
                .save(provider);
    }
}
