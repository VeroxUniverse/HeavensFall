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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.pixeldream.heavensfall.blocks.blockentity.AltarBlockEntity;
import net.pixeldream.heavensfall.blocks.blockentity.HFBlockEntities;
import net.pixeldream.heavensfall.blocks.model.MultiblockPart;
import net.pixeldream.heavensfall.blocks.model.MultiblockProperties;
import net.pixeldream.heavensfall.recipes.ritual.RitualHelper;
import org.jetbrains.annotations.Nullable;

public class AltarBlock extends BaseEntityBlock {

    public static final MapCodec<AltarBlock> CODEC = simpleCodec(AltarBlock::new);

    private boolean updatingStructure = false;

    protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 8.0, 14.0);

    @Override
    public MapCodec<? extends AltarBlock> codec() {
        return CODEC;
    }

    public AltarBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(MultiblockProperties.PART, MultiblockPart.NONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MultiblockProperties.PART);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) tryFormMultiblock(level, pos);
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
                        if (!(state.getBlock() instanceof AltarBlock)) return;
                    } else {
                        if (!(state.getBlock() instanceof ChalkBlock)) return;
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
                    if ((state.getBlock() instanceof AltarBlock || state.getBlock() instanceof ChalkBlock) &&
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
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean isMoving) {
        if (!level.isClientSide) {
            tryFormMultiblock(level, pos);
        }
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
        return new AltarBlockEntity(blockPos, blockState);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(state.getBlock() != newState.getBlock()) {
            if(level.getBlockEntity(pos) instanceof AltarBlockEntity altarBlockEntity) {
                altarBlockEntity.drops();
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (!level.isClientSide) breakMultiblock(level, pos);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(level.getBlockEntity(pos) instanceof AltarBlockEntity altarBlockEntity) {
            ItemStack altarStack = altarBlockEntity.inventory.getStackInSlot(0);

            if(altarStack.isEmpty() && !stack.isEmpty()) {
                altarBlockEntity.inventory.insertItem(0, stack.copy(), false);
                stack.shrink(1);
                altarBlockEntity.setItemInRecipe(RitualHelper.isValidRecipe(level, pos, altarBlockEntity.inventory.getStackInSlot(0)));
                level.playSound(player, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 2f);
            } else if(stack.isEmpty() && !altarStack.isEmpty()) {
                ItemStack extracted = altarBlockEntity.inventory.extractItem(0, 1, false);
                player.setItemInHand(InteractionHand.MAIN_HAND, extracted);
                altarBlockEntity.setItemInRecipe(false);
                level.playSound(player, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1f);
            }
        }
        return ItemInteractionResult.SUCCESS;
    }


    @Override
    public @org.jetbrains.annotations.Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if(level.isClientSide()) {
            return null;
        }

        return createTickerHelper(blockEntityType, HFBlockEntities.ALTAR_ENTITY.get(),
                (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1));
    }

}
