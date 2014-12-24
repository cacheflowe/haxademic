package com.haxademic.core.hardware.dmx;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ColorHax;

public class DmxInterface {
	protected Runnable _asyncDmxRequest;
	protected Thread _requestThread;
	protected boolean _isRequesting = false;
	protected int _color1 = 0;
	protected int _color2 = 0;

	public void updateColors( int color, int color2 ) {
		_color1 = color;
		_color2 = color2;
		
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
			// FUCKING JENKY, using Pro-Manager's web server
			try {
//				final URL url = new URL("http://localhost:55555/dmxfader");
				HttpURLConnection httpcon = (HttpURLConnection) ((new URL("http://127.0.0.1:55555/dmxfader").openConnection()));
				httpcon.setDoOutput(true);
				httpcon.setRequestProperty("Content-Type", "application/json");
				httpcon.setRequestProperty("Accept", "application/json");
				httpcon.setRequestMethod("POST");
				httpcon.connect();

				String outputString = "[0,";
				// light 1 rgb
//				outputString += (127+P.sin(p.frameCount/10f)*127) + ",";
//				outputString += (127+P.sin((100+p.frameCount)/15f)*127) + ",";
//				outputString += (127+P.sin((200+p.frameCount)/18f)*127) + ",";
				outputString += ColorHax.redFromColorInt(_color1) + ",";
				outputString += ColorHax.greenFromColorInt(_color1) + ",";
				outputString += ColorHax.blueFromColorInt(_color1) + ",";
				// light 2 rgb
//				outputString += (127+P.sin(p.frameCount/3f)*127) + ",";
//				outputString += (127+P.sin((100+p.frameCount)/5f)*127) + ",";
//				outputString += (127+P.sin((200+p.frameCount)/8f)*127) + ",";
				outputString += ColorHax.redFromColorInt(_color2) + ",";
				outputString += ColorHax.greenFromColorInt(_color2) + ",";
				outputString += ColorHax.blueFromColorInt(_color2) + ",";
//				P.println(ColorHax.redFromColorInt(_color2) + "," + ColorHax.greenFromColorInt(_color2) + "," + ColorHax.blueFromColorInt(_color2));
				// fill in the rest
				outputString += "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]";
				
				
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
