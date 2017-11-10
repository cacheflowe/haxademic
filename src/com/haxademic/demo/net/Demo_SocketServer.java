package com.haxademic.demo.net;

import java.net.UnknownHostException;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.net.IPAddress;
import com.haxademic.core.net.SocketServer;
import com.haxademic.core.net.SocketServerHandler;
import com.haxademic.core.system.SystemUtil;

public class Demo_SocketServer
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected SocketServer server;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.FPS, 90);
	}

	public void setup() {
		super.setup();	
		buildSocketServer();
		// open www/web-socket-demo/index.html to try it out
	}
	
	protected void buildSocketServer() {
		try {
			server = new SocketServer(new CustomSocketHandler(SocketServer.PORT), true);
			p.debugView.setValue("WS Server", "ws://" + IPAddress.getIP() + ":" + SocketServer.PORT);
		} catch (UnknownHostException e) {
			e.printStackTrace(); 
		}
	}
	
	public void drawApp() {
		background(0);
		p.fill(255);
		// send a simple message to clients
		if(p.frameCount % 100 == 0) server.sendMessage("{\"frameCount\": \""+ p.frameCount + "\"}");
	}
	
	// Example custom WebSocket server responder

	public class CustomSocketHandler extends SocketServerHandler {

		public CustomSocketHandler(int port) throws UnknownHostException {
			super(port);
		}

		protected void receiveMessage(String message) {
			P.println("CustomSocketHandler.receiveMessage() : ", message);
		}
	}

}
