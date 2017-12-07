package com.haxademic.demo.net;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.net.WebServer;
import com.haxademic.core.net.WebServerRequestHandlerUIControls;
import com.haxademic.core.system.SystemUtil;

public class Demo_WebServer
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected WebServer server;
	
	public void setup() {
		super.setup();	
		server = new WebServer(new WebServerRequestHandlerUIControls(), true);
	}
	
	public void drawApp() {
		background(0);
		if(p.frameCount == 200) SystemUtil.openWebPage(WebServer.getServerAddress() + "web-server-demo/");
		// draw slider val
		p.fill(255);
		p.rect(0, 0, P.map(P.p.browserInputState.getValue("slider1"), 0, 1, 0, p.width), p.height);
	}
	
}
