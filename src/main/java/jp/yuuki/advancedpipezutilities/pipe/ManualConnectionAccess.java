package jp.yuuki.advancedpipezutilities.pipe;

import net.minecraft.core.Direction;

public interface ManualConnectionAccess {

    boolean advancedPipezUtilities$isManuallyConnected(Direction side);

    void advancedPipezUtilities$setManuallyConnected(Direction side, boolean connected);

    boolean advancedPipezUtilities$hasManualConnection();
}
