package jp.yuuki.advancedpipezutilities.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

import java.util.List;

public final class AdvancedPipeWrenchItem extends Item {

    public AdvancedPipeWrenchItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.advanced_pipez_utilities.advanced_pipe_wrench.connection")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.advanced_pipez_utilities.advanced_pipe_wrench.extraction")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.advanced_pipez_utilities.advanced_pipe_wrench.grid")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
