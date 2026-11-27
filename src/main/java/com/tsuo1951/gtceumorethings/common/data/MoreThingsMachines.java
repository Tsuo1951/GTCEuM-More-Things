package com.tsuo1951.gtceumorethings.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.tsuo1951.gtceumorethings.GTCEuMoreThings;
import com.tsuo1951.gtceumorethings.common.machine.multiblock.ProcessingArrayMachine;
import net.minecraft.network.chat.Component;

public class MoreThingsMachines {

    public static MultiblockMachineDefinition PROCESSING_ARRAY;

    public static void init() {
        PROCESSING_ARRAY = GTCEuMoreThings.REGISTRATE
                .multiblock("processing_array", holder -> new ProcessingArrayMachine(holder, 16))
                .langValue("Processing Array")
                .tier(GTValues.IV)
                .rotationState(RotationState.ALL)
                .recipeType(GTRecipeTypes.DUMMY_RECIPES)
                .recipeModifiers(ProcessingArrayMachine::recipeModifier, GTRecipeModifiers.OC_NON_PERFECT)
                .appearanceBlock(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST)
                .pattern(definition -> FactoryBlockPattern.start()
                        .aisle("CCC", "CCC", "CCC")
                        .aisle("CCC", "CAC", "CCC")
                        .aisle("CCC", "CSC", "CCC")
                        .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                        .where('A', Predicates.air())
                        .where('C', Predicates.blocks(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get())
                                .setMinGlobalLimited(14)
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.INPUT_ENERGY)
                                        .setMinGlobalLimited(1)
                                        .setMaxGlobalLimited(2)
                                        .setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1)))
                        .build())
                .workableCasingModel(
                        GTCEu.id("block/casings/solid/machine_casing_robust_tungstensteel"),
                        GTCEu.id("block/multiblock/gcym/large_assembler"))
                .tooltips(
                        Component.translatable("gtceumorethings.machine.processing_array.tooltip.0"),
                        Component.translatable("gtceumorethings.machine.processing_array.tooltip.1"))
                .register();
    }
}
