package net.pixeldream.heavensfall.blocks.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustParticleOptions;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.pixeldream.heavensfall.blocks.RuneBlock;
import net.pixeldream.heavensfall.recipes.ritual.AngelRitualHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public class AltarPillarBlockEntity extends BlockEntity {

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

    public AltarPillarBlockEntity(BlockPos pos, BlockState state) {
        super(HFBlockEntities.ALTAR_PILLAR_ENTITY.get(), pos, state);
    }

    public boolean hasValidChalkMultiblock(Level level, BlockPos pos) {
        BlockPos[] offsets = {
                pos.north(), pos.south(), pos.east(), pos.west(),
                pos.north().east(), pos.north().west(),
                pos.south().east(), pos.south().west()
        };

        for (BlockPos offsetPos : offsets) {
            if (!(level.getBlockState(offsetPos).getBlock() instanceof RuneBlock)) {
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
            boolean recipeCheck = AngelRitualHelper.isValidRecipe(level, pos, inventory.getStackInSlot(0));
            isValidRitual = chalkCheck && recipeCheck;

            if (isValidRitual) {

                if (counter == 0) {
                    if (level instanceof ServerLevel serverLevel) {
                        AngelRitualHelper.spawnAngelRitualStartEffect(serverLevel, pos);
                    }
                }

                List<BlockEntity> pedestals = AngelRitualHelper.getSurroundingPedestals(pos, level);

                for (BlockEntity pedestal : pedestals) {
                    if (pedestal instanceof PedestalTableBlockEntity pedestalBE && pedestalBE.getAltarPos() == null) {
                        pedestalBE.startAnimation(pos);
                    }
                }
            }
        }

        if (isValidRitual && !level.isClientSide()) {
            List<BlockEntity> pedestals = AngelRitualHelper.getSurroundingPedestals(pos, level);
            int maxCounter = 100;

            int animationStep = (int) ((double) counter / maxCounter * PedestalTableBlockEntity.TOTAL_ANIMATION_STEPS);
            animationStep = Math.min(animationStep, PedestalTableBlockEntity.TOTAL_ANIMATION_STEPS);

            for (BlockEntity pedestal : pedestals) {
                if (pedestal instanceof PedestalTableBlockEntity pedestalBE) {
                    pedestalBE.setAnimationStep(animationStep);
                    pedestalBE.setAltarPos(pos);
                }
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
                    AngelRitualHelper.spawnSmokeAtPedestals(level, pos);
                }
            }

            if (counter >= maxCounter) {
                Item result = AngelRitualHelper.getResultForRecipe(level, pos, inventory.getStackInSlot(0));
                AngelRitualHelper.clearItemsFromPedestals(level, pos);
                inventory.setStackInSlot(0, new ItemStack(result));

                for (BlockEntity pedestal : pedestals) {
                    if (pedestal instanceof PedestalTableBlockEntity pedestalBE) {
                        pedestalBE.resetAnimation();
                    }
                }

                level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
                level.playSound(null, pos, SoundEvents.DRAGON_FIREBALL_EXPLODE,
                        SoundSource.BLOCKS, 1.0f, 1.0f);

                AngelRitualHelper.spawnRitualCompletionParticles(level, pos);

                counter = 0;
                itemInRecipe = false;
                isValidRitual = false;
                pulseStep = 0;
                soundCooldown = 0;
            }
        }
    }

}