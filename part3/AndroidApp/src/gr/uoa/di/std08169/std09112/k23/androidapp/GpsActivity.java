package gr.uoa.di.std08169.std09112.k23.androidapp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class GpsActivity extends Activity {

	private TextView latitude;
	private TextView longitude;
	private TextView altitude;
	private Button monitoring;
	private LocationManager locationManager;
	private GpsListener gpsListener;
	private long minTime;
	private float minDistance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gps);
		
		//location: http://developer.android.com/guide/topics/location/strategies.html
        // Acquire a reference to the system Location Manager
        //Context: to perivallon ekteleshs efarmoghs. Epistrefei ton Location manager gia na psaxei tin thesh sto GPS
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Pairnei tin teleutaia gnwsth topothesia (an uparxei) 'h null an den uparxei
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        
        latitude = (TextView)findViewById(R.id.latitude);
        longitude = (TextView)findViewById(R.id.longitude);
        altitude = (TextView)findViewById(R.id.altitude);
        
        if(location != null) {
        	latitude.setText(Double.toString(location.getLatitude())); //Pairnei kai apothikeuei to geografiko platos
        	longitude.setText(Double.toString(location.getLongitude()));//georgrafiko mhkos
        	altitude.setText(Double.toString(location.getAltitude()));//geografiko upsos
        	
        	Memory.getInstance().setLatitude(location.getLatitude());
        	Memory.getInstance().setLongitude(location.getLongitude());
        	Memory.getInstance().setAltitude(location.getAltitude());
        }
        
        monitoring = (Button)findViewById(R.id.gps_button);
        monitoring.setText(R.string.disabled); //Arxika einai disabled
        
        Properties properties = new Properties();
		
		try {
			//Anoigoume to configuration.properties apo to fakelo assets kai
			//to fortwnoume se ena properties antikeimeno.
			InputStream inputStream = getAssets().open("configuration.properties");
			properties.load(inputStream);
			inputStream.close();
			
			minTime = Long.parseLong(properties.getProperty("minTime"));
			minDistance = Float.parseFloat(properties.getProperty("minDistance"));
			
		} catch (IOException e) {
			Log.e("GpsActivity", "Den boresa n' anoixw to propery file", e);
		}
		
        gpsListener = new GpsListener(latitude, longitude, altitude);
        
        monitoring.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//an einai disabled
				//R.string.disabled: epistrefei id, opote me tin 
				//getResources().getString pairnoume to string pou theloume
				if(monitoring.getText().toString().equals(getResources().getString(R.string.disabled))) {
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
					monitoring.setText(R.string.enabled);
					Memory.getInstance().setGpsMonitoring(true);
				} else {
					//Apenergopoihsh tou batteryReceiver
					locationManager.removeUpdates(gpsListener);
					monitoring.setText(R.string.disabled);
					Memory.getInstance().setGpsMonitoring(false);
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		Memory memory = Memory.getInstance();
		
		latitude.setText(Double.toString(memory.getLatitude()));
		longitude.setText(Double.toString(memory.getLongitude()));
		altitude.setText(Double.toString(memory.getAltitude()));
		
		monitoring.setText(memory.isGpsMonitoring() ? R.string.enabled : R.string.disabled);
		
		//an htan anoixto, prin pagwsei, to xana xekinaei
		if(monitoring.getText().toString().equals(getResources().getString(R.string.enabled))) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_gps, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
