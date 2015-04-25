package gr.uoa.di.std09112.std08169.k23;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MasterThread implements Runnable {
    private static final Logger LOGGER =  Logger.getLogger(MasterThread.class.getName());
    
    private Set<String> networkInterfaceNames;
    private Map<String, Thread> threads;
    private Map<String, InterfaceThread> interfaceThreads;
    private NetworkData networkData;
    private long sleepTime;
    //mhkos alusidas - oles oi katastaseis pou uparxoun stin alusida
    private int k;
    //apotuxies mexri na paw stin epomenh katastash
    private int c;
    //metavolh
    private float x;
    private long currentSleepTime;
    
    public MasterThread(long sleepTime, int k, int c, float x) {
        networkInterfaceNames = new HashSet<String>();
        //hashMap gia tin apothikeush twn Threads
        threads = new HashMap<String, Thread>();
        //hashMap gia tin apothikeush interfaceThreads
        interfaceThreads = new HashMap<String, InterfaceThread>();
        
        // Petage sfalma me ta adeia set, opote to sxoliazoume kai frontizei o automatos kwdikas
        //apo to DummyClient (to Web Service Client) na mhn meinoun null, alla na einai adeies listes
        this.networkData = new NetworkData();
        //this.networkData = new NetworkData(new HashSet<NetworkInterface>(), new HashSet<AccessPoint>());
        
        this.sleepTime = sleepTime;
        this.k = k;
        this.c = c;
        this.x = x;
        LOGGER.log(Level.INFO, "Sleep time: " + sleepTime + ", K : " + k + ", C : " + c + ", X : " + x);
    }
    
    public MasterThread(Properties properties) {
                                                                                        //default timh to 1 gia to k
        this(Long.parseLong(properties.getProperty("sleepTime")), Integer.parseInt(properties.getProperty("k", "1")), 
                Integer.parseInt(properties.getProperty("c", "1")), Float.parseFloat(properties.getProperty("x")));
    }

    //klhsh apo to printInfoThread
    public synchronized NetworkData getNetworkData() {
        //katharismos twn set tou NetworkData
        networkData.getNetworkInterfaces().clear();
        networkData.getWirelessNetworkInterfaces().clear();
        networkData.getAccessPoints().clear();
        
        for (String interfaceName : interfaceThreads.keySet()) {
            InterfaceThread interfaceThread = interfaceThreads.get(interfaceName);
            //prosthikh sto set NetworkInterfaces tou networkData
            if (interfaceThread.isMonitorNetworkInterface()) {
            
                NetworkInterface networkInterface = interfaceThread.getNetworkInterface();
                
                if(networkInterface instanceof WirelessNetworkInterface) {
                    WirelessNetworkInterface wirelessNetworkInterface = (WirelessNetworkInterface)networkInterface;
                    networkData.getWirelessNetworkInterfaces().add(wirelessNetworkInterface);
                } else {
                    networkData.getNetworkInterfaces().add(networkInterface);
                }

            }
            if (interfaceThread.isScanAccessPoints()) { // an kanei scan - exei vrei accessPoints
                Set<AccessPoint> accessPoints = interfaceThread.getAccessPoints();
                if(accessPoints == null) {
                    accessPoints = new HashSet<AccessPoint>();
                }
                //prosthikh sto set AccessPoint tou NetworkData
                networkData.getAccessPoints().addAll(accessPoints);
            }
        }
        return networkData;
    }
    
    @Override
    public void run() {
        try {
            currentSleepTime = sleepTime;
            
            //condition varialble: trexei to MasterThread mexri na dextei shma diakophs
outer:      while (!Thread.currentThread().isInterrupted()) {
                //Gia kathe epanalhpsh eite einai epituxeia eite oxi
                for(int i = 0; i < c; i++) {
                    boolean success = false;
                    LOGGER.log(Level.INFO, "State is: " + (k-(currentSleepTime/sleepTime)+1) + 
                                                                    ", Internal Loop is: " + i);
                    //trexousa xronikh stigmh se milisecond
                    long time = System.currentTimeMillis();

                    //Synxronismos stis diaforopoihseis twn networkInterfaces ('h na allaxoun ola 'h tipota)
                    synchronized (this) {
                        Set<String> newNetworkInterfaceNames = NetworkInterface.getAvailableNetworkInterfaceNames();

                        //Gia kathe stoixeio tou neou set
                        for (String newNetworkInterfaceName : newNetworkInterfaceNames) {
                            //an to palio set den periexei to kainourio onoma
                            if (!networkInterfaceNames.contains(newNetworkInterfaceName)) {

                                //Dhmiourgeia neou nhmatos gia to neo interface
                                InterfaceThread interfaceThread = new InterfaceThread(newNetworkInterfaceName, sleepTime, k, c, x);
                                Thread newInterfaceThread = new Thread(interfaceThread, newNetworkInterfaceName);
                                threads.put(newNetworkInterfaceName, newInterfaceThread);
                                interfaceThreads.put(newNetworkInterfaceName, interfaceThread);
                                newInterfaceThread.start();
                                LOGGER.log(Level.INFO, "Xekinise nhma gia to interface: " + newNetworkInterfaceName);
                            }
                        }

                        for (String networkInterfaceName : networkInterfaceNames) {
                            if (!newNetworkInterfaceNames.contains(networkInterfaceName)) {

                                //Klhsh tou hashmap me to sugkekrimeno onoma interface pou epese.
                                Thread toBeKilledThread = threads.get(networkInterfaceName);

                                if(toBeKilledThread != null) {
                                    //diakoph nhmatos
                                    toBeKilledThread.interrupt();
                                    //diagrafh tou nhmatos apo to hashMap threads
                                    threads.remove(networkInterfaceName);
                                    //diagrafh tou nhmatos apo to hashMap interfaceThreads
                                    interfaceThreads.remove(networkInterfaceName);

                                    LOGGER.log(Level.INFO, "Stamatise to nhma : " + networkInterfaceName);
                                }
                            }
                        }
                        
                        InterfaceThread minInterfaceThread = null;
                        int countWirelessInterfaces = 0;
                        //apo auta pou mhnane sto hashMap, vriskw to asurmato interface me 
                        //to mikrotero pososto katanaluskomenou eurous zvnhs (bandwidth)
                        for(String interfaceName : interfaceThreads.keySet()) {
                            //epistrofh tou interfaceThread apo to Map
                            InterfaceThread interfaceThread = interfaceThreads.get(interfaceName);
                            //apo to nhma pairnoume to networkInterface
                            NetworkInterface networkInterface = interfaceThread.getNetworkInterface();
                            
                            if (networkInterface instanceof WirelessNetworkInterface) {
                                //vrike wireless
                                countWirelessInterfaces ++;
                                float currentBandwidthRatio = networkInterface.getCurrentBandwidthRatio();
                                
                                    //an den uparxei kapoio mikrotero akoma
                                if ((minInterfaceThread == null) ||
                                        //elaxisto pososto pou exw vrei mexri stigmhs                           //trexon pososto
                                        (minInterfaceThread.getNetworkInterface().getCurrentBandwidthRatio() > currentBandwidthRatio)) {
                                
                                    minInterfaceThread = interfaceThread;
                                }
                            }
                        }
                        
                        if (countWirelessInterfaces == 0)
                            LOGGER.warning("Den vrika kanena asurmato interface, opote den tha skanarw gia AccessPoints.");
                        else if (countWirelessInterfaces == 1) {
                            LOGGER.warning("Vrika mono ena asurmato interface kai tha kanei skanarisma twn access points kai tis kartas diktuou");
                            //tha kanei kai tis duo doulies
                            minInterfaceThread.setScanAccessPoints(true);
                            minInterfaceThread.setMonitorNetworkInterface(true);
                        }
                        else {//vrethikan polla wireless interfaces 
                            //To nhma me to elaxisto bandwidth tha psaxei gia ta diktua tis perioxhs
                            minInterfaceThread.setScanAccessPoints(true);
                            //kai den tha parakolouthei tin karta diktuou
                            minInterfaceThread.setMonitorNetworkInterface(false);
                        }

                        //an allaxe to plithos twn interfaces
                        success = networkInterfaceNames.size() != newNetworkInterfaceNames.size();
                        networkInterfaceNames = newNetworkInterfaceNames;
                    }
                    
                    if (success) {
                        //einai epituxeia
                        currentSleepTime = sleepTime;
                        long t = currentSleepTime - (System.currentTimeMillis() - time);
                        if (t > 0)
                            Thread.sleep(t);

                        //xekinaei na metraei apotuxeies apo to mhden
                        continue outer;
                    }
                    
                    long t = currentSleepTime - (System.currentTimeMillis() - time);
                    if (t > 0)
                        //teleiwse h apotuxeia kai koimatai
                        Thread.sleep(t);
                   
                }
                
                //an den einai stin teleutaia katastash (tin S1)
                if (currentSleepTime < k*sleepTime) {
                    //epomenh katastash
                    currentSleepTime += sleepTime;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MasterThread.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch (InterruptedException ex) {} //To InterruptedException den einai oti kati phge strava alla prepei na diakopei h ektelesn.
        
        LOGGER.log(Level.INFO, "To MasterThread termatise");
    }
    
    public static void main(final String[] arguments) {
        if (arguments.length != 1)
        {
            LOGGER.log(Level.SEVERE, "Den vrika properties file 'h edwses polla orismata");
            return;
        }
        
        try {
            Properties properties = new Properties();
            //fortwnei to prwto orisma (upothetei oti einai arxeio me to FileInputStream)
            properties.load(new FileInputStream(arguments[0]));
            MasterThread masterThread = new MasterThread(properties);
            final Thread masterThreadThread = new Thread(masterThread);
            PrintInfoThread printInfoThread = new PrintInfoThread(masterThread, properties);
            final Thread printInfoThreadThread = new Thread(printInfoThread);
            masterThreadThread.start();
            printInfoThreadThread.start();
            
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        //shma diakophs sto nhma printInfoThreadThread
                        printInfoThreadThread.interrupt();
                        printInfoThreadThread.join();
                        //shma diakophs sto nhma masterThread
                        masterThreadThread.interrupt();
                        masterThreadThread.join();
                        System.out.println("\nOla ta nhmata termatistikan...");
                    } catch (InterruptedException ex) { }
                }
            }));
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "To properties file " + arguments[0] + " den uparxei", ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Den boresa na diavasw properties file "  + arguments[0], ex);
        }
        
    }
}
