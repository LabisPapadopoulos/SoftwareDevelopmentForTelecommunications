package gr.uoa.di.std09112.std08169.k23;

/**
 *
 * @author labis
 */
public class AndroidDevice {
    private String androidId;
    private String model;
    private String manufacturer;
    private String version;
    private double latitude;
    private double longitude;
    private double altitude;
    private float level;
    private String charging;
    
    public AndroidDevice() {}

    public AndroidDevice(String androidId, String model, String manufacturer, String version, double latitude, double longitude, double altitude, float level, String charging) {
        this.androidId = androidId;
        this.model = model;
        this.manufacturer = manufacturer;
        this.version = version;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.level = level;
        this.charging = charging;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public float getLevel() {
        return level * 100;
    }

    public void setLevel(float level) {
        this.level = level;
    }

    public String getCharging() {
        return charging;
    }

    public void setCharging(String charging) {
        this.charging = charging;
    }
}
