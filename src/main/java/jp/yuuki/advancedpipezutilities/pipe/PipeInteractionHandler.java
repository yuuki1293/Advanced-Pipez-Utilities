package jp.yuuki.advancedpipezutilities.pipe;

import de.maxhenkel.pipez.blocks.PipeBlock;
import de.maxhenkel.pipez.blocks.tileentity.PipeTileEntity;
import jp.yuuki.advancedpipezutilities.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class PipeInteractionHandler {

    private PipeInteractionHandler() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPipeClick(PlayerInteractEvent.RightClickBlock event) {
        ItemStack held = event.getEntity().getItemInHand(event.getHand());
        if (!held.is(ModItems.ADVANCED_PIPE_WRENCH.get())) {
            return;
        }

        BlockState state = event.getLevel().getBlockState(event.getPos());
        if (!(state.getBlock() instanceof PipeBlock pipe)) {
            return;
        }

        event.setUseBlock(Event.Result.DENY);
        event.setUseItem(Event.Result.DENY);
        event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
        event.setCanceled(true);

        if (event.getLevel().isClientSide) {
            return;
        }

        Direction side = PipeSideSelector.select(event.getHitVec());
        if (event.getEntity().isShiftKeyDown()) {
            toggleExtraction(event.getLevel(), event.getPos(), pipe, side, event);
        } else {
            toggleConnection(event.getLevel(), event.getPos(), pipe, side);
        }
    }

    private static void toggleConnection(Level level, BlockPos pos, PipeBlock pipe, Direction side) {
        if (!pipe.isAbleToConnect(level, pos, side)) {
            return;
        }

        boolean connect = !pipe.isConnected(level, pos, side);
        setConnection(level, pos, pipe, side, connect);
        playWrenchSound(level, pos, connect ? 1.15F : 0.85F);
        PipeTileEntity.markPipesDirty(level, pos);
    }

    private static void toggleExtraction(Level level, BlockPos pos, PipeBlock pipe, Direction side,
                                         PlayerInteractEvent.RightClickBlock event) {
        if (!pipe.canConnectTo(level, pos, side)) {
            event.getEntity().displayClientMessage(
                    Component.translatable("message.advanced_pipez_utilities.no_inventory"), true);
            return;
        }

        if (!pipe.isConnected(level, pos, side)) {
            setConnection(level, pos, pipe, side, true);
        }

        boolean extracting = pipe.isExtracting(level, pos, side);
        pipe.setExtracting(level, pos, side, !extracting);
        playWrenchSound(level, pos, extracting ? 0.9F : 1.25F);
        PipeTileEntity.markPipesDirty(level, pos);
    }

    private static void setConnection(Level level, BlockPos pos, PipeBlock pipe, Direction side, boolean connected) {
        BlockPos neighborPos = pos.relative(side);
        if (pipe.isPipe(level, pos, side)) {
            pipe.setDisconnected(level, pos, side, !connected);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (neighborState.getBlock() instanceof PipeBlock neighborPipe) {
                neighborPipe.setDisconnected(level, neighborPos, side.getOpposite(), !connected);
            }
            return;
        }

        ensurePipeData(level, pos, pipe);
        PipeTileEntity tile = pipe.getTileEntity(level, pos);
        if (tile instanceof ManualConnectionAccess access) {
            access.advancedPipezUtilities$setManuallyConnected(side, connected);
        }
        pipe.setDisconnected(level, pos, side, !connected);
        tile = pipe.getTileEntity(level, pos);
        if (tile != null) {
            tile.syncData();
        }
    }

    private static void ensurePipeData(Level level, BlockPos pos, PipeBlock pipe) {
        if (pipe.getTileEntity(level, pos) == null) {
            pipe.setHasData(level, pos, true);
        }
    }

    private static void playWrenchSound(Level level, BlockPos pos, float pitch) {
        level.playSound(null, pos, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 0.55F, pitch);
    }
}
