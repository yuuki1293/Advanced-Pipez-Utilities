package jp.yuuki.advancedpipezutilities.pipe;

import de.maxhenkel.pipez.blocks.PipeBlock;
import de.maxhenkel.pipez.blocks.tileentity.PipeTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public final class PipePlacementHandler {

    private PipePlacementHandler() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPipePlaced(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof Level level) || level.isClientSide) {
            return;
        }

        BlockState placedState = event.getPlacedBlock();
        if (!(placedState.getBlock() instanceof PipeBlock pipe)) {
            return;
        }

        BlockPos pos = event.getPos();
        Direction targetSide = PipePlacementIntent.consume(level, pos);
        for (Direction side : Direction.values()) {
            boolean placementTarget = side == targetSide;
            BlockPos neighborPos = pos.relative(side);
            BlockState neighborState = level.getBlockState(neighborPos);

            if (pipe.isPipe(level, pos, side)) {
                setPipePairConnected(level, pos, pipe, neighborPos, neighborState, side, placementTarget);
            } else if (placementTarget && pipe.canConnectTo(level, pos, side)) {
                setInventoryConnected(level, pos, pipe, side);
            }
        }

        PipeTileEntity tile = pipe.getTileEntity(level, pos);
        if (tile != null && !tile.hasReasonToStay()) {
            pipe.setHasData(level, pos, false);
        } else if (tile != null) {
            tile.syncData();
        }
        PipeTileEntity.markPipesDirty(level, pos);
    }

    private static void setPipePairConnected(Level level, BlockPos pos, PipeBlock pipe,
                                             BlockPos neighborPos, BlockState neighborState,
                                             Direction side, boolean connected) {
        pipe.setDisconnected(level, pos, side, !connected);
        if (neighborState.getBlock() instanceof PipeBlock neighborPipe) {
            neighborPipe.setDisconnected(level, neighborPos, side.getOpposite(), !connected);
        }
    }

    private static void setInventoryConnected(Level level, BlockPos pos, PipeBlock pipe, Direction side) {
        PipeTileEntity tile = pipe.getTileEntity(level, pos);
        if (tile instanceof ManualConnectionAccess access) {
            access.advancedPipezUtilities$setManuallyConnected(side, true);
        }
        pipe.setDisconnected(level, pos, side, false);
    }
}
