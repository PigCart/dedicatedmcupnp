package pigcart.upeenpiss;

import com.dosse.upnp.UPnP;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class UPeeNPiss implements ModInitializer {
    private static final Path CONFIG_FILE = Paths.get("config", "upnp.yaml");
    Integer[] tcpPorts;
    Integer[] udpPorts;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);

        try {
            if (!Files.exists(CONFIG_FILE)) {
                this.createConfig();
            }
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            PortsConfig config = mapper.readValue(CONFIG_FILE.toFile(), PortsConfig.class);
            tcpPorts = config.getTcpPorts();
            udpPorts = config.getUdpPorts();
            System.out.println(Arrays.toString(tcpPorts) + Arrays.toString(udpPorts));
            if (tcpPorts == null || udpPorts == null){
                this.createConfig();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onServerStarted(MinecraftServer server) {
        System.out.println("Attempting UPnP port forwarding...");
        if (UPnP.isUPnPAvailable()) { //is UPnP available?

            for (int port: tcpPorts) {
                if (UPnP.isMappedTCP(port)) { //is the port already mapped?
                    System.out.println("UPnP cannot open TCP port "+port+": port is already mapped");
                } else if (UPnP.openPortTCP(port, "Minecraft Server")) { //try to map port
                    System.out.println("UPnP opened TCP port "+port);
                } else {
                    System.out.println("UPnP failed to open TCP port "+port);
                }
            }
            for (int port: udpPorts) {
                if (UPnP.isMappedUDP(port)) { //is the port already mapped?
                    System.out.println("UPnP cannot open UDP port "+port+": port is already mapped");
                } else if (UPnP.openPortUDP(port, "Minecraft Server")) { //try to map port
                    System.out.println("UPnP opened UDP port "+port);
                } else {
                    System.out.println("UPnP failed to open UDP port "+port);
                }
            }
        } else {
            System.out.println("UPnP is not available");
        }
    }

    private void onServerStopping(MinecraftServer server) {
        System.out.println("Closing UPnP ports...");
        for (int port: tcpPorts) {
            if (UPnP.closePortTCP(port)) {
                System.out.println("Closed TCP port " + port);
            }
        }
        for (int port: udpPorts) {
            if (UPnP.closePortUDP(port)) {
                System.out.println("Closed UDP port " + port);
            }
        }
    }

    private void createConfig() throws IOException {
        if (!Files.exists(CONFIG_FILE)) {
            OutputStream out = Files.newOutputStream(CONFIG_FILE);
            InputStream defaultConfigInputStream = UPeeNPiss.class.getClassLoader().getResourceAsStream("default_ports_config.yaml");
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
