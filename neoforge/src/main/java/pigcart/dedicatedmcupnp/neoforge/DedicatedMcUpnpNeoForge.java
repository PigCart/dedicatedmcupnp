package pigcart.dedicatedmcupnp.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import pigcart.dedicatedmcupnp.DedicatedMcUpnp;

@Mod(DedicatedMcUpnp.MOD_ID)
public class DedicatedMcUpnpNeoForge {

    public DedicatedMcUpnpNeoForge(IEventBus modEventBus) {
        DedicatedMcUpnp.init();
    }

    private void setup(final FMLCommonSetupEvent event) {
    }

}