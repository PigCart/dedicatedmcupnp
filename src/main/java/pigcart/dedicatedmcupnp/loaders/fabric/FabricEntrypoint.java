//? if fabric {
package pigcart.dedicatedmcupnp.loaders.fabric;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import pigcart.dedicatedmcupnp.DedicatedMcUpnp;

public class FabricEntrypoint implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register(DedicatedMcUpnp::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(DedicatedMcUpnp::onServerStopping);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(DedicatedMcUpnp.getCommands());
        });
    }
}
//?}