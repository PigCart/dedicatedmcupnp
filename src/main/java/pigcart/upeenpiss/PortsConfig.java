package pigcart.upeenpiss;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PortsConfig {

    private Integer[] tcpPorts;
    private Integer[] udpPorts;

    public Integer[] getTcpPorts() {
        return this.tcpPorts;
    }
    public Integer[] getUdpPorts() {
        return this.udpPorts;
    }
}
