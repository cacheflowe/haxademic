package com.haxademic.core.net;

import java.io.IOException;
import java.util.HashMap;

import processing.data.JSONObject;

public class JsonRequest {
	
	protected String requestURL;
	protected JsonHttpRequest request;
	protected Thread requestThread;
		
	public JsonRequest(String requestURL) {
		this.requestURL = requestURL;
	}
	
	public void setURL(String requestURL) {
		this.requestURL = requestURL;
	}
	
	public JsonHttpRequest request() {
		return request;
	}
	
	public void requestJsonData(IJsonRequestDelegate delegate) throws IOException {
		// send request with empty json
		postJsonDataWithHeaders(null, null, delegate);
	}
	
	public void postJsonData(JSONObject jsonOut, IJsonRequestDelegate delegate) throws IOException {
		// send request with posted json data
		postJsonDataWithHeaders(jsonOut, null, delegate);
	}
	
	public void postJsonDataWithHeaders(JSONObject jsonOut, HashMap<String, String> headers, IJsonRequestDelegate delegate) throws IOException {
		delegate.aboutToRequest(request);
		// start thread
		request = new JsonHttpRequest(requestURL, jsonOut, headers, delegate);
		requestThread = new Thread( request );
		requestThread.start();
	}
	
}
