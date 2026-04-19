package net.pixeldream.heavensfall.blocks.blockentity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.blocks.HFBlocks;

import java.util.function.Supplier;

public class HFBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, HeavensFallMod.MODID);

    public static final Supplier<BlockEntityType<DemonAltarBlockEntity>> ALTAR_ENTITY = BLOCK_ENTITIES.register("altar_entity",
            () -> BlockEntityType.Builder.of(DemonAltarBlockEntity::new, HFBlocks.ALTAR_BLOCK.get()).build(null));

    public static final Supplier<BlockEntityType<DemonPedestalBlockEntity>> PEDESTAL_ENTITY = BLOCK_ENTITIES.register("pedestal_entity",
            () -> BlockEntityType.Builder.of(DemonPedestalBlockEntity::new, HFBlocks.PEDESTAL_BLOCK.get()).build(null));

    public static final Supplier<BlockEntityType<AngelAltarBlockEntity>> ALTAR_PILLAR_ENTITY = BLOCK_ENTITIES.register("altar_pillar_entity",
            () -> BlockEntityType.Builder.of(AngelAltarBlockEntity::new, HFBlocks.ALTAR_PILLAR_BLOCK.get()).build(null));

    public static final Supplier<BlockEntityType<AngelPedestalBlockEntity>> PEDESTAL_TABLE_ENTITY = BLOCK_ENTITIES.register("pedestal_table_entity",
            () -> BlockEntityType.Builder.of(AngelPedestalBlockEntity::new, HFBlocks.PEDESTAL_TABLE_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

}
