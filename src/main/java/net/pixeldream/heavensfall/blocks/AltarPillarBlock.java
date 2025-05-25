package net.pixeldream.heavensfall.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.pixeldream.heavensfall.blocks.blockentity.AltarPillarBlockEntity;
import net.pixeldream.heavensfall.blocks.blockentity.HFBlockEntities;
import net.pixeldream.heavensfall.blocks.model.MultiblockPart;
import net.pixeldream.heavensfall.blocks.model.MultiblockProperties;
import net.pixeldream.heavensfall.recipes.ritual.AngelRitualHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AltarPillarBlock extends BaseEntityBlock {

    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    private boolean updatingStructure = false;

    public static final VoxelShape SHAPE_LOWER = Block.box(2.0D, 0.01D, 2.0D, 14.0D, 16.0D, 14.0D);
    public static final VoxelShape SHAPE_UPPER = Block.box(2.0D, 0.01D, 2.0D, 14.0D, 8.0D, 14.0D);

    public static final MapCodec<AltarPillarBlock> CODEC = simpleCodec(AltarPillarBlock::new);

    public AltarPillarBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(MultiblockProperties.PART, MultiblockPart.NONE));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    private VoxelShape getShapeForState(BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER ? SHAPE_UPPER : SHAPE_LOWER;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShapeForState(state);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING, MultiblockProperties.PART);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (pos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(pos.above()).canBeReplaced(context)) {
            return this.defaultBlockState()
                    .setValue(FACING, context.getHorizontalDirection())
                    .setValue(HALF, DoubleBlockHalf.LOWER);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
        if (!level.isClientSide) tryFormMultiblock(level, pos);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide && state.getBlock() != newState.getBlock()) {
            breakMultiblock(level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            tryFormMultiblock(level, pos);
        }
    }

    public void tryFormMultiblock(Level level, BlockPos center) {
        if (updatingStructure) return;
        updatingStructure = true;
        try {
            BlockPos[][] positions = {
                    { center.south().east(), center.south(), center.south().west() },
                    { center.east(), center, center.west() },
                    { center.north().east(), center.north(), center.north().west() }
            };

            MultiblockPart[][] parts = {
                    { MultiblockPart.TOP_LEFT, MultiblockPart.TOP, MultiblockPart.TOP_RIGHT },
                    { MultiblockPart.LEFT, MultiblockPart.CENTER, MultiblockPart.RIGHT },
                    { MultiblockPart.BOTTOM_LEFT, MultiblockPart.BOTTOM, MultiblockPart.BOTTOM_RIGHT }
            };

            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    BlockPos pos = positions[y][x];
                    BlockState state = level.getBlockState(pos);
                    MultiblockPart expectedPart = parts[y][x];

                    if (expectedPart == MultiblockPart.CENTER) {
                        if (!(state.getBlock() instanceof AltarPillarBlock)) return;
                    } else {
                        if (!(state.getBlock() instanceof RuneBlock)) return;
                    }
                }
            }

            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    BlockPos pos = positions[y][x];
                    BlockState state = level.getBlockState(pos);
                    MultiblockPart part = parts[y][x];
                    if (state.hasProperty(MultiblockProperties.PART) && state.getValue(MultiblockProperties.PART) != part) {
                        level.setBlock(pos, state.setValue(MultiblockProperties.PART, part), 3);
                    }
                }
            }

            //level.playSound(null, center, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, 1.0f);

        } finally {
            updatingStructure = false;
        }
    }

    public void breakMultiblock(Level level, BlockPos center) {
        if (updatingStructure) return;
        updatingStructure = true;
        try {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos pos = center.offset(dx, 0, dz);
                    BlockState state = level.getBlockState(pos);
                    if ((state.getBlock() instanceof AltarPillarBlock || state.getBlock() instanceof RuneBlock) &&
                            state.hasProperty(MultiblockProperties.PART) &&
                            state.getValue(MultiblockProperties.PART) != MultiblockPart.NONE) {
                        level.setBlock(pos, state.setValue(MultiblockProperties.PART, MultiblockPart.NONE), 3);
                    }
                }
            }
        } finally {
            updatingStructure = false;
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (facing.getAxis() == Direction.Axis.Y && half == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {
            return facingState.is(this) && facingState.getValue(HALF) != half
                    ? state.setValue(FACING, facingState.getValue(FACING))
                    : Blocks.AIR.defaultBlockState();
        } else {
            return half == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(level, currentPos)
                    ? Blocks.AIR.defaultBlockState()
                    : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (state.hasProperty(HALF) && state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            pos = pos.below();
            state = level.getBlockState(pos);
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof AltarPillarBlockEntity altarBlockEntity)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        ItemStack altarStack = altarBlockEntity.inventory.getStackInSlot(0);
        ItemStack playerStack = player.getItemInHand(hand);

        if (!playerStack.isEmpty()) {
            if (altarStack.isEmpty()) {
                altarBlockEntity.inventory.setStackInSlot(0, playerStack.copyWithCount(1));
                playerStack.shrink(1);
                altarBlockEntity.setItemInRecipe(AngelRitualHelper.isValidRecipe(level, pos, altarBlockEntity.inventory.getStackInSlot(0)));
                level.playSound(player, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 2f);
                return ItemInteractionResult.SUCCESS;
            }
            if (ItemStack.isSameItemSameComponents(playerStack, altarStack)) {
                return ItemInteractionResult.FAIL;
            }
            ItemStack altarCopy = altarStack.copy();
            altarBlockEntity.inventory.setStackInSlot(0, playerStack.copyWithCount(1));
            playerStack.shrink(1);
            if (playerStack.isEmpty()) {
                player.setItemInHand(hand, altarCopy);
            } else {
                if (!player.getInventory().add(altarCopy)) {
                    player.drop(altarCopy, false);
                }
            }
            altarBlockEntity.setItemInRecipe(AngelRitualHelper.isValidRecipe(level, pos, altarBlockEntity.inventory.getStackInSlot(0)));
            level.playSound(player, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 2f);
            return ItemInteractionResult.SUCCESS;
        }

        if (!altarStack.isEmpty()) {
            boolean canAdd = canAddToMainInventory(player, altarStack);
            if (!canAdd) {
                return ItemInteractionResult.FAIL;
            }
            ItemStack pedestalCopy = altarStack.copy();
            if (player.getItemInHand(hand).isEmpty()) {
                player.setItemInHand(hand, pedestalCopy);
            } else {
                if (!player.getInventory().add(pedestalCopy)) {
                    player.drop(pedestalCopy, false);
                }
            }
            altarBlockEntity.inventory.setStackInSlot(0, ItemStack.EMPTY);
            level.playSound(player, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1f);
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    /*

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof AltarPillarBlockEntity altarBlockEntity)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        ItemStack altarStack = altarBlockEntity.inventory.getStackInSlot(0);
        ItemStack playerStack = player.getItemInHand(hand);
        if (!playerStack.isEmpty()) {
            if (altarStack.isEmpty()) {
                altarBlockEntity.inventory.setStackInSlot(0, playerStack.copyWithCount(1));
                playerStack.shrink(1);
                altarBlockEntity.setItemInRecipe(AngelRitualHelper.isValidRecipe(level, pos, altarBlockEntity.inventory.getStackInSlot(0)));
                level.playSound(player, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 2f);
                return ItemInteractionResult.SUCCESS;
            }
            if (ItemStack.isSameItemSameComponents(playerStack, altarStack)) {
                return ItemInteractionResult.FAIL;
            }
            ItemStack altarCopy = altarStack.copy();
            altarBlockEntity.inventory.setStackInSlot(0, playerStack.copyWithCount(1));
            playerStack.shrink(1);
            if (playerStack.isEmpty()) {
                player.setItemInHand(hand, altarCopy);
            } else {
                if (!player.getInventory().add(altarCopy)) {
                    player.drop(altarCopy, false);
                }
            }
            altarBlockEntity.setItemInRecipe(AngelRitualHelper.isValidRecipe(level, pos, altarBlockEntity.inventory.getStackInSlot(0)));
            level.playSound(player, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 2f);
            return ItemInteractionResult.SUCCESS;
        }
        if (!altarStack.isEmpty()) {
            boolean canAdd = canAddToMainInventory(player, altarStack);
            if (!canAdd) {
                return ItemInteractionResult.FAIL;
            }
            ItemStack pedestalCopy = altarStack.copy();
            if (player.getItemInHand(hand).isEmpty()) {
                player.setItemInHand(hand, pedestalCopy);
            } else {
                if (!player.getInventory().add(pedestalCopy)) {
                    player.drop(pedestalCopy, false);
                }
            }
            altarBlockEntity.inventory.setStackInSlot(0, ItemStack.EMPTY);
            level.playSound(player, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1f);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

     */

    private boolean canAddToMainInventory(Player player, ItemStack stack) {
        for (int i = 0; i < 36; i++) {
            ItemStack slotStack = player.getInventory().getItem(i);
            if (slotStack.isEmpty()) return true;
            if (ItemStack.isSameItemSameComponents(slotStack, stack) && slotStack.getCount() < slotStack.getMaxStackSize())
                return true;
        }
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new AltarPillarBlockEntity(blockPos, blockState);
    }

    @Override
    public @org.jetbrains.annotations.Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if(level.isClientSide()) {
            return null;
        }

        return createTickerHelper(blockEntityType, HFBlockEntities.ALTAR_PILLAR_ENTITY.get(),
                (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1));
    }
}
