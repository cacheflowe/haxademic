package com.haxademic.core.net;

public interface IPostJSONCallback {
	public void postSuccess(String requestId, int responseTime);
	public void postFailure(String requestId, int responseTime);
}
