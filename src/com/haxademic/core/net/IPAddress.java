package com.haxademic.core.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPAddress {

	public static String getLocalAddress() {
		String localAddress = "";
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			localAddress = "http://" + addr.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return localAddress;
	}
	
}
