package com.tsuo1951.gtceumorethings.data.recipe;

import net.minecraft.data.recipes.FinishedRecipe;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;

import com.tsuo1951.gtceumorethings.common.data.MoreThingsMachines;
import com.tsuo1951.gtceumorethings.common.data.MoreThingsBlocks;
import com.tsuo1951.gtceumorethings.common.data.MoreThingsMachines;
import com.tsuo1951.gtceumorethings.common.data.MoreThingsRecipeTypes;

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

        ASSEMBLER_RECIPES.recipeBuilder("plasma_synthesis_reactor")
                .inputItems(GTMachines.HULL[ZPM].asStack())
                .inputItems(GTItems.FIELD_GENERATOR_ZPM.asStack(4))
                .inputItems(CustomTags.ZPM_CIRCUITS, 4)
                .inputItems(MoreThingsBlocks.PLASMA_SYNTHESIS_CASING.asStack(4))
                .inputItems(MoreThingsBlocks.MAGNETIC_CONFINEMENT_COIL.asStack(4))
                .inputItems(plate, TitaniumCarbideMXene, 4)
                .outputItems(MoreThingsMachines.PLASMA_SYNTHESIS_REACTOR.asStack())
                .duration(800)
                .EUt(VA[ZPM])
                .save(provider);

        MoreThingsRecipeTypes.PLASMA_SYNTHESIS_RECIPES.recipeBuilder("mxene_plasma_synthesis")
                .inputItems(dust, Titanium, 3)
                .inputItems(dust, Carbon, 2)
                .inputFluids(Helium.getFluid(FluidStorageKeys.PLASMA, 1000))
                .outputItems(dust, TitaniumCarbideMXene, 4)
                .outputFluids(Helium.getFluid(1000))
                .addData("mode", 1)
                .addData("min_temp", 30000)
                .addData("max_temp", 0)
                .addData("min_density", 10)
                .addData("min_confinement", 800)
                .duration(200)
                .EUt(VA[ZPM])
                .save(provider);

        MoreThingsRecipeTypes.PLASMA_SYNTHESIS_RECIPES.recipeBuilder("mxene_plasma_deposition")
                .inputItems(plate, TungstenSteel, 1)
                .inputItems(foil, TitaniumCarbideMXene, 2)
                .inputFluids(Oxygen.getFluid(FluidStorageKeys.PLASMA, 500))
                .outputItems(plate, TitaniumCarbideMXene, 2)
                .outputFluids(Oxygen.getFluid(500))
                .addData("mode", 2)
                .addData("min_temp", 10000)
                .addData("max_temp", 22000)
                .addData("min_density", 7)
                .addData("min_confinement", 600)
                .duration(160)
                .EUt(VA[ZPM])
                .save(provider);

        MoreThingsRecipeTypes.PLASMA_SYNTHESIS_RECIPES.recipeBuilder("black_steel_plasma_nitriding")
                .inputItems(ingot, Steel, 4)
                .inputFluids(Nitrogen.getFluid(FluidStorageKeys.PLASMA, 500))
                .outputItems(ingot, BlackSteel, 4)
                .outputFluids(Nitrogen.getFluid(500))
                .addData("mode", 3)
                .addData("min_temp", 5000)
                .addData("max_temp", 15000)
                .addData("min_density", 8)
                .addData("min_confinement", 500)
                .duration(120)
                .EUt(VA[LuV])
                .save(provider);

        MoreThingsRecipeTypes.PLASMA_SYNTHESIS_RECIPES.recipeBuilder("silicon_plasma_oxidation")
                .inputItems(dust, Silicon, 4)
                .inputFluids(Oxygen.getFluid(FluidStorageKeys.PLASMA, 1000))
                .outputItems(dust, SiliconDioxide, 12)
                .outputFluids(Oxygen.getFluid(1000))
                .addData("mode", 4)
                .addData("min_temp", 10000)
                .addData("max_temp", 25000)
                .addData("min_density", 10)
                .addData("min_confinement", 700)
                .duration(140)
                .EUt(VA[ZPM])
                .save(provider);

        MoreThingsRecipeTypes.PLASMA_SYNTHESIS_RECIPES.recipeBuilder("rutile_plasma_reduction")
                .inputItems(dust, Rutile, 3)
                .inputFluids(Iron.getFluid(FluidStorageKeys.PLASMA, 1000))
                .outputItems(ingot, Titanium, 3)
                .outputFluids(Oxygen.getFluid(2000))
                .outputFluids(Iron.getFluid(1000))
                .addData("mode", 5)
                .addData("min_temp", 20000)
                .addData("max_temp", 35000)
                .addData("min_density", 20)
                .addData("min_confinement", 1200)
                .duration(240)
                .EUt(VA[UV])
                .save(provider);
    }
}
