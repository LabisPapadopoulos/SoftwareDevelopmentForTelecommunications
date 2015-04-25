package gr.uoa.di.std09112.std08169.k23;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author labis
 */
public interface Manager {
    
    public void addNetworkInterface(String device, NetworkInterface networkInterface) throws SQLException;
    
    public void addAccessPoint(String device, AccessPoint accessPoint) throws SQLException;
    
    public List<String> getDevices() throws SQLException;
    
    public List<String> getNetworkInterfaces(String device) throws SQLException;
    
    public List<String> getAccessPoints(String device) throws SQLException;
    
    public NetworkInterface getNetworkInterface(String device, String interfaceName) throws SQLException;
    
    public AccessPoint getAccessPoint(String device, String macAddress) throws SQLException;
    
    public void deleteTimedOutItems() throws SQLException;
}
