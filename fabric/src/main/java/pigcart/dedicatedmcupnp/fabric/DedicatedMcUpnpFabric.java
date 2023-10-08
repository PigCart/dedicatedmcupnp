package pigcart.dedicatedmcupnp.fabric;

import pigcart.dedicatedmcupnp.DedicatedMcUpnp;
import net.fabricmc.api.ModInitializer;

public class DedicatedMcUpnpFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        DedicatedMcUpnp.init();
    }
}
