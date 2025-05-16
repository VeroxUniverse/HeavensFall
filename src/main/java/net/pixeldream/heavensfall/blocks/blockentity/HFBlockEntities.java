package net.pixeldream.heavensfall.blocks.blockentity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.blocks.HFBlocks;

import java.util.function.Supplier;

public class HFBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, HeavensFallMod.MODID);

    public static final Supplier<BlockEntityType<AltarBlockEntity>> ALTAR_ENTITY = TILES.register("altar_entity",
            () -> BlockEntityType.Builder.of(AltarBlockEntity::new, HFBlocks.ALTAR_BLOCK.get()).build(null));

    public static final Supplier<BlockEntityType<PedestalBlockEntity>> PEDESTAL_ENTITY = TILES.register("pedestal_entity",
            () -> BlockEntityType.Builder.of(PedestalBlockEntity::new, HFBlocks.PEDESTAL_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
        TILES.register(eventBus);
    }

}
