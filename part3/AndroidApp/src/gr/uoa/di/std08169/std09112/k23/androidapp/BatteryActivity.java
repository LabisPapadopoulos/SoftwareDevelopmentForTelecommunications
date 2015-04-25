package gr.uoa.di.std08169.std09112.k23.androidapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class BatteryActivity extends Activity {

	private TextView level;
	private TextView charging;
	private Button monitoring;
	private BatteryReceiver batteryReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_battery);
		
		level = (TextView)findViewById(R.id.level);
		charging = (TextView)findViewById(R.id.charging);
		monitoring = (Button)findViewById(R.id.battery_button);

		batteryReceiver = new BatteryReceiver(level, charging);
		
		monitoring.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//an einai disabled
				//R.string.disabled: epistrefei id, opote me tin 
				//getResources().getString pairnoume to string pou theloume
				if(monitoring.getText().toString().equals(getResources().getString(R.string.disabled))) {
					//Enhmerwnetai apo to susthma gia tin katastash ths batarias
			        //Gia auto to intentFilter, tha enhmerwnetai o BroadcastReceiver
			        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
					monitoring.setText(R.string.enabled);
					Memory.getInstance().setBatteryMonitoring(true);
				} else {
					//Apenergopoihsh tou batteryReceiver
					unregisterReceiver(batteryReceiver);
					monitoring.setText(R.string.disabled);
					Memory.getInstance().setBatteryMonitoring(false);
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		Memory memory = Memory.getInstance();
		level.setText((memory.getLevel() * 100) + " %");
		charging.setText(memory.getCharging());
		monitoring.setText(memory.isBatteryMonitoring() ? R.string.enabled : R.string.disabled);
		
		//an htan anoixto, prin pagwsei, to xana xekinaei
		if(monitoring.getText().toString().equals(getResources().getString(R.string.enabled))) {
			registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_battery, menu);
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
