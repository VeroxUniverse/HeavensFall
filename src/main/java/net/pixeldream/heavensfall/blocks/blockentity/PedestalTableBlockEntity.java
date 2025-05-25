package net.pixeldream.heavensfall.blocks.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.pixeldream.heavensfall.recipes.ritual.AngelRitualHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PedestalTableBlockEntity extends BlockEntity {

    public static final int TOTAL_ANIMATION_STEPS = 40;
    private int animationStep = 0;
    private int lastAnimationStep = 0;
    private BlockPos altarPos = null;

    public final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);

                if (tryStartRitual()) {
                    System.out.println("Ritual start from pedestal triggered.");
                }
            }
        }
    };

    private boolean tryStartRitual() {
        List<BlockPos> possibleAltars = List.of(
                worldPosition.north(2), worldPosition.south(2),
                worldPosition.east(2), worldPosition.west(2)
        );

        for (BlockPos altarPos : possibleAltars) {
            if (level.getBlockEntity(altarPos) instanceof AltarPillarBlockEntity altar) {
                boolean isValid = AngelRitualHelper.isValidRecipe(level, altarPos, altar.inventory.getStackInSlot(0));
                if (isValid) {
                    altar.setItemInRecipe(true);
                    return true;
                }
            }
        }
        return false;
    }

    private float rotation;

    public PedestalTableBlockEntity(BlockPos pos, BlockState blockState) {
        super(HFBlockEntities.PEDESTAL_TABLE_ENTITY.get(), pos, blockState);
    }

    public void setAnimationStep(int step) {
        if (this.animationStep != step) {
            this.lastAnimationStep = this.animationStep;
            this.animationStep = step;

            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            }
        }
    }

    public void setAltarPos(BlockPos altarPos) {
        this.altarPos = altarPos;
        setChanged();
        if (!level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    public void startAnimation(BlockPos altarPos) {
        this.altarPos = altarPos;
        this.animationStep = 0;
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    public void resetAnimation() {
        this.altarPos = null;
        this.animationStep = 0;
    }

    public int getAnimationStep() {
        return animationStep;
    }

    public int getLastAnimationStep() {
        return lastAnimationStep;
    }

    public BlockPos getAltarPos() {
        return altarPos;
    }

    public float getRenderingRotation() {
        rotation += 0.5f;
        if (rotation >= 360) rotation = 0;
        return rotation;
    }

    public void clearContents() {
        inventory.setStackInSlot(0, ItemStack.EMPTY);
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    public ItemStack getHeldItem() {
        return inventory.getStackInSlot(0);
    }

    public void setHeldItem(ItemStack stack) {
        inventory.setStackInSlot(0, stack);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putInt("AnimationStep", animationStep);
        if (altarPos != null) {
            tag.putLong("AltarPos", altarPos.asLong());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        animationStep = tag.getInt("AnimationStep");
        if (tag.contains("AltarPos")) {
            altarPos = BlockPos.of(tag.getLong("AltarPos"));
        } else {
            altarPos = null;
        }
    }

    public Vec3 getCurrentRenderItemPosition(float partialTicks) {
        if (altarPos == null || animationStep >= TOTAL_ANIMATION_STEPS) {
            return new Vec3(worldPosition.getX() + 0.5, worldPosition.getY() + 1.15, worldPosition.getZ() + 0.5);
        }

        double t = (lastAnimationStep + partialTicks) / (double) TOTAL_ANIMATION_STEPS;
        t = Math.min(1.0, t);

        double startX = worldPosition.getX() + 0.5;
        double startY = worldPosition.getY() + 1.15;
        double startZ = worldPosition.getZ() + 0.5;

        double endX = altarPos.getX() + 0.5;
        double endY = altarPos.getY() + 1.15;
        double endZ = altarPos.getZ() + 0.5;

        double x = startX + (endX - startX) * t;
        double y = startY + (endY - startY) * t;
        double z = startZ + (endZ - startZ) * t;

        return new Vec3(x, y, z);
    }

    public void clientTick() {
        this.lastAnimationStep = this.animationStep;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}
