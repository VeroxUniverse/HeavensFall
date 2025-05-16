package net.pixeldream.heavensfall.blocks.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.pixeldream.heavensfall.recipes.ritual.RitualHelper;
import org.jetbrains.annotations.Nullable;

public class AltarBlockEntity extends BlockEntity {

    private static final String NBT_HELD_ITEM = "heldItem";
    private ItemStack heldItem = ItemStack.EMPTY;
    private boolean itemInRecipe = false;
    private boolean isValidRitual = false;
    private int counter = 0;

    public final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    private float rotation;

    public AltarBlockEntity(BlockPos pos, BlockState blockState) {
        super(HFBlockEntities.ALTAR_ENTITY.get(), pos, blockState);
    }

    public float getRenderingRotation() {
        rotation += 0.5f;
        if(rotation >= 360) {
            rotation = 0;
        }
        return rotation;
    }

    public void clearContents() {
        inventory.setStackInSlot(0, ItemStack.EMPTY);
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for(int i = 0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    public ItemStack getHeldItem() {
        return heldItem;
    }

    public void setHeldItem(ItemStack newItem) {
        heldItem = newItem;
        setChanged();
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
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    public void setItemInRecipe(boolean value) {
        this.itemInRecipe = value;
    }

    public boolean isItemInRecipe() {
        return this.itemInRecipe;
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState){
        if(itemInRecipe && !pLevel.isClientSide()){
            isValidRitual = RitualHelper.isValidRecipe(pLevel, pPos, getHeldItem());
        }

        if(isValidRitual && !pLevel.isClientSide()){
            if(counter < 100){
                counter++;
                if(counter % 10 == 0){
                    Item result = RitualHelper.getResultForRecipe(pLevel, pPos, getHeldItem());

                    ParticleOptions particles = RitualHelper.getParticleForItem(result);
                    if (particles != null) {
                        RitualHelper.spawnSmokeAtPedestals(pLevel, pPos);
                    }
                }
            }
            if(counter == 100){
                Item result = RitualHelper.getResultForRecipe(pLevel, pPos, getHeldItem());

                ParticleOptions particles = RitualHelper.getParticleForItem(result);

                RitualHelper.clearItemsFromPedestals(pLevel, pPos);
                BlockState prevState = this.getBlockState();
                this.setHeldItem(new ItemStack(result));
                BlockState nextState = this.getBlockState();
                pLevel.sendBlockUpdated(pPos, prevState, nextState, Block.UPDATE_CLIENTS);

                counter = 0;
                itemInRecipe = false;
                isValidRitual = false;
            }
        }
    }
}