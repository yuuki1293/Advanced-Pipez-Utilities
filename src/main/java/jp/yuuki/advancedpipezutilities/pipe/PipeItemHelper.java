package jp.yuuki.advancedpipezutilities.pipe;

import de.maxhenkel.pipez.blocks.PipeBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public final class PipeItemHelper {

    private PipeItemHelper() {
    }

    public static boolean isHoldingSamePipe(Player player, PipeBlock pipe) {
        return isSamePipe(player.getMainHandItem(), pipe) || isSamePipe(player.getOffhandItem(), pipe);
    }

    public static boolean isSamePipe(ItemStack stack, PipeBlock pipe) {
        return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() == pipe;
    }
}
