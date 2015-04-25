package gr.uoa.di.std08169.std09112.k23.androidapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.TextView;
import gr.uoa.di.std08169.std09112.k23.androidapp.R;

public class BatteryReceiver extends BroadcastReceiver {

	private TextView level;
	private TextView charging;
	
	public BatteryReceiver(TextView level, TextView charging) {
		this.level = level;
		this.charging = charging;
	}
	
	//Intent: to ti emathe apo to context (pragmata gia ta opoia endiaferetai)
	@Override
	public void onReceive(Context context, Intent intent) {
		int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
		//-1: epeidh einai paronomasths, mhn ginei lathos kai ginei diairesh me 0
		int maxValue = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		int batteryPlugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
		float charged = batteryLevel / (float) maxValue;
		
		level.setText((charged * 100) + " %");
		
		Memory memory = Memory.getInstance();
		//apostolh dedomenwn sti mnhmh
		memory.setLevel(charged);
		
		switch (batteryPlugged)
        {
            case BatteryManager.BATTERY_PLUGGED_AC:
                charging.setText(R.string.acCharger);
                memory.setCharging(charging.getText().toString());
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
            	charging.setText(R.string.usbPort);
            	memory.setCharging(charging.getText().toString());
                break;
            default:
                charging.setText(R.string.battery);
                memory.setCharging(charging.getText().toString());
        }
	}

}
