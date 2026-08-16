package jp.yuuki.advancedpipezutilities.item;

import jp.yuuki.advancedpipezutilities.AdvancedPipezUtilities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {

    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(BuiltInRegistries.ITEM, AdvancedPipezUtilities.MOD_ID);

    public static final DeferredHolder<Item, AdvancedPipeWrenchItem> ADVANCED_PIPE_WRENCH =
            ITEMS.register("advanced_pipe_wrench", () -> new AdvancedPipeWrenchItem(new Item.Properties()));

    private ModItems() {
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }

    public static void addToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ADVANCED_PIPE_WRENCH.get());
        }
    }
}
