package com.haxademic.core.net;

public interface ISocketClientDelegate {
	public void socketConnected(String connection);
	public void socketDisconnected(String connection);
	public void messageReceived(String message);
}
