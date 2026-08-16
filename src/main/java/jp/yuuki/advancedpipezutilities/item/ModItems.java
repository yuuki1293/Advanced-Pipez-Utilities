package jp.yuuki.advancedpipezutilities.item;

import jp.yuuki.advancedpipezutilities.AdvancedPipezUtilities;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {

    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, AdvancedPipezUtilities.MOD_ID);

    public static final RegistryObject<AdvancedPipeWrenchItem> ADVANCED_PIPE_WRENCH =
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
