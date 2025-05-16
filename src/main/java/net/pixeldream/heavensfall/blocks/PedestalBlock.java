package net.pixeldream.heavensfall.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.pixeldream.heavensfall.blocks.blockentity.PedestalBlockEntity;
import org.jetbrains.annotations.Nullable;

public class PedestalBlock extends BaseEntityBlock {

    public static final MapCodec<PedestalBlock> CODEC = simpleCodec(PedestalBlock::new);

    protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 8.0, 14.0);

    @Override
    public MapCodec<? extends PedestalBlock> codec() {
        return CODEC;
    }

    public PedestalBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new PedestalBlockEntity(blockPos, blockState);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(state.getBlock() != newState.getBlock()) {
            if(level.getBlockEntity(pos) instanceof PedestalBlockEntity pedestalBlockEntity) {
                pedestalBlockEntity.drops();
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof PedestalBlockEntity pedestalBlockEntity)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        ItemStack pedestalStack = pedestalBlockEntity.inventory.getStackInSlot(0);
        ItemStack playerStack = player.getItemInHand(hand);
        if (!playerStack.isEmpty()) {
            if (pedestalStack.isEmpty()) {
                pedestalBlockEntity.inventory.setStackInSlot(0, playerStack.copyWithCount(1));
                playerStack.shrink(1);
                level.playSound(player, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 2f);
                return ItemInteractionResult.SUCCESS;
            }
            if (ItemStack.isSameItemSameComponents(playerStack, pedestalStack)) {
                return ItemInteractionResult.FAIL;
            }
            ItemStack pedestalCopy = pedestalStack.copy();
            pedestalBlockEntity.inventory.setStackInSlot(0, playerStack.copyWithCount(1));
            playerStack.shrink(1);
            if (playerStack.isEmpty()) {
                player.setItemInHand(hand, pedestalCopy);
            } else {
                if (!player.getInventory().add(pedestalCopy)) {
                    player.drop(pedestalCopy, false);
                }
            }
            level.playSound(player, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 2f);
            return ItemInteractionResult.SUCCESS;
        }
        if (!pedestalStack.isEmpty()) {
            boolean canAdd = canAddToMainInventory(player, pedestalStack);
            if (!canAdd) {
                return ItemInteractionResult.FAIL;
            }
            ItemStack pedestalCopy = pedestalStack.copy();
            if (player.getItemInHand(hand).isEmpty()) {
                player.setItemInHand(hand, pedestalCopy);
            } else {
                if (!player.getInventory().add(pedestalCopy)) {
                    player.drop(pedestalCopy, false);
                }
            }
            pedestalBlockEntity.inventory.setStackInSlot(0, ItemStack.EMPTY);
            level.playSound(player, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1f);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private boolean canAddToMainInventory(Player player, ItemStack stack) {
        for (int i = 0; i < 36; i++) {
            ItemStack slotStack = player.getInventory().getItem(i);
            if (slotStack.isEmpty()) return true;
            if (ItemStack.isSameItemSameComponents(slotStack, stack) && slotStack.getCount() < slotStack.getMaxStackSize())
                return true;
        }
        return false;
    }


}

