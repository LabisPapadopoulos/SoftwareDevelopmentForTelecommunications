
package gr.uoa.di.std09112.std08169.k23;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for networkData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="networkData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="accessPoints" type="{http://k23.std08169.std09112.di.uoa.gr/}accessPoint" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="networkInterfaces" type="{http://k23.std08169.std09112.di.uoa.gr/}networkInterface" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="wirelessNetworkInterfaces" type="{http://k23.std08169.std09112.di.uoa.gr/}wirelessNetworkInterface" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "networkData", propOrder = {
    "accessPoints",
    "networkInterfaces",
    "wirelessNetworkInterfaces"
})
public class NetworkData {

    @XmlElement(nillable = true)
    protected List<AccessPoint> accessPoints;
    @XmlElement(nillable = true)
    protected List<NetworkInterface> networkInterfaces;
    @XmlElement(nillable = true)
    protected List<WirelessNetworkInterface> wirelessNetworkInterfaces;

    /**
     * Gets the value of the accessPoints property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the accessPoints property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccessPoints().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AccessPoint }
     * 
     * 
     */
    public List<AccessPoint> getAccessPoints() {
        if (accessPoints == null) {
            accessPoints = new ArrayList<AccessPoint>();
        }
        return this.accessPoints;
    }

    /**
     * Gets the value of the networkInterfaces property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the networkInterfaces property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNetworkInterfaces().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NetworkInterface }
     * 
     * 
     */
    public List<NetworkInterface> getNetworkInterfaces() {
        if (networkInterfaces == null) {
            networkInterfaces = new ArrayList<NetworkInterface>();
        }
        return this.networkInterfaces;
    }

    /**
     * Gets the value of the wirelessNetworkInterfaces property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the wirelessNetworkInterfaces property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWirelessNetworkInterfaces().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WirelessNetworkInterface }
     * 
     * 
     */
    public List<WirelessNetworkInterface> getWirelessNetworkInterfaces() {
        if (wirelessNetworkInterfaces == null) {
            wirelessNetworkInterfaces = new ArrayList<WirelessNetworkInterface>();
        }
        return this.wirelessNetworkInterfaces;
    }

}
