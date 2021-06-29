package com.haxademic.core.net;

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.haxademic.core.app.P;

import processing.data.JSONObject;

public class JsonPoller 
implements IJsonRequestDelegate {

	protected JsonRequest jsonRequest;
	protected IJsonRequestDelegate delegate;
	protected JSONObject postData;
	protected HashMap<String, String> headers;
	
	protected int numRequests = 0;
	protected boolean isRequesting = false;
	protected int lastRequestTime = -99999;
	protected int interval;

	protected Timer timer;


	public JsonPoller(String url, int interval, IJsonRequestDelegate delegate) {
		jsonRequest = new JsonRequest(url);
		this.interval = interval;
		this.delegate = delegate;
		start();
	}
	
	public void setURL(String requestURL) {
		jsonRequest.setURL(requestURL);
	}

	public int numRequests() {
		return numRequests;
	}
	
	public boolean isRequesting() {
		return isRequesting;
	}
	
	public int interval() {
		return interval;
	}
	
	public void interval(int interval) {
		this.interval = interval;
		stop();
		start();
	}
	
	public void setPostData(JSONObject jsonObj) {
		postData = jsonObj;
	}
	
	public void setHeaders(HashMap<String, String> headers) {
		this.headers = headers;
	}
	
	public void start() {
		timer = new Timer();
		timer.schedule(new TimerTask() { public void run() {
			delegate.aboutToRequest(jsonRequest.request());
			requestJson();
		}}, 0, interval);	 // delay, [repeat]
	}
	
	public void stop() {
		if(timer != null) timer.cancel();
	}
	
	/////////////////////////////////////
	// JSON polling
	/////////////////////////////////////
		
	protected void requestJson() {
		try {
			if(headers != null) {
				jsonRequest.postJsonDataWithHeaders(postData, headers, this);
			} else if(postData != null) {
				jsonRequest.postJsonData(postData, this);
			} else {
				jsonRequest.requestJsonData(this);
			}
			lastRequestTime = P.p.millis();
			isRequesting = true;
			numRequests++;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/////////////////////////////////////
	// IJsonRequestCallback interface
	// Pass through 
	/////////////////////////////////////

	public void postSuccess(String responseText, int responseCode, String requestId, int responseTime) {
		delegate.postSuccess(responseText, responseCode, requestId, responseTime);
	}

	public void postFailure(String responseText, int responseCode, String requestId, int responseTime, String errorMessage) {
		delegate.postFailure(responseText, responseCode, requestId, responseTime, errorMessage);

	}

	public void aboutToRequest(JsonHttpRequest request) {
		// Don't use this version here - use our own version before we make the JsonRequest. See above in the timer.schedule() task 
	}

}
