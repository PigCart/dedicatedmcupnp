//? if forge {
/*package pigcart.dedicatedmcupnp.loaders.forge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.fml.common.Mod;
import pigcart.dedicatedmcupnp.DedicatedMcUpnp;

@Mod(DedicatedMcUpnp.MOD_ID)
public class ForgeEntrypoint {

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(DedicatedMcUpnp.getCommands());
    }

    public static void onServerStarted(ServerStartedEvent event) {
        DedicatedMcUpnp.onServerStarted(event.getServer());
    }

    public static void onServerStopping(ServerStoppingEvent event) {
        DedicatedMcUpnp.onServerStopping(event.getServer());
    }

    public ForgeEntrypoint() {
        MinecraftForge.EVENT_BUS.addListener(ForgeEntrypoint::onServerStarted);
        MinecraftForge.EVENT_BUS.addListener(ForgeEntrypoint::onServerStopping);
        MinecraftForge.EVENT_BUS.addListener(ForgeEntrypoint::onRegisterCommands);
    }
}
*///?}