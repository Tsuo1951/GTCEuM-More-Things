package com.tsuo1951.gtceumorethings.common.data;

import com.tsuo1951.gtceumorethings.GTCEuMoreThings;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;

public class MoreThingsBlocks {

    public static BlockEntry<Block> PLASMA_SYNTHESIS_CASING;
    public static BlockEntry<Block> MAGNETIC_CONFINEMENT_COIL;

    public static void init() {
        PLASMA_SYNTHESIS_CASING = GTCEuMoreThings.REGISTRATE
                .block("plasma_synthesis_casing", Block::new)
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p
                        .sound(SoundType.METAL)
                        .strength(5.0f, 10.0f)
                        .requiresCorrectToolForDrops()
                        .isValidSpawn((state, level, pos, ent) -> false))
                .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL)
                .simpleItem()
                .register();

        MAGNETIC_CONFINEMENT_COIL = GTCEuMoreThings.REGISTRATE
                .block("magnetic_confinement_coil", Block::new)
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p
                        .sound(SoundType.METAL)
                        .strength(5.0f, 10.0f)
                        .requiresCorrectToolForDrops()
                        .isValidSpawn((state, level, pos, ent) -> false))
                .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL)
                .simpleItem()
                .register();
    }
}
