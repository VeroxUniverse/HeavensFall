package net.pixeldream.heavensfall.blocks.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
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
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.blocks.ChalkBlock;
import net.pixeldream.heavensfall.recipes.ritual.DemonRitualHelper;
import net.pixeldream.heavensfall.util.HFCapabilities;
import org.jetbrains.annotations.Nullable;
import org.jline.utils.InfoCmp;

import java.util.List;

public class AltarBlockEntity extends BlockEntity {

    public static final int RITUAL_DURATION_TICKS = 100;

    private boolean itemInRecipe = false;
    private boolean isValidRitual = false;
    private int counter = 0;
    private int soundCooldown = 0;
    private float rotation = 0f;

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

        return DemonRitualHelper.getAllResultItems().contains(stack.getItem());
    }

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
        tag.putBoolean("ItemInRecipe", itemInRecipe);
        tag.putBoolean("IsValidRitual", isValidRitual);
        tag.putInt("Counter", counter);
        tag.putInt("SoundCooldown", soundCooldown);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        itemInRecipe = tag.getBoolean("ItemInRecipe");
        isValidRitual = tag.getBoolean("IsValidRitual");
        counter = tag.getInt("Counter");
        soundCooldown = tag.getInt("SoundCooldown");
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
            boolean recipeCheck = DemonRitualHelper.isValidRecipe(level, pos, inventory.getStackInSlot(0));
            isValidRitual = chalkCheck && recipeCheck;

            if (isValidRitual) {
                List<BlockEntity> pedestals = DemonRitualHelper.getSurroundingPedestals(pos, level);
                for (BlockEntity pedestal : pedestals) {
                    if (pedestal instanceof PedestalBlockEntity pedestalBE && !pedestalBE.isAnimating()) {
                        pedestalBE.startAnimation(pos);
                    }
                }
            }
        }

        if (isValidRitual && !level.isClientSide()) {
            List<BlockEntity> pedestals = DemonRitualHelper.getSurroundingPedestals(pos, level);
            DemonRitualHelper.spawnItemTrailParticles(level, pos);
            for (BlockEntity pedestal : pedestals) {
                if (pedestal instanceof PedestalBlockEntity pedestalBE) {
                    pedestalBE.setAltarPos(pos);
                    pedestalBE.clientTick();
                }
            }

            if (soundCooldown-- <= 0) {
                level.playSound(null, pos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 0.1f, 2.0f);
                soundCooldown = 20;
            }

            if (counter++ >= RITUAL_DURATION_TICKS) {
                Item result = DemonRitualHelper.getResultForRecipe(level, pos, inventory.getStackInSlot(0));
                inventory.setStackInSlot(0, new ItemStack(result));

                for (BlockEntity pedestal : pedestals) {
                    if (pedestal instanceof PedestalBlockEntity pedestalBE) {
                        pedestalBE.clearContents();
                        pedestalBE.resetAnimation();
                    }
                }

                level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
                if (level instanceof ServerLevel server) {
                    DemonRitualHelper.spawnExplosionDust(server, pos, 3.0f, 120);
                    level.playSound(null, pos, SoundEvents.WITHER_SPAWN, SoundSource.BLOCKS, 1.0f, 2.0f);
                }

                counter = 0;
                itemInRecipe = false;
                isValidRitual = false;
                soundCooldown = 0;
            }
        }
    }
}
