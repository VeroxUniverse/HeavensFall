package net.pixeldream.heavensfall.blocks.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.pixeldream.heavensfall.blocks.ChalkBlock;
import net.pixeldream.heavensfall.recipes.ritual.RitualHelper;
import org.joml.Vector3f;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AltarBlockEntity extends BlockEntity {

    private boolean itemInRecipe = false;
    private boolean isValidRitual = false;
    private int counter = 0;
    private int soundCooldown = 0;

    private int pulseStep = 0;
    private static final int PULSE_STEPS = 20;

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
            }
        }
    };

    private float rotation;

    public AltarBlockEntity(BlockPos pos, BlockState state) {
        super(HFBlockEntities.ALTAR_ENTITY.get(), pos, state);
    }

    public boolean hasValidChalkMultiblock(Level level, BlockPos pos) {
        BlockPos[] offsets = {
                pos.north(), pos.south(), pos.east(), pos.west(),
                pos.north().east(), pos.north().west(),
                pos.south().east(), pos.south().west()
        };

        for (BlockPos offsetPos : offsets) {
            if (!(level.getBlockState(offsetPos).getBlock() instanceof ChalkBlock)) {
                return false;
            }
        }

        return true;
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

    public void setItemInRecipe(boolean value) {
        this.itemInRecipe = value;
    }

    public boolean isItemInRecipe() {
        return this.itemInRecipe;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
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

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (itemInRecipe && !level.isClientSide()) {
            boolean chalkCheck = hasValidChalkMultiblock(level, pos);
            boolean recipeCheck = RitualHelper.isValidRecipe(level, pos, inventory.getStackInSlot(0));
            isValidRitual = chalkCheck && recipeCheck;

            if (isValidRitual) {
                List<BlockEntity> pedestals = RitualHelper.getSurroundingPedestals(pos, level);

                for (BlockEntity pedestal : pedestals) {
                    if (pedestal instanceof PedestalBlockEntity pedestalBE && pedestalBE.getAltarPos() == null) {
                        pedestalBE.startAnimation(pos);
                    }
                }
            }
        }

        if (isValidRitual && !level.isClientSide()) {
            List<BlockEntity> pedestals = RitualHelper.getSurroundingPedestals(pos, level);
            int maxCounter = 100;

            int animationStep = (int) ((double) counter / maxCounter * PedestalBlockEntity.TOTAL_ANIMATION_STEPS);
            animationStep = Math.min(animationStep, PedestalBlockEntity.TOTAL_ANIMATION_STEPS);

            for (BlockEntity pedestal : pedestals) {
                if (pedestal instanceof PedestalBlockEntity pedestalBE) {
                    pedestalBE.setAnimationStep(animationStep);
                    pedestalBE.setAltarPos(pos);
                }
            }

            for (int pulseCount = 0; pulseCount < 3; pulseCount++) {
                for (BlockEntity pedestal : pedestals) {
                    if (pedestal instanceof PedestalBlockEntity pedestalBE) {
                        ItemStack stack = pedestalBE.getHeldItem();
                        Vector3f color = RitualHelper.getColorForItem(stack.getItem());
                        DustParticleOptions dust = new DustParticleOptions(color, 1.0f);

                        Vec3 from = pedestalBE.getCurrentRenderItemPosition(pulseStep / (float) PULSE_STEPS);
                        Vec3 to = new Vec3(pos.getX() + 0.5, pos.getY() + 1.15, pos.getZ() + 0.5);

                        RitualHelper.spawnParticleBeamFromTo(
                                (ServerLevel) level, from, to, dust, pulseStep, PULSE_STEPS
                        );
                    }
                }
                pulseStep = (pulseStep + 1) % PULSE_STEPS;
            }

            if (soundCooldown <= 0) {
                level.playSound(null, pos, SoundEvents.BEACON_AMBIENT,
                        net.minecraft.sounds.SoundSource.BLOCKS, 0.1f, 2.0f);
                soundCooldown = 20;
            } else {
                soundCooldown--;
            }

            if (counter < maxCounter) {
                counter++;
                if (counter % 10 == 0) {
                    RitualHelper.spawnSmokeAtPedestals(level, pos);
                }
            }

            if (counter >= maxCounter) {
                Item result = RitualHelper.getResultForRecipe(level, pos, inventory.getStackInSlot(0));
                RitualHelper.clearItemsFromPedestals(level, pos);
                inventory.setStackInSlot(0, new ItemStack(result));

                for (BlockEntity pedestal : pedestals) {
                    if (pedestal instanceof PedestalBlockEntity pedestalBE) {
                        pedestalBE.resetAnimation();
                    }
                }

                level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
                level.playSound(null, pos, SoundEvents.WITHER_SPAWN,
                        net.minecraft.sounds.SoundSource.BLOCKS, 1f, 2.0f);

                counter = 0;
                itemInRecipe = false;
                isValidRitual = false;
                pulseStep = 0;
                soundCooldown = 0;
            }
        }
    }

}
