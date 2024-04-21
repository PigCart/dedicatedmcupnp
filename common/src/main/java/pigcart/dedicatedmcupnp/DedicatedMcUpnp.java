package pigcart.dedicatedmcupnp;

import com.dosse.upnp.UPnP;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.commands.Commands.literal;

public class DedicatedMcUpnp {
    public static final String MOD_ID = "dedicatedmcupnp";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    public static void init() {
        LifecycleEvent.SERVER_STARTED.register(DedicatedMcUpnp::onServerStarted);
        LifecycleEvent.SERVER_STOPPING.register(DedicatedMcUpnp::onServerStopping);
        CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) -> {
            if (selection.equals(Commands.CommandSelection.DEDICATED)) {
                LiteralArgumentBuilder<CommandSourceStack> cmd = literal("upnp")
                        .requires((commandSourceStack) -> commandSourceStack.hasPermission(4))
                        .executes(ctx -> {
                            if (!UPnP.isUPnPAvailable()) {
                                ctx.getSource().sendSuccess(() -> Component.literal("UPnP is not available on this network."), false);
                            } else {
                                ctx.getSource().sendSuccess(() -> Component.literal("IP Address: " + UPnP.getExternalIP()), false);
                                ctx.getSource().sendSuccess(() -> Component.literal("The following ports are mapped: "), false);
                                int portsMapped = 0;
                                for (int port : Config.tcpPorts) {
                                    if (UPnP.isMappedTCP(port)) {
                                        ctx.getSource().sendSuccess(() -> Component.literal("TCP " + port), false);
                                        portsMapped++;
                                    }
                                }
                                for (int port : Config.udpPorts) {
                                    if (UPnP.isMappedUDP(port)) {
                                        ctx.getSource().sendSuccess(() -> Component.literal("UDP " + port), false);
                                        portsMapped++;
                                    }
                                }
                                if (portsMapped == 0) {
                                    ctx.getSource().sendSuccess(() -> Component.literal("No ports are mapped."), false);
                                }
                            }
                            return 0;
                        });
                dispatcher.register(cmd);
            }
        });
    }
    private static void onServerStarted(MinecraftServer server) {
        if (server.isDedicatedServer()) {
            Config.readConfig();
            LOGGER.info("Attempting UPnP port forwarding...");
            if (UPnP.isUPnPAvailable()) { //is UPnP available?
                for (int port : Config.tcpPorts) {
                    if (UPnP.isMappedTCP(port)) { //is the port already mapped?
                        LOGGER.error("UPnP cannot open TCP port " + port + ": port is already mapped. (Another service might be using it!)");
                    } else if (UPnP.openPortTCP(port, "Minecraft Server")) { //try to map port
                        LOGGER.info("UPnP opened TCP port " + port);
                    } else {
                        LOGGER.error("UPnP failed to open TCP port " + port);
                    }
                }
                for (int port : Config.udpPorts) {
                    if (UPnP.isMappedUDP(port)) { //is the port already mapped?
                        LOGGER.error("UPnP cannot open UDP port " + port + ": port is already mapped. (Another service might be using it!)");
                    } else if (UPnP.openPortUDP(port, "Minecraft Server")) { //try to map port
                        LOGGER.info("UPnP opened UDP port " + port);
                    } else {
                        LOGGER.error("UPnP failed to open UDP port " + port);
                    }
                }

            } else {
                LOGGER.error("UPnP is not available on this network. If you are an admin please check the settings on your router/hub");
            }
        }
    }

    private static void onServerStopping(MinecraftServer server) {
        if (server.isDedicatedServer()) {
            LOGGER.info("Closing UPnP ports...");
            for (int port : Config.tcpPorts) {
                if (UPnP.closePortTCP(port)) {
                    LOGGER.info("Closed TCP port " + port);
                }
            }
            for (int port : Config.udpPorts) {
                if (UPnP.closePortUDP(port)) {
                    LOGGER.info("Closed UDP port " + port);
                }
            }
        }
    }
}
