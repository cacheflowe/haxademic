package com.haxademic.core.net;

public interface IPostJSONCallback {
	public void postSuccess(String responseText, int responseCode, String requestId, int responseTime);
	public void postFailure(String responseText, int responseCode, String requestId, int responseTime);
}
