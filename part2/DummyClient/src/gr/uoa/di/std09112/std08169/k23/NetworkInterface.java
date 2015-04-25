
package gr.uoa.di.std09112.std08169.k23;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for networkInterface complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="networkInterface">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="broadcastAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="currentBandwidthRatio" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="currentTrafficRate" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="defaultGateway" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="interfaceName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ipAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="macAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="maxTrafficRate" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="netmask" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="networkAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="oldTime" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="oldTrafficBytes" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="packetErrorRate" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "networkInterface", propOrder = {
    "broadcastAddress",
    "currentBandwidthRatio",
    "currentTrafficRate",
    "defaultGateway",
    "interfaceName",
    "ipAddress",
    "macAddress",
    "maxTrafficRate",
    "netmask",
    "networkAddress",
    "oldTime",
    "oldTrafficBytes",
    "packetErrorRate"
})
@XmlSeeAlso({
    WirelessNetworkInterface.class
})
public class NetworkInterface {
      private static final String IFCONFIG = "/sbin/ifconfig";
    private static final String SHORT_LIST = "-s";
    private static final String ROUTE = "route";
    private static final String NUMERICAL_ADDRESS = "-n";
    private static final String ETHTOOL = "ethtool";
    
    // --- Regular Expressions ---
    //eth0
    private static final String INTERFACE_NAME_REG_EX = "^(\\S+).*$";
    
    //eth0      Link encap:Ethernet  HWaddr 1c:6f:65:d7:57:3c
    private static final String MAC_ADDRESS_REG_EX = "^.*HWaddr (([0-9a-fA-F]{2}\\:){5}[0-9a-fA-F]{2}).*$";
    
    //          inet addr:192.168.1.6  Bcast:192.168.1.255  Mask:255.255.255.0
    private static final String IP_ADDRESS_REG_EX = "^.*inet addr\\:((\\d{1,3}\\.){3}\\d{1,3}).*$";
    private static final String BROADCAST_ADDRESS_REG_EX = "^.*Bcast\\:((\\d{1,3}\\.){3}\\d{1,3}).*$";
    private static final String NETMASK_REG_EX = "^.*Mask\\:((\\d{1,3}\\.){3}\\d{1,3}).*$";

//    Kernel IP routing table
//    Destination     Gateway         Genmask         Flags Metric Ref    Use Iface
//    0.0.0.0         192.168.1.1     0.0.0.0         UG    0      0        0 wlan0
//    169.254.0.0     0.0.0.0         255.255.0.0     U     1000   0        0 wlan0
//    192.168.1.0     0.0.0.0         255.255.255.0   U     2      0        0 wlan0
    private static final String DEFAULT_GATEWAY_REG_EX = "^0\\.0\\.0\\.0\\s+((\\d{1,3}\\.){3}\\d{1,3}).*$";
    
    //RX bytes:38050329 (38.0 MB)  TX bytes:4369750 (4.3 MB)
    private static final String RECEIVED_BYTES_REG_EX = "^.*RX bytes\\:(\\d+).*$";
    private static final String TRANSMITTED_BYTES_REG_EX = "^.*TX bytes\\:(\\d+).*$";
    
    //RX packets:34750 errors:0 dropped:0 overruns:0 frame:0
    private static final String RECEIVED_PACKETS_REG_EX = "^.*RX packets\\:(\\d+) errors\\:(\\d+).*$";
    //TX packets:28031 errors:0 dropped:0 overruns:0 carrier:0
    private static final String TRANSMITTED_PACKETS_REG_EX = "^.*TX packets\\:(\\d+) errors\\:(\\d+).*$";
    private static final String MAX_TRAFFIC_RATE_REG_EX = "^.*Speed\\:\\s+(\\d+(\\.\\d+)?).*$";

    private static final Pattern MAC_ADDRESS_PATTERN = Pattern.compile(MAC_ADDRESS_REG_EX);
    private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile(IP_ADDRESS_REG_EX);
    private static final Pattern NETMASK_PATTERN = Pattern.compile(NETMASK_REG_EX);
    private static final Pattern BROADCAST_ADDRESS_PATTERN = Pattern.compile(BROADCAST_ADDRESS_REG_EX);
    private static final Pattern TRANSMITTED_BYTES_PATTERN = Pattern.compile(TRANSMITTED_BYTES_REG_EX);
    private static final Pattern RECEIVED_BYTES_PATTERN = Pattern.compile(RECEIVED_BYTES_REG_EX);
    private static final Pattern TRANSMITTED_PACKETS_PATTERN = Pattern.compile(TRANSMITTED_PACKETS_REG_EX); //+ transmittedErrors
    private static final Pattern RECEIVED_PACKETS_PATTERN = Pattern.compile(RECEIVED_PACKETS_REG_EX); //+ receivedErrors
    private static final Pattern DEFAULT_GATEWAY_PATTERN = Pattern.compile(DEFAULT_GATEWAY_REG_EX);
    private static final Pattern INTERFACE_NAME_PATTERN = Pattern.compile(INTERFACE_NAME_REG_EX);
    private static final Pattern MAX_TRAFFIC_RATE_PATTERN = Pattern.compile(MAX_TRAFFIC_RATE_REG_EX);
    
    private static final int ETHTOOL_NO_DATA_EXIT_STATUS = 75;
    
    protected String broadcastAddress;
    protected float currentBandwidthRatio;
    protected float currentTrafficRate;
    protected String defaultGateway;
    protected String interfaceName;
    protected String ipAddress;
    protected String macAddress;
    protected float maxTrafficRate;
    protected String netmask;
    protected String networkAddress;
    protected long oldTime;
    protected int oldTrafficBytes;
    protected float packetErrorRate;
    
    public NetworkInterface() {}
    
    public NetworkInterface(String interfaceName, String macAddress, String ipAddress, 
            String netmask, String networkAddress, String broadcastAddress, 
            String defaultGateway, float maxTrafficRate, float currentTrafficRate, 
            float currentBandwidthRatio, float packetErrorRate, int oldTrafficBytes, 
            long oldTime) {
        
        if(interfaceName == null) {
            throw new IllegalArgumentException("To interface prepei na exei onoma");
        }
        
        this.interfaceName = interfaceName;
        this.macAddress = macAddress;
        this.ipAddress = ipAddress;
        this.netmask = netmask;
        this.networkAddress = networkAddress;
        this.broadcastAddress = broadcastAddress;
        this.defaultGateway = defaultGateway;
        this.maxTrafficRate = maxTrafficRate;
        this.currentTrafficRate = currentTrafficRate;
        this.currentBandwidthRatio = currentBandwidthRatio;
        this.packetErrorRate = packetErrorRate;
        this.oldTrafficBytes = oldTrafficBytes;
        this.oldTime = oldTime;
    }

    /**
     * Gets the value of the broadcastAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBroadcastAddress() {
        return broadcastAddress;
    }

    /**
     * Sets the value of the broadcastAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBroadcastAddress(String value) {
        this.broadcastAddress = value;
    }

    /**
     * Gets the value of the currentBandwidthRatio property.
     * 
     */
    public float getCurrentBandwidthRatio() {
        return currentBandwidthRatio;
    }

    /**
     * Sets the value of the currentBandwidthRatio property.
     * 
     */
    public void setCurrentBandwidthRatio(float value) {
        this.currentBandwidthRatio = value;
    }

    /**
     * Gets the value of the currentTrafficRate property.
     * 
     */
    public float getCurrentTrafficRate() {
        return currentTrafficRate;
    }

    /**
     * Sets the value of the currentTrafficRate property.
     * 
     */
    public void setCurrentTrafficRate(float value) {
        this.currentTrafficRate = value;
    }

    /**
     * Gets the value of the defaultGateway property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultGateway() {
        return defaultGateway;
    }

    /**
     * Sets the value of the defaultGateway property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultGateway(String value) {
        this.defaultGateway = value;
    }

    /**
     * Gets the value of the interfaceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInterfaceName() {
        return interfaceName;
    }

    /**
     * Sets the value of the interfaceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInterfaceName(String value) {
        this.interfaceName = value;
    }

    /**
     * Gets the value of the ipAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets the value of the ipAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIpAddress(String value) {
        this.ipAddress = value;
    }

    /**
     * Gets the value of the macAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * Sets the value of the macAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMacAddress(String value) {
        this.macAddress = value;
    }

    /**
     * Gets the value of the maxTrafficRate property.
     * 
     */
    public float getMaxTrafficRate() {
        return maxTrafficRate;
    }

    /**
     * Sets the value of the maxTrafficRate property.
     * 
     */
    public void setMaxTrafficRate(float value) {
        this.maxTrafficRate = value;
    }

    /**
     * Gets the value of the netmask property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNetmask() {
        return netmask;
    }

    /**
     * Sets the value of the netmask property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNetmask(String value) {
        this.netmask = value;
    }

    /**
     * Gets the value of the networkAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNetworkAddress() {
        return networkAddress;
    }

    /**
     * Sets the value of the networkAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNetworkAddress(String value) {
        this.networkAddress = value;
    }

    /**
     * Gets the value of the oldTime property.
     * 
     */
    public long getOldTime() {
        return oldTime;
    }

    /**
     * Sets the value of the oldTime property.
     * 
     */
    public void setOldTime(long value) {
        this.oldTime = value;
    }

    /**
     * Gets the value of the oldTrafficBytes property.
     * 
     */
    public int getOldTrafficBytes() {
        return oldTrafficBytes;
    }

    /**
     * Sets the value of the oldTrafficBytes property.
     * 
     */
    public void setOldTrafficBytes(int value) {
        this.oldTrafficBytes = value;
    }

    /**
     * Gets the value of the packetErrorRate property.
     * 
     */
    public float getPacketErrorRate() {
        return packetErrorRate;
    }

    /**
     * Sets the value of the packetErrorRate property.
     * 
     */
    public void setPacketErrorRate(float value) {
        this.packetErrorRate = value;
    }
    
    public static NetworkInterface getNetworkInterface(String interfaceName, int oldTrafficBytes, long oldTime) throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
                                //p.x: ifconfig eth0
        String[] ifconfigArguments = {IFCONFIG, interfaceName};
                                //p.x: route -n
        String[] routeArguments = {ROUTE, NUMERICAL_ADDRESS};
        String[] ethtoolArguments = {ETHTOOL, interfaceName};
        
        Process ifconfigProcess = runtime.exec(ifconfigArguments);
        Process routeProcess = runtime.exec(routeArguments);
        Process ethtoolProcess = runtime.exec(ethtoolArguments);
        
        String line;
        String macAddress = null;
        String ipAddress = null;
        String netmask = null;
        String broadcastAddress = null;
        String networkAddress = null;
        String defaultGateway = null;
        float maxTrafficRate = 0f;
        int transmittedBytes = 0;
        int receivedBytes = 0;
        int transmittedPackets = 0;
        int receivedPackets = 0;
        int transmittedErrors = 0;
        int receivedErrors = 0;
        
        if (ifconfigProcess.waitFor() != 0)
            Logger.getLogger(interfaceName).log(Level.WARNING, "Provlima me tin " + IFCONFIG);
        else {
            ///Diavasma xaraktirwn apo tin ektelesh ths ifconfig
            InputStreamReader ifconfigInputStreamReader = new InputStreamReader(ifconfigProcess.getInputStream());
            //Diavasma grammwn apo tin ektelesh ths ifconfig
            BufferedReader ifconfigBufferedReader = new BufferedReader(ifconfigInputStreamReader);
            
            while((line = ifconfigBufferedReader.readLine()) != null) {

                Matcher macAddressMatcher = MAC_ADDRESS_PATTERN.matcher(line);

                if (macAddressMatcher.matches()) {
                    macAddress = macAddressMatcher.group(1);
                }


                Matcher ipAddressMathcer = IP_ADDRESS_PATTERN.matcher(line);

                if(ipAddressMathcer.matches()) {
                    ipAddress = ipAddressMathcer.group(1);
                }

                Matcher netmaskMatcher = NETMASK_PATTERN.matcher(line);

                if (netmaskMatcher.matches()) {
                    netmask = netmaskMatcher.group(1);
                }

                Matcher broadcastAdressMatcher = BROADCAST_ADDRESS_PATTERN.matcher(line);

                if(broadcastAdressMatcher.matches()) {
                    broadcastAddress = broadcastAdressMatcher.group(1);
                }

                Matcher transmittedBytesMatcher = TRANSMITTED_BYTES_PATTERN.matcher(line);

                if (transmittedBytesMatcher.matches()) {
                    transmittedBytes = Integer.parseInt(transmittedBytesMatcher.group(1));
                }

                Matcher receivedBytesMatcher = RECEIVED_BYTES_PATTERN.matcher(line);

                if (receivedBytesMatcher.matches()) {
                    receivedBytes = Integer.parseInt(receivedBytesMatcher.group(1));
                }

                Matcher transmittedPacketsMatcher = TRANSMITTED_PACKETS_PATTERN.matcher(line);

                if (transmittedPacketsMatcher.matches()) {
                    transmittedPackets = Integer.parseInt(transmittedPacketsMatcher.group(1));
                    transmittedErrors = Integer.parseInt(transmittedPacketsMatcher.group(2));
                }

                Matcher receivedPacketsMatcher = RECEIVED_PACKETS_PATTERN.matcher(line);

                if (receivedPacketsMatcher.matches()) {
                    receivedPackets = Integer.parseInt(receivedPacketsMatcher.group(1));
                    receivedErrors = Integer.parseInt(receivedPacketsMatcher.group(2));
                }
            }
        }
        
        if (routeProcess.waitFor() != 0) 
            Logger.getLogger(interfaceName).log(Level.WARNING, "Provlima me tin " + ROUTE);
        else {
            //Diavasma xarakthrwn apo tin ektelesh tis route
            InputStreamReader routeInputStreamReader = new InputStreamReader(routeProcess.getInputStream());
            //Diavasma grammwn apo tin ektelesh tis route
            BufferedReader routeBufferedReader = new BufferedReader(routeInputStreamReader);
            
            while((line = routeBufferedReader.readLine()) != null) {

                Matcher defaultGatewayMatcher = DEFAULT_GATEWAY_PATTERN.matcher(line);

                if (defaultGatewayMatcher.matches()) {
                    defaultGateway = defaultGatewayMatcher.group(1);
                    //default_Gateway = defaultGatewayMatcher.group(1);
                }
            }
        }
        
        int exitStatus = ethtoolProcess.waitFor();
        if ((exitStatus != 0) && (exitStatus != ETHTOOL_NO_DATA_EXIT_STATUS))
            Logger.getLogger(interfaceName).log(Level.WARNING,"Provlima stin " + ETHTOOL);
        else {
            InputStreamReader ethtoolInputStreamReader = new InputStreamReader(ethtoolProcess.getInputStream());
            BufferedReader ethtoolBufferedReader = new BufferedReader(ethtoolInputStreamReader);

            while ((line = ethtoolBufferedReader.readLine())!=null){
                Matcher maxTrafficRateMatcher = MAX_TRAFFIC_RATE_PATTERN.matcher(line);

                if (maxTrafficRateMatcher.matches()) {
                    maxTrafficRate = Float.parseFloat(maxTrafficRateMatcher.group(1)) * 1024; //Mbps * 1024 = kbps
                }
            }

        }
        
        if((ipAddress != null) && (netmask != null)) {
            byte[] ipBytes = InetAddress.getByName(ipAddress).getAddress();
            byte[] maskBytes = InetAddress.getByName(netmask).getAddress();
            byte[] networkBytes = new byte[ipBytes.length];

            for(int i = 0; i < ipBytes.length; i++) {
                //logiko and byte pros byte metaxh ip address kai netmask
                networkBytes[i] = (byte)(ipBytes[i] & maskBytes[i]);
            }

            networkAddress = InetAddress.getByAddress(networkBytes).toString();
        }
        
        //athroisma twn traffic bytes (kainouria)
        int trafficBytes = receivedBytes + transmittedBytes;
        int trafficPackets = receivedPackets + transmittedPackets;
        int trafficErrors = receivedErrors + transmittedErrors;
        long time = System.currentTimeMillis(); //trexon xronos

        //trexon ruthmos metadwshs
        float currentTrafficRate;
        if ( (float)(time - oldTime) != 0.0)
            currentTrafficRate = //((Rx + Tx)(t + 1) - (Rx + Tx)(t)) / Δt
                                                                //bytes se kilobits
                (((trafficBytes - oldTrafficBytes) * 8) / (float)1024)/
                                                        //milliseconds se seconds
                (((float)(time - oldTime))/1000);
        else
            currentTrafficRate = 0;
        
        //pososto katanaliskomenou (poso xrhsimopoiei apo oso exei na dwsei) eurous grammhs
        float currentBandwidthRatio;
        if (maxTrafficRate != 0)
            currentBandwidthRatio = (currentTrafficRate/maxTrafficRate) * 100;
        else
            currentBandwidthRatio = 0;
        
        //ruthmos sfalmatwn
        float packetErrorRate;
        if (trafficPackets != 0)
            packetErrorRate = trafficErrors/(float)trafficPackets * 100;
        else
            packetErrorRate = 0;
        
        return new NetworkInterface(interfaceName, macAddress, ipAddress, netmask, networkAddress, 
                broadcastAddress, defaultGateway, maxTrafficRate, currentTrafficRate,
                                                        //enhmerwsh twn bytes, time 
                currentBandwidthRatio, packetErrorRate, trafficBytes, time);
    }

    public static Set<String> getAvailableNetworkInterfaceNames() throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
                                //ifconfig -s
        String[] arguments = {IFCONFIG, SHORT_LIST};
        
        Process process = runtime.exec(arguments);
        
        if (process.waitFor() != 0) { //Eixe provlima kai epestrepse to paidi != 0
            throw new IOException("Provlima stin " + IFCONFIG);
        }
        
         //Diavasma xarakthrwn me to InputStreamReader
        InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
        //Diavasma grammwn
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = bufferedReader.readLine(); //Diavazei tin 1h grammh pou den ti theloume
        
     
       Set<String> interfaceNames = new HashSet<String>();
        
        while((line = bufferedReader.readLine()) != null ) {
            Matcher matcher = INTERFACE_NAME_PATTERN.matcher(line);
            if (matcher.matches()) {
                interfaceNames.add(matcher.group(1));
            }
        }
        
        return interfaceNames;
        
    }
}
