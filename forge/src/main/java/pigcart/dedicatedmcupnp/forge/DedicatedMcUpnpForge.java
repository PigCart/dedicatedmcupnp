package pigcart.dedicatedmcupnp.forge;

import dev.architectury.platform.forge.EventBuses;
import pigcart.dedicatedmcupnp.DedicatedMcUpnp;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("dedicatedmcupnp")
public class DedicatedMcUpnpForge {
    public DedicatedMcUpnpForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus("dedicatedmcupnp", FMLJavaModLoadingContext.get().getModEventBus());
        DedicatedMcUpnp.init();
    }
}
