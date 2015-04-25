package gr.uoa.di.std08169.std09112.k23.androidapp;

import java.util.ArrayList;

import gr.uoa.di.std08169.std09112.k23.androidapp.R;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainMenu extends ListActivity {

	private ArrayList<String> options;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		options = new ArrayList<String>();
		options.add(getResources().getString(R.string.title_activity_gps));
		options.add(getResources().getString(R.string.title_activity_battery));
		options.add(getResources().getString(R.string.title_activity_general_info));
		
		//Gia tin lista ftaxnei enan adapter (ftiaxnei grafikh lista), pws deixnei tin lista kai oi
		//epiloges tis listas. To this molis patithei kati apo tin lista kalei tin onListItemClick.
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_menu, menu);
		return true;
	}
	
	//me to pou pataei o xrhsths klick stin lista apofasizei ti tha kanei
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Intent activityIntent = null;
		
		switch (position) {
		case 0:
			//Dhmiourgeia intent gia na xekinisei to activity
			//feugei apo to this(auto to acrivity) kai paei sto epomeno ()
			activityIntent = new Intent(this, GpsActivity.class);
			break;
		case 1:
			activityIntent = new Intent(this, BatteryActivity.class);
			break;
		case 2:
			activityIntent = new Intent(this, GeneralInfoActivity.class);
			break;
		}
		
		//katharizei tin othonh kai paei stin epomenh
		activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		startActivity(activityIntent);
	}
}
