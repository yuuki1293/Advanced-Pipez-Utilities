package jp.yuuki.advancedpipezutilities.mixin;

import de.maxhenkel.pipez.blocks.tileentity.PipeTileEntity;
import jp.yuuki.advancedpipezutilities.pipe.ManualConnectionAccess;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PipeTileEntity.class, remap = false)
public abstract class PipeTileEntityMixin implements ManualConnectionAccess {

    @Unique
    private static final String ADVANCED_PIPEZ_UTILITIES_MANUAL_CONNECTIONS = "AdvancedPipezUtilitiesManualConnections";

    @Unique
    private byte advancedPipezUtilities$manualConnections;

    @Override
    public boolean advancedPipezUtilities$isManuallyConnected(Direction side) {
        return (advancedPipezUtilities$manualConnections & (1 << side.get3DDataValue())) != 0;
    }

    @Override
    public void advancedPipezUtilities$setManuallyConnected(Direction side, boolean connected) {
        int bit = 1 << side.get3DDataValue();
        if (connected) {
            advancedPipezUtilities$manualConnections = (byte) (advancedPipezUtilities$manualConnections | bit);
        } else {
            advancedPipezUtilities$manualConnections = (byte) (advancedPipezUtilities$manualConnections & ~bit);
        }
        ((PipeTileEntity) (Object) this).setChanged();
    }

    @Override
    public boolean advancedPipezUtilities$hasManualConnection() {
        return advancedPipezUtilities$manualConnections != 0;
    }

    @Inject(method = "hasReasonToStay", at = @At("RETURN"), cancellable = true)
    private void advancedPipezUtilities$keepManualConnections(CallbackInfoReturnable<Boolean> cir) {
        if (advancedPipezUtilities$hasManualConnection()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void advancedPipezUtilities$loadManualConnections(CompoundTag tag, HolderLookup.Provider provider,
                                                               CallbackInfo ci) {
        advancedPipezUtilities$manualConnections = tag.getByte(ADVANCED_PIPEZ_UTILITIES_MANUAL_CONNECTIONS);
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void advancedPipezUtilities$saveManualConnections(CompoundTag tag, HolderLookup.Provider provider,
                                                               CallbackInfo ci) {
        if (advancedPipezUtilities$manualConnections != 0) {
            tag.putByte(ADVANCED_PIPEZ_UTILITIES_MANUAL_CONNECTIONS,
                    advancedPipezUtilities$manualConnections);
        }
    }
}
