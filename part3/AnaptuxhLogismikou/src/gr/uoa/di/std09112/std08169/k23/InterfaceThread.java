package gr.uoa.di.std09112.std08169.k23;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InterfaceThread implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(InterfaceThread.class.getName());
    
    private String interfaceName;
    private long sleepTime;
    //mhkos alusidas - oles oi katastaseis pou uparxoun stin alusida
    private int k;
    //apotuxies mexri na paw stin epomenh katastash
    private int c;
    //metavolh
    private float x;
    
    private NetworkInterface networkInterface;
    private Set<AccessPoint> accessPoints;
    //flag gia parakolouthish tis kartas diktuou
    private boolean monitorNetworkInterface;
    //flag gia skanarisma tis geitonias
    private boolean scanAccessPoints;
    
    public InterfaceThread(String interfaceName, long sleepTime, int k, int c, float x) {
        this.interfaceName = interfaceName;
        this.sleepTime = sleepTime;
        this.k = k;
        this.c = c;
        this.x = x;
        monitorNetworkInterface = true;
        scanAccessPoints = false;
        accessPoints = new HashSet<AccessPoint>();
                
    }

    //Sth methodo auth ginetai sunxronismos gia na parei to master Thread to networkInterface
    public synchronized NetworkInterface getNetworkInterface() {
        return networkInterface;
    }

    public synchronized void setMonitorNetworkInterface(boolean monitorNetworkInterface) {
        this.monitorNetworkInterface = monitorNetworkInterface;
    }

    public synchronized void setScanAccessPoints(boolean scanAccessPoints) {
        this.scanAccessPoints = scanAccessPoints;
    }

    public synchronized boolean isMonitorNetworkInterface() {
        return monitorNetworkInterface;
    }

    public synchronized boolean isScanAccessPoints() {
        return scanAccessPoints;
    }
    
    public synchronized Set<AccessPoint> getAccessPoints() {
        return accessPoints;
    }
    
    @Override
    public void run() {
        long currentSleepTime = sleepTime;
        int accessPointCounter = 0;
        try {
            
outer:      while(!Thread.currentThread().isInterrupted()) {
                
                //metraei apotuxies (an den allaxe kati)
                for(int i = 0; i < c; i++) {
                    LOGGER.log(Level.INFO, interfaceName + " " + "State is: " + (k-(currentSleepTime/sleepTime)+1) + 
                            ", Internal Loop is: " + i);
                    
                    long t = System.currentTimeMillis();
                    Set<AccessPoint> newAccessPoints = null;
                    NetworkInterface newNetworkInterface = null;
                    
                    boolean monitorNetworkInterface = true;
                    boolean scanAccessPoints = false;
                    
                    //sunxornismos twn topikwn metavlitwn me tis metavlites tou antikeimenou
                    //gia logous oikonomias xronou. An htan to synchronized stin getNetworkInterface kai
                    //stin getAccessPoints tha argouse poli kai den tha borousan na allaxtoun oi boolean
                    //metavlites tou antikeimenou
                    synchronized (this) {
                        monitorNetworkInterface = this.monitorNetworkInterface;
                        scanAccessPoints = this.scanAccessPoints;
                    }
                    
                    if (monitorNetworkInterface) {
                        int oldTrafficBytes = 0;
                        //trexousa wra
                        long oldTime = System.currentTimeMillis();
                        
                        if(networkInterface != null) {
                            oldTrafficBytes = networkInterface.getOldTrafficBytes();
                            oldTime = networkInterface.getOldTime();
                        }
                        
                        newNetworkInterface = WirelessNetworkInterface.getNetworkInterface(interfaceName,
                                //perniountai ta palia Traffic Bytes kai time pou vrike stin prohgoumenh epanalhpsh
                                oldTrafficBytes, oldTime);
                    }
                    
                    if (scanAccessPoints) {
                            //Vriskei ta asurmata duktua tis perioxhs
                            newAccessPoints = AccessPoint.getAccessPoints(interfaceName);
                    }
                        
                    
                    if (Thread.currentThread().isInterrupted()) {
                        return; //diakoph sunartishs run()
                    }
                    
                    int newAccessPointCounter = 0;
                    
                    if (newAccessPoints != null)
                        newAccessPointCounter = newAccessPoints.size();

                    boolean success = isSuccess(newNetworkInterface, networkInterface, 
                            monitorNetworkInterface, scanAccessPoints, newAccessPointCounter,accessPointCounter);
                    
                    //ta kainouria networkInterface, accessPoints pou vrike ta vazei
                    //sta private paidia (san na xrhsimopoioume setter synchronized) tou interfaceThread
                    synchronized (this) {
                        networkInterface = newNetworkInterface;
                        accessPoints = newAccessPoints;
                    }
                    
                    if(newAccessPoints != null)
                        accessPointCounter = accessPoints.size(); 
                    
                    if (success) { //an allaxe kati
                        //gurnaei stin arxikh katastash Sk
                        currentSleepTime = sleepTime;
                        
                        t = System.currentTimeMillis() - t;

                        //an menei xronos na koimithei, koimatai ston xrono tis
                        //arxikhs katastashs
                        if (currentSleepTime - t > 0)
                            Thread.sleep(currentSleepTime - t);
                        
                        //xekinaei apo tin arxh, stin Sk arxikh katastash
                        continue outer;
                    }
                    
                    
                    t = System.currentTimeMillis() - t;

                    //egine apotuxia kai koimatai ton xrono pou tou analogei
                    if (currentSleepTime - t > 0)
                        Thread.sleep(currentSleepTime - t);
 
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }
                }
                //An den exei ftasei sto telos proxwraei parakatw
                if(currentSleepTime < k*sleepTime) {     
                    //epomenh katastash. auxanetai o xronos kata sleep time
                    currentSleepTime += sleepTime;
                }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(InterfaceThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) { }
    }
    
     private boolean isSuccess(NetworkInterface newIn ,NetworkInterface oldIn, 
                                        boolean monitor, boolean scan, int newCounter, int oldCounter) {
        
        //an kanei mono monitor (parakolouthei tin karta)
        if(monitor) {
            if (oldIn == null)
                return true;
            
            boolean macSuccess = false;
            
            if (oldIn.getMacAddress() == null)
                macSuccess = !(newIn.getMacAddress() == null);
            else
                macSuccess = !oldIn.getMacAddress().equals(newIn.getMacAddress());
            
            if (macSuccess)
                return true;
            
            boolean ipAddressSuccess = false;
            
            if (oldIn.getIpAddress() == null)
                ipAddressSuccess = !(newIn.getIpAddress() == null);
            else
                ipAddressSuccess = !oldIn.getIpAddress().equals(newIn.getIpAddress());
            
            if (ipAddressSuccess)
                return true;
            
            boolean defaultGatewaySuccess = false;
            
            if (oldIn.getDefaultGateway() == null)
                defaultGatewaySuccess = !(newIn.getDefaultGateway() == null);
            else
                defaultGatewaySuccess = !oldIn.getDefaultGateway().equals(newIn.getDefaultGateway());
            
            if (defaultGatewaySuccess)
                return true;
            
            boolean netmaskSuccess = false;
            
            if (oldIn.getNetmask() == null)
                netmaskSuccess = !(newIn.getNetmask() == null);
            else
                netmaskSuccess = !oldIn.getNetmask().equals(newIn.getNetmask());
            
            if (netmaskSuccess)
                return true;
            
            if ((Math.abs((oldIn.getCurrentBandwidthRatio() - newIn.getCurrentBandwidthRatio())) > x) || 
                (Math.abs(oldIn.getPacketErrorRate() - newIn.getPacketErrorRate()) > x))
                       return true;
            
            if ((oldIn instanceof WirelessNetworkInterface) && (newIn instanceof WirelessNetworkInterface)) {
                WirelessNetworkInterface oldWi = (WirelessNetworkInterface) oldIn;
                WirelessNetworkInterface newWi = (WirelessNetworkInterface) newIn;
                
                if ((oldWi.getTransmissionPower() != newWi.getTransmissionPower()))
                    return true;
                if ((oldWi.getNoiseLevel() != newWi.getNoiseLevel()))
                    return true;
              
		boolean linkQualitySuccess = false;
			    
		if (oldWi.getLinkQuality() == null)
			linkQualitySuccess = !(newWi.getLinkQuality() == null);
		else
			linkQualitySuccess = !oldWi.getLinkQuality().equals(newWi.getLinkQuality());
			    
		if (linkQualitySuccess)
			return true;
            }
        }
        
        //an kanei skanarisma ta diktua tis perioxhs kai exei allaxei to plithos twn access point
        if ((scan) && (newCounter != oldCounter)) {
            return true;
        }
        
        return false;
    }
}
