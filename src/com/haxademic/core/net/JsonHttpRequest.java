package com.haxademic.core.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.text.RandomStringUtil;

import processing.data.JSONObject;

public class JsonHttpRequest 
implements Runnable {
	
	protected String requestURL;
	protected JSONObject jsonOut;
	protected HashMap<String, String> headers;
	protected IJsonRequestDelegate delegate;
	protected String requestId;
	protected String responseText = null;
	protected int responseCode = 0;

	public JsonHttpRequest(String requestURL, JSONObject jsonOut, HashMap<String, String> headers, IJsonRequestDelegate delegate) {
		this.requestURL = requestURL;
		this.jsonOut = jsonOut;
		this.headers = headers;
		this.delegate = delegate;
	}    

	public void run() {
		// create request id
		int startTime = P.p.millis();
		requestId = RandomStringUtil.randomUUID(16);
		responseCode = 0;
		
		try {
			// make http connection
			HttpURLConnection httpcon = (HttpURLConnection) ((new URL(requestURL).openConnection()));
			HttpURLConnection.setFollowRedirects(true);
			// Pretend we're a web browser and let the server know we want json returned
			httpcon.setDoOutput(true);
			httpcon.setRequestProperty("Accept", "application/json");
			httpcon.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			// add custom headers
			if(this.headers != null) {
				for (HashMap.Entry<String, String> entry : this.headers.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					httpcon.setRequestProperty(key, value);
				}
			}
			// POST json or GET if we're not sending data, but want json response
			if(jsonOut != null) {
				httpcon.setRequestMethod("POST");
				httpcon.setRequestProperty("Content-Type", "application/json");
			} else {
				httpcon.setRequestMethod("GET");
			}
			httpcon.connect();

			// write json data to http stream
			if(jsonOut != null) {
				String outputString = jsonOut.toString();
				byte[] outputBytes = outputString.getBytes("UTF-8");
				OutputStream os = httpcon.getOutputStream();
				os.write(outputBytes);
				os.close();
			}
			
			// trigger request
			responseCode = httpcon.getResponseCode(); 
			
			// get response
			getResponseText(httpcon.getInputStream());
			// boolean success = (200 <= responseCode && responseCode <= 299);
			
			// wrap-up
			int responseTime = P.p.millis() - startTime;
			if(delegate != null) delegate.postSuccess(responseText, responseCode, requestId, responseTime);
		} catch (IOException e) {
			// fail!
			// e.printStackTrace();
			int responseTime = P.p.millis() - startTime;
			if(delegate != null) delegate.postFailure(responseText, responseCode, requestId, responseTime, e.toString());
		}
	} 
	
	public void getResponseText(InputStream istream) throws IOException {
		responseText = "";
		BufferedReader in = new BufferedReader(new InputStreamReader(istream));
		String line = null;
		while ((line = in.readLine()) != null) {
			responseText += line;
		}
	}
}		
