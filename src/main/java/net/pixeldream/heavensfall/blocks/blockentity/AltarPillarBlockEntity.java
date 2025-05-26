package net.pixeldream.heavensfall.blocks.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
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
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.pixeldream.heavensfall.blocks.RuneBlock;
import net.pixeldream.heavensfall.recipes.ritual.AngelRitualHelper;
import net.pixeldream.heavensfall.recipes.ritual.DemonRitualHelper;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class AltarPillarBlockEntity extends BlockEntity {

    public static final int RITUAL_DURATION_TICKS = 100;

    private boolean itemInRecipe = false;
    private boolean isValidRitual = false;
    private int counter = 0;
    private int soundCooldown = 0;
    private float rotation = 0;

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

    public IItemHandler getHopperHandler(@Nullable Direction side) {
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return inventory.getSlots();
            }

            @Override
            public ItemStack getStackInSlot(int slot) {
                return inventory.getStackInSlot(slot);
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                return inventory.insertItem(slot, stack, simulate);
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (!inventory.getStackInSlot(slot).isEmpty() && canExtractOutput()) {
                    return inventory.extractItem(slot, amount, simulate);
                }
                return ItemStack.EMPTY;
            }

            @Override
            public int getSlotLimit(int slot) {
                return inventory.getSlotLimit(slot);
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return false;
            }
        };
    }

    public boolean canExtractOutput() {
        if (isValidRitualRunning()) return false;
        ItemStack stack = inventory.getStackInSlot(0);
        if (stack.isEmpty()) return false;

        return AngelRitualHelper.getAllResultItems().contains(stack.getItem());
    }

    public AltarPillarBlockEntity(BlockPos pos, BlockState state) {
        super(HFBlockEntities.ALTAR_PILLAR_ENTITY.get(), pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide() && itemInRecipe) {
            boolean chalkValid = hasValidChalkMultiblock(level, pos);
            boolean recipeValid = AngelRitualHelper.isValidRecipe(level, pos, inventory.getStackInSlot(0));
            isValidRitual = chalkValid && recipeValid;

            if (isValidRitual) {
                if (counter == 0 && level instanceof ServerLevel serverLevel) {
                    AngelRitualHelper.spawnAngelRitualStartEffect(serverLevel, pos);
                }

                List<BlockEntity> pedestals = AngelRitualHelper.getSurroundingPedestals(pos, level);
                for (BlockEntity be : pedestals) {
                    if (be instanceof PedestalTableBlockEntity pedestal) {
                        if (!pedestal.isAnimating()) {
                            pedestal.startAnimation(pos);
                        }
                        pedestal.setAltarPos(pos);
                    }
                }
            }
        }

        if (!level.isClientSide() && isValidRitual) {
            List<BlockEntity> pedestals = AngelRitualHelper.getSurroundingPedestals(pos, level);

            if (soundCooldown-- <= 0) {
                level.playSound(null, pos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 0.1f, 2.0f);
                soundCooldown = 20;
            }

            if (counter++ % 10 == 0) {
                AngelRitualHelper.spawnSmokeAtPedestals(level, pos);
            }

            if (counter >= RITUAL_DURATION_TICKS) {
                Item result = AngelRitualHelper.getResultForRecipe(level, pos, inventory.getStackInSlot(0));
                AngelRitualHelper.clearItemsFromPedestals(level, pos);
                inventory.setStackInSlot(0, new ItemStack(result));

                for (BlockEntity be : pedestals) {
                    if (be instanceof PedestalTableBlockEntity pedestal) {
                        pedestal.resetAnimation();
                    }
                }

                level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
                level.playSound(null, pos, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.BLOCKS, 1.0f, 1.0f);
                AngelRitualHelper.spawnRitualCompletionParticles(level, pos);

                counter = 0;
                itemInRecipe = false;
                isValidRitual = false;
                soundCooldown = 0;
            }
        }
    }

    public boolean hasValidChalkMultiblock(Level level, BlockPos pos) {
        BlockPos[] offsets = {
                pos.north(), pos.south(), pos.east(), pos.west(),
                pos.north().east(), pos.north().west(),
                pos.south().east(), pos.south().west()
        };
        for (BlockPos offset : offsets) {
            if (!(level.getBlockState(offset).getBlock() instanceof RuneBlock)) {
                return false;
            }
        }
        return true;
    }

    public float getRenderingRotation() {
        rotation = (rotation + 0.5f) % 360f;
        return rotation;
    }

    public void clearContents() {
        inventory.setStackInSlot(0, ItemStack.EMPTY);
    }

    public boolean isValidRitualRunning() {
        return isValidRitual && counter < RITUAL_DURATION_TICKS;
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
}
