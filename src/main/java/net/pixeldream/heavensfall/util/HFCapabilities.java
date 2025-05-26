package net.pixeldream.heavensfall.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.pixeldream.heavensfall.blocks.blockentity.*;

public class HFCapabilities {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                HFBlockEntities.PEDESTAL_ENTITY.get(),
                PedestalBlockEntity::getHopperHandler
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                HFBlockEntities.ALTAR_ENTITY.get(),
                AltarBlockEntity::getHopperHandler
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                HFBlockEntities.PEDESTAL_TABLE_ENTITY.get(),
                PedestalTableBlockEntity::getHopperHandler
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                HFBlockEntities.ALTAR_PILLAR_ENTITY.get(),
                (blockEntity, direction) -> {
                    BlockState state = blockEntity.getBlockState();
                    BlockPos basePos = blockEntity.getBlockPos();
                    if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)
                            && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
                        basePos = basePos.below();
                    }

                    BlockEntity lowerBE = blockEntity.getLevel().getBlockEntity(basePos);
                    if (lowerBE instanceof AltarPillarBlockEntity altar) {
                        return altar.getHopperHandler(direction);
                    }

                    return null;
                }
        );

    }
}