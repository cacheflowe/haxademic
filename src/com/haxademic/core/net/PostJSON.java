package com.haxademic.core.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.haxademic.core.app.P;
import com.haxademic.core.text.RandomStringUtil;

import processing.data.JSONObject;

public class PostJSON {
	
	protected String serverPath;
	protected IPostJSONCallback delegate;
	protected String requestId;
	protected JSONRequest request;
	protected Thread requestThread;
	protected String jsonData;
	public static boolean DEBUG = false;
	
	public PostJSON(String serverPath) {
		this(serverPath, null);
	}
	
	public PostJSON(String serverPath, IPostJSONCallback delegate) {
		this.serverPath = serverPath;
		this.delegate = delegate;
		if(DEBUG) P.out("PostJSON to server:", serverPath);
	}
	
	public void sendData(JSONObject jsonOut) throws IOException {
		// start thread
		request = new JSONRequest(jsonOut);
		requestThread = new Thread( request );
		requestThread.start();
	}
	
	class JSONRequest implements Runnable {
		
		protected JSONObject jsonOut;
		public JSONRequest(JSONObject jsonOut) {
			this.jsonOut = jsonOut;
		}    

		public void run() {
			// create request id
			int startTime = P.p.millis();
			requestId = RandomStringUtil.randomUUID(16);
			
			try {
				// make http connection
				HttpURLConnection httpcon = (HttpURLConnection) ((new URL(serverPath).openConnection()));
				httpcon.setDoOutput(true);
				httpcon.setRequestProperty("Content-Type", "application/json");
				httpcon.setRequestProperty("Accept", "application/json");
				httpcon.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
				httpcon.setRequestMethod("POST");
				httpcon.connect();

				// write json data to http stream
				String outputString = jsonOut.toString();
				byte[] outputBytes = outputString.getBytes("UTF-8");
				OutputStream os = httpcon.getOutputStream();
				os.write(outputBytes);
				os.close();
				
				// trigger request
				int responseCode = httpcon.getResponseCode(); 
				
				// success messaging
				if(DEBUG == true) {
					P.out("============ JSON DEBUG");
					P.out("outputString:", outputString);
					P.out("responseCode:", responseCode);
					P.out("httpcon.getResponseMessage:", httpcon.getResponseMessage());
					if (200 <= httpcon.getResponseCode() && httpcon.getResponseCode() <= 299) {
						printResponse(httpcon.getInputStream());
					} else {
						printResponse(httpcon.getInputStream());
					}
					P.out("============ DEBUG END");
				}
				int responseTime = P.p.millis() - startTime;
				if(delegate != null) delegate.postSuccess(requestId, responseTime);
			} catch (IOException e) {
				
				// fail!
				if(DEBUG == true) {
					e.printStackTrace();
					P.out("JSON POST ERROR --------------");
				}
				int responseTime = P.p.millis() - startTime;
				if(delegate != null) delegate.postFailure(requestId, responseTime);
			}
		} 
		
		public void printResponse(InputStream istream) throws IOException {
			P.out("============ JSON FROM SERVER");
			BufferedReader in = new BufferedReader(new InputStreamReader(istream));
			String line = null;
			while ((line = in.readLine()) != null) {
				P.out(line);
			}
		}
	}		
}
