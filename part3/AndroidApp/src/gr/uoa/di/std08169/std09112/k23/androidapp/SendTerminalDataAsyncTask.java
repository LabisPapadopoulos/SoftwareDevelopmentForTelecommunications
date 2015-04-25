package gr.uoa.di.std08169.std09112.k23.androidapp;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import android.util.Log;

public class SendTerminalDataAsyncTask extends AsyncTask<Void, Void, Void> {

	private static final String NAMESPACE = "http://k23.std08169.std09112.di.uoa.gr/";
	private static final String METHOD_NAME = "setTerminalData";
	
	private long sleepInterval;
	private String webServiceUrl;
	
	public SendTerminalDataAsyncTask(long sleepInterval, String webServiceUrl) {
		this.sleepInterval = sleepInterval;
		this.webServiceUrl = webServiceUrl;
	}

	@Override
	protected Void doInBackground(Void... params) {
		
		Memory memory = Memory.getInstance();
		
		while(!isCancelled()) {

			Log.i("Monitor", "Starting SOAP");
	        
			//Dhmiourgeia mhnumatos me orismata (request) kai tsekarei 
			//me poia methodo mhlaei
	    	SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	    	request.addProperty("device", memory.getAndroidId());
	    	request.addProperty("androidData", memory.toString());
	    	
	    	//http paketo pou metaferei to soap mhnuma
	    	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    	envelope.setOutputSoapObject(request);
	
	    	//Anoigei sto port tou service tin sugkekrimenh sundesh
	    	HttpTransportSE httpTransport = new HttpTransportSE(webServiceUrl);
	    	
	    	 try {
	    		httpTransport.call(NAMESPACE + METHOD_NAME, envelope);
	    		SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
	    		Log.i("Monitor", "Phra apotelesma " + result);
	    		
	    		Thread.sleep(sleepInterval);
	    		
	    	 } catch (IOException e) {
				Log.e("Monitor", "Den boresa na steilw terminal data", e);
	    	 } catch (XmlPullParserException e) {
	    		 Log.e("Monitor", "Den boresa na steilw terminal data", e);
	    	 } catch (InterruptedException e) {
				break;
	    	 }	    	 
		}
    	return null;
	}
}
