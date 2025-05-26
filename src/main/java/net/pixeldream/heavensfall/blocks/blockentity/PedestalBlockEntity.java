package net.pixeldream.heavensfall.blocks.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.pixeldream.heavensfall.recipes.ritual.DemonRitualHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PedestalBlockEntity extends BlockEntity {

    public static final int TOTAL_ANIMATION_STEPS = AltarBlockEntity.RITUAL_DURATION_TICKS;

    private BlockPos altarPos = null;
    private float animationStep = 0f;
    private float lastAnimationStep = 0f;
    private float renderRotation = 0f;

    private boolean isAnimating = false;
    private boolean isMoving = false;
    private boolean triedToStartRitual = false;

    public final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                triedToStartRitual = false;
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public PedestalBlockEntity(BlockPos pos, BlockState state) {
        super(HFBlockEntities.PEDESTAL_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PedestalBlockEntity pedestal) {
        if (!level.isClientSide) {
            if (!pedestal.triedToStartRitual && !pedestal.isAnimating && !pedestal.isMoving && !pedestal.getHeldItem().isEmpty()) {
                if (pedestal.tryStartRitual()) {
                    pedestal.triedToStartRitual = true;
                }
            }
        }
    }

    private boolean tryStartRitual() {
        List<BlockPos> altars = List.of(
                worldPosition.north(2), worldPosition.south(2),
                worldPosition.east(2), worldPosition.west(2)
        );

        for (BlockPos pos : altars) {
            if (level.getBlockEntity(pos) instanceof AltarBlockEntity altar) {
                if (DemonRitualHelper.isValidRecipe(level, pos, altar.inventory.getStackInSlot(0))) {
                    altar.setItemInRecipe(true);
                    return true;
                }
            }
        }
        return false;
    }

    public void startAnimation(BlockPos altarPos) {
        if (!isAnimating && !isMoving) {
            this.altarPos = altarPos;
            this.animationStep = 0f;
            this.lastAnimationStep = 0f;
            this.isAnimating = true;
            this.isMoving = true;
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            }
        }
    }

    public void resetAnimation() {
        this.animationStep = TOTAL_ANIMATION_STEPS;
        this.lastAnimationStep = TOTAL_ANIMATION_STEPS;
        this.isAnimating = false;
        this.isMoving = false;
        this.altarPos = null;
        this.triedToStartRitual = false;
    }

    public void clientTick() {
        renderRotation = (renderRotation + 0.5f) % 360f;
        if (isMoving) {
            lastAnimationStep = animationStep;
            if (animationStep < TOTAL_ANIMATION_STEPS) {
                animationStep++;
            } else {
                resetAnimation();
            }
        }
    }

    public Vec3 getCurrentRenderItemPosition(float partialTicks) {
        if (altarPos == null || (!isAnimating && !isMoving)) {
            return new Vec3(worldPosition.getX() + 0.5, worldPosition.getY() + 1.15, worldPosition.getZ() + 0.5);
        }

        double t = (animationStep + partialTicks) / TOTAL_ANIMATION_STEPS;
        t = Math.min(1.0, t);

        double fromX = worldPosition.getX() + 0.5;
        double fromY = worldPosition.getY() + 1.15;
        double fromZ = worldPosition.getZ() + 0.5;

        double centerX = altarPos.getX() + 0.5;
        double centerY = altarPos.getY() + 1.15;
        double centerZ = altarPos.getZ() + 0.5;

        double angleOffset = getSpiralStartAngle();
        double spiralTurns = 2.5;
        double spiralT = t;
        double angle = angleOffset + spiralTurns * 2 * Math.PI * spiralT;
        double radius = 1.5 * (1.0 - spiralT);

        double spiralX = centerX + Math.cos(angle) * radius;
        double spiralZ = centerZ + Math.sin(angle) * radius;

        double spiralY;
        if (spiralT < 0.5) {
            spiralY = centerY + 0.5 * spiralT * 2;
        } else {
            spiralY = centerY + 0.5 * (1.0 - (spiralT - 0.5) * 2);
        }

        double blend = Math.min(t * 10.0, 1.0); // 0 -> 1 bei t = 0.1

        double x = lerp(blend, fromX, spiralX);
        double y = lerp(blend, fromY, spiralY);
        double z = lerp(blend, fromZ, spiralZ);

        if (level instanceof ServerLevel server && server.getGameTime() % 2 == 0) {
            server.sendParticles(DemonRitualHelper.BLACK_DUST, x, y + 0.05, z,
                    1, 0.02, 0.02, 0.02, 0.001);
        }

        return new Vec3(x, y, z);
    }

    private double lerp(double t, double a, double b) {
        return a + (b - a) * t;
    }

    private double getSpiralStartAngle() {
        int seed = (worldPosition.getX() * 31 + worldPosition.getZ() * 17) & 0xFF;
        return (seed / 256.0) * 2 * Math.PI;
    }

    public float getRenderingRotation(float partialTicks) {
        return renderRotation + partialTicks * 0.5f;
    }

    public boolean isAnimating() {
        return isAnimating;
    }

    public BlockPos getAltarPos() {
        return altarPos;
    }

    public void setAltarPos(BlockPos altarPos) {
        this.altarPos = altarPos;
    }

    public ItemStack getHeldItem() {
        return inventory.getStackInSlot(0);
    }

    public void setHeldItem(ItemStack stack) {
        inventory.setStackInSlot(0, stack);
        setChanged();
        if (!level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    public void clearContents() {
        inventory.setStackInSlot(0, ItemStack.EMPTY);
        triedToStartRitual = false;
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putFloat("AnimationStep", animationStep);
        tag.putBoolean("IsAnimating", isAnimating);
        tag.putBoolean("IsMoving", isMoving);
        tag.putBoolean("TriedToStartRitual", triedToStartRitual);
        if (altarPos != null) tag.putLong("AltarPos", altarPos.asLong());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        animationStep = tag.getFloat("AnimationStep");
        isAnimating = tag.getBoolean("IsAnimating");
        isMoving = tag.getBoolean("IsMoving");
        triedToStartRitual = tag.getBoolean("TriedToStartRitual");
        if (tag.contains("AltarPos")) altarPos = BlockPos.of(tag.getLong("AltarPos"));
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
