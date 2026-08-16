package jp.yuuki.advancedpipezutilities.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public final class PipeSideSelector {

    private static final double EDGE = 0.25D;

    private PipeSideSelector() {
    }

    /**
     * Selects all six pipe directions from one visible block face.
     * The four edge bands address the directions shown by that edge, the center
     * addresses the visible face, and a corner addresses the hidden opposite face.
     */
    public static Direction select(BlockHitResult hit) {
        BlockPos pos = hit.getBlockPos();
        Vec3 local = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
        return select(hit.getDirection(), local.x, local.y, local.z);
    }

    /** Matches GTCEu Modern's six-direction wrench grid. */
    public static Direction select(Direction face, double x, double y, double z) {
        Direction opposite = face.getOpposite();
        return switch (face) {
            case DOWN, UP -> {
                if (x < EDGE) {
                    if (z < EDGE || z > 1D - EDGE) yield opposite;
                    yield Direction.WEST;
                }
                if (x > 1D - EDGE) {
                    if (z < EDGE || z > 1D - EDGE) yield opposite;
                    yield Direction.EAST;
                }
                if (z < EDGE) yield Direction.NORTH;
                if (z > 1D - EDGE) yield Direction.SOUTH;
                yield face;
            }
            case NORTH, SOUTH -> {
                if (x < EDGE) {
                    if (y < EDGE || y > 1D - EDGE) yield opposite;
                    yield Direction.WEST;
                }
                if (x > 1D - EDGE) {
                    if (y < EDGE || y > 1D - EDGE) yield opposite;
                    yield Direction.EAST;
                }
                if (y < EDGE) yield Direction.DOWN;
                if (y > 1D - EDGE) yield Direction.UP;
                yield face;
            }
            case WEST, EAST -> {
                if (z < EDGE) {
                    if (y < EDGE || y > 1D - EDGE) yield opposite;
                    yield Direction.NORTH;
                }
                if (z > 1D - EDGE) {
                    if (y < EDGE || y > 1D - EDGE) yield opposite;
                    yield Direction.SOUTH;
                }
                if (y < EDGE) yield Direction.DOWN;
                if (y > 1D - EDGE) yield Direction.UP;
                yield face;
            }
        };
    }
}
