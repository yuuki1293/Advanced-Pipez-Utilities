package jp.yuuki.advancedpipezutilities.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Carries the selected placement face to the corresponding placement event.
 * Neighbor updates can recalculate Pipez block-state properties before that event.
 */
public final class PipePlacementIntent {
    private static final ThreadLocal<Intent> CURRENT = new ThreadLocal<>();

    private PipePlacementIntent() {
    }

    public static void record(Level level, BlockPos placedPos, @Nullable Direction targetSide) {
        CURRENT.set(new Intent(level, placedPos.immutable(), targetSide));
    }

    @Nullable
    public static Direction consume(Level level, BlockPos placedPos) {
        Intent intent = CURRENT.get();
        CURRENT.remove();
        if (intent == null || intent.level() != level || !intent.placedPos().equals(placedPos)) {
            return null;
        }
        return intent.targetSide();
    }

    private record Intent(Level level, BlockPos placedPos, @Nullable Direction targetSide) {
    }
}
