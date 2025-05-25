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

    public static final Supplier<BlockEntityType<AltarBlockEntity>> ALTAR_ENTITY = BLOCK_ENTITIES.register("altar_entity",
            () -> BlockEntityType.Builder.of(AltarBlockEntity::new, HFBlocks.ALTAR_BLOCK.get()).build(null));

    public static final Supplier<BlockEntityType<PedestalBlockEntity>> PEDESTAL_ENTITY = BLOCK_ENTITIES.register("pedestal_entity",
            () -> BlockEntityType.Builder.of(PedestalBlockEntity::new, HFBlocks.PEDESTAL_BLOCK.get()).build(null));

    public static final Supplier<BlockEntityType<AltarPillarBlockEntity>> ALTAR_PILLAR_ENTITY = BLOCK_ENTITIES.register("altar_pillar_entity",
            () -> BlockEntityType.Builder.of(AltarPillarBlockEntity::new, HFBlocks.ALTAR_PILLAR_BLOCK.get()).build(null));

    public static final Supplier<BlockEntityType<PedestalTableBlockEntity>> PEDESTAL_TABLE_ENTITY = BLOCK_ENTITIES.register("pedestal_table_entity",
            () -> BlockEntityType.Builder.of(PedestalTableBlockEntity::new, HFBlocks.PEDESTAL_TABLE_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

}
