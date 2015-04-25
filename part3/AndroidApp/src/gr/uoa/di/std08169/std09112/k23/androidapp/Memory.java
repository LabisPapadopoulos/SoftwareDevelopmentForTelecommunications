package gr.uoa.di.std08169.std09112.k23.androidapp;

import android.util.Log;

public final class Memory {

	private static Memory memory;
	
	private float level = 0.0f;
	private String charging = "";
	private boolean batteryMonitoring = false; //Default einai disabled
	
	private double latitude = 0.0;
	private double longitude = 0.0;
	private double altitude = 0.0;
	private boolean gpsMonitoring = false;
	
	private String androidId = "";
	private String model = "";
	private String manufacturer = "";
	private String version = "";	
	private boolean sendData = false;
	
	private SendTerminalDataAsyncTask asyncTask;
	
	public static synchronized Memory getInstance() {
		if(memory == null) {
			memory = new Memory();
		}
		
		return memory;
	}

	//synchronized gia na einai thread Safe. Na min berdeuetai to nhma
	//pou tha stelnei dedomena ston athroisth me to nhma pou tha
	//gemizei dedomena tin mnhmh
	public synchronized float getLevel() {
		return level;
	}

	public synchronized void setLevel(float level) {
		this.level = level;
	}

	public synchronized String getCharging() {
		return charging;
	}

	public synchronized void setCharging(String charging) {
		this.charging = charging;
	}

	public synchronized boolean isBatteryMonitoring() {
		return batteryMonitoring;
	}

	public synchronized void setBatteryMonitoring(boolean batteryMonitoring) {
		this.batteryMonitoring = batteryMonitoring;
	}

	public synchronized double getLatitude() {
		return latitude;
	}

	public synchronized void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public synchronized double getLongitude() {
		return longitude;
	}

	public synchronized void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public synchronized double getAltitude() {
		return altitude;
	}

	public synchronized void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public synchronized boolean isGpsMonitoring() {
		return gpsMonitoring;
	}

	public synchronized void setGpsMonitoring(boolean gpsMonitoring) {
		this.gpsMonitoring = gpsMonitoring;
	}
	
	public synchronized String getAndroidId() {
		return androidId;
	}

	public synchronized void setAndroidId(String id) {
		this.androidId = id;
	}
	
	public synchronized String getModel() {
		return model;
	}

	public synchronized void setModel(String model) {
		this.model = model;
	}

	public synchronized String getManufacturer() {
		return manufacturer;
	}

	public synchronized void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public synchronized String getVersion() {
		return version;
	}

	public synchronized void setVersion(String version) {
		this.version = version;
	}

	public synchronized boolean isSendData() {
		return sendData;
	}

	public synchronized void setSendData(boolean sendData) {
		this.sendData = sendData;
	}
	
	public synchronized SendTerminalDataAsyncTask getAsyncTask() {
		return asyncTask;
	}

	public synchronized void setAsyncTask(SendTerminalDataAsyncTask asyncTask) {
		this.asyncTask = asyncTask;
	}

	@Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Yparxei ena kai monadiko stigmiotupo ths Memory! "
                + "Den mporei na dhmiourghthei klonos");
    }
	
	@Override
	public synchronized String toString() {
		Log.d("Phra dedomena", model + ":" + manufacturer + ":" + version + ":" + latitude + ":" + 
				longitude + ":" + altitude + ":" + level + ":" + charging);
		return (model + ":" + manufacturer + ":" + version + ":" + latitude + ":" + 
				longitude + ":" + altitude + ":" + level + ":" + charging);
	}
}
