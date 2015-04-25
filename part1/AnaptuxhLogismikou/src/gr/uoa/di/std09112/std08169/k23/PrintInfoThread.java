package gr.uoa.di.std09112.std08169.k23;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrintInfoThread implements Runnable {
    private static final Logger LOGGER =  Logger.getLogger(PrintInfoThread.class.getName());
    
    private MasterThread masterThread;
    private long sleepTime;
    
    public PrintInfoThread(MasterThread masterThread, long sleepTime) {
        this.masterThread = masterThread;
        this.sleepTime = sleepTime;
    }
    
    public PrintInfoThread(MasterThread masterThread, Properties properties) {
        this(masterThread, Long.parseLong(properties.getProperty("sleepTime")));
    }
    
    @Override
    public void run() {
        try {
            while(!Thread.currentThread().isInterrupted()) {
                //networkData: to paketo me ta networkInterfaces kai ta accessPoints
                NetworkData networkData = masterThread.getNetworkData();
                //elexnos stin periptwsh kenou netwrokData (pou den exei prolavei na gemisei akoma)
                if(networkData != null) {
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
