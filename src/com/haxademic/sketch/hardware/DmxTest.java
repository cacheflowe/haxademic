package com.haxademic.sketch.hardware;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;

@SuppressWarnings("serial")
public class DmxTest
extends PAppletHax {
	
	protected Runnable _asyncDmxRequest;
	protected Thread _requestThread;
	protected boolean _isRequesting = false;
	

	protected void overridePropsFile() {
		//		 _appConfig.setProperty( "osc_active", "true" );
	}

	public void setup() {
		super.setup();
		// make sure to be running Pro-Manager and select the ENTTEC DMXUSB PRO as the device from the web interface
	}

	public void drawApp() {
		background(0);

		int nbChannel=512;  
		background(0);

		// test draw to make sure light update requests don't hurt framerate
		p.fill(255);
		p.stroke(127);
		p.strokeWeight(5);
		p.scale(1 + 0.5f * P.sin(p.frameCount/10f));
		p.rect(p.frameCount % p.width, p.height * 0.5f, 100, 100);
		
		// only make a request if the previous one has finished. 
		if(_isRequesting == false) {
			_isRequesting = true;
			if(_asyncDmxRequest == null) _asyncDmxRequest = new DmxProThread(null); //init Runnable 
			//Thread class is used for starting a thread (runnable instance)
			_requestThread = new Thread(_asyncDmxRequest);//init thread object, but haven't started yet
			_requestThread.start();//start the thread simultaneously
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
			//some work
			if(this.c != null) this.c.callback();//callback
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
				outputString += 255f * p._audioInput.getFFT().spectrum[10] + ",";
				outputString += 255f * p._audioInput.getFFT().spectrum[20] + ",";
				outputString += 255f * p._audioInput.getFFT().spectrum[30] + ",";
				// light 2 rgb
//				outputString += (127+P.sin(p.frameCount/3f)*127) + ",";
//				outputString += (127+P.sin((100+p.frameCount)/5f)*127) + ",";
//				outputString += (127+P.sin((200+p.frameCount)/8f)*127) + ",";
				outputString += 255f * p._audioInput.getFFT().spectrum[100] + ",";
				outputString += 255f * p._audioInput.getFFT().spectrum[200] + ",";
				outputString += 255f * p._audioInput.getFFT().spectrum[220] + ",";
				// fill in the rest
				outputString += "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]";
				
				
				byte[] outputBytes = (outputString).getBytes("UTF-8");
				OutputStream os = httpcon.getOutputStream();
				os.write(outputBytes);
				os.close();
				
				int responseCode = httpcon.getResponseCode();

				//			outputStream.write(("[0,"+(p.frameCount%255)+",175,127,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]").getBytes("UTF-8"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			_isRequesting = false;
		}
	}
}





