package gr.uoa.di.std08169.std09112.k23.androidapp;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.TextView;

//http://developer.android.com/guide/topics/location/strategies.html
public class GpsListener implements LocationListener {
	
	private TextView latitude;
	private TextView longitude;
	private TextView altitude;

	public GpsListener(TextView latitude, TextView longitude, TextView altitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}

	@Override
	public void onLocationChanged(Location location) {
		//enhmerwsh tis mnhmhs me ta dedomena
		Memory memory = Memory.getInstance();
		memory.setLatitude(location.getLatitude());
		memory.setLongitude(location.getLongitude());
		memory.setAltitude(location.getAltitude());
		
		latitude.setText(Double.toString(location.getLatitude()));
		longitude.setText(Double.toString(location.getLongitude()));
		altitude.setText(Double.toString(location.getAltitude()));
	}

	@Override
	public void onProviderDisabled(String provider) {}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}

}
