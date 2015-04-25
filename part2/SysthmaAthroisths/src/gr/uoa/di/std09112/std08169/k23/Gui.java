package gr.uoa.di.std09112.std08169.k23;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.xml.ws.Endpoint;

/**
 * box layout: http://docs.oracle.com/javase/tutorial/uiswing/layout/box.html
 * @author labis
 */
public class Gui implements Runnable {
    
    private final Manager manager;
    //Einai private gia na to vlepei kai o swing Worker kai h run()
    private final JComboBox<String> devices;
    private final JLabel macAddress;
    private final JLabel ipAddress;
    private final JLabel netMask;
    private final JLabel broadcastAddress;
    private final JLabel defaultGateway;
    private final JLabel maxTrafficRate;
    private final JLabel currentTrafficRate;
    private final JLabel packetErrorRate;
    private final JLabel baseStationMacAddress;
    private final JLabel essid;
    private final JLabel channel;
    private final JLabel mode;
    private final JLabel transmissionPower;
    private final JLabel linkQuality;
    private final JLabel signalLevel;
    private final JLabel noiseLevel;
    private final JLabel discardedPackets;
    
    //AccessPoint
    private final JLabel accessPointEssid;
    private final JLabel accessPointChannel;
    private final JLabel accessPointMode;
    private final JLabel accessPointSignalLevel;
    private final long guiRefreshTime;
    
    private SwingWorker<Object, List<String>> swingWorker;
    
    public Gui(Manager manager, long guiRefreshTime) throws ClassNotFoundException, SQLException {
        this.manager = manager;
        //Dhmiourgeia listas twn devices
        devices = new JComboBox<String>();
        macAddress = new JLabel();
        ipAddress = new JLabel();
        netMask = new JLabel();
        broadcastAddress = new JLabel();
        defaultGateway = new JLabel();
        maxTrafficRate = new JLabel();
        currentTrafficRate = new JLabel();
        packetErrorRate = new JLabel();
        baseStationMacAddress = new JLabel();
        essid = new JLabel();
        channel = new JLabel();
        mode = new JLabel();
        transmissionPower = new JLabel();
        linkQuality = new JLabel();
        signalLevel = new JLabel();
        noiseLevel = new JLabel();
        discardedPackets = new JLabel();
        
        accessPointEssid = new JLabel();
        accessPointChannel = new JLabel();
        accessPointMode = new JLabel();
        accessPointSignalLevel = new JLabel();
        
        this.guiRefreshTime = guiRefreshTime;
        
        //Douleuei sto background, rwtaei tin vash kai enhmerwnei to grafiko
        //Object: teliko apotelesma
        //List<String>: Endiamesa apotelesmata
        //http://docs.oracle.com/javase/7/docs/api/javax/swing/SwingWorker.html
        swingWorker = new SwingWorker<Object, List<String>>() {

            @Override
            protected Object doInBackground() throws Exception {
                while(!Thread.currentThread().isInterrupted()) {
                    //Ta dhmosieuei sto swing kai otan ta mathei kai borei kalei
                    //tin process
                    //to this tis ap' exw klashs, oxi tou SwingWorker
                    publish(Gui.this.manager.getDevices());
                    Thread.sleep(Gui.this.guiRefreshTime);
                }
                return null;
            }
            
            @Override
            //kaleitai gia na enhmerwthei to swing gia ta kainouria interfaces
            protected void process(List<List<String>> devicesHistory) {
                //Apothikeuontai oloi oi action listeners tou device
                ActionListener[] actionListeners = devices.getActionListeners();
                
                //afairountai enas enas oi action listeners tou device
                for(ActionListener actionListener : actionListeners) {
                    devices.removeActionListener(actionListener);
                }
                
                //Epeidh ginontai allages sto JComboBox kai den theloume gia kathe
                //allagh pou ginetai na kalountai oi ActionListeners, afairountai
                //proswrina (pio panw), ginontai oi allages pou xrhazontai kai meta
                //xana prosthithontai.
                
                //Fernei to trexon epilegmeno antikeimeno apo to JComboBox 
                //(poio exei epilexei o xrhsths) 'h null an den exei epilexthei kanena
                String selected = (String)devices.getSelectedItem();
                //adeiazei tin lista
                devices.removeAllItems();
                //Xana ftiaxnei tin lista (to JComboBox, to dropdown sto grafiko)
                //gia ka8e suskeuh sthn pio prosfath lista (teleutaia lista)
                for(String device : devicesHistory.get(devicesHistory.size() - 1)) {
                    devices.addItem(device);
                }
                //kanei enable tin lista otan uparxoun polla antikeimena (>1)
                //sto JComboBox
                devices.setEnabled((devices.getItemCount() > 1));
                
                //xana prosthikh twn arxikwn actionListeners gia to device.
                for(ActionListener actionListener : actionListeners) {
                    devices.addActionListener(actionListener);
                }
                
                //an htan kapoio prin epilegmeno, to xana epilegei an xana 
                //uparxei stin lista
                if(selected != null)
                    devices.setSelectedItem(selected);
            }
            
        };
    }

    @Override
    public void run() {
        //Dhmiourgeia parathurou
        final JFrame jFrame = new JFrame("Systhma Athroisths");
        //me to 'x' kleinei kai termatizei (exit) h JVM
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //moirasma tou parathurou tou ContentPane tou jFrame (se koutia)
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        //Stin prwth grammh pou xwristhke dhmiourgoume ena topPanel gia ta periexomena
        //tis 1hs grammhs
        //FLowLayout gia topothetish twn periexomenwn sto kentro
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setMaximumSize(new Dimension(1024, 300));
        topPanel.add(new JLabel("Available Devices:"));
        
        topPanel.add(devices);
        jFrame.add(topPanel);
        //----------------------------telos 1hs grammhs------------------------------
        
        //Dhmiourgeia tab gia Interfaces kai Access Points
        JTabbedPane jTabbedPane = new JTabbedPane();
        
        JPanel interfacePanel = new JPanel();
        interfacePanel.setLayout(new BoxLayout(interfacePanel, BoxLayout.Y_AXIS));
        
        JPanel topInterfacePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topInterfacePanel.add(new JLabel("Available Interfaces:"));
        
        final JComboBox<String> interfaces = new JComboBox<String>();        
        topInterfacePanel.add(interfaces);
        
        interfacePanel.add(topInterfacePanel);
        
        //Dhmiourgeia tou Grid
        JPanel interfaceGrid = new JPanel();
        interfaceGrid.setLayout(new GridLayout(0, 2));
        interfaceGrid.add(new JLabel("Mac Address:"));
        interfaceGrid.add(macAddress);
        
        interfaceGrid.add(new JLabel("Ip Address:"));
        interfaceGrid.add(ipAddress);
        
        interfaceGrid.add(new JLabel("NetMask:"));
        interfaceGrid.add(netMask);
        
        interfaceGrid.add(new JLabel("Broadcast Address"));
        interfaceGrid.add(broadcastAddress);
        
        interfaceGrid.add(new JLabel("Default Gateway"));
        interfaceGrid.add(defaultGateway);
        
        interfaceGrid.add(new JLabel("Max Traffic Rate"));
        interfaceGrid.add(maxTrafficRate);
        
        interfaceGrid.add(new JLabel("Current Traffic Rate"));
        interfaceGrid.add(currentTrafficRate);
        
        interfaceGrid.add(new JLabel("Packet Error Rate"));
        interfaceGrid.add(packetErrorRate);
        
        interfaceGrid.add(new JLabel(""));
        interfaceGrid.add(new JLabel(""));
        
        //Wireless Network Interface
        interfaceGrid.add(new JLabel("-- Wireless Data --"));
        interfaceGrid.add(new JLabel(""));
        
        interfaceGrid.add(new JLabel("Base Station Mac Address"));
        interfaceGrid.add(baseStationMacAddress);
        
        interfaceGrid.add(new JLabel("Essid"));
        interfaceGrid.add(essid);
        
        interfaceGrid.add(new JLabel("Channel"));
        interfaceGrid.add(channel);
        
        interfaceGrid.add(new JLabel("Mode"));
        interfaceGrid.add(mode);
        
        interfaceGrid.add(new JLabel("Transmission Power"));
        interfaceGrid.add(transmissionPower);
        
        interfaceGrid.add(new JLabel("Link Quality"));
        interfaceGrid.add(linkQuality);
        
        interfaceGrid.add(new JLabel("Signal Level"));
        interfaceGrid.add(signalLevel);
        
        interfaceGrid.add(new JLabel("Noise Level"));
        interfaceGrid.add(noiseLevel);
        
        interfaceGrid.add(new JLabel("Discarded Packets"));
        interfaceGrid.add(discardedPackets);
        
        interfacePanel.add(interfaceGrid);
        
        interfacePanel.add(new JPanel());
        jTabbedPane.addTab("Interfaces", interfacePanel);
        //----------------------------telos 1ou tab---------------------------------
        
        JPanel accessPointJPanel = new JPanel();
        accessPointJPanel.setLayout(new BoxLayout(accessPointJPanel, BoxLayout.Y_AXIS));
        
        JPanel topAccessPointPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topAccessPointPanel.add(new JLabel("Available Access Points:"));
        
        final JComboBox<String> accessPoints = new JComboBox<String>();
        
        topAccessPointPanel.add(accessPoints);
        accessPointJPanel.add(topAccessPointPanel);
        
        //Dhmiourgeia tou Grid
        JPanel accessPointGrid = new JPanel();
        accessPointGrid.setLayout(new GridLayout(0, 2));
       
        accessPointGrid.add(new JLabel("Essid:"));
        accessPointGrid.add(accessPointEssid);
        
        accessPointGrid.add(new JLabel("Channel:"));
        accessPointGrid.add(accessPointChannel);
        
        accessPointGrid.add(new JLabel("Mode:"));
        accessPointGrid.add(accessPointMode);
        
        accessPointGrid.add(new JLabel("Signal Level:"));
        accessPointGrid.add(accessPointSignalLevel);
        
        //accessPointGrid.add(new JLabel("Time:"));
        
        accessPointJPanel.add(accessPointGrid);
        jTabbedPane.addTab("Access Points", accessPointJPanel);
        //----------------------------telos 2ou tab---------------------------------
        
        jFrame.add(jTabbedPane);
        //sumazeuetai oso xreiazetai
        jFrame.pack();
        
        //Oso pio pros to telos einai oi ActionListeners, toso ligotero tha klithoun
        //kata to xtisimo tou parathirou.

        //Otan o xrhsths dialexei sugkekrimeno device, tha ferei ta interfaces tou
        //sugkekrimenou device.
        //Oi emfoleumenes klaseis ektelountai apo to nhma tou swing molis ginei to 
        //antistoixo event
        devices.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String device = (String)devices.getSelectedItem();
                //-------------------- Gia kartela Interfaces ----------------------------
                
                //Kratiountai oi action Listeners gia tin lista interfaces
                ActionListener[] actionListeners = interfaces.getActionListeners();
                
                //Diagrafontai gia na mhn energopoihthoun enw ginontai allages sto JComboBox interfaces
                for(ActionListener actionListener : actionListeners) {
                    interfaces.removeActionListener(actionListener);
                }
                
                String selected = (String)interfaces.getSelectedItem();
                //adeiazei tin lista
                interfaces.removeAllItems();
                try {
                    //Xana ftiaxnei tin lista (to JComboBox, to dropdown sto grafiko)
                    for(String networkInterface : manager.getNetworkInterfaces(device)) {
                            interfaces.addItem(networkInterface);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Gui.class.getName()).log(Level.WARNING, "Den boresa na vrw ta interfaces tou device " + device, ex);
                }
                
                //kanei enable tin lista otan uparxoun polla antikeimena (>1)
                //sto JComboBox
                interfaces.setEnabled((interfaces.getItemCount() > 1));
                
                //xana prosthikh twn arxikwn actionListeners gia to interface.
                for(ActionListener actionListener : actionListeners) {
                    interfaces.addActionListener(actionListener);
                }
                
                //an htan kapoio prin epilegmeno, to xana epilegei an xana 
                //uparxei stin lista
                if(selected != null)
                    interfaces.setSelectedItem(selected);
                
                //-------------------- Gia kartela Access Points ----------------------------
                
                //Kratiountai oi action Listeners gia tin lista Access Points
                ActionListener[] accessPointActionListeners = accessPoints.getActionListeners();
                        
                for(ActionListener actionListener : accessPointActionListeners) {
                    accessPoints.removeActionListener(actionListener);
                }
                
                String accessPointSelected = (String)accessPoints.getSelectedItem();
                //adeiazei tin lista
                accessPoints.removeAllItems();
                try {
                    //Xana ftiaxnei tin lista (to JComboBox, to dropdown sto grafiko)
                    for(String accessPoint : manager.getAccessPoints(device)) {
                            accessPoints.addItem(accessPoint);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Gui.class.getName()).log(Level.WARNING, "Den boresa na vrw ta access points tou device " + device, ex);
                }
                
                //kanei enable tin lista otan uparxoun polla antikeimena (>1)
                //sto JComboBox
                accessPoints.setEnabled((accessPoints.getItemCount() > 1));
                
                //xana prosthikh twn arxikwn actionListeners gia to interface.
                for(ActionListener actionListener : accessPointActionListeners) {
                    accessPoints.addActionListener(actionListener);
                }
                
                //an htan kapoio prin epilegmeno, to xana epilegei an xana 
                //uparxei stin lista
                if(accessPointSelected != null)
                    accessPoints.setSelectedItem(accessPointSelected);
            }
        });
        
        interfaces.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
               
                String device = (String)devices.getSelectedItem();
                String interfaceName = (String)interfaces.getSelectedItem();
                try {
                    //Xana ftiaxnei tin lista (to JComboBox, to dropdown sto grafiko)
                    NetworkInterface networkInterface = manager.getNetworkInterface(device, interfaceName);
                    
                    if(networkInterface != null) {
                        macAddress.setText(networkInterface.getMacAddress());
                        ipAddress.setText(networkInterface.getIpAddress());
                        netMask.setText(networkInterface.getNetmask());
                        broadcastAddress.setText(networkInterface.getBroadcastAddress());
                        defaultGateway.setText(networkInterface.getDefaultGateway());
                        maxTrafficRate.setText(Float.toString(networkInterface.getMaxTrafficRate()) + " kb");
                        currentTrafficRate.setText(Float.toString(networkInterface.getCurrentTrafficRate()) + " kb");
                        packetErrorRate.setText(Float.toString(networkInterface.getPacketErrorRate()));
                        
                        if(networkInterface instanceof WirelessNetworkInterface) {
                            WirelessNetworkInterface wirelessNetworkInterface = (WirelessNetworkInterface)networkInterface;
                            baseStationMacAddress.setText(wirelessNetworkInterface.getBaseStationMacAddress());
                            essid.setText(wirelessNetworkInterface.getEssid());
                            channel.setText(Integer.toString(wirelessNetworkInterface.getChannel()));
                            mode.setText(wirelessNetworkInterface.getMode());
                            transmissionPower.setText(Float.toString(wirelessNetworkInterface.getTransmissionPower()));
                            linkQuality.setText(wirelessNetworkInterface.getLinkQuality());
                            signalLevel.setText(Integer.toString(wirelessNetworkInterface.getSignalLevel()));
                            noiseLevel.setText(Integer.toString(wirelessNetworkInterface.getNoiseLevel()));
                            discardedPackets.setText(Integer.toString(wirelessNetworkInterface.getDiscardedPackets()));
                        } else {
                            baseStationMacAddress.setText(null);
                            essid.setText(null);
                            channel.setText(null);
                            mode.setText(null);
                            transmissionPower.setText(null);
                            linkQuality.setText(null);
                            signalLevel.setText(null);
                            noiseLevel.setText(null);
                            discardedPackets.setText(null);
                        }
                        
                    } else {
                        macAddress.setText(null);
                        ipAddress.setText(null);
                        netMask.setText(null);
                        broadcastAddress.setText(null);
                        defaultGateway.setText(null);
                        maxTrafficRate.setText(null);
                        currentTrafficRate.setText(null);
                        packetErrorRate.setText(null);
                        baseStationMacAddress.setText(null);
                        essid.setText(null);
                        channel.setText(null);
                        mode.setText(null);
                        transmissionPower.setText(null);
                        linkQuality.setText(null);
                        signalLevel.setText(null);
                        noiseLevel.setText(null);
                        discardedPackets.setText(null);
                    }
                    
                } catch (SQLException ex) {
                    Logger.getLogger(Gui.class.getName()).log(Level.WARNING, "Den boresa na vrw to interface " + interfaceName + 
                            " tou device " + device, ex);
                }
               
            }
        });
        
        accessPoints.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String device = (String)devices.getSelectedItem();
                String accessPointMacAddress = (String)accessPoints.getSelectedItem();
                
                try {
                    AccessPoint accessPoint = manager.getAccessPoint(device, accessPointMacAddress);
                    
                    accessPointEssid.setText(accessPoint.getEssid());
                    accessPointChannel.setText(Integer.toString(accessPoint.getChannel()));
                    accessPointMode.setText(accessPoint.getMode());
                    accessPointSignalLevel.setText(Integer.toString(accessPoint.getSignalLevel()));

                } catch (SQLException ex) {
                    Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, "Den boresa na vrw to accessPoint " + accessPointMacAddress
                            + " tou device " + device, ex);
                }
                
            }
        });
        
        //Prokaloun window event
        jFrame.setVisible(true);
        //pragmatopoish sto trexon (epilegmeno) parathiro
        jFrame.requestFocus();

    }
    
    public static void main(String[] args) {
        if(args.length == 0) {
            System.err.println("Eisagete to properties file");
            return;
        }
        try {
             final Properties properties = new Properties();
            //fortwnei to prwto orisma (upothetei oti einai arxeio me to FileInputStream)
            properties.load(new FileInputStream(args[0]));

            final Manager databaseManager = DatabaseManager.getInstance(properties.getProperty("jdbcDriver"), 
                    properties.getProperty("jdbcUrl"), Long.parseLong(properties.getProperty("T")));
            final Manager cacheManager = new CacheManager(databaseManager);
            
            Endpoint.publish(properties.getProperty("webServiceUrl"), new WebServiceAthroisthsImpl(cacheManager));
            Gui gui = new Gui(cacheManager, Long.parseLong(properties.getProperty("guiRefreshTime")));
            
            //ektelesh tou gui apo to nhma tou Swing
            SwingUtilities.invokeLater(gui);
            //enhmerwnei to grafiko
            gui.swingWorker.execute();
            
            //Thread gia na katharizei tin vash apo ta timed out antikeimena
            Thread cleanUpThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            databaseManager.deleteTimedOutItems();
                        } catch (SQLException ex) {
                            Logger.getLogger(Gui.class.getName()).log(Level.WARNING, "Den katharisa tin vash apo ta timed out antikeimena", ex);
                        }
                        try {
                            Thread.sleep(Long.parseLong(properties.getProperty("T")) * 1000);
                        } catch (InterruptedException ex) {
                            break;
                        }
                    }
                }
            });
            //Gia na trexei anexartita apo ola ta alla kai na mhn krataei anagkastika
            //anoixth thn JVM otan ola ta upoloipa nhmata termatisoun
            cleanUpThread.setDaemon(true);
            cleanUpThread.start();

        } catch (IOException ex) {
            Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, "Den boresa na fortwsw properties", ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, "Den boresa na xekinhsw GUI", ex);
        } catch (SQLException ex) {
            Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, "Den boresa na xekinhsw GUI", ex);
        }
    }
}
