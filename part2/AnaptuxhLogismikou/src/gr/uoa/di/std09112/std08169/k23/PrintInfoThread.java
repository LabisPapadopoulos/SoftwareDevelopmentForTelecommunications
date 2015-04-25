package gr.uoa.di.std09112.std08169.k23;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrintInfoThread implements Runnable {
    private static final Logger LOGGER =  Logger.getLogger(PrintInfoThread.class.getName());
    
    private MasterThread masterThread;
    private long sleepTime;
    private URL webServiceUrl;
    
    public PrintInfoThread(MasterThread masterThread, long sleepTime, URL webServiceUrl) {
        this.masterThread = masterThread;
        this.sleepTime = sleepTime;
        this.webServiceUrl = webServiceUrl;
    }
    
    public PrintInfoThread(MasterThread masterThread, Properties properties) throws MalformedURLException {
        this(masterThread, Long.parseLong(properties.getProperty("sleepTime")), new URL(properties.getProperty("webServiceUrl")));
    }
    
    @Override
    public void run() {
        try {
            while(!Thread.currentThread().isInterrupted()) {
                //networkData: to paketo me ta networkInterfaces kai ta accessPoints
                NetworkData networkData = masterThread.getNetworkData();
                //elexnos stin periptwsh kenou netwrokData (pou den exei prolavei na gemisei akoma)
                if(networkData != null) {
                    
                    //Dhmiourgeia enos anikeimenou (client) pou sumberiferetai san web service apo to url apo sto opoio exei 
                    //ginei deploy to webservice. Einai kati san to arxiko interface (WebServiceAthrisths) apo wsdl se java.
                    WebServiceAthroisthsImplService webService = new WebServiceAthroisthsImplService(webServiceUrl);
                    try {
                        //Me to webservice pou dhmiourgithike pairnoume sugkekrimenh ulopoihsh tou (port gia to WebServiceAthroisths).
                        WebServiceAthroisths webServiceAthroisths = webService.getPort(WebServiceAthroisths.class);
                        //apostolh sto webservice tou network data pou exei mazeutei
                                                            //Dieuthinsh tou topikou mhxanimatos
                        webServiceAthroisths.setNetworkData(java.net.InetAddress.getLocalHost().getHostAddress(), networkData);
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(PrintInfoThread.class.getName()).log(Level.WARNING, "", ex);
                    }
                    
                    
                    System.out.println("***** NetworkInterfaces *****");
                    for(NetworkInterface networkInterface : networkData.getNetworkInterfaces()) {
                        if(networkInterface != null) {
                            
                            
                            
                            System.out.println(networkInterface.toString());
                            System.out.println("==============================");
                        }
                    }

                    System.out.println();
                    System.out.println();
                    System.out.println("***** AccessPoints *****");
                    for(AccessPoint accessPoint : networkData.getAccessPoints()) {
                        if(accessPoint != null) {
                            System.out.println(accessPoint.toString());
                            System.out.println("==============================");
                        }
                    }
                    
                    System.out.println();
                    System.out.println();
                }

                Thread.sleep(sleepTime);
                
            }
        } catch(InterruptedException ex) { }
        LOGGER.log(Level.INFO, "To PrintInfoThread termatise");
    }
}
