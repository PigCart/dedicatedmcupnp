package pigcart.dedicatedmcupnp;

import com.dosse.upnp.UPnP;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static net.minecraft.commands.Commands.literal;

public class DedicatedMcUpnp {
    private static final Path CONFIG_FILE = Paths.get("config", "upnp.yaml");
    static Integer[] tcpPorts;
    static Integer[] udpPorts;
    
    public static void init() {
        LifecycleEvent.SERVER_STARTED.register(DedicatedMcUpnp::onServerStarted);
        LifecycleEvent.SERVER_STOPPING.register(DedicatedMcUpnp::onServerStopping);
        CommandRegistrationEvent.EVENT.register((dispatcher, selection) -> {
            if (selection.equals(Commands.CommandSelection.DEDICATED)) {
                LiteralArgumentBuilder<CommandSourceStack> cmd = literal("upnp")
                        .requires((commandSourceStack) -> commandSourceStack.hasPermission(4))
                        .executes(ctx -> {
                            if (!UPnP.isUPnPAvailable()) {
                                ctx.getSource().sendSuccess(new TextComponent("UPnP is not available on this network."), false);
                            } else {
                                ctx.getSource().sendSuccess(new TextComponent("IP Address: " + UPnP.getExternalIP()), false);
                                ctx.getSource().sendSuccess(new TextComponent("The following ports are mapped: "), false);
                                int portsMapped = 0;
                                for (int port : tcpPorts) {
                                    if (UPnP.isMappedTCP(port)) {
                                        ctx.getSource().sendSuccess(new TextComponent("TCP " + port), false);
                                        portsMapped++;
                                    }
                                }
                                for (int port : udpPorts) {
                                    if (UPnP.isMappedUDP(port)) {
                                        ctx.getSource().sendSuccess(new TextComponent("UDP " + port), false);
                                        portsMapped++;
                                    }
                                }
                                if (portsMapped == 0) {
                                    ctx.getSource().sendSuccess(new TextComponent("No ports are mapped."), false);
                                }
                            }
                            return 0;
                        });
                dispatcher.register(cmd);
            }
        });
        try {
            if (!Files.exists(CONFIG_FILE)) {
                createConfig();
            }
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            PortsConfig config = mapper.readValue(CONFIG_FILE.toFile(), PortsConfig.class);
            tcpPorts = config.getTcpPorts();
            udpPorts = config.getUdpPorts();
            if (tcpPorts == null || udpPorts == null){
                createConfig();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void onServerStarted(MinecraftServer server) {
        if (server.isDedicatedServer()) {
            System.out.println("Attempting UPnP port forwarding...");
            if (UPnP.isUPnPAvailable()) { //is UPnP available?

                for (int port : tcpPorts) {
                    if (UPnP.isMappedTCP(port)) { //is the port already mapped?
                        System.out.println("UPnP cannot open TCP port " + port + ": port is already mapped. (Another service might be using it!)");
                    } else if (UPnP.openPortTCP(port, "Minecraft Server")) { //try to map port
                        System.out.println("UPnP opened TCP port " + port);
                    } else {
                        System.out.println("UPnP failed to open TCP port " + port);
                    }
                }
                for (int port : udpPorts) {
                    if (UPnP.isMappedUDP(port)) { //is the port already mapped?
                        System.out.println("UPnP cannot open UDP port " + port + ": port is already mapped. (Another service might be using it!)");
                    } else if (UPnP.openPortUDP(port, "Minecraft Server")) { //try to map port
                        System.out.println("UPnP opened UDP port " + port);
                    } else {
                        System.out.println("UPnP failed to open UDP port " + port);
                    }
                }

            } else {
                System.out.println("UPnP is not available on this network. If you are an admin please check the settings on your router/hub");
            }
        }
    }

    private static void onServerStopping(MinecraftServer server) {
        if (server.isDedicatedServer()) {
            System.out.println("Closing UPnP ports...");
            for (int port : tcpPorts) {
                if (UPnP.closePortTCP(port)) {
                    System.out.println("Closed TCP port " + port);
                }
            }
            for (int port : udpPorts) {
                if (UPnP.closePortUDP(port)) {
                    System.out.println("Closed UDP port " + port);
                }
            }
        }
    }

    private static void createConfig() throws IOException {
        if (!Files.exists(CONFIG_FILE)) {
            if (!Files.exists(CONFIG_FILE.getParent())) {
                Files.createDirectories(CONFIG_FILE.getParent().toAbsolutePath());
            }
            OutputStream out = Files.newOutputStream(CONFIG_FILE);
            InputStream defaultConfigInputStream = DedicatedMcUpnp.class.getClassLoader().getResourceAsStream("default_ports_config.yaml");
            if (defaultConfigInputStream == null) {
                throw new IOException("Could not load default_ports_config.yaml");
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = defaultConfigInputStream.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            defaultConfigInputStream.close();
            out.close();
        }
    }
}
