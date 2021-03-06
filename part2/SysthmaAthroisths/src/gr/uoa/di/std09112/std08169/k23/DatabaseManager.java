package gr.uoa.di.std09112.std08169.k23;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author labis
 * Sigleton Pattern: DatabaseManager
 */
public final class DatabaseManager implements Manager {
        
    //Diagrafh ths vashs
    private static final String DROP_DISCOVERS = 
            "DROP TABLE IF EXISTS Discovers;";
    
    private static final String DROP_ACCESS_POINTS = 
            "DROP TABLE IF EXISTS AccessPoints;";

    private static final String DROP_WIRELESS_NETWORK_INTERFACES = 
            "DROP TABLE IF EXISTS WirelessNetworkInterfaces;";        
    
    private static final String DROP_NETWORK_INTERFACES = 
            "DROP TABLE IF EXISTS NetworkInterfaces;";
    
    /* NetworkInterfaces table */
    private static final String CREATE_NETWORK_INTERFACES = 
            "CREATE TABLE NetworkInterfaces (" +
            "device VARCHAR(50) NOT NULL, " +
            "interfaceName VARCHAR (10) NOT NULL, " +
            "time TIMESTAMP NOT NULL, " + 
            "macAddress VARCHAR (30), " +
            "ipAddress VARCHAR (20), " +
            "netmask VARCHAR (20), " +
            "broadcastAddress VARCHAR (20), " +
            "defaultGateway VARCHAR (20), " +
            "maxTrafficRate FLOAT, " +
            "currentTrafficRate FLOAT, " +
            "packetErrorRate FLOAT, " +
            "PRIMARY KEY (device, interfaceName) " +
            ") CHARACTER SET utf8, ENGINE InnoDB;";

    /* WirelessNetworkInterfaces table */
    private static final String CREATE_WIRELESS_NETWORK_INTERFACES = 
            "CREATE TABLE WirelessNetworkInterfaces (" +
            "device VARCHAR(50) NOT NULL, " +
            "interfaceName VARCHAR (10) NOT NULL, " +
            "time TIMESTAMP NOT NULL, " + 
            "baseStationMacAddress VARCHAR (30), " +
            "essid VARCHAR (80), " +
            "channel INT, " +
            "mode VARCHAR (20), " +
            "transmissionPower FLOAT, " +
            "linkQuality VARCHAR (10), " +
            "signalLevel INT, " +
            "noiseLevel INT, " +
            "discardedPackets INT, " +
            //Logo klhronomikothtas pairnei ta kleidia tou NetworkInterfaces kai ta exei kai to
            //WirelessNetworkInterfaces gia dika tou.
            "PRIMARY KEY (device, interfaceName), " +
            //Ta proteuonta kleidia ginontai kai xena giati logo klhronomikothtas den borei na 
            //einai aplo WirelessNetworkInterface xwris na einai NetworkInterface
            //ON DELETE CASCADE: An diagrafei kati apo ton pinaka NetworkInterfaces diagrafetai
            //automata kai apo auton.
            "FOREIGN KEY (device, interfaceName) REFERENCES NetworkInterfaces (device, interfaceName) ON DELETE CASCADE ON UPDATE CASCADE " +
            ") CHARACTER SET utf8, ENGINE InnoDB;";
    
    /* AccessPoint table */
    private static final String CREATE_ACCESS_POINTS = 
            "CREATE TABLE AccessPoints (" +
            "macAddress VARCHAR (30) NOT NULL PRIMARY KEY, " + 
            "essid VARCHAR (80) NOT NULL," + 
            "channel INT NOT NULL, " + 
            "mode VARCHAR (10) NOT NULL, " + 
            "time TIMESTAMP NOT NULL " + 
            ") CHARACTER SET utf8, ENGINE InnoDB;";
    
    /* Discover table */
    private static final String CREATE_DISCOVERS = 
            "CREATE TABLE Discovers (" + 
            "device VARCHAR (30) NOT NULL, " + 
            "macAddress VARCHAR (30) NOT NULL, " + 
            "signalLevel INT NOT NULL," + 
            "time TIMESTAMP NOT NULL, " + 
            "PRIMARY KEY (device, macAddress), " + 
            "FOREIGN KEY (device) REFERENCES WirelessNetworkInterfaces (device) ON DELETE CASCADE ON UPDATE CASCADE, " +
            "FOREIGN KEY (macAddress) REFERENCES AccessPoints (macAddress) ON DELETE CASCADE ON UPDATE CASCADE " +
            ") CHARACTER SET utf8, ENGINE InnoDB;";
    
    private static final String INSERT_NETWORK_INTERFACES = 
            "INSERT INTO NetworkInterfaces "
            + "(device, interfaceName, time, macAddress, ipAddress, netmask, broadcastAddress, defaultGateway, maxTrafficRate, "
            + "currentTrafficRate, packetErrorRate) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
            //An to zeugari (device, interfaceName) uparxei 'hdh (dhl. to primary key),
            //tote ektelei to parakatw update anti gia to parapanw insert
            + "ON DUPLICATE KEY UPDATE "
            + "time = ?, "
            + "macAddress = ?, "
            + "ipAddress = ?, "
            + "netmask = ?, "
            + "broadcastAddress = ?, "
            + "defaultGateway = ?, "
            + "maxTrafficRate = ?, "
            + "currentTrafficRate = ?, "
            + "packetErrorRate = ?;";
    
    private static final String INSERT_WIRELESS_NETWORK_INTERFACES = 
            "INSERT INTO WirelessNetworkInterfaces "
            + "(device, interfaceName, time, baseStationMacAddress, essid, channel, mode, transmissionPower, "
            + "linkQuality, signalLevel, noiseLevel, discardedPackets) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
            + "ON DUPLICATE KEY UPDATE "
            + "time = ?, "
            + "baseStationMacAddress = ?, "
            + "essid = ?, "
            + "channel = ?, "
            + "mode = ?, "
            + "transmissionPower = ?, "
            + "linkQuality = ?, "
            + "signalLevel = ?, "
            + "noiseLevel = ?, "
            + "discardedPackets = ?;";
    
    private static final String INSERT_ACCESS_POINTS = 
            "INSERT INTO AccessPoints (macAddress, essid, channel, mode, time) "
            + "VALUES (?, ?, ?, ?, ?) "
            + "ON DUPLICATE KEY UPDATE "
            + "essid = ?, "
            + "channel = ?, "
            + "mode = ?, "
            + "time = ?;";
 
    private static final String INSERT_DISCOVERS = 
            "INSERT INTO Discovers (device, macAddress, signalLevel, time) "
            + "VALUES (?, ?, ?, ?) "
            + "ON DUPLICATE KEY UPDATE "
            + "signalLevel = ?, "
            + "time = ?;";
    
    private static final String DELETE_NETWORK_INTERFACES = 
            "DELETE FROM NetworkInterfaces "
            + "WHERE (NOW() - time) >= ?;";
            
    private static final String DELETE_WIRELESS_NETWORK_INTERFACES = 
            "DELETE FROM WirelessNetworkInterfaces "
            + "WHERE (NOW() - time) >= ?;";
    
    private static final String DELETE_ACCESS_POINTS = 
            "DELETE FROM AccessPoints "
            + "WHERE (NOW() - time) >= ?;";
    
    private static final String DELETE_DISCOVERS = 
            "DELETE FROM NetworkInterfaces "
            + "WHERE (NOW() - time) >= ?;";
    
    private static final String GET_DEVICES = 
            //DISTINCT: epistrefei tis monadikes suskeues, 
            //gia na mn exoume diplotupa an exoume duo kartes
            "SELECT DISTINCT device FROM NetworkInterfaces "
            //H diafora tou twra (now()) apo tin wra pou bike
            + "WHERE (NOW() - time) < ?;";
    
    private static final String GET_NETWORK_INTERFACES = 
            "SELECT DISTINCT interfaceName FROM NetworkInterfaces "
            + "WHERE device = ? AND (NOW() - time) < ?;";
    
    private static final String GET_ACCESS_POINTS = 
            "SELECT DISTINCT ap.macAddress "
            + "FROM AccessPoints ap, Discovers d "
            + "WHERE d.device = ? AND d.macAddress = ap.macAddress AND "
            + "(NOW() - d.time) < ? AND (NOW() - ap.time) < ?;";

    private static final String GET_NETWORK_INTERFACE = 
            "SELECT macAddress, ipAddress, netmask, broadcastAddress, defaultGateway, maxTrafficRate, "
            + "currentTrafficRate, packetErrorRate "
            + "FROM NetworkInterfaces "
            + "WHERE device = ? AND interfaceName = ? AND (NOW() - time) < ?;";
    
    private static final String GET_WIRELESS_NETWORK_INTERFACE = 
            "SELECT baseStationMacAddress, essid, channel, mode, transmissionPower, linkQuality, "
            + "signalLevel, noiseLevel, discardedPackets "
            + "FROM WirelessNetworkInterfaces "
            + "WHERE device = ? AND interfaceName = ? AND (NOW() - time) < ?;";

    private static final String GET_ACCESS_POINT = 
            "SELECT ap.essid, ap.channel, ap.mode, d.signalLevel "
            + "FROM AccessPoints ap, Discovers d "
            + "WHERE d.device = ? AND d.macAddress = ? AND "
            + "d.macAddress = ap.macAddress AND (NOW() - d.time) < ? "
            + "AND (NOW() - ap.time) < ?;";
    
    private static final Logger LOGGER =  Logger.getLogger(DatabaseManager.class.getName());
    
    private  static DatabaseManager databaseManager;
    
    private Connection connection;
    private long t;
    
    // dhmiourgeitai mono ena stigmiotupo apo ton 1o pou tha thn kalesei,
    // vazoume synchronized wste an 2 nhmata thn kalesoun tautoxrona na mhn 
    // uparxei kindunos dhmiourgias 2 stigmiotupwn
    public static synchronized DatabaseManager getInstance(String jdbcDriver, String jdbcUrl, long t) throws ClassNotFoundException, SQLException { 
        
        if (databaseManager == null) {
            databaseManager = new DatabaseManager(jdbcDriver, jdbcUrl, t);
        }
        
        return databaseManager;
    }
    
    //Arxikopoihsh kai sundesh me tin vash.
    private DatabaseManager(String jdbcDriver, String jdbcUrl, long t) throws ClassNotFoundException, SQLException {
        //Fortwsh tou driver pou mhlaei me tin vash
        Class.forName(jdbcDriver);
        
        //Sundesh me tin vash
        connection = DriverManager.getConnection(jdbcUrl);
        this.t = t;

        //connection.setAutoCommit(false);
        try {
            //Stelnetai ston server kai to proetoimazei gia ektelesh
            PreparedStatement preparedStatement = connection.prepareStatement(DROP_DISCOVERS);
            try {
                preparedStatement.executeUpdate();
                //An xtuphsei kanena exception, prwta tha kleisei to preparedStatement 
                //me to finally kai meta tha petaxei to SQLException.
            } finally {
                preparedStatement.close();
            }

            preparedStatement = connection.prepareStatement(DROP_ACCESS_POINTS);
            try {
                preparedStatement.executeUpdate();
            } finally {
                preparedStatement.close();
            }

            preparedStatement = connection.prepareStatement(DROP_WIRELESS_NETWORK_INTERFACES);
            try {
                preparedStatement.executeUpdate();
            } finally {
                preparedStatement.close();
            }

            preparedStatement = connection.prepareStatement(DROP_NETWORK_INTERFACES);
            try {
                preparedStatement.executeUpdate();
            } finally {
                preparedStatement.close();
            }


            preparedStatement = connection.prepareStatement(CREATE_NETWORK_INTERFACES);
            try {
                //kanei update tin vash prosthetontas ton pinaka
                preparedStatement.executeUpdate();
            } finally { 
                preparedStatement.close();
            }

            preparedStatement = connection.prepareStatement(CREATE_WIRELESS_NETWORK_INTERFACES);
            try {
                preparedStatement.executeUpdate();
            } finally { 
                preparedStatement.close();
            }

            preparedStatement = connection.prepareStatement(CREATE_ACCESS_POINTS);
            try {
                preparedStatement.executeUpdate();
            } finally { 
                preparedStatement.close();
            }

            preparedStatement = connection.prepareStatement(CREATE_DISCOVERS);
            try {
                preparedStatement.executeUpdate();
            } finally { 
                preparedStatement.close();
            }
            
            //connection.commit();
            
        } catch(SQLException ex) {
            //connection.rollback();
            throw ex;
        }
        
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Yparxei ena kai monadiko stigmiotupo ths DatabaseManager! "
                + "Den mporei na dhmiourghthei klonos");
    }
    
    @Override
    protected void finalize() { //O Destructor ths java
        try {
            connection.close();
            //Gia na min xana xrhsimopoihthei meta to kleisimo
            connection = null;
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Den borese na kleisei h sundesh me tin vash", ex);
        }
    }
 
    @Override
    public void addNetworkInterface(String device, NetworkInterface networkInterface) throws SQLException {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        //connection.setAutoCommit(false);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_NETWORK_INTERFACES);
            
            try {              
                preparedStatement.setString(1, device);
                preparedStatement.setString(2, networkInterface.getInterfaceName());
                preparedStatement.setTimestamp(3, timestamp);
                
                if(networkInterface.getMacAddress() != null)
                    preparedStatement.setString(4, networkInterface.getMacAddress());
                else
                    //An einai null to macAddress tote topotheteitai stin vash VARCHAR NULL (sql null)
                    preparedStatement.setNull(4, Types.VARCHAR);
                
                if(networkInterface.getIpAddress() != null)
                    preparedStatement.setString(5, networkInterface.getIpAddress().toString());
                else
                    preparedStatement.setNull(5, Types.VARCHAR);
                
                if(networkInterface.getNetmask() != null)
                    preparedStatement.setString(6, networkInterface.getNetmask().toString());
                else
                    preparedStatement.setNull(6, Types.VARCHAR);
                
                if(networkInterface.getBroadcastAddress() != null)
                    preparedStatement.setString(7, networkInterface.getBroadcastAddress().toString());
                else
                    preparedStatement.setNull(7, Types.VARCHAR);
                
                if(networkInterface.getDefaultGateway() != null)
                    preparedStatement.setString(8, networkInterface.getDefaultGateway().toString());
                else
                    preparedStatement.setNull(8, Types.VARCHAR);
                
                preparedStatement.setFloat(9, networkInterface.getMaxTrafficRate());
                preparedStatement.setFloat(10, networkInterface.getCurrentTrafficRate());
                preparedStatement.setFloat(11, networkInterface.getPacketErrorRate());
                
                preparedStatement.setTimestamp(12, timestamp);
                
                if(networkInterface.getMacAddress() != null)
                    preparedStatement.setString(13, networkInterface.getMacAddress());
                else
                    preparedStatement.setNull(13, Types.VARCHAR);
                
                if(networkInterface.getIpAddress() != null)
                    preparedStatement.setString(14, networkInterface.getIpAddress().toString());
                else
                    preparedStatement.setNull(14, Types.VARCHAR);
                
                if(networkInterface.getNetmask() != null)
                    preparedStatement.setString(15, networkInterface.getNetmask().toString());
                else
                    preparedStatement.setNull(15, Types.VARCHAR);
                
                if(networkInterface.getBroadcastAddress() != null)
                    preparedStatement.setString(16, networkInterface.getBroadcastAddress().toString());
                else
                    preparedStatement.setNull(16, Types.VARCHAR);
                
                if(networkInterface.getDefaultGateway() != null)
                    preparedStatement.setString(17, networkInterface.getDefaultGateway().toString());
                else
                    preparedStatement.setNull(17, Types.VARCHAR);
                
                preparedStatement.setFloat(18, networkInterface.getMaxTrafficRate());
                preparedStatement.setFloat(19, networkInterface.getCurrentTrafficRate());
                preparedStatement.setFloat(20, networkInterface.getPacketErrorRate());

                preparedStatement.executeUpdate();

            } finally {
                preparedStatement.close();
            }
            
            if(networkInterface instanceof WirelessNetworkInterface) {
                WirelessNetworkInterface wirelessNetworkInterface = (WirelessNetworkInterface)networkInterface;
                
                preparedStatement = connection.prepareStatement(INSERT_WIRELESS_NETWORK_INTERFACES);
                try {
                    preparedStatement.setString(1, device);
                    preparedStatement.setString(2, wirelessNetworkInterface.getInterfaceName());
                    preparedStatement.setTimestamp(3, timestamp);
                    
                    if(wirelessNetworkInterface.getBaseStationMacAddress() != null)
                        preparedStatement.setString(4, wirelessNetworkInterface.getBaseStationMacAddress());
                    else
                        preparedStatement.setNull(4, Types.VARCHAR);
                    
                    if(wirelessNetworkInterface.getEssid() != null)
                        preparedStatement.setString(5, wirelessNetworkInterface.getEssid());
                    else
                        preparedStatement.setNull(5, Types.VARCHAR);
                    
                    if(wirelessNetworkInterface.getChannel() != null)
                        preparedStatement.setInt(6, wirelessNetworkInterface.getChannel());
                    else
                        preparedStatement.setNull(6, Types.INTEGER);
                    
                    if(wirelessNetworkInterface.getMode() != null)
                        preparedStatement.setString(7, wirelessNetworkInterface.getMode());
                    else
                        preparedStatement.setNull(7, Types.VARCHAR);
                    
                    preparedStatement.setFloat(8, wirelessNetworkInterface.getTransmissionPower());
                    preparedStatement.setString(9, wirelessNetworkInterface.getLinkQuality());
                    
                    preparedStatement.setInt(10, wirelessNetworkInterface.getSignalLevel());
                    preparedStatement.setInt(11, wirelessNetworkInterface.getNoiseLevel());
                    preparedStatement.setInt(12, wirelessNetworkInterface.getDiscardedPackets());
                    
                    preparedStatement.setTimestamp(13, timestamp);
                    
                    if(wirelessNetworkInterface.getBaseStationMacAddress() != null)
                        preparedStatement.setString(14, wirelessNetworkInterface.getBaseStationMacAddress());
                    else
                        preparedStatement.setNull(14, Types.VARCHAR);
                    
                    if(wirelessNetworkInterface.getEssid() != null)
                        preparedStatement.setString(15, wirelessNetworkInterface.getEssid());
                    else
                        preparedStatement.setNull(15, Types.VARCHAR);
                    
                    if(wirelessNetworkInterface.getChannel() != null)
                        preparedStatement.setInt(16, wirelessNetworkInterface.getChannel());
                    else
                        preparedStatement.setNull(16, Types.INTEGER);
                    
                    if(wirelessNetworkInterface.getMode() != null)
                        preparedStatement.setString(17, wirelessNetworkInterface.getMode());
                    else
                        preparedStatement.setNull(17, Types.VARCHAR);
                    
                    preparedStatement.setFloat(18, wirelessNetworkInterface.getTransmissionPower());
                    preparedStatement.setString(19, wirelessNetworkInterface.getLinkQuality());
                    
                    preparedStatement.setInt(20, wirelessNetworkInterface.getSignalLevel());
                    preparedStatement.setInt(21, wirelessNetworkInterface.getNoiseLevel());
                    preparedStatement.setInt(22, wirelessNetworkInterface.getDiscardedPackets());
                    
                    preparedStatement.executeUpdate();
                    
                } finally {
                    preparedStatement.close();
                }
            }
            
            //connection.commit();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.WARNING, "Den boresa na valw network interface stin vash", ex);
            //connection.rollback();
            throw ex;
        }
    }
    
    @Override
    public void addAccessPoint(String device, AccessPoint accessPoint) throws SQLException {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        //Na mhn kanei automato commit se kathe eperwtish, alla na kanei 
        //commit otan 'h rollback opote tou poume emeis.
        //connection.setAutoCommit(false);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ACCESS_POINTS);
            
            try {
                preparedStatement.setString(1, accessPoint.getMacAddress());
                preparedStatement.setString(2, accessPoint.getEssid());
                preparedStatement.setInt(3, accessPoint.getChannel());
                preparedStatement.setString(4, accessPoint.getMode());
                preparedStatement.setTimestamp(5, timestamp);
                
                preparedStatement.setString(6, accessPoint.getEssid());
                preparedStatement.setInt(7, accessPoint.getChannel());
                preparedStatement.setString(8, accessPoint.getMode());
                preparedStatement.setTimestamp(9, timestamp);

                preparedStatement.executeUpdate();
            } finally {
                preparedStatement.close();
            }

            /*eisagwgh ston pinaka Discovers */
            preparedStatement = connection.prepareStatement(INSERT_DISCOVERS);
            try {
                preparedStatement.setString(1, device);
                preparedStatement.setString(2, accessPoint.getMacAddress());
                
                preparedStatement.setInt(3, accessPoint.getSignalLevel());
                preparedStatement.setTimestamp(4, timestamp);
                
                preparedStatement.setInt(5, accessPoint.getSignalLevel());
                preparedStatement.setTimestamp(6, timestamp);
                
                preparedStatement.executeUpdate();

            } finally {
                preparedStatement.close();
            }
            //Commit kai gia tis duo eperwthseis
            //connection.commit();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.WARNING, "Den boresa na valw access point sti vash", ex);
            //An opoiadhpote eperwthsh apotuxei, einai san na min egine kamia apo tis duo.
            //connection.rollback();
            throw ex;
        } 
    }
    
    @Override
    public List<String> getDevices() throws SQLException {
        //epeidh tha ektelesei mono mia eperwtish tha kanei commit automata molis tin teleiwsei
        //connection.setAutoCommit(true);
        PreparedStatement devicesPreparedStatement = connection.prepareStatement(GET_DEVICES);
        List<String> devices = new ArrayList<String>();
        try {
            devicesPreparedStatement.setLong(1, t);
            ResultSet devicesResultSet = devicesPreparedStatement.executeQuery();
            try { 
            //An xtuphsei SQLException, prwta tha klisei to resultSet
            //me to finally kai meta petaei exw to SQLException
                while(devicesResultSet.next()) {
                    devices.add(devicesResultSet.getString("device"));
                }
            } finally {
                devicesResultSet.close();
            }
        } finally {
            devicesPreparedStatement.close();
        }
        return devices;
    }
    
    @Override
    public List<String> getNetworkInterfaces(String device) throws SQLException {
        //connection.setAutoCommit(true);
        PreparedStatement devicesPreparedStatement = connection.prepareStatement(GET_NETWORK_INTERFACES);
        try {
            devicesPreparedStatement.setString(1, device);
            devicesPreparedStatement.setLong(2, t);
            
            ResultSet devicesResultSet = devicesPreparedStatement.executeQuery();
            try {
                List<String> networkInterfaces = new ArrayList<String>();
                while(devicesResultSet.next()) {
                    networkInterfaces.add(devicesResultSet.getString("interfaceName"));
                }
                return networkInterfaces;
            } finally {
                devicesResultSet.close();
            }
        } finally {
            devicesPreparedStatement.close();
        }
    }
    
    @Override
    public List<String> getAccessPoints(String device) throws SQLException {
        //connection.setAutoCommit(true);
        PreparedStatement accessPointsPreparedStatement = connection.prepareStatement(GET_ACCESS_POINTS);
        List<String> accessPoints = new ArrayList<String>();
        try {
            accessPointsPreparedStatement.setString(1, device);
            accessPointsPreparedStatement.setLong(2, t);
            accessPointsPreparedStatement.setLong(3, t);
            
            ResultSet accessPointsResultSet = accessPointsPreparedStatement.executeQuery();
            try { 
                while(accessPointsResultSet.next()) {
                    accessPoints.add(accessPointsResultSet.getString("macAddress"));
                }
                
                return accessPoints;
            } finally {
                accessPointsResultSet.close();
            }
        } finally {
            accessPointsPreparedStatement.close();
        }
    }
    
    @Override
    public NetworkInterface getNetworkInterface(String device, String interfaceName) throws SQLException {
        //connection.setAutoCommit(false);
        
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_NETWORK_INTERFACE);
            try {
                preparedStatement.setString(1, device);
                preparedStatement.setString(2, interfaceName);
                preparedStatement.setLong(3, t);

                ResultSet resultSet = preparedStatement.executeQuery();

                try {
                    //Vrethike ena interface (1 grammh)
                    if(resultSet.next()) {
                        String macAddress = resultSet.getString("macAddress");
                        String ipAddress = resultSet.getString("ipAddress");
                        String netmask = resultSet.getString("netmask");
                        String broadcastAddress = resultSet.getString("broadcastAddress");
                        String defaultGateway = resultSet.getString("defaultGateway");
                        float maxTrafficRate = resultSet.getFloat("maxTrafficRate");
                        float currentTrafficRate = resultSet.getFloat("currentTrafficRate");
                        float packetErrorRate = resultSet.getFloat("packetErrorRate");

                        //An eixe topothetithei stin vash tha eixame sunartisiakh exartish, opote 
                        //tha peutame stin 2h kanonikh morfh.
                        float currentBandwidthRatio = 0.0f;
                        if (maxTrafficRate != 0)
                            currentBandwidthRatio = (currentTrafficRate/maxTrafficRate) * 100;

                        String networkAddress = null;

                        try {
                            if((ipAddress != null) && (netmask != null)) {
                                byte[] ipBytes = InetAddress.getByName(ipAddress).getAddress();
                                byte[] maskBytes = InetAddress.getByName(netmask).getAddress();
                                byte[] networkBytes = new byte[ipBytes.length];

                                for(int i = 0; i < ipBytes.length; i++) {
                                    //logiko and byte pros byte metaxh ip address kai netmask
                                    networkBytes[i] = (byte)(ipBytes[i] & maskBytes[i]);
                                }

                                networkAddress = InetAddress.getByAddress(networkBytes).toString();
                            }
                        } catch (UnknownHostException ex) {
                            Logger.getLogger(DatabaseManager.class.getName()).log(Level.WARNING, 
                                    "Den boresa na upologisw tin dieuthinsh diktuou gia to interface " + interfaceName + 
                                " tou device " + device, ex);
                        }

                        PreparedStatement wirelessPreparedStatement = connection.prepareStatement(GET_WIRELESS_NETWORK_INTERFACE);

                        try {
                            wirelessPreparedStatement.setString(1, device);
                            wirelessPreparedStatement.setString(2, interfaceName);
                            wirelessPreparedStatement.setLong(3, t);

                            ResultSet wirelessResultSet = wirelessPreparedStatement.executeQuery();
                            try {
                                //an uparxei wireless, tha gemisei ena kai tha to epistrepsei
                                if(wirelessResultSet.next()) {
                                    String baseStationMacAddress = wirelessResultSet.getString("baseStationMacAddress");
                                    String essid = wirelessResultSet.getString("essid");
                                    Integer channel = wirelessResultSet.getInt("channel");
                                    String mode = wirelessResultSet.getString("mode");
                                    float transmissionPower = wirelessResultSet.getFloat("transmissionPower");
                                    String linkQuality = wirelessResultSet.getString("linkQuality");
                                    int signalLevel = wirelessResultSet.getInt("signalLevel");
                                    int noiseLevel = wirelessResultSet.getInt("noiseLevel");
                                    int discardedPackets = wirelessResultSet.getInt("discardedPackets");

                                    //connection.commit();

                                    return new WirelessNetworkInterface(interfaceName, macAddress, ipAddress, netmask, 
                                            networkAddress, broadcastAddress, defaultGateway, maxTrafficRate, currentTrafficRate, 
                                            currentBandwidthRatio, packetErrorRate, 0, 0, baseStationMacAddress, 
                                            essid, channel, mode, transmissionPower, linkQuality, signalLevel, noiseLevel, discardedPackets);

                                }

                                //connection.commit();

                                return new NetworkInterface(interfaceName, macAddress, ipAddress, netmask, networkAddress, 
                                        broadcastAddress, defaultGateway, maxTrafficRate, currentTrafficRate, currentBandwidthRatio, 
                                        packetErrorRate, 0, 0);

                            } finally {
                                wirelessResultSet.close();
                            }

                        } finally {
                            wirelessPreparedStatement.close();
                        }
                    }

                    //connection.commit();

                    return null;
                } finally {
                    resultSet.close();
                }
            } finally {
                preparedStatement.close();
            }
        } catch (SQLException ex) {
            //connection.rollback();
            throw ex;
        }
        
    }
    
    @Override
    public AccessPoint getAccessPoint(String device, String macAddress) throws SQLException {
        //connection.setAutoCommit(true);
        PreparedStatement preparedStatement = connection.prepareStatement(GET_ACCESS_POINT);
        try {
            preparedStatement.setString(1, device);
            preparedStatement.setString(2, macAddress);
            preparedStatement.setLong(3, t);
            preparedStatement.setLong(4, t);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            try {
                //Vrethike ena Access Point (1 grammh)
                if(resultSet.next()) {
                    String essid = resultSet.getString("essid");
                    int channel = resultSet.getInt("channel");
                    String mode = resultSet.getString("mode");
                    int signalLevel = resultSet.getInt("signalLevel");

                    return new AccessPoint(null, macAddress, essid, channel, mode, signalLevel);
                }
                return null;
            } finally {
                resultSet.close();
            }
        } finally {
            preparedStatement.close();
        }
    }
    
    @Override
    public void deleteTimedOutItems() throws SQLException {
        //connection.setAutoCommit(false);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_NETWORK_INTERFACES);
            try {
                preparedStatement.setLong(1, t);
                preparedStatement.executeUpdate();
            } finally {
                preparedStatement.close();
            }

            preparedStatement = connection.prepareStatement(DELETE_WIRELESS_NETWORK_INTERFACES);
            try {
                preparedStatement.setLong(1, t);
                preparedStatement.executeUpdate();
            } finally {
                preparedStatement.close();
            }

            preparedStatement = connection.prepareStatement(DELETE_ACCESS_POINTS);
            try {
                preparedStatement.setLong(1, t);
                preparedStatement.executeUpdate();
            } finally {
                preparedStatement.close();
            }

            preparedStatement = connection.prepareStatement(DELETE_DISCOVERS);
            try {
                preparedStatement.setLong(1, t);
                preparedStatement.executeUpdate();
            } finally {
                preparedStatement.close();
            }
            //connection.commit();
        } catch(SQLException ex) {
            //connection.rollback();
            throw ex;
        }
    }
}
