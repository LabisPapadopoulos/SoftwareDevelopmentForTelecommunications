package gr.uoa.di.std09112.std08169.k23;

import java.util.HashSet;
import java.util.Set;

public class NetworkData {
    
    private Set<NetworkInterface> networkInterfaces;
    //Gia na pernaei xexwrista sto SOAP kai na min ta thewrei ola apla network interfaces.
    private Set<WirelessNetworkInterface> wirelessNetworkInterfaces;
    private Set<AccessPoint> accessPoints;

    public NetworkData() {
        networkInterfaces = new HashSet<NetworkInterface>();
        wirelessNetworkInterfaces = new HashSet<WirelessNetworkInterface>();
        accessPoints = new HashSet<AccessPoint>();
    }

    public NetworkData(Set<NetworkInterface> networkInterfaces, Set<WirelessNetworkInterface> wirelessNetworkInterfaces, 
            Set<AccessPoint> accessPoints) {
        this.networkInterfaces = networkInterfaces;
        this.wirelessNetworkInterfaces = wirelessNetworkInterfaces;
        this.accessPoints = accessPoints;
    }
    
    public synchronized void setNetworkInterfaces(Set<NetworkInterface> networkInterfaces) {
        this.networkInterfaces = networkInterfaces;
    }

    public synchronized void setWirelessNetworkInterfaces(Set<WirelessNetworkInterface> wirelessNetworkInterfaces) {
        this.wirelessNetworkInterfaces = wirelessNetworkInterfaces;
    }
    
    public synchronized void setAccessPoints(Set<AccessPoint> accessPoints) {
        this.accessPoints = accessPoints;
    }
    
    public synchronized Set<NetworkInterface> getNetworkInterfaces() {
        return networkInterfaces;
    }
    
    public synchronized Set<WirelessNetworkInterface> getWirelessNetworkInterfaces() {
        return wirelessNetworkInterfaces;
    }

    public synchronized Set<AccessPoint> getAccessPoints() {
        return accessPoints;
    }
}
