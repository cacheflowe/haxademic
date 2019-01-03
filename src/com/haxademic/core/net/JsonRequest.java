package com.haxademic.core.net;

import java.io.IOException;

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
	
	public void requestJsonData(IPostJSONCallback delegate) throws IOException {
		// send request with empty json
		postJsonData(new JSONObject(), delegate);
	}
	
	public void postJsonData(JSONObject jsonOut, IPostJSONCallback delegate) throws IOException {
		// start thread
		request = new JsonHttpRequest(requestURL, jsonOut, delegate);
		requestThread = new Thread( request );
		requestThread.start();
	}
	
}
