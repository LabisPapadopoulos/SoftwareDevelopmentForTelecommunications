package gr.uoa.di.std09112.std08169.k23;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccessPoint {
    private static final String IWLIST = "iwlist";
    private static final String SCAN = "scan";
    //Cell 01
    private static final String CELL_NAME_REG_EX = "^.*(Cell\\s+\\S+).*$";
    private static final String MAC_ADDRESS_REG_EX = "^.*Address\\:\\s+(([0-9a-fA-F]{2}\\:){5}[0-9a-fA-F]{2}).*$";
    private static final String ESSID_REG_EX = "^.*ESSID\\:\"([^\"]+)\".*$";
    private static final String CHANNEL_REG_EX = "^.*Frequency\\:(2\\.4[0-9]{2}) GHz.*$";
    private static final String MODE_REG_EX = "^.*Mode\\:(\\S+).*$";
    private static final String SIGNAL_LEVEL_REG_EX = "^.*Signal level[\\=\\:](\\S+).*$";
    
    private static final Pattern CELL_NAME_PATTERN = Pattern.compile(CELL_NAME_REG_EX);
    private static final Pattern MAC_ADDRESS_PATTERN = Pattern.compile(MAC_ADDRESS_REG_EX);
    private static final Pattern ESSID_PATTERN = Pattern.compile(ESSID_REG_EX);
    private static final Pattern CHANNEL_PATTERN = Pattern.compile(CHANNEL_REG_EX);
    private static final Pattern MODE_PATTERN = Pattern.compile(MODE_REG_EX);
    private static final Pattern SIGNAL_LEVEL_PATTERN = Pattern.compile(SIGNAL_LEVEL_REG_EX);
    
    private String cellName;
    private String macAddress;
    private String essid;
    private int channel;
    private String mode;
    private int signalLevel;
    
    public AccessPoint(String cellName, String macAddress, String essid, int channel, String mode, int signalLevel) {
        this.cellName = cellName;
        this.macAddress = macAddress;
        this.essid = essid;
        this.channel = channel;
        this.mode = mode;
        this.signalLevel = signalLevel;
    }
    
    public String getCellName() {
        return cellName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getEssid() {
        return essid;
    }

    public int getChannel() {
        return channel;
    }

    public String getMode() {
        return mode;
    }

    public int getSignalLevel() {
        return signalLevel;
    }
    
    public static Set<AccessPoint> getAccessPoints(String interfaceName) throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        String[] iwlistArguments = {IWLIST, interfaceName,SCAN};
        Process iwlistProcess = runtime.exec(iwlistArguments);
        String line;

        String cellName = null;
        String cellNameOld = null;
        String macAddress = null;
        String essid = null;
        int channel = 0;
        String mode = null;
        int signalLevel = 0;
        float frequency = 0f;

        int status = iwlistProcess.waitFor();
        if ( status != 0) {
            Logger.getLogger(interfaceName).log(Level.WARNING, "Provlima me tin " + IWLIST + " " + 
                    status + " " + interfaceName);
            return new HashSet<AccessPoint>();
        } else {
            ///Diavasma xaraktirwn apo tin ektelesh ths iwlist
            InputStreamReader iwlistInputStreamReader = new InputStreamReader(iwlistProcess.getInputStream());
            //Diavasma grammwn apo tin ektelesh ths iwlist
            BufferedReader iwlistBufferedReader = new BufferedReader(iwlistInputStreamReader);

            Set<AccessPoint> accessPoints = new HashSet<AccessPoint>();
            

            line = iwlistBufferedReader.readLine();
            
            while((line = iwlistBufferedReader.readLine()) != null) {
                
                Matcher cellNameMatcher = CELL_NAME_PATTERN.matcher(line);
                
                if (cellNameMatcher.matches()) {
                    cellName = cellNameMatcher.group(1);
                    
                    if ((cellNameOld != null) && (!cellName.equals(cellNameOld))) {
                        
                        AccessPoint accessPoint = new AccessPoint(cellNameOld, macAddress, essid, channel, mode, signalLevel);
                        accessPoints.add(accessPoint);

                        cellName = null;   
                        macAddress = null;
                        essid = null;
                        channel = 0;
                        mode = null;
                        signalLevel = 0;
                        frequency = 0f;
                    }
                    
                    cellNameOld = cellName;
                }
                
                Matcher macAddressMatcher = MAC_ADDRESS_PATTERN.matcher(line);

                if (macAddressMatcher.matches()) {
                    macAddress = macAddressMatcher.group(1);
                }

                 Matcher essidMatcher = ESSID_PATTERN.matcher(line);

                if (essidMatcher.matches()) {
                    essid = essidMatcher.group(1);
                }

                Matcher channelMatcher = CHANNEL_PATTERN.matcher(line);

                if (channelMatcher.matches()) {
                    frequency = Float.parseFloat(channelMatcher.group(1)); //frequency
                    
                    for(int i = 0; i < WirelessNetworkInterface.CHANNELS.length; i++) {
                        if(WirelessNetworkInterface.CHANNELS[i] == frequency)
                            channel = i + 1;
                    }
                }

                 Matcher modeMatcher = MODE_PATTERN.matcher(line);

                if (modeMatcher.matches()) {
                    mode = modeMatcher.group(1);
                }

                 Matcher signalLevelMatcher = SIGNAL_LEVEL_PATTERN.matcher(line);

                if (signalLevelMatcher.matches()) {
                    signalLevel = Integer.parseInt(signalLevelMatcher.group(1));
                }
            }
        
            if (cellNameOld != null) {

                AccessPoint accessPoint = new AccessPoint(cellName, macAddress, essid, channel, mode, signalLevel);
                accessPoints.add(accessPoint);

                cellName = null;
                macAddress = null;
                essid = null;
                channel = 0;
                mode = null;
                signalLevel = 0;
                frequency = 0f;
            }
            
            return accessPoints;
        }
    }
    
    /**
     * sugkrinei auto to accessPoint me ena allo antikeimeno(px gia na vrei ta diplotupa to hashSet)
     * @param object to antikeimeno me to opoio 8a sugkrinoume
     * @return true an to object einai tupou accessPoint kai an ta essid twn antikeimenwn einai idia, false alliws
     */
    @Override
    public boolean equals (final Object object) {
        return (object instanceof AccessPoint) && essid.equals(((AccessPoint) object).essid);
    }
    
    /**
     * katakermatismos me vash to essid
     * @return to hashcode
     */
    @Override
    public int hashCode() {
        if (essid == null)
            return 0;
        else 
            return essid.hashCode();
    }
    
    @Override
    public String toString() {
        return "ESSID: " + essid + "\nMAC Address: " + macAddress + "\nChannel: " + channel +
                "\nMode: " + mode + "\nSignal Level: " + signalLevel + " dBm";
    }
}
