
package gr.uoa.di.std09112.std08169.k23;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for setNetworkData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="setNetworkData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="device" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="networkData" type="{http://k23.std08169.std09112.di.uoa.gr/}networkData" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "setNetworkData", propOrder = {
    "device",
    "networkData"
})
public class SetNetworkData {

    protected String device;
    protected NetworkData networkData;

    /**
     * Gets the value of the device property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDevice() {
        return device;
    }

    /**
     * Sets the value of the device property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDevice(String value) {
        this.device = value;
    }

    /**
     * Gets the value of the networkData property.
     * 
     * @return
     *     possible object is
     *     {@link NetworkData }
     *     
     */
    public NetworkData getNetworkData() {
        return networkData;
    }

    /**
     * Sets the value of the networkData property.
     * 
     * @param value
     *     allowed object is
     *     {@link NetworkData }
     *     
     */
    public void setNetworkData(NetworkData value) {
        this.networkData = value;
    }

}
