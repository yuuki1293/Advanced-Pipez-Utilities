package jp.yuuki.advancedpipezutilities;

import jp.yuuki.advancedpipezutilities.client.PipeWrenchOverlayRenderer;
import jp.yuuki.advancedpipezutilities.item.ModItems;
import jp.yuuki.advancedpipezutilities.pipe.PipeInteractionHandler;
import jp.yuuki.advancedpipezutilities.pipe.PipePlacementHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(AdvancedPipezUtilities.MOD_ID)
public final class AdvancedPipezUtilities {

    public static final String MOD_ID = "advanced_pipez_utilities";

    public AdvancedPipezUtilities() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.register(modBus);
        modBus.addListener(ModItems::addToCreativeTabs);
        MinecraftForge.EVENT_BUS.register(PipeInteractionHandler.class);
        MinecraftForge.EVENT_BUS.register(PipePlacementHandler.class);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modBus.addListener(PipeWrenchOverlayRenderer::registerItemColors);
            MinecraftForge.EVENT_BUS.register(PipeWrenchOverlayRenderer.class);
        }
    }
}
