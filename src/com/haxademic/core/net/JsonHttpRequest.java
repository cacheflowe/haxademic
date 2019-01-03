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

public class JsonHttpRequest 
implements Runnable {
	
	protected String requestURL;
	protected JSONObject jsonOut;
	protected IJsonRequestCallback delegate;
	protected String requestId;
	protected String responseText = null;
	protected int responseCode = 0;

	public JsonHttpRequest(String requestURL, JSONObject jsonOut, IJsonRequestCallback delegate) {
		this.requestURL = requestURL;
		this.jsonOut = jsonOut;
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
			responseCode = httpcon.getResponseCode(); 
			
			// get response
			getResponseText(httpcon.getInputStream());
			// boolean success = (200 <= responseCode && responseCode <= 299);
			
			// wrap-up
			int responseTime = P.p.millis() - startTime;
			if(delegate != null) delegate.postSuccess(responseText, responseCode, requestId, responseTime);
		} catch (IOException e) {
			// fail!
			e.printStackTrace();
			int responseTime = P.p.millis() - startTime;
			if(delegate != null) delegate.postFailure(responseText, responseCode, requestId, responseTime);
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
