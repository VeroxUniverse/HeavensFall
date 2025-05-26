package net.pixeldream.heavensfall.blocks.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.pixeldream.heavensfall.recipes.ritual.AngelRitualHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PedestalTableBlockEntity extends BlockEntity {

    public static final int TOTAL_ANIMATION_STEPS = AltarPillarBlockEntity.RITUAL_DURATION_TICKS;

    private BlockPos altarPos = null;
    private float animationStep = 0f;
    private float renderRotation = 0f;
    private float lastAnimationStep;

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


    public PedestalTableBlockEntity(BlockPos pos, BlockState state) {
        super(HFBlockEntities.PEDESTAL_TABLE_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PedestalTableBlockEntity pedestal) {
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
            if (level.getBlockEntity(pos) instanceof AltarPillarBlockEntity altar) {
                if (AngelRitualHelper.isValidRecipe(level, pos, altar.inventory.getStackInSlot(0))) {
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
            this.animationStep = 0;
            this.lastAnimationStep = 0;
            this.isAnimating = true;
            this.isMoving = true;
            if (level != null && !level.isClientSide()) {
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
        renderRotation += 0.5f;
        if (renderRotation >= 360f) renderRotation = 0f;

        if (isMoving) {
            lastAnimationStep = animationStep;
            if (animationStep < TOTAL_ANIMATION_STEPS) {
                animationStep++;
            } else {
                resetAnimation();
            }
        }

        if (isMoving && animationStep >= TOTAL_ANIMATION_STEPS * 0.85f && animationStep < TOTAL_ANIMATION_STEPS * 0.95f) {
            if (level instanceof ServerLevel server) {
                if (server.getGameTime() % 4 == 0) {
                    double cx = altarPos.getX() + 0.5;
                    double cy = altarPos.getY() + 1.5;
                    double cz = altarPos.getZ() + 0.5;

                    server.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                            cx, cy, cz,
                            8, 0.3, 0.3, 0.3, 0.1);

                    server.playSound(null, altarPos, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.BLOCKS, 0.2f, 2.0f);
                }
            }
        }
    }

    public Vec3 getCurrentRenderItemPosition(float partialTicks) {
        if (altarPos == null || (!isAnimating && !isMoving)) {
            return new Vec3(worldPosition.getX() + 0.5, worldPosition.getY() + 1.15, worldPosition.getZ() + 0.5);
        }

        double t = (animationStep + partialTicks) / TOTAL_ANIMATION_STEPS;
        t = Math.min(1.0, t);

        double startX = worldPosition.getX() + 0.5;
        double startY = worldPosition.getY() + 1.15;
        double startZ = worldPosition.getZ() + 0.5;

        double centerX = altarPos.getX() + 0.5;
        double centerY = altarPos.getY() + 1.15;
        double centerZ = altarPos.getZ() + 0.5;

        double angleOffset = getSpiralStartAngle();
        double x, y, z;

        if (t < 0.15) {
            double nt = t / 0.15;
            double eased = nt * (2 - nt);
            double spiralT = 0;
            double radius = 2.0 * (1.0 - spiralT);
            double angle = angleOffset;

            x = startX * (1 - nt) + (centerX + Math.cos(angle) * radius) * nt;
            z = startZ * (1 - nt) + (centerZ + Math.sin(angle) * radius) * nt;
            y = startY + 1.0 * eased;
        } else if (t < 0.85) {
            double spiralT = (t - 0.15) / 0.7;
            double turns = 2.5;
            double angle = turns * Math.PI * spiralT + angleOffset;
            double radius = 2.0 * (1.0 - spiralT);

            x = centerX + Math.cos(angle) * radius;
            z = centerZ + Math.sin(angle) * radius;
            y = startY + 1.0 + (2.0 * spiralT);
        } else if (t < 0.95) {
            x = centerX;
            y = centerY + 3.0;
            z = centerZ;
        } else {
            double fallT = (t - 0.95) / 0.05;
            double ease = fallT * fallT;
            x = centerX;
            y = centerY + 3.0 * (1.0 - ease);
            z = centerZ;
        }

        return new Vec3(x, y, z);
    }

    private double getSpiralStartAngle() {
        int seed = (worldPosition.getX() * 31 + worldPosition.getZ() * 17) & 0xFF;
        return (seed / 256.0) * 2 * Math.PI;
    }

    public ItemStack getHeldItem() {
        return inventory.getStackInSlot(0);
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
        if (altarPos != null) tag.putLong("AltarPos", altarPos.asLong());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        animationStep = tag.getFloat("AnimationStep");
        isAnimating = tag.getBoolean("IsAnimating");
        isMoving = tag.getBoolean("IsMoving");
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

    public float getRenderingRotation(float partialTicks) {
        return renderRotation + partialTicks * 0.5f;
    }

    public void setAltarPos(BlockPos altarPos) {
        this.altarPos = altarPos;
    }

    public boolean isAnimating() {
        return isAnimating;
    }

    public BlockPos getAltarPos() {
        return altarPos;
    }

    public void setHeldItem(ItemStack stack) {
        inventory.setStackInSlot(0, stack);
        setChanged();
        if (!level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }
}
