package vg.skye.teawieks.wie

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

open class WieBlock: HorizontalDirectionalBlock(Properties.copy(Blocks.WHITE_WOOL)) {
    companion object {
        val SHAPES: Map<Direction, VoxelShape> = mapOf(
            Direction.SOUTH to box(5.0, 0.0, 4.0, 11.0, 12.0, 10.0),
            Direction.NORTH to box(5.0, 0.0, 6.0, 11.0, 12.0, 12.0),
            Direction.EAST to box(4.0, 0.0, 5.0, 10.0, 12.0, 11.0),
            Direction.WEST to box(6.0, 0.0, 5.0, 12.0, 12.0, 11.0),
        )
    }

    @Suppress("OVERRIDE_DEPRECATION") // minecraft is incredibly dumb
    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext)
        = state.getValue(FACING).let { SHAPES[it] ?: error("impossible direction: $it") }


    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState
        = defaultBlockState().setValue(FACING, context.horizontalDirection.opposite)
}


object HiPolywieBlock: WieBlock() {
    private val SHAPE: VoxelShape = box(-40.0, 0.0, -40.0, 40.0, 112.0, 40.0)

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext) = SHAPE

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getCollisionShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext) = SHAPE
}