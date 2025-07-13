package pigcart.dedicatedmcupnp;

import com.dosse.upnp.UPnP;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DedicatedMcUpnp {
    public static final String MOD_ID = "dedicatedmcupnp";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @SuppressWarnings("unchecked")
    public static <S> LiteralArgumentBuilder<S> getCommands() {
        return (LiteralArgumentBuilder<S>) Commands.literal("upnp")
                .requires((commandSourceStack) -> commandSourceStack.hasPermission(4)) //"owner" permission level
                .executes(ctx -> {
                    if (!UPnP.isUPnPAvailable()) {
                        StonecutterUtil.sendMsg(ctx, "UPnP is not available on this network.");
                    } else {
                        StonecutterUtil.sendMsg(ctx, "IP Address: " + UPnP.getExternalIP());
                        StonecutterUtil.sendMsg(ctx, "The following ports are mapped: ");
                        int portsMapped = 0;
                        for (int port : Config.tcpPorts) {
                            if (UPnP.isMappedTCP(port)) {
                                StonecutterUtil.sendMsg(ctx, "TCP " + port);
                                portsMapped++;
                            }
                        }
                        for (int port : Config.udpPorts) {
                            if (UPnP.isMappedUDP(port)) {
                                StonecutterUtil.sendMsg(ctx, "UDP " + port);
                                portsMapped++;
                            }
                        }
                        if (portsMapped == 0) {
                            StonecutterUtil.sendMsg(ctx, "No ports are mapped.");
                        }
                    }
                    return 0;
                });
    }

    public static void onServerStarted(MinecraftServer server) {
        if (server.isDedicatedServer()) {
            Config.readConfig();
            LOGGER.info("Attempting UPnP port forwarding...");
            if (UPnP.isUPnPAvailable()) {
                for (int port : Config.tcpPorts) {
                    if (UPnP.isMappedTCP(port)) {
                        LOGGER.error("UPnP cannot open TCP port {}: port is already mapped. (Another service might be using it!)", port);
                    } else if (UPnP.openPortTCP(port, "Minecraft Server (dedicatedmcupnp)")) {
                        LOGGER.info("UPnP opened TCP port {}", port);
                    } else {
                        LOGGER.error("UPnP failed to open TCP port {}", port);
                    }
                }
                for (int port : Config.udpPorts) {
                    if (UPnP.isMappedUDP(port)) {
                        LOGGER.error("UPnP cannot open UDP port {}: port is already mapped. (Another service might be using it!)", port);
                    } else if (UPnP.openPortUDP(port, "Minecraft Server (dedicatedmcupnp)")) {
                        LOGGER.info("UPnP opened UDP port {}", port);
                    } else {
                        LOGGER.error("UPnP failed to open UDP port {}", port);
                    }
                }

            } else {
                LOGGER.error("UPnP is not available on this network. If you are an admin please check the settings on your router/hub");
            }
        }
    }

    public static void onServerStopping(MinecraftServer server) {
        if (server.isDedicatedServer()) {
            LOGGER.info("Closing UPnP ports...");
            for (int port : Config.tcpPorts) {
                if (UPnP.closePortTCP(port)) {
                    LOGGER.info("Closed TCP port {}", port);
                }
            }
            for (int port : Config.udpPorts) {
                if (UPnP.closePortUDP(port)) {
                    LOGGER.info("Closed UDP port {}", port);
                }
            }
        }
    }
}
