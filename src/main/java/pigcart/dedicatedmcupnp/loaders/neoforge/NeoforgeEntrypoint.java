//? if neoforge {
/*package pigcart.dedicatedmcupnp.loaders.neoforge;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import pigcart.dedicatedmcupnp.DedicatedMcUpnp;

@Mod(DedicatedMcUpnp.MOD_ID)
public class NeoforgeEntrypoint {

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(DedicatedMcUpnp.getCommands());
    }

    public static void onServerStarted(ServerStartedEvent event) {
        DedicatedMcUpnp.onServerStarted(event.getServer());
    }

    public static void onServerStopping(ServerStoppingEvent event) {
        DedicatedMcUpnp.onServerStopping(event.getServer());
    }

    public NeoforgeEntrypoint() {
        NeoForge.EVENT_BUS.addListener(NeoforgeEntrypoint::onServerStarted);
        NeoForge.EVENT_BUS.addListener(NeoforgeEntrypoint::onServerStopping);
        NeoForge.EVENT_BUS.addListener(NeoforgeEntrypoint::onRegisterCommands);
    }
}
*///?}