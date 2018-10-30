package com.haxademic.core.hardware.dmx;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.EasingColor;

public class DmxAjaxProManagerInterface {
	// We use Pro-Manager's web server. This is a hack but it works.
	protected String PRO_MANAGER_ENDPOINT = "http://localhost:55555/dmxfader";
	protected String REQUEST_PREFIX = "[0,";
	protected String REQUEST_SUFFIX = "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]";
	protected Runnable _asyncDmxRequest;
	protected Thread _requestThread;
	protected boolean _isRequesting = false;
	protected int _numLights = 0;
	protected int[] _colors;
	protected int _color2 = 0;
	
	public DmxAjaxProManagerInterface(int numLights) {
		_numLights = numLights;
		_colors = new int[_numLights];
		P.println("substr start index: "+((_numLights * 2)));
		REQUEST_SUFFIX = REQUEST_SUFFIX.substring((_numLights * 2)); // remove r,g,b values for each number of light
	}
	
	public void setColorAtIndex(int index, int newColor) {
		_colors[index] = newColor;
	}

	public void updateColors() {
		// only make a request if the previous one has finished. 
		if(_isRequesting == false) {
			_isRequesting = true;
			if(_asyncDmxRequest == null) _asyncDmxRequest = new DmxProThread(null); //init Runnable 
			//Thread class is used for starting a thread (runnable instance)
			_requestThread = new Thread(_asyncDmxRequest); //init thread object, but haven't started yet
			_requestThread.start(); //start the thread simultaneously
		} else {
			// P.println("waiting for request to finish");
		}
	}
	
	interface Callback{
		void callback();
	}

	public class DmxProThread implements Runnable {
		Callback c; 
		public DmxProThread(Callback c){
			this.c=c;
		}

		public void run(){
			makeDmxRequest();
			if(this.c != null) this.c.callback();
		}
		
		protected void makeDmxRequest() {
			try {
				HttpURLConnection httpcon = (HttpURLConnection) ((new URL(PRO_MANAGER_ENDPOINT).openConnection()));
				httpcon.setDoOutput(true);
				httpcon.setRequestProperty("Content-Type", "application/json");
				httpcon.setRequestProperty("Accept", "application/json");
				httpcon.setRequestMethod("POST");
				httpcon.connect();

				String outputString = REQUEST_PREFIX;
				for (int i = 0; i < _numLights; i++) {
					int curColor = _colors[i];
					outputString += EasingColor.redFromColorInt(curColor) + ",";
					outputString += EasingColor.greenFromColorInt(curColor) + ",";
					outputString += EasingColor.blueFromColorInt(curColor) + ",";
				}
				outputString += REQUEST_SUFFIX;
				
				byte[] outputBytes = (outputString).getBytes("UTF-8");
				OutputStream os = httpcon.getOutputStream();
				os.write(outputBytes);
				os.close();
				
				int responseCode = httpcon.getResponseCode(); // forces request to be made
			} catch (IOException e) {
				e.printStackTrace();
			}

			_isRequesting = false;
		}
	}

}
