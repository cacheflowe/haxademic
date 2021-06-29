package com.haxademic.core.net;

public interface IJsonRequestDelegate {
	public void aboutToRequest(JsonHttpRequest request);
	public void postSuccess(String responseText, int responseCode, String requestId, int responseTime);
	public void postFailure(String responseText, int responseCode, String requestId, int responseTime, String errorMessage);
}
