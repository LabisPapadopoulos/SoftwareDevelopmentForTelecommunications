package gr.uoa.di.std09112.std08169.k23;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
/**
 *
 * @author labis
 */
@WebService(name = "WebServiceAthroisths")
public interface WebServiceAthroisths {

    //public void setMonitorData(String device , MonitorData MD)
    @WebMethod(operationName = "setNetworkData")
    public void setNetworkData(@WebParam(name = "device") String device , @WebParam(name = "networkData") NetworkData networkData);
    
    @WebMethod(operationName = "setTerminalData")
    public void setTerminalData(@WebParam(name = "device") String device, @WebParam(name = "androidData") String androidData);
}
