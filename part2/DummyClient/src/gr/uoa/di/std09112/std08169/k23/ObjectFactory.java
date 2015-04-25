
package gr.uoa.di.std09112.std08169.k23;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the gr.uoa.di.std09112.std08169.k23 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _SetNetworkData_QNAME = new QName("http://k23.std08169.std09112.di.uoa.gr/", "setNetworkData");
    private final static QName _SetNetworkDataResponse_QNAME = new QName("http://k23.std08169.std09112.di.uoa.gr/", "setNetworkDataResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: gr.uoa.di.std09112.std08169.k23
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SetNetworkDataResponse }
     * 
     */
    public SetNetworkDataResponse createSetNetworkDataResponse() {
        return new SetNetworkDataResponse();
    }

    /**
     * Create an instance of {@link SetNetworkData }
     * 
     */
    public SetNetworkData createSetNetworkData() {
        return new SetNetworkData();
    }

    /**
     * Create an instance of {@link NetworkInterface }
     * 
     */
    public NetworkInterface createNetworkInterface() {
        return new NetworkInterface();
    }

    /**
     * Create an instance of {@link AccessPoint }
     * 
     */
    public AccessPoint createAccessPoint() {
        return new AccessPoint();
    }

    /**
     * Create an instance of {@link NetworkData }
     * 
     */
    public NetworkData createNetworkData() {
        return new NetworkData();
    }

    /**
     * Create an instance of {@link WirelessNetworkInterface }
     * 
     */
    public WirelessNetworkInterface createWirelessNetworkInterface() {
        return new WirelessNetworkInterface();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetNetworkData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://k23.std08169.std09112.di.uoa.gr/", name = "setNetworkData")
    public JAXBElement<SetNetworkData> createSetNetworkData(SetNetworkData value) {
        return new JAXBElement<SetNetworkData>(_SetNetworkData_QNAME, SetNetworkData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetNetworkDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://k23.std08169.std09112.di.uoa.gr/", name = "setNetworkDataResponse")
    public JAXBElement<SetNetworkDataResponse> createSetNetworkDataResponse(SetNetworkDataResponse value) {
        return new JAXBElement<SetNetworkDataResponse>(_SetNetworkDataResponse_QNAME, SetNetworkDataResponse.class, null, value);
    }

}
