package com.haxademic.core.net;

public interface IJsonRequestCallback {
	public void postSuccess(String responseText, int responseCode, String requestId, int responseTime);
	public void postFailure(String responseText, int responseCode, String requestId, int responseTime, String errorMessage);
}
