
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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for accessPoint complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accessPoint">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cellName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="channel" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="essid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="macAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="signalLevel" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "accessPoint", propOrder = {
    "cellName",
    "channel",
    "essid",
    "macAddress",
    "mode",
    "signalLevel"
})
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

    protected String cellName;
    protected int channel;
    protected String essid;
    protected String macAddress;
    protected String mode;
    protected int signalLevel;
    
    public AccessPoint() {}
    
    public AccessPoint(String cellName, String macAddress, String essid, int channel, String mode, int signalLevel) {
        this.cellName = cellName;
        this.macAddress = macAddress;
        this.essid = essid;
        this.channel = channel;
        this.mode = mode;
        this.signalLevel = signalLevel;
    }

    /**
     * Gets the value of the cellName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCellName() {
        return cellName;
    }

    /**
     * Sets the value of the cellName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCellName(String value) {
        this.cellName = value;
    }

    /**
     * Gets the value of the channel property.
     * 
     */
    public int getChannel() {
        return channel;
    }

    /**
     * Sets the value of the channel property.
     * 
     */
    public void setChannel(int value) {
        this.channel = value;
    }

    /**
     * Gets the value of the essid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEssid() {
        return essid;
    }

    /**
     * Sets the value of the essid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEssid(String value) {
        this.essid = value;
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
     * Gets the value of the mode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMode() {
        return mode;
    }

    /**
     * Sets the value of the mode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMode(String value) {
        this.mode = value;
    }

    /**
     * Gets the value of the signalLevel property.
     * 
     */
    public int getSignalLevel() {
        return signalLevel;
    }

    /**
     * Sets the value of the signalLevel property.
     * 
     */
    public void setSignalLevel(int value) {
        this.signalLevel = value;
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

}
