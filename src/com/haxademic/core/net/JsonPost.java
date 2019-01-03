package com.haxademic.core.net;

import java.io.IOException;

import processing.data.JSONObject;

public class JsonPost {
	
	protected String requestURL;
	protected JsonRequest request;
	protected Thread requestThread;
		
	public JsonPost(String requestURL) {
		this.requestURL = requestURL;
	}
	
	public void setURL(String requestURL) {
		this.requestURL = requestURL;
	}
	
	public void requestData(IPostJSONCallback delegate) throws IOException {
		// send request with empty json
		sendData(new JSONObject(), delegate);
	}
	
	public void sendData(JSONObject jsonOut, IPostJSONCallback delegate) throws IOException {
		// start thread
		request = new JsonRequest(requestURL, jsonOut, delegate);
		requestThread = new Thread( request );
		requestThread.start();
	}
	
}
