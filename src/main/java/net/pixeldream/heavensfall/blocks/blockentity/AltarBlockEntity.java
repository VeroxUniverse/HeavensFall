package net.pixeldream.heavensfall.blocks.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class AltarBlockEntity extends BlockEntity {

    private static final String NBT_HELD_ITEM = "heldItem";
    private ItemStack heldItem = ItemStack.EMPTY;

    public AltarBlockEntity(BlockPos pos, BlockState state) {
        super(HFBlockEntities.ALTAR_ENTITY.get(), pos, state);
    }

    public ItemStack getItem() {
        return heldItem;
    }

    public void setItem(ItemStack stack) {
        this.heldItem = stack;
        setChanged();
    }

    public void clearItem() {
        this.heldItem = ItemStack.EMPTY;
        setChanged();
    }

    public ItemInteractionResult onUse(Player player, InteractionHand hand) {
        if (level == null || level.isClientSide) return ItemInteractionResult.SUCCESS;

        ItemStack held = player.getItemInHand(hand);
        if (!heldItem.isEmpty() && held.isEmpty()) {
            player.setItemInHand(hand, heldItem);
            clearItem();
        } else if (heldItem.isEmpty() && !held.isEmpty()) {
            heldItem = held.split(1);
        }

        setChanged();
        return ItemInteractionResult.SUCCESS;
    }

    public List<PedestalBlockEntity> getLinkedPedestals() {
        List<PedestalBlockEntity> pedestals = new ArrayList<>();
        if (level == null) return pedestals;

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (Math.abs(dx) + Math.abs(dz) != 2) continue;
                BlockPos pos = worldPosition.offset(dx, 0, dz);
                if (level.getBlockEntity(pos) instanceof PedestalBlockEntity pedestal) {
                    pedestals.add(pedestal);
                }
            }
        }

        return pedestals;
    }

    public void drops() {
        SimpleContainer simpleContainer = new SimpleContainer(heldItem);
        Containers.dropContents(this.level, this.worldPosition, simpleContainer);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    private CompoundTag writeNBT(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        if (!heldItem.isEmpty()) {
            nbt.put(NBT_HELD_ITEM, heldItem.save(pRegistries));
        } else {
            nbt.put(NBT_HELD_ITEM, new CompoundTag());
        }
        return nbt;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        readNBT(tag, registries);
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag, HolderLookup.Provider registries) {
        writeNBT(tag, registries);
    }

    private CompoundTag readNBT(CompoundTag nbt, HolderLookup.Provider registries) {
        if (nbt.contains(NBT_HELD_ITEM)) {
            heldItem = ItemStack.parseOptional(registries, nbt.getCompound(NBT_HELD_ITEM));
        }
        return nbt;
    }

}
