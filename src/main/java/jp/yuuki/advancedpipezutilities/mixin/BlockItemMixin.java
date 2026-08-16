package jp.yuuki.advancedpipezutilities.mixin;

import de.maxhenkel.pipez.blocks.PipeBlock;
import jp.yuuki.advancedpipezutilities.pipe.PipeSideSelector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

    @ModifyArg(
            method = "useOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/BlockItem;place(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/InteractionResult;"
            ),
            index = 0
    )
    private BlockPlaceContext advancedPipezUtilities$placeSamePipeOnSelectedSide(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        BlockPos targetPos = context.getClickedPos().relative(clickedFace.getOpposite());
        if (!(context.getLevel().getBlockState(targetPos).getBlock() instanceof PipeBlock targetPipe)
                || ((BlockItem) (Object) this).getBlock() != targetPipe) {
            return context;
        }

        Vec3 click = context.getClickLocation();
        Direction selected = PipeSideSelector.select(
                clickedFace,
                click.x - targetPos.getX(),
                click.y - targetPos.getY(),
                click.z - targetPos.getZ()
        );
        BlockHitResult selectedHit = new BlockHitResult(click, selected, targetPos, context.isInside());
        return new BlockPlaceContext(
                context.getLevel(), context.getPlayer(), context.getHand(), context.getItemInHand(), selectedHit);
    }
}
