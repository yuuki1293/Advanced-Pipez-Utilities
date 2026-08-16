package jp.yuuki.advancedpipezutilities.mixin;

import de.maxhenkel.pipez.blocks.PipeBlock;
import de.maxhenkel.pipez.blocks.tileentity.PipeTileEntity;
import jp.yuuki.advancedpipezutilities.item.ModItems;
import jp.yuuki.advancedpipezutilities.pipe.ManualConnectionAccess;
import jp.yuuki.advancedpipezutilities.pipe.PipeItemHelper;
import jp.yuuki.advancedpipezutilities.pipe.PipePlacementIntent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PipeBlock.class, remap = false)
public abstract class PipeBlockMixin {

    @Inject(method = "getStateForPlacement", at = @At("RETURN"), cancellable = true, remap = true)
    private void advancedPipezUtilities$connectOnlyToPlacementTarget(BlockPlaceContext context,
                                                                     CallbackInfoReturnable<BlockState> cir) {
        BlockState state = cir.getReturnValue();
        if (state == null) {
            return;
        }

        PipeBlock self = (PipeBlock) (Object) this;
        Direction targetSide = context.getClickedFace().getOpposite();
        boolean hasExternalTarget = !context.replacingClickedOnBlock()
                && self.isAbleToConnect(context.getLevel(), context.getClickedPos(), targetSide);

        if (!context.getLevel().isClientSide) {
            PipePlacementIntent.record(
                    context.getLevel(),
                    context.getClickedPos(),
                    hasExternalTarget ? targetSide : null
            );
        }

        for (Direction side : Direction.values()) {
            state = state.setValue(self.getProperty(side), hasExternalTarget && side == targetSide);
        }

        // The placement event needs a tile entity to persist the initial connection policy.
        cir.setReturnValue(state.setValue(PipeBlock.HAS_DATA, true));
    }

    @Inject(method = "isConnected", at = @At("HEAD"), cancellable = true)
    private void advancedPipezUtilities$requireManualInventoryConnection(LevelAccessor level, BlockPos pos,
                                                                          Direction side,
                                                                          CallbackInfoReturnable<Boolean> cir) {
        PipeBlock self = (PipeBlock) (Object) this;
        if (self.isPipe(level, pos, side) || !self.canConnectTo(level, pos, side)) {
            return;
        }

        PipeTileEntity tile = self.getTileEntity(level, pos);
        boolean manuallyConnected = tile instanceof ManualConnectionAccess access
                && access.advancedPipezUtilities$isManuallyConnected(side);
        cir.setReturnValue(manuallyConnected && !tile.isDisconnected(side));
    }

    @Inject(method = "neighborChanged", at = @At("TAIL"), remap = true)
    private void advancedPipezUtilities$clearMissingInventoryConnection(BlockState state, Level level,
                                                                         BlockPos pos, Block neighborBlock,
                                                                         BlockPos neighborPos,
                                                                         boolean movedByPiston, CallbackInfo ci) {
        if (level.isClientSide) {
            return;
        }

        Direction changedSide = null;
        for (Direction side : Direction.values()) {
            if (pos.relative(side).equals(neighborPos)) {
                changedSide = side;
                break;
            }
        }
        if (changedSide == null) {
            return;
        }

        PipeBlock self = (PipeBlock) (Object) this;
        PipeTileEntity tile = self.getTileEntity(level, pos);
        if (!(tile instanceof ManualConnectionAccess access)
                || !access.advancedPipezUtilities$isManuallyConnected(changedSide)) {
            return;
        }
        if (!self.isPipe(level, pos, changedSide) && self.canConnectTo(level, pos, changedSide)) {
            return;
        }

        access.advancedPipezUtilities$setManuallyConnected(changedSide, false);
        if (tile.hasReasonToStay()) {
            tile.syncData();
        } else {
            self.setHasData(level, pos, false);
        }
        PipeTileEntity.markPipesDirty(level, pos);
    }

    @Inject(
            method = "getShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
            at = @At("HEAD"),
            cancellable = true,
            remap = true
    )
    private void advancedPipezUtilities$useFullBlockWrenchSelection(BlockState state, BlockGetter level,
                                                                    BlockPos pos, CollisionContext context,
                                                                    CallbackInfoReturnable<VoxelShape> cir) {
        if (context instanceof EntityCollisionContext entityContext
                && entityContext.getEntity() instanceof Player player
                && (player.getMainHandItem().is(ModItems.ADVANCED_PIPE_WRENCH.get())
                    || player.getOffhandItem().is(ModItems.ADVANCED_PIPE_WRENCH.get())
                    || PipeItemHelper.isHoldingSamePipe(player, (PipeBlock) (Object) this))) {
            cir.setReturnValue(Shapes.block());
        }
    }
}
