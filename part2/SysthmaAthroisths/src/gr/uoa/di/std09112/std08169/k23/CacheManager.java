package gr.uoa.di.std09112.std08169.k23;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author labis
 */
public class CacheManager implements Manager {

    //Gia na douleuei me opoiondhpote manager (Database Manager 'h Cache Manager)
    private Manager manager;
    //<device      <interfaceName, NetworkInterface>>
    Map<String, Map<String, NetworkInterface>> networkInterfaces;
    //<device      <macAddress, AccessPoint>>
    Map<String, Map<String, AccessPoint>> accessPoints;
    
    public CacheManager(Manager manager) {
        this.manager = manager;
        
        networkInterfaces = new HashMap<>();
        accessPoints = new HashMap<>();
    }
    
    @Override
    public synchronized void addNetworkInterface(String device, NetworkInterface networkInterface) throws SQLException {
        //Prosthikh stin vash
        manager.addNetworkInterface(device, networkInterface);
        
        //Prosthikh stin cache
        //se periptwsh pou den uparxei to eswteriko hash map, to dhmiourgei
        if(networkInterfaces.get(device) == null) {
            networkInterfaces.put(device, new HashMap<String, NetworkInterface>());
        }
        
        //update tin cache se periptwsh pou uparxei hdh
        networkInterfaces.get(device).put(networkInterface.getInterfaceName(), networkInterface);
    }

    @Override
    public synchronized void addAccessPoint(String device, AccessPoint accessPoint) throws SQLException {
        //Prosthikh stin vash
        manager.addAccessPoint(device, accessPoint);
        
        //Prosthikh stin cache
        //se periptwsh pou den uparxei to eswteriko hash map, to dhmiourgei
        if(accessPoints.get(device) == null) {
            accessPoints.put(device, new HashMap<String, AccessPoint>());
        }
        
        //update tin cache se periptwsh pou uparxei hdh
        accessPoints.get(device).put(accessPoint.getMacAddress(), accessPoint);
    }

    @Override
    public synchronized List<String> getDevices() throws SQLException {
        return manager.getDevices();
    }

    //Ta onomata twn network interfaces kai twn access points ta pairnoume panda apo tin vash
    //giati borei na mhn exoun erthei apo tin vash stin cache. Dhladh an o xrhsths den 
    //zhthse akoma kapoio p.x interface name, den to efere stin cache.
    @Override
    public synchronized List<String> getNetworkInterfaces(String device) throws SQLException {
        return manager.getNetworkInterfaces(device);
    }

    @Override
    public synchronized List<String> getAccessPoints(String device) throws SQLException {
        return manager.getAccessPoints(device);
    }

    @Override
    public synchronized NetworkInterface getNetworkInterface(String device, String interfaceName) throws SQLException {
        //Den uparxei tipota stin cache kai tha ferei auto pou tis zhththike
        //apo tin vash
        if(networkInterfaces.get(device) == null) {
            //dhmiourgeia kenou hash map gia to eswteriko
            networkInterfaces.put(device, new HashMap<String, NetworkInterface>());
        }
        
        //An einai null, den uparxei to network interface pou zhththike
        if(networkInterfaces.get(device).get(interfaceName) == null) {
            //prosthikh tou interface stin cache (apo tin vash)
            networkInterfaces.get(device).put(interfaceName, manager.getNetworkInterface(device, interfaceName));
        }
        
        //An epistrepsei null, den uparxei oute stin vash.
        //Diaforetika uparxei kai stin cache kai epistrefetai
        return networkInterfaces.get(device).get(interfaceName);
    }

    @Override
    public synchronized AccessPoint getAccessPoint(String device, String macAddress) throws SQLException {
        if(accessPoints.get(device) == null) {
            accessPoints.put(device, new HashMap<String, AccessPoint>());
        }
        
        if(accessPoints.get(device).get(macAddress) == null) {
            accessPoints.get(device).put(macAddress, manager.getAccessPoint(device, macAddress));
        }
        
        return accessPoints.get(device).get(macAddress);
    }

    //Kathe fora pou ligei to T, diagrafei tin cache kai enhmerwnei tis times
    @Override
    public synchronized void deleteTimedOutItems() throws SQLException {
        manager.deleteTimedOutItems();
        networkInterfaces.clear();
        accessPoints.clear();
    }
}
