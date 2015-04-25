package gr.uoa.di.std09112.std08169.k23;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WirelessNetworkInterface extends NetworkInterface {
    
    private static final String IWCONFIG = "iwconfig";
    private static final String CAT = "cat";
    // /proc/net/wireless
    private static final String PROC_NET_WIRELESS = "/proc/net/wireless";
//wlan0     IEEE 802.11bgn  ESSID:"int main (int argc, char **argv)"  
//          Mode:Managed  Frequency:2.437 GHz  Access Point: 00:13:33:82:48:0D   
//          Bit Rate=48 Mb/s   Tx-Power=19 dBm   
//          Retry  long limit:7   RTS thr:off   Fragment thr:off
//          Power Management:off
//          Link Quality=50/70  Signal level=-60 dBm  
//          Rx invalid nwid:0  Rx invalid crypt:0  Rx invalid frag:0
//          Tx excessive retries:14  Invalid misc:762   Missed beacon:0
    private static final String NO_WIRELESS = "^.*no wireless extensions.*$";
    
    private static final String BASE_STATION_MAC_ADDRESS_REG_EX = "^.*Access Point\\: (([0-9a-fA-F]{2}\\:){5}[0-9a-fA-F]{2}).*$";//"^.*Access Point\\: (\\S+).*$";
                                            //[^\"]: ektos apo diplo eisagwgiko
    private static final String ESSID_REG_EX = "^.*ESSID\\:\"([^\"]+)\".*$";
    private static final String CHANNEL_REG_EX = "^.*Frequency\\:(2\\.4[0-9]{2}) GHz.*$";
    //private static String ACCESS_POINT_REG_EX = "^.*Access Point\\: (([0-9a-f]{2}\\:){5}[0-9a-f]{2}).*$";
    //katastash leitourgias tou Access Point
    private static final String MODE_REG_EX = "^.*Mode\\:(\\S+).*$";
    //isxus ekpomphs tou interface
    private static final String TRANSMISSION_POWER_REG_EX = "^.*Tx-Power\\=([0-9]+) dBm.*$";
    //poiothta sundeshs
    //Link Quality=55/70  Signal level=-55 dBm 
    private static final String LINK_QUALITY_REG_EX = "^.*Link Quality[\\=\\:](\\S+).*$";
    //Link Quality=55/70  Signal level=-55 dBm 
    private static final String SIGNAL_LEVEL_REG_EX = "^.*Signal level[\\=\\:](\\S+).*$";
    /* $ cat /proc/net/wireless
       Inter-| sta-|   Quality        |   Discarded packets               | Missed | WE
        face | tus | link level noise |  nwid  crypt   frag  retry   misc | beacon | 22
        wlan0: 0000   52.  -58.  -256        0      0      0      0     19        0
     */
    private static final String NOISE_LEVEL_REG_EX = "^.*wlan0\\: ((\\S+\\s+){3}(\\S+)).*$";
    //private static final String NOISE_LEVEL_REG_EX = "^.*Noise level[\\=\\:](\\S+).*$";
    //Rx invalid nwid:0  Rx invalid crypt:0  Rx invalid frag:0
    
    //NWID: network id
    private static final String INVALID_NWID_REG_EX = "^.*Rx invalid nwid[\\=\\:](\\d+).*$";
    //CRYPT: lathos stin kruptografish
    private static final String INVALID_CRYPT_REG_EX = "^.*Rx invalid crypt[\\=\\:](\\d+).*$";
    //FRAG: tmhma
    private static final String INVALID_FRAG_REG_EX = "^.*Rx invalid frag[\\=\\:](\\d+).*$";
    //Tx excessive retries:33  Invalid misc:1021   Missed beacon:0
    //Apotuxhmenes apopeires apostolhs
    private static final String INVALID_RETRY_REG_EX = "^.*Tx excessive retries[\\=\\:](\\d+).*$";
    //Diafora akura paketa
    private static final String INVALID_MISC_REG_EX = "^.*Invalid misc[\\=\\:](\\d+).*$";
    //Bit Rate=6 Mb/s   Tx-Power=19 dBm
    private static final String BIT_RATE_REG_EX = "^.*Bit Rate[\\=\\:](\\d+).*$";
    
    private static final int NO_WIRELESS_EXIT_STATUS = 161;
    
    //patern gia elenxo an einai to interface asurmato
    private static final Pattern NO_WIRELESS_PATTERN = Pattern.compile(NO_WIRELESS);
    private static final Pattern BASE_STATION_MAC_ADDRESS_PATTERN = Pattern.compile(BASE_STATION_MAC_ADDRESS_REG_EX);
    private static final Pattern ESSID_PATTERN = Pattern.compile(ESSID_REG_EX);
    private static final Pattern CHANNEL_PATTERN = Pattern.compile(CHANNEL_REG_EX);
    private static final Pattern MODE_PATTERN = Pattern.compile(MODE_REG_EX);
    private static final Pattern TRANSMISSION_POWER_PATTERN = Pattern.compile(TRANSMISSION_POWER_REG_EX);
    private static final Pattern LINK_QUALITY_PATTERN = Pattern.compile(LINK_QUALITY_REG_EX);
    private static final Pattern SIGNAL_LEVEL_PATTERN = Pattern.compile(SIGNAL_LEVEL_REG_EX);
    private static final Pattern NOISE_LEVEL_PATTERN = Pattern.compile(NOISE_LEVEL_REG_EX);
    private static final Pattern INVALID_NWID_PATTERN = Pattern.compile(INVALID_NWID_REG_EX);
    private static final Pattern INVALID_CRYPT_PATTERN = Pattern.compile(INVALID_CRYPT_REG_EX);
    private static final Pattern INVALID_FRAG_PATTERN = Pattern.compile(INVALID_FRAG_REG_EX);
    private static final Pattern INVALID_RETRY_PATTERN = Pattern.compile(INVALID_RETRY_REG_EX);
    private static final Pattern INVALID_MISC_PATTERN = Pattern.compile(INVALID_MISC_REG_EX);
    private static final Pattern BIT_RATE_PATTERN = Pattern.compile(BIT_RATE_REG_EX);
    
    public static final float[] CHANNELS = { 2.412f, 2.417f, 2.422f, 2.427f, 2.432f, 2.437f, 2.442f, 2.447f,
        2.452f, 2.457f, 2.462f };
    
    private String baseStationMacAddress;
    private String essid;
    private Integer channel;
    private String mode;
    //isxus ekpomphs tou interface
    private float transmissionPower;
    private String linkQuality;
    private int signalLevel;
    private int noiseLevel;
    //aporrifthenda paketa
    private int discardedPackets;
    
    
    public WirelessNetworkInterface() { }
    
    public WirelessNetworkInterface(String interfaceName, String wirelessMacAddress, String ipAddress,
            String netmask, String networkAddress, String broadcastAddress, String defaultGateway, 
            float maxTrafficRate, float currentTrafficRate, float currentBandwidthRatio, float packetErrorRate, 
            int trafficBytes, long time, 
            String baseStationMacAddress, String essid, Integer channel, String mode, float transmissionPower, 
            String linkQuality, int signalLevel, int noiseLevel, int descardedPackets) {
        
        super(interfaceName, wirelessMacAddress, ipAddress, netmask, networkAddress, broadcastAddress
                , defaultGateway, maxTrafficRate, currentTrafficRate, currentBandwidthRatio, packetErrorRate, trafficBytes, time);
        this.baseStationMacAddress = baseStationMacAddress;
        this.essid = essid;
        this.channel = channel;
        this.mode = mode;
        this.transmissionPower = transmissionPower;
        this.linkQuality = linkQuality;
        this.signalLevel = signalLevel;
        this.noiseLevel = noiseLevel;
        this.discardedPackets = descardedPackets;
    }

    public void setBaseStationMacAddress(String baseStationMacAddress) {
        this.baseStationMacAddress = baseStationMacAddress;
    }

    public void setEssid(String essid) {
        this.essid = essid;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setTransmissionPower(float transmissionPower) {
        this.transmissionPower = transmissionPower;
    }

    public void setLinkQuality(String linkQuality) {
        this.linkQuality = linkQuality;
    }

    public void setSignalLevel(int signalLevel) {
        this.signalLevel = signalLevel;
    }

    public void setNoiseLevel(int noiseLevel) {
        this.noiseLevel = noiseLevel;
    }

    public void setDiscardedPackets(int discardedPackets) {
        this.discardedPackets = discardedPackets;
    }

    public String getBaseStationMacAddress() {
        return baseStationMacAddress;
    }

    public String getEssid() {
        return essid;
    }

    public Integer getChannel() {
        return channel;
    }

    public String getMode() {
        return mode;
    }

    public String getLinkQuality() {
        return linkQuality;
    }

    public float getTransmissionPower() {
        return transmissionPower;
    }

    public int getSignalLevel() {
        return signalLevel;
    }

    public int getNoiseLevel() {
        return noiseLevel;
    }

    public int getDiscardedPackets() {
        return discardedPackets;
    }
    
    public static NetworkInterface getNetworkInterface(String wirelessInterfaceName, int oldTrafficBytes, long oldTime) throws IOException, InterruptedException {

        Runtime runtime = Runtime.getRuntime();
                                //iwconfig wlan0
        String [] arguments = {IWCONFIG, wirelessInterfaceName};
        Process process = runtime.exec(arguments);
        
        //cat /proc/net/wireless
        String [] args = {CAT, PROC_NET_WIRELESS};
        Process wirelessNoiseProcess = runtime.exec(args);
        
        String baseStationMacAddress = null;
        String essid = null;
        int channel = 0;
        String mode = null;
        //isxus ekpomphs tou interface
        float transmissionPower = 0f;
        String linkQuality = null;
        int signalLevel = 0;
        int noiseLevel = 0;
        //aporrifthenda paketa
        int discardedPackets;
        String line = null;
        float frequency = 0f;
        int invalidNwid = 0;
        int invalidCrypt = 0;
        int invalidFrag = 0;
        int invalidRetries = 0;
        int invalidMisc = 0;
        
        NetworkInterface networkInterface = NetworkInterface.getNetworkInterface(wirelessInterfaceName, oldTrafficBytes, oldTime);
        
        int status = wirelessNoiseProcess.waitFor();
        if(status != 0) {
            Logger.getLogger(wirelessInterfaceName).log(Level.WARNING, "Provlima me tin " + 
                    PROC_NET_WIRELESS + "gia to interface " + wirelessInterfaceName);
            
            return networkInterface;
        } else {
            
            InputStreamReader wirelessNoiseInputStreamReader = new InputStreamReader(wirelessNoiseProcess.getInputStream());
            
            BufferedReader wirelessNoiseBufferedReader = new BufferedReader(wirelessNoiseInputStreamReader);
            
            while((line = wirelessNoiseBufferedReader.readLine()) != null) {

                Matcher noiseLevelMatcher = NOISE_LEVEL_PATTERN.matcher(line);
                
                if (noiseLevelMatcher.matches()) {
                    noiseLevel = Integer.parseInt(noiseLevelMatcher.group(3));
                }
            }
        }
        
        int exitStatus = process.waitFor();
        if ((exitStatus != 0) && (exitStatus != NO_WIRELESS_EXIT_STATUS)) {
            Logger.getLogger(wirelessInterfaceName).log(Level.WARNING, "Provlima stin " + IWCONFIG + 
                    " gia to interface " + wirelessInterfaceName);
            //periptwsh apotuxeias tis iwconfig gia to wireless, epistrefei oti emathe apo tin ifconfig
            return networkInterface;
        }
        else {            
            //an to interface den einai asurmato tha to pei sto error stream
            InputStreamReader errorStreamReader = new InputStreamReader(process.getErrorStream());
            BufferedReader errorBufferedReader = new BufferedReader(errorStreamReader);
            
            while((line = errorBufferedReader.readLine()) != null) {

                Matcher noWirelessMatcher = NO_WIRELESS_PATTERN.matcher(line);

                if (noWirelessMatcher.matches()) {
                    //den einai asurmato interface
                    return networkInterface;
                }
            }
            
            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            
            while((line = bufferedReader.readLine()) != null) {
                
                Matcher baseStationMacAddressMatcher = BASE_STATION_MAC_ADDRESS_PATTERN.matcher(line);

                if (baseStationMacAddressMatcher.matches()) {
                    baseStationMacAddress = baseStationMacAddressMatcher.group(1);
                }

                Matcher essidMatcher = ESSID_PATTERN.matcher(line);

                if (essidMatcher.matches()) {
                    essid = essidMatcher.group(1);
                }

                Matcher channelMatcher = CHANNEL_PATTERN.matcher(line);

                if (channelMatcher.matches()) {
                    frequency = Float.parseFloat(channelMatcher.group(1)); //frequency
                }
                
                Matcher modeMatcher = MODE_PATTERN.matcher(line);

                if (modeMatcher.matches()) {
                    mode = modeMatcher.group(1);
                }

                Matcher transmissionPowerMatcher = TRANSMISSION_POWER_PATTERN.matcher(line);

                if (transmissionPowerMatcher.matches()) {
                    transmissionPower = Float.parseFloat(transmissionPowerMatcher.group(1));
                }

                Matcher linkQualityMatcher = LINK_QUALITY_PATTERN.matcher(line);

                if (linkQualityMatcher.matches()) {
                    linkQuality = linkQualityMatcher.group(1);
                }
                
                Matcher signalLevelMatcher = SIGNAL_LEVEL_PATTERN.matcher(line);
                
                if (signalLevelMatcher.matches()) {
                    signalLevel = Integer.parseInt(signalLevelMatcher.group(1));
                }
                
                Matcher invalidNwidMatcher = INVALID_NWID_PATTERN.matcher(line);
                
                if (invalidNwidMatcher.matches()) {
                    invalidNwid = Integer.parseInt(invalidNwidMatcher.group(1));
                }
                
                Matcher invalidCryptMatcher = INVALID_CRYPT_PATTERN.matcher(line);
                
                if (invalidCryptMatcher.matches()) {
                    invalidCrypt = Integer.parseInt(invalidCryptMatcher.group(1));
                }
                
                Matcher invalidFragMatcher = INVALID_FRAG_PATTERN.matcher(line);
                
                if (invalidFragMatcher.matches()) {
                    invalidFrag = Integer.parseInt(invalidFragMatcher.group(1));
                }
                
                Matcher invalidRetriesMatcher = INVALID_RETRY_PATTERN.matcher(line);
                
                if (invalidRetriesMatcher.matches()) {
                    invalidRetries = Integer.parseInt(invalidRetriesMatcher.group(1));
                }
                
                Matcher invalidMiscMatcher = INVALID_MISC_PATTERN.matcher(line);
                
                if (invalidMiscMatcher.matches()) {
                    invalidMisc = Integer.parseInt(invalidMiscMatcher.group(1));
                }
                
                Matcher bitRateMatcher = BIT_RATE_PATTERN.matcher(line);
                
                if (bitRateMatcher.matches()) {
                    networkInterface.maxTrafficRate = Float.parseFloat(bitRateMatcher.group(1));
                    networkInterface.currentBandwidthRatio = 100 * networkInterface.getCurrentTrafficRate()/(float)networkInterface.maxTrafficRate;
                }
            }
            
            //for ston pinaka channels.
            //euresh tou channel
            for(int i = 0; i < CHANNELS.length; i++) {
                if(CHANNELS[i] == frequency)
                    channel = i + 1;
            }
            
            discardedPackets = invalidNwid + invalidCrypt + invalidFrag + invalidRetries + invalidMisc;
        }
        
        return new WirelessNetworkInterface(wirelessInterfaceName, networkInterface.getMacAddress(), 
                networkInterface.getIpAddress(), networkInterface.getNetmask(), networkInterface.getNetworkAddress(), 
                networkInterface.getBroadcastAddress(), networkInterface.getDefaultGateway(), 
                networkInterface.getMaxTrafficRate(), networkInterface.getCurrentTrafficRate(), 
                networkInterface.getCurrentBandwidthRatio(), networkInterface.getPacketErrorRate(), 
                networkInterface.getOldTrafficBytes(), networkInterface.getOldTime(), 
                baseStationMacAddress, essid, channel, mode, transmissionPower, linkQuality, signalLevel, noiseLevel, 
                discardedPackets);
    }
    
    @Override
    public String toString() {
        return super.toString() + 
        "\nBase Station MacAddress: " + getBaseStationMacAddress() + "\nEssid: " + getEssid() + 
        "\nChannel: " + getChannel() + "\nMode: " + getMode() + "\nTransmissionPower: " + getTransmissionPower() +
        " dBm\nLink Quality: " + getLinkQuality() + "\nSignal Level: " + getSignalLevel() + 
        " dBm\nNoise Level: " + getNoiseLevel() + " dBm\nDiscarded Packets: " + getDiscardedPackets();
    }
}
