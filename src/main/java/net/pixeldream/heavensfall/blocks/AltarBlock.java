package net.pixeldream.heavensfall.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.pixeldream.heavensfall.blocks.Multiblock.MultiblockPart;
import net.pixeldream.heavensfall.blocks.Multiblock.MultiblockProperties;
import net.pixeldream.heavensfall.blocks.blockentity.AltarBlockEntity;
import net.pixeldream.heavensfall.blocks.blockentity.PedestalBlockEntity;

public class AltarBlock extends Block {

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

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);
        if (!level.isClientSide) breakMultiblock(level, pos);
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
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof AltarBlockEntity altar) return altar.onUse(player, hand);
        }
        return ItemInteractionResult.SUCCESS;
    }


}
