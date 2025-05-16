package net.pixeldream.heavensfall.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.pixeldream.heavensfall.blocks.multiblock.MultiblockPart;
import net.pixeldream.heavensfall.blocks.multiblock.MultiblockProperties;

public class ChalkBlock extends Block {

    public static final MapCodec<ChalkBlock> CODEC = simpleCodec(ChalkBlock::new);

    private boolean updatingStructure = false;

    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);

    @Override
    public MapCodec<? extends ChalkBlock> codec() {
        return CODEC;
    }

    public ChalkBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(MultiblockProperties.PART, MultiblockPart.NONE));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    protected BlockState updateShape(
            BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos blockPos, BlockPos neighborPos
    ) {
        return !state.canSurvive(level, blockPos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighborState, level, blockPos, neighborPos);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos blockPos) {
        return !level.isEmptyBlock(blockPos.below());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MultiblockProperties.PART);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) updateMultiblockState(level, pos);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);
        if (!level.isClientSide) resetStructureAround(level, pos);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        if (!level.isClientSide) {
            updateMultiblockState(level, pos);
        }
    }

    private void updateMultiblockState(Level level, BlockPos pos) {
        if (updatingStructure) return;
        updatingStructure = true;
        try {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos center = pos.offset(-dx, 0, -dz);
                    BlockState centerState = level.getBlockState(center);
                    if (centerState.getBlock() instanceof AltarBlock altar) {
                        altar.tryFormMultiblock(level, center);
                        return;
                    }
                }
            }
        } finally {
            updatingStructure = false;
        }
    }

    private void resetStructureAround(Level level, BlockPos pos) {
        if (updatingStructure) return;
        updatingStructure = true;
        try {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos nearby = pos.offset(dx, 0, dz);
                    BlockState nearbyState = level.getBlockState(nearby);
                    if ((nearbyState.getBlock() instanceof ChalkBlock || nearbyState.getBlock() instanceof AltarBlock) &&
                            nearbyState.hasProperty(MultiblockProperties.PART) &&
                            nearbyState.getValue(MultiblockProperties.PART) != MultiblockPart.NONE) {
                        level.setBlock(nearby, nearbyState.setValue(MultiblockProperties.PART, MultiblockPart.NONE), 3);
                    }
                }
            }
        } finally {
            updatingStructure = false;
        }
    }
}
