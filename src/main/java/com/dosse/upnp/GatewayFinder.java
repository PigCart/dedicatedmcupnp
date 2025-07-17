/*
 * Copyright (C) 2015 Federico Dossena (adolfintel.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.dosse.upnp;

import pigcart.dedicatedmcupnp.DedicatedMcUpnp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.util.Enumeration;
import java.util.LinkedList;

/**
 *
 * @author Federico
 */
abstract class GatewayFinder {

    private static final String[] SEARCH_MESSAGES;

    static {
        LinkedList<String> m = new LinkedList<>();
        for (String type : new String[]{"urn:schemas-upnp-org:device:InternetGatewayDevice:1", "urn:schemas-upnp-org:service:WANIPConnection:1", "urn:schemas-upnp-org:service:WANPPPConnection:1"}) {
            m.add("M-SEARCH * HTTP/1.1\r\nHOST: 239.255.255.250:1900\r\nST: " + type + "\r\nMAN: \"ssdp:discover\"\r\nMX: 2\r\n\r\n");
        }
        SEARCH_MESSAGES = m.toArray(new String[]{});
    }

    private class GatewayListener extends Thread {

        private Inet4Address ip;
        private String request;

        public GatewayListener(Inet4Address ip, String request) {
            setName("Gateway Listener");
            this.ip = ip;
            this.request = request;
        }

        @Override
        public void run() {
            boolean foundgw = false;
            Gateway gateway = null;
            try {
                byte[] request = this.request.getBytes();
                DatagramSocket s = new DatagramSocket(new InetSocketAddress(ip, 0));
                // The Simple Service Discovery Protocol uses multicast address 239.255.255.250 on UDP port 1900
                s.send(new DatagramPacket(request, request.length, new InetSocketAddress("239.255.255.250", 1900)));
                s.setSoTimeout(3000);
                while (true) {
                    try {
                        DatagramPacket packet = new DatagramPacket(new byte[1536], 1536);
                        s.receive(packet);
                        gateway = new Gateway(packet.getData(), ip, packet.getAddress());
                        String extIp = gateway.getExternalIPv4();
                        if (extIp!=null && !extIp.equalsIgnoreCase("0.0.0.0")){ //Exclude gateways without an external IP
                            gatewayFound(gateway);
                            foundgw=true;
                        }
                    } catch (SocketTimeoutException ignored) {
                        // seems to throw regardless of whether receive was successful
                        break;
                    } catch (Throwable t) {
                        DedicatedMcUpnp.LOGGER.error("Error finding gateway: {}", t.getMessage());
                    }
                }
            } catch (Throwable t) {
                DedicatedMcUpnp.LOGGER.error("Error finding gateway: {}", t.getMessage());
            }
            if (!foundgw && gateway != null) { //Pick the last GW if none have an external IP - internet not up yet??
                DedicatedMcUpnp.LOGGER.info("Picking last gateway as none have external IP");
                gatewayFound(gateway);
            }
        }
    }

    private final LinkedList<GatewayListener> listeners = new LinkedList<>();

    public GatewayFinder() {
        for (Inet4Address ip : getLocalIPs()) {
            for (String request : SEARCH_MESSAGES) {
                GatewayListener listener = new GatewayListener(ip, request);
                listener.start();
                listeners.add(listener);
            }
        }
    }

    public boolean isSearching() {
        for (GatewayListener l : listeners) {
            if (l.isAlive()) {
                return true;
            }
        }
        return false;
    }

    public abstract void gatewayFound(Gateway g);

    private static Inet4Address[] getLocalIPs() {
        LinkedList<Inet4Address> ret = new LinkedList<>();
        try {
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            while (ifaces.hasMoreElements()) {
                try {
                    NetworkInterface iface = ifaces.nextElement();
                    if (!iface.isUp() || iface.isLoopback() || iface.isVirtual() || iface.isPointToPoint()) {
                        continue;
                    }
                    Enumeration<InetAddress> addrs = iface.getInetAddresses();
                    if (addrs == null) {
                        continue;
                    }
                    while (addrs.hasMoreElements()) {
                        InetAddress addr = addrs.nextElement();
                        if (addr instanceof Inet4Address) {
                            ret.add((Inet4Address) addr);
                        }
                    }
                } catch (Throwable t) {
                    DedicatedMcUpnp.LOGGER.error("Error getting local IPs: {}", t.getMessage());
                }
            }
        } catch (Throwable t) {
            DedicatedMcUpnp.LOGGER.error("Error getting local IPs: {}", t.getMessage());
        }
        return ret.toArray(new Inet4Address[]{});
    }

}