package com.tsuo1951.gtceumorethings.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.tsuo1951.gtceumorethings.GTCEuMoreThings;
import com.tsuo1951.gtceumorethings.common.machine.multiblock.PlasmaSynthesisReactorMachine;
import net.minecraft.network.chat.Component;

public class MoreThingsMachines {

    public static MultiblockMachineDefinition PLASMA_SYNTHESIS_REACTOR;

    private static TraceabilityPredicate magneticConfinementCoils() {
        return Predicates.custom(state -> {
            if (state.getBlockState().is(MoreThingsBlocks.MAGNETIC_CONFINEMENT_COIL.get())) {
                state.getMatchContext().increment("magnetic_coils", 1);
                return true;
            }
            return false;
        }, () -> new BlockInfo[]{
                BlockInfo.fromBlockState(MoreThingsBlocks.MAGNETIC_CONFINEMENT_COIL.get().defaultBlockState())
        });
    }

    public static void init() {
        PLASMA_SYNTHESIS_REACTOR = GTCEuMoreThings.REGISTRATE
                .multiblock("plasma_synthesis_reactor", PlasmaSynthesisReactorMachine::new)
                .langValue("Plasma Synthesis Reactor")
                .tier(GTValues.ZPM)
                .rotationState(RotationState.ALL)
                .recipeType(MoreThingsRecipeTypes.PLASMA_SYNTHESIS_RECIPES)
                .recipeModifiers(
                        PlasmaSynthesisReactorMachine::recipeModifier,
                        GTRecipeModifiers.PARALLEL_HATCH,
                        GTRecipeModifiers.OC_PERFECT_SUBTICK)
                .appearanceBlock(MoreThingsBlocks.PLASMA_SYNTHESIS_CASING)
                .pattern(definition -> FactoryBlockPattern.start()
                        .aisle(" CCCCC ", "CMGGGMC", "CGGGGGC", "CGGSGGC", "CGGGGGC", "CMGGGMC", " CCCCC ")
                        .aisle(" CPCPC ", "CMHHHMC", "CHAAAHC", "MHAAAHM", "CHAAAHC", "CMHHHMC", " CPCPC ")
                        .aisle(" CCMCC ", "CMHHHMC", "GHAAAHG", "MHAAAHM", "GHAAAHG", "CMHHHMC", " CCMCC ")
                        .setRepeatable(1, 4)
                        .aisle(" CPCPC ", "CMHHHMC", "CHAAAHC", "MHAAAHM", "CHAAAHC", "CMHHHMC", " CPCPC ")
                        .aisle(" CCCCC ", "CMGGGMC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CMGGGMC", " CCCCC ")
                        .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                        .where('A', Predicates.air())
                        .where(' ', Predicates.any())
                        .where('M', magneticConfinementCoils())
                        .where('H', Predicates.heatingCoils())
                        .where('G', Predicates.blocks(GTBlocks.FUSION_GLASS.get()))
                        .where('P', Predicates.blocks(GTBlocks.CASING_TUNGSTENSTEEL_PIPE.get()))
                        .where('C', Predicates.blocks(MoreThingsBlocks.PLASMA_SYNTHESIS_CASING.get())
                                .setMinGlobalLimited(45)
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.INPUT_ENERGY)
                                        .setMinGlobalLimited(1)
                                        .setMaxGlobalLimited(2)
                                        .setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.PARALLEL_HATCH).setMaxGlobalLimited(1))
                                .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1)))
                        .build())
                .workableCasingModel(
                        GTCEuMoreThings.id("block/plasma_synthesis_casing"),
                        GTCEu.id("block/multiblock/fusion_reactor"))
                .tooltips(
                        Component.translatable("gtceumorethings.machine.plasma_synthesis_reactor.tooltip.0"),
                        Component.translatable("gtceumorethings.machine.plasma_synthesis_reactor.tooltip.1"),
                        Component.translatable("gtceumorethings.machine.plasma_synthesis_reactor.tooltip.2"))
                .register();
    }
}
