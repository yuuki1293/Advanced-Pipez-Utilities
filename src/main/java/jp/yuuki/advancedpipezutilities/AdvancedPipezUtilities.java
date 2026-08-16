package jp.yuuki.advancedpipezutilities;

import jp.yuuki.advancedpipezutilities.client.PipeWrenchOverlayRenderer;
import jp.yuuki.advancedpipezutilities.item.ModItems;
import jp.yuuki.advancedpipezutilities.pipe.PipeInteractionHandler;
import jp.yuuki.advancedpipezutilities.pipe.PipePlacementHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

@Mod(AdvancedPipezUtilities.MOD_ID)
public final class AdvancedPipezUtilities {

    public static final String MOD_ID = "advanced_pipez_utilities";

    public AdvancedPipezUtilities(IEventBus modBus) {
        ModItems.register(modBus);
        modBus.addListener(ModItems::addToCreativeTabs);
        NeoForge.EVENT_BUS.register(PipeInteractionHandler.class);
        NeoForge.EVENT_BUS.register(PipePlacementHandler.class);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modBus.addListener(PipeWrenchOverlayRenderer::registerItemColors);
            NeoForge.EVENT_BUS.register(PipeWrenchOverlayRenderer.class);
        }
    }
}
