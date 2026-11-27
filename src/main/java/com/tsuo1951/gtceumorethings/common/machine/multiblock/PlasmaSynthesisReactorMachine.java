package com.tsuo1951.gtceumorethings.common.machine.multiblock;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.CoilWorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.DropSaved;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.utils.Size;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlasmaSynthesisReactorMachine extends CoilWorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            PlasmaSynthesisReactorMachine.class, CoilWorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    public static final int STABILITY_STABLE = 0;
    public static final int STABILITY_UNSTABLE = 1;
    public static final int STABILITY_CRITICAL = 2;

    @Persisted
    @DropSaved
    protected final NotifiableItemStackHandler circuitStorage;

    @DescSynced
    private int magneticCoilCount;

    @DescSynced
    private int currentDensity;

    @DescSynced
    private int maxAllowedDensity;

    @DescSynced
    private int confinementCapacity;

    @DescSynced
    private int plasmaTemperature;

    @DescSynced
    private int stabilityState = STABILITY_CRITICAL;

    private TickableSubscription metricTickSubs;

    public PlasmaSynthesisReactorMachine(IMachineBlockEntity holder) {
        super(holder);
        this.circuitStorage = new NotifiableItemStackHandler(this, 1, IO.NONE) {
            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return stack.is(GTItems.PROGRAMMABLE_CIRCUIT.get());
            }

            @Override
            public void onContentsChanged() {
                super.onContentsChanged();
                if (recipeLogic != null) {
                    recipeLogic.markLastRecipeDirty();
                    recipeLogic.updateTickSubscription();
                }
            }
        };
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        this.magneticCoilCount = getMultiblockState().getMatchContext().getOrDefault("magnetic_coils", 0);
        updatePlasmaMetrics();
        if (!isRemote()) {
            metricTickSubs = subscribeServerTick(metricTickSubs, this::tickPlasmaMetrics);
        }
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (metricTickSubs != null) {
            metricTickSubs.unsubscribe();
            metricTickSubs = null;
        }
        this.magneticCoilCount = 0;
        this.currentDensity = 0;
        this.maxAllowedDensity = 0;
        this.confinementCapacity = 0;
        this.plasmaTemperature = 0;
        this.stabilityState = STABILITY_CRITICAL;
    }

    @Override
    public void onUnload() {
        super.onUnload();
        if (metricTickSubs != null) {
            metricTickSubs.unsubscribe();
            metricTickSubs = null;
        }
    }

    private void tickPlasmaMetrics() {
        if (getOffsetTimer() % 10 == 0) {
            updatePlasmaMetrics();
        }
    }

    public int getOperationMode() {
        ItemStack stack = circuitStorage.getStackInSlot(0);
        if (stack.isEmpty() || !stack.is(GTItems.PROGRAMMABLE_CIRCUIT.get())) {
            return 0;
        }
        if (!stack.hasTag() || !stack.getTag().contains("Configuration")) {
            return 0;
        }
        int config = stack.getTag().getInt("Configuration");
        return (config >= 1 && config <= 5) ? config : 0;
    }

    public void updatePlasmaMetrics() {
        if (!isFormed()) {
            return;
        }

        double densitySum = 0.0;
        int maxPlasmaTempBonus = 0;

        for (IRecipeHandler<?> handler : getCapabilitiesFlat(IO.IN, FluidRecipeCapability.CAP)) {
            if (handler instanceof IFluidHandler fluidHandler) {
                for (int i = 0; i < fluidHandler.getTanks(); i++) {
                    FluidStack fluid = fluidHandler.getFluidInTank(i);
                    if (!fluid.isEmpty()) {
                        double weight = getPlasmaDensityWeight(fluid);
                        if (weight > 0.0) {
                            densitySum += (fluid.getAmount() / 100.0) * weight;
                            int tempBonus = getPlasmaTemperatureBonus(fluid);
                            if (tempBonus > maxPlasmaTempBonus) {
                                maxPlasmaTempBonus = tempBonus;
                            }
                        }
                    }
                }
            }
        }

        int coilTier = getCoilTier();
        int coilTemp = getCoilType().getCoilTemperature();
        int voltageBonus = Math.max(0, getTier() - GTValues.IV) * 2500;

        this.currentDensity = (int) Math.round(densitySum);
        this.confinementCapacity = this.magneticCoilCount * (25 + coilTier * 10);
        this.maxAllowedDensity = (this.magneticCoilCount * 2) + ((coilTier + 1) * 35);
        this.plasmaTemperature = (coilTemp * 2) + voltageBonus + maxPlasmaTempBonus;

        if (this.magneticCoilCount <= 0
                || this.currentDensity <= 0
                || this.currentDensity > this.confinementCapacity
                || this.currentDensity > this.maxAllowedDensity) {
            this.stabilityState = STABILITY_CRITICAL;
        } else if (this.currentDensity > (this.confinementCapacity * 3) / 4
                || this.currentDensity > (this.maxAllowedDensity * 4) / 5) {
            this.stabilityState = STABILITY_UNSTABLE;
        } else {
            this.stabilityState = STABILITY_STABLE;
        }
    }

    private static boolean matchesPlasma(FluidStack stack, Material material) {
        if (!material.hasProperty(PropertyKey.FLUID)) {
            return false;
        }
        Fluid plasma = material.getProperty(PropertyKey.FLUID).getStorage().get(FluidStorageKeys.PLASMA);
        return plasma != null && stack.getFluid() == plasma;
    }

    private static double getPlasmaDensityWeight(FluidStack stack) {
        if (matchesPlasma(stack, GTMaterials.Helium)) {
            return 1.0;
        }
        if (matchesPlasma(stack, GTMaterials.Oxygen)) {
            return 1.4;
        }
        if (matchesPlasma(stack, GTMaterials.Nitrogen)) {
            return 1.6;
        }
        if (matchesPlasma(stack, GTMaterials.Iron)) {
            return 2.4;
        }
        if (matchesPlasma(stack, GTMaterials.Nickel)) {
            return 2.8;
        }
        ResourceLocation key = ForgeRegistries.FLUIDS.getKey(stack.getFluid());
        if (key != null && key.getPath().contains("plasma")) {
            return 2.0;
        }
        return 0.0;
    }

    private static int getPlasmaTemperatureBonus(FluidStack stack) {
        if (matchesPlasma(stack, GTMaterials.Helium)) {
            return 2000;
        }
        if (matchesPlasma(stack, GTMaterials.Oxygen)) {
            return 4000;
        }
        if (matchesPlasma(stack, GTMaterials.Nitrogen)) {
            return 5000;
        }
        if (matchesPlasma(stack, GTMaterials.Iron)) {
            return 10000;
        }
        if (matchesPlasma(stack, GTMaterials.Nickel)) {
            return 14000;
        }
        ResourceLocation key = ForgeRegistries.FLUIDS.getKey(stack.getFluid());
        if (key != null && key.getPath().contains("plasma")) {
            return 8000;
        }
        return 0;
    }

    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof PlasmaSynthesisReactorMachine reactor)) {
            return ModifierFunction.cancel(Component.translatable("gtceu.recipe_modifier.invalid_machine"));
        }

        reactor.updatePlasmaMetrics();

        if (reactor.magneticCoilCount <= 0) {
            return ModifierFunction.cancel(Component.translatable("gtceumorethings.recipe_modifier.no_confinement"));
        }

        int requiredMode = recipe.data.getInt("mode");
        if (requiredMode > 0 && reactor.getOperationMode() != requiredMode) {
            return ModifierFunction.cancel(Component.translatable("gtceumorethings.recipe_modifier.wrong_mode"));
        }

        int minConfinement = recipe.data.getInt("min_confinement");
        if (reactor.confinementCapacity < minConfinement) {
            return ModifierFunction.cancel(Component.translatable("gtceumorethings.recipe_modifier.low_confinement"));
        }

        int minDensity = recipe.data.getInt("min_density");
        if (reactor.currentDensity < minDensity) {
            return ModifierFunction.cancel(Component.translatable("gtceumorethings.recipe_modifier.low_density"));
        }

        if (reactor.stabilityState == STABILITY_CRITICAL) {
            return ModifierFunction.cancel(Component.translatable("gtceumorethings.recipe_modifier.critical_stability"));
        }

        int minTemp = recipe.data.getInt("min_temp");
        int maxTemp = recipe.data.getInt("max_temp");
        if (reactor.plasmaTemperature < minTemp || (maxTemp > 0 && reactor.plasmaTemperature > maxTemp)) {
            return ModifierFunction.cancel(Component.translatable("gtceumorethings.recipe_modifier.wrong_temperature"));
        }

        if (reactor.stabilityState == STABILITY_UNSTABLE) {
            return ModifierFunction.builder()
                    .eutModifier(ContentModifier.multiplier(1.5))
                    .build();
        }

        return ModifierFunction.IDENTITY;
    }

    @Override
    public boolean onWorking() {
        if (getOffsetTimer() % 5 == 0) {
            updatePlasmaMetrics();
            if (this.stabilityState == STABILITY_CRITICAL) {
                if (recipeLogic != null) {
                    recipeLogic.interruptRecipe();
                }
                return false;
            }
            if (this.stabilityState == STABILITY_UNSTABLE) {
                long extraPenalty = GTValues.V[Math.max(GTValues.LV, getTier())] / 4;
                if (energyContainer.getEnergyStored() >= extraPenalty) {
                    energyContainer.removeEnergy(extraPenalty);
                } else {
                    if (recipeLogic != null) {
                        recipeLogic.interruptRecipe();
                    }
                    return false;
                }
            }
        }
        return super.onWorking();
    }

    @Override
    public Widget createUIWidget() {
        Widget widget = super.createUIWidget();
        if (widget instanceof WidgetGroup group) {
            Size size = group.getSize();
            group.addWidget(new SlotWidget(circuitStorage, 0, size.width - 26, 4)
                    .setBackgroundTexture(GuiTextures.SLOT));
        }
        return widget;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed()) {
            int mode = getOperationMode();
            if (mode >= 1 && mode <= 5) {
                textList.add(Component.translatable("gtceumorethings.multiblock.plasma_reactor.mode",
                                Component.translatable("gtceumorethings.recipe.plasma_mode." + mode))
                        .withStyle(ChatFormatting.AQUA));
            } else {
                textList.add(Component.translatable("gtceumorethings.multiblock.plasma_reactor.no_mode")
                        .withStyle(ChatFormatting.YELLOW));
            }

            textList.add(Component.translatable("gtceumorethings.multiblock.plasma_reactor.temperature",
                            plasmaTemperature)
                    .withStyle(ChatFormatting.GOLD));

            textList.add(Component.translatable("gtceumorethings.multiblock.plasma_reactor.density",
                            currentDensity, maxAllowedDensity)
                    .withStyle(ChatFormatting.LIGHT_PURPLE));

            textList.add(Component.translatable("gtceumorethings.multiblock.plasma_reactor.confinement",
                            confinementCapacity, magneticCoilCount)
                    .withStyle(ChatFormatting.BLUE));

            Component stabilityComp = switch (stabilityState) {
                case STABILITY_STABLE -> Component.translatable("gtceumorethings.multiblock.plasma_reactor.stability.stable")
                        .withStyle(ChatFormatting.GREEN);
                case STABILITY_UNSTABLE -> Component.translatable("gtceumorethings.multiblock.plasma_reactor.stability.unstable")
                        .withStyle(ChatFormatting.YELLOW);
                default -> Component.translatable("gtceumorethings.multiblock.plasma_reactor.stability.critical")
                        .withStyle(ChatFormatting.RED);
            };
            textList.add(Component.translatable("gtceumorethings.multiblock.plasma_reactor.stability", stabilityComp));
        }
    }
}
