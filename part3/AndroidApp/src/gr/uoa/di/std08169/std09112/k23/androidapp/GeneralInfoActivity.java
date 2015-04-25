package gr.uoa.di.std08169.std09112.k23.androidapp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class GeneralInfoActivity extends Activity {
	
	private TextView androidId;
	
	//montelo suskeuhs
	private TextView model;
	
	//kataskeuasths suskeuhs
	private TextView manufacturer;
	
	//Ekdosh android SDK
	private TextView version;
	
	private Button sendData;
	
	private long sleepInterval;
	private String webServiceUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_general_info);
		
		Memory memory = Memory.getInstance();
								//psarema dedomenwn apo xml
		androidId = (TextView)findViewById(R.id.androidId);
		//getApplicationContext: Perivallon ekteleshs tis efarmoghs
		//ContentResolver: Gurnaei apo tin efarmogh periexomeno
		androidId.setText(Secure.getString(getContentResolver(), Secure.ANDROID_ID));
		memory.setAndroidId(Secure.getString(getContentResolver(), Secure.ANDROID_ID));
		//to id pou phre apo tin main_activity.xml
		model = (TextView)findViewById(R.id.model);
		//eisagwgh dedomenwn sti mnhmh
		memory.setModel(Build.MODEL);
		model.setText(Build.MODEL);
		
		manufacturer = (TextView)findViewById(R.id.manufacturer);
		memory.setManufacturer(Build.MANUFACTURER);
		manufacturer.setText(Build.MANUFACTURER);
		
		version = (TextView)findViewById(R.id.sdkVersion);
		memory.setVersion(Build.VERSION.RELEASE);
		version.setText(Build.VERSION.RELEASE);
		
		sendData = (Button)findViewById(R.id.sendDataButton);
		sendData.setText(R.string.disabled); //Arxika einai disabled
		
		Properties properties = new Properties();
		
		try {
			//Anoigoume to configuration.properties apo to fakelo assets kai
			//to fortwnoume se ena properties antikeimeno.
			InputStream inputStream = getAssets().open("configuration.properties");
			properties.load(inputStream);
			inputStream.close();
			
			sleepInterval = Long.parseLong(properties.getProperty("sleepInterval"));
			webServiceUrl = properties.getProperty("webServiceUrl");
			
		} catch (IOException e) {
			Log.e("GeneralInfoActivity", "Den boresa n' anoixw to propery file", e);
		}

		sendData.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Memory memory = Memory.getInstance();
				//an einai disabled
				//R.string.disabled: epistrefei id, opote me tin 
				//getResources().getString pairnoume to string pou theloume
				if(sendData.getText().toString().equals(getResources().getString(R.string.disabled))) {
					//dhmiourgeia nhmatos gia apostolh dedomenwn
					memory.setAsyncTask(new SendTerminalDataAsyncTask(sleepInterval, webServiceUrl));
					//ektelesh tou nhmatos (trexei sto paraskinio)
					memory.getAsyncTask().execute();
					sendData.setText(R.string.enabled);
					memory.setSendData(true);
				} else {
					//termatismos nhmatos
					memory.getAsyncTask().cancel(true);
					sendData.setText(R.string.disabled);
					memory.setSendData(false);
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		Memory memory = Memory.getInstance();
		
		sendData.setText(memory.isSendData() ? R.string.enabled : R.string.disabled);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_general_info, menu);
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
