package pigcart.upeenpiss;

import com.dosse.upnp.UPnP;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class UPeeNPiss implements ModInitializer {
    int port = 25565;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
    }

    private void onServerStarted(MinecraftServer server) {
        System.out.println("Attempting UPnP port forwarding... (port 25565)");
        if (UPnP.isUPnPAvailable()) { //is UPnP available?
            if (UPnP.isMappedTCP(port)) { //is the port already mapped?
                System.out.println("UPnP port forwarding not enabled: port is already mapped");
            } else if (UPnP.openPortTCP(port, "Minecraft Server")) { //try to map port
                System.out.println("UPnP port forwarding enabled");
            } else {
                System.out.println("UPnP port forwarding failed");
            }
        } else {
            System.out.println("UPnP is not available");
        }
    }

    private void onServerStopping(MinecraftServer server) {
        if (UPnP.closePortTCP(port)) {
            System.out.println("Closed port " + port);
        }
    }
}
