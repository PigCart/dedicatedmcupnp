package pigcart.dedicatedmcupnp;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Config implements Serializable {
    public static ArrayList<Integer> tcpPorts = new ArrayList<>();
    public static ArrayList<Integer> udpPorts = new ArrayList<>();

    private static final Path CONFIG_FILE = Paths.get("config", "upnp.txt");

    public static void readConfig() {
        if (!Files.exists(CONFIG_FILE)) {
            createDefaultConfig();
        }
        try (BufferedReader br = Files.newBufferedReader(CONFIG_FILE)) {
            String line;
            while((line = br.readLine()) != null) {
                if (!line.isBlank() && !line.startsWith("#")) {
                    if (line.startsWith("TCP ")) {
                        tcpPorts.add(Integer.valueOf(line.substring(4)));
                    } else if (line.startsWith("UDP ")) {
                        udpPorts.add(Integer.valueOf(line.substring(4)));
                    } else {
                        createDefaultConfig();
                        DedicatedMcUpnp.LOGGER.error("Invalid config entry: {}", line);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            DedicatedMcUpnp.LOGGER.error("Error loading config: {}", String.valueOf(e));
        } catch (NumberFormatException e) {
            createDefaultConfig();
            DedicatedMcUpnp.LOGGER.error("Malformed config file. {}", String.valueOf(e));
        }

        // delete old version of config file
        try {
            Path oldFile = Paths.get("config", "upnp.yaml");
            if (Files.exists(oldFile)) {
                DedicatedMcUpnp.LOGGER.info("Deleting old config");
                Files.delete(oldFile);
            }
        } catch (IOException e) {
            DedicatedMcUpnp.LOGGER.error("Error deleting old config: {}", String.valueOf(e));
        }
    }
    private static void createDefaultConfig() {
        try {
            if (!Files.exists(CONFIG_FILE.getParent())) {
                Files.createDirectories(CONFIG_FILE.getParent().toAbsolutePath());
            }
            OutputStream out = Files.newOutputStream(CONFIG_FILE);
            InputStream defaultConfigInputStream = DedicatedMcUpnp.class.getClassLoader().getResourceAsStream("default_ports_config.txt");
            if (defaultConfigInputStream == null) {
                throw new IOException("Could not load default ports config");
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = defaultConfigInputStream.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            defaultConfigInputStream.close();
            out.close();
        } catch (IOException e) {
            DedicatedMcUpnp.LOGGER.error("Error creating default config: {}", String.valueOf(e));
        }
    }
}
