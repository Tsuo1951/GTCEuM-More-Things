package com.tsuo1951.gtceumorethings.common.machine.multiblock;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DropSaved;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.utils.Size;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProcessingArrayMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            ProcessingArrayMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @DropSaved
    protected final NotifiableItemStackHandler machineStorage;

    private final int maxMachines;

    public ProcessingArrayMachine(IMachineBlockEntity holder, int maxMachines) {
        super(holder);
        this.maxMachines = maxMachines;
        this.machineStorage = new NotifiableItemStackHandler(this, 1, IO.NONE) {
            @Override
            public int getSlotLimit(int slot) {
                return maxMachines;
            }

            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                if ((recipeLogic != null && recipeLogic.isWorking()) || !isValidMachineItem(stack)) {
                    return stack;
                }
                ItemStack existing = getStackInSlot(slot);
                int maxInsert = maxMachines - existing.getCount();
                if (maxInsert <= 0) {
                    return stack;
                }
                if (stack.getCount() > maxInsert) {
                    ItemStack toInsert = stack.copyWithCount(maxInsert);
                    ItemStack remainder = super.insertItem(slot, toInsert, simulate);
                    return stack.copyWithCount(stack.getCount() - maxInsert + remainder.getCount());
                }
                return super.insertItem(slot, stack, simulate);
            }

            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (recipeLogic != null && recipeLogic.isWorking()) {
                    return ItemStack.EMPTY;
                }
                return super.extractItem(slot, amount, simulate);
            }

            @Override
            public void onContentsChanged() {
                super.onContentsChanged();
                onMachineStorageChanged();
            }
        };
        @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return isValidMachineItem(stack);
            }
        }
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public static boolean isValidMachineItem(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof MetaMachineItem machineItem)) {
            return false;
        }
        MachineDefinition def = machineItem.getDefinition();
        if (def instanceof MultiblockMachineDefinition) {
            return false;
        }
        if (def.getTier() < GTValues.LV || def.getTier() >= GTValues.V.length) {
            return false;
        }
        GTRecipeType[] types = def.getRecipeTypes();
        if (types == null || types.length == 0) {
            return false;
        }
        GTRecipeType primary = types[0];
        return primary != GTRecipeTypes.DUMMY_RECIPES
                && primary != GTRecipeTypes.STEAM_TURBINE_FUELS
                && primary != GTRecipeTypes.GAS_TURBINE_FUELS
                && primary != GTRecipeTypes.COMBUSTION_GENERATOR_FUELS
                && primary != GTRecipeTypes.PLASMA_GENERATOR_FUELS;
    }

    @Nullable
    public MachineDefinition getInnerMachineDefinition() {
        ItemStack stack = machineStorage.getStackInSlot(0);
        if (!isValidMachineItem(stack)) {
            return null;
        }
        return ((MetaMachineItem) stack.getItem()).getDefinition();
    }

    public boolean isValidMachine() {
        return getInnerMachineDefinition() != null && getMachineCount() > 0;
    }

    public int getMachineCount() {
        ItemStack stack = machineStorage.getStackInSlot(0);
        if (!isValidMachineItem(stack)) {
            return 0;
        }
        return Math.min(stack.getCount(), maxMachines);
    }

    public int getInnerMachineTier() {
        MachineDefinition inner = getInnerMachineDefinition();
        return inner == null ? 0 : inner.getTier();
    }

    protected void onMachineStorageChanged() {
        if (getActiveRecipeType() >= getRecipeTypes().length) {
            setActiveRecipeType(0);
        }
        if (recipeLogic != null) {
            recipeLogic.markLastRecipeDirty();
            recipeLogic.updateTickSubscription();
        }
    }

    @Override
    public @NotNull GTRecipeType[] getRecipeTypes() {
        MachineDefinition inner = getInnerMachineDefinition();
        if (inner != null && inner.getRecipeTypes() != null && inner.getRecipeTypes().length > 0) {
            return inner.getRecipeTypes();
        }
        return super.getRecipeTypes();
    }

    @Override
    public @NotNull GTRecipeType getRecipeType() {
        GTRecipeType[] types = getRecipeTypes();
        int index = getActiveRecipeType();
        if (index < 0 || index >= types.length) {
            index = 0;
        }
        return types[index];
    }

    @Override
    public long getOverclockVoltage() {
        if (!isValidMachine()) {
            return 0L;
        }
        long machineMaxVoltage = GTValues.V[getInnerMachineTier()] * getMachineCount();
        return Math.min(super.getOverclockVoltage(), machineMaxVoltage);
    }

    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof ProcessingArrayMachine pa)) {
            return ModifierFunction.cancel(Component.translatable("gtceu.recipe_modifier.invalid_machine"));
        }
        if (!pa.isValidMachine()) {
            return ModifierFunction.cancel(Component.translatable("gtceumorethings.recipe_modifier.no_machine"));
        }
        if (RecipeHelper.getRecipeEUtTier(recipe) > pa.getInnerMachineTier()) {
            return ModifierFunction.cancel(Component.translatable("gtceumorethings.recipe_modifier.machine_tier_too_low"));
        }
        int maxParallel = pa.getMachineCount();
        int achievable = ParallelLogic.getParallelAmount(machine, recipe, maxParallel);
        if (achievable <= 1) {
            return ModifierFunction.IDENTITY;
        }
        return ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(achievable))
                .parallels(achievable)
                .build();
    }

    @Override
    public Widget createUIWidget() {
        Widget widget = super.createUIWidget();
        if (widget instanceof WidgetGroup group) {
            Size size = group.getSize();
            group.addWidget(new SlotWidget(machineStorage, 0, size.width - 30, size.height - 30)
                    .setBackgroundTexture(GuiTextures.SLOT));
        }
        return widget;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed()) {
            if (isValidMachine()) {
                textList.add(Component.translatable("gtceumorethings.multiblock.processing_array.machine",
                                getMachineCount(), machineStorage.getStackInSlot(0).getHoverName())
                        .withStyle(ChatFormatting.AQUA));
                textList.add(Component.translatable("gtceumorethings.multiblock.processing_array.max_tier",
                                GTValues.VNF[getInnerMachineTier()])
                        .withStyle(ChatFormatting.GOLD));
            } else {
                textList.add(Component.translatable("gtceumorethings.multiblock.processing_array.empty")
                        .withStyle(ChatFormatting.RED));
            }
        }
    }
}
