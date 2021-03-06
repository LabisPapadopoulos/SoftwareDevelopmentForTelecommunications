package gr.uoa.di.std09112.std08169.k23;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebService;
/**
 *
 * @author labis
 */
@WebService(endpointInterface="gr.uoa.di.std09112.std08169.k23.WebServiceAthroisths")
public class WebServiceAthroisthsImpl implements WebServiceAthroisths {

    private Manager manager;
    
    public WebServiceAthroisthsImpl(Manager manager) {
        this.manager = manager;
    }
    
    //Gia apostolh dedomewn apo to printInfoThread mesw diktuou sto webservice
    //kai telika stin vash
    @Override
    public void setNetworkData(String device, NetworkData networkData) {
        try {
            //Eisagwgh dedomenwn stin vash
            //eisagwgh twn network interface stin vash
            for(NetworkInterface networkInterface : networkData.getNetworkInterfaces()) {
                if (networkInterface != null)
                    manager.addNetworkInterface(device, networkInterface);
            }
            
            //eisagwgh twn wireless network interface stin vash
            for(WirelessNetworkInterface wirelessNetworkInterface : networkData.getWirelessNetworkInterfaces()) {
                if (wirelessNetworkInterface != null)
                    manager.addNetworkInterface(device, wirelessNetworkInterface);
            }
            
            //eisagwgh twn accessPoints stin vash
            for(AccessPoint accessPoint : networkData.getAccessPoints()) {
                if(accessPoint != null)
                    manager.addAccessPoint(device, accessPoint);
            }
        
        } catch (SQLException ex) {
            Logger.getLogger(WebServiceAthroisthsImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public AndroidDevice fromString(String device, String androidData) {
        String[] androidDataArray = androidData.split(":");
        
        String model = androidDataArray[0];
        String manufacturer = androidDataArray[1];
        String version = androidDataArray[2];
        String latitude = androidDataArray[3];
        String longitude = androidDataArray[4];
        String altitude = androidDataArray[5];
        String level = androidDataArray[6];
        String charging = androidDataArray[7];

        return new AndroidDevice(device, model, manufacturer, version, Double.valueOf(latitude), 
                Double.valueOf(longitude), Double.valueOf(altitude), Float.valueOf(level), charging);        
    }

    @Override
    public void setTerminalData(String device, String androidData) {
        
        AndroidDevice androidDevice = fromString(device, androidData);
        try {
            manager.addDeviceData(androidDevice);
        } catch (SQLException ex) {
            Logger.getLogger(WebServiceAthroisthsImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
