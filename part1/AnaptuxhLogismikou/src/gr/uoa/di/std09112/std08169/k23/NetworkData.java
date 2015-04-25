package gr.uoa.di.std09112.std08169.k23;

import java.util.Set;

public class NetworkData {
    
    private Set<NetworkInterface> networkInterfaces; 
    private Set<AccessPoint> accessPoints;

    public NetworkData(Set<NetworkInterface> networkInterfaces, Set<AccessPoint> accessPoints) {
        this.networkInterfaces = networkInterfaces;
        this.accessPoints = accessPoints;
    }
    
    public synchronized Set<NetworkInterface> getNetworkInterfaces() {
        return networkInterfaces;
    }

    public synchronized Set<AccessPoint> getAccessPoints() {
        return accessPoints;
    }
}
