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

/**
 * pattern: http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
 */
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
    
    private String interfaceName;
    private String macAddress;
    //InetAddress: klassh pou antiproswpeuei mia ip dieuthinsh
    private InetAddress ipAddress;
    private InetAddress netmask;
    private InetAddress networkAddress;
    private InetAddress broadcastAddress;
    //ip tou router pou einai sundedemeno
    private InetAddress defaultGateway;
    //Megistos ruthmos metadoshs dedomenwn
    protected float maxTrafficRate;
    private float currentTrafficRate;
    //Trexon pososto katanaliskomenou eurous grammhs %
    protected float currentBandwidthRatio;
    private float packetErrorRate;
    
    //old traffic bytes
    private int oldTrafficBytes = 0;
    private long oldTime = System.currentTimeMillis();
    
    public NetworkInterface(String interfaceName, String macAddress, InetAddress ipAddress, 
            InetAddress netmask, InetAddress networkAddress, InetAddress broadcastAddress, 
            InetAddress defaultGateway, float maxTrafficRate, float currentTrafficRate, 
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

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public InetAddress getNetmask() {
        return netmask;
    }

    public InetAddress getNetworkAddress() {
        return networkAddress;
    }

    public InetAddress getBroadcastAddress() {
        return broadcastAddress;
    }

    public InetAddress getDefaultGateway() {
        return defaultGateway;
    }

    public float getMaxTrafficRate() {
        return maxTrafficRate;
    }

    public float getCurrentTrafficRate() {
        return currentTrafficRate;
    }

    public float getCurrentBandwidthRatio() {
        return currentBandwidthRatio;
    }

    public float getPacketErrorRate() {
        return packetErrorRate;
    }

    public int getOldTrafficBytes() {
        return oldTrafficBytes;
    }

    public long getOldTime() {
        return oldTime;
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
        InetAddress ipAddress = null;
        InetAddress netmask = null;
        InetAddress broadcastAddress = null;
        InetAddress networkAddress = null;
        InetAddress defaultGateway = null;
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
                    ipAddress = InetAddress.getByName(ipAddressMathcer.group(1));
                }

                Matcher netmaskMatcher = NETMASK_PATTERN.matcher(line);

                if (netmaskMatcher.matches()) {
                    netmask = InetAddress.getByName(netmaskMatcher.group(1));
                }

                Matcher broadcastAdressMatcher = BROADCAST_ADDRESS_PATTERN.matcher(line);

                if(broadcastAdressMatcher.matches()) {
                    broadcastAddress = InetAddress.getByName(broadcastAdressMatcher.group(1));
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
                    defaultGateway = InetAddress.getByName(defaultGatewayMatcher.group(1));
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
            byte[] ipBytes = ipAddress.getAddress();
            byte[] maskBytes = netmask.getAddress();
            byte[] networkBytes = new byte[ipBytes.length];

            for(int i = 0; i < ipBytes.length; i++) {
                //logiko and byte pros byte metaxh ip address kai netmask
                networkBytes[i] = (byte)(ipBytes[i] & maskBytes[i]);
            }

            networkAddress = InetAddress.getByAddress(networkBytes);
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
    
    @Override
    public boolean equals(Object object) {
        //elegxos oti to object einai tupou NetworkInterface kai den einai null
        //giati to null den einai instanceof tipota
        if(object instanceof NetworkInterface) { 
            //casting tou object se NetworkInterface (afou einai tupou NetworkInterface) gia
            //na parw to onoma tou
            if(interfaceName.equals(((NetworkInterface)object).interfaceName)) {
                //tairiazoun kai sto onoma
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return interfaceName.hashCode();
    }
    
    @Override
    public String toString() {
        return "Interface name: " + interfaceName + "\nMac address: " + macAddress + "\nIp address: " + ipAddress + 
                "\nNetmask: " + netmask + "\nNetwork address: " + networkAddress + "\nBroadcast address: " + 
                broadcastAddress + "\nDefault gateway: " + defaultGateway + "\nMax traffic rate: " + maxTrafficRate +
                " kbps\nCurrent traffic rate: " + currentTrafficRate + " kbps\nCurrent bandwidth ratio: " + currentBandwidthRatio + 
                "%\nPacket error rate: " + packetErrorRate + "%";
    }
}
