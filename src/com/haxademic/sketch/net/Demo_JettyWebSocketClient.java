package com.haxademic.sketch.net;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

import javax.websocket.ClientEndpoint;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.io.RuntimeIOException;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;

public class Demo_JettyWebSocketClient
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	RemoteEndpoint remote;
	
	public void firstFrame() {
        try {
        	// web socket
        	HttpClient http = new HttpClient();
        	http.start();
            WebSocketClient websocket = new WebSocketClient(http);
            websocket.start();
        	try {

        		URI uri = new URI("ws://localhost:8787/websocket");
        		P.out("Connecting to: {}...", uri);
        		Session session = websocket.connect(new ToUpper356ClientSocket(), uri, new ClientUpgradeRequest()).get();
        		P.out("Connected to: {}", uri);
        		remote = session.getRemote();
        		remote.sendString("Hello World");
        	} catch (Exception e) {
        		throw new RuntimeIOException(e);
        	}
        	
//        	ClientUpgradeRequest request = new ClientUpgradeRequest();
//        	
//        	HttpClient http = new HttpClient();
//        	http.start();
//            WebSocketClient websocket = new WebSocketClient(http);
//            websocket.start();
//            try
//            {
//                String dest = "ws://localhost:8787/";
//                websocket.connect(new ToUpper356ClientSocket(), new URI(dest), request);
//            }
//            finally
//            {
//                websocket.stop();
//            } 
        } catch (Throwable t) {
            t.printStackTrace();
        }

	}
	
	public void drawApp() {
		p.background(0);
		if(p.frameCount % 100 == 0) {
    		try {
				remote.sendString(""+p.frameCount);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@ClientEndpoint
	public class ToUpper356ClientSocket implements WebSocketListener {
	 
	    CountDownLatch latch = new CountDownLatch(1);
	    private Session session;
	    
		@Override
		public void onWebSocketClose(int arg0, String arg1) {
	        System.out.println("Closing a WebSocket due to " + arg1);
		}

		@Override
		public void onWebSocketConnect(Session arg0) {
	        System.out.println("Connected to server");
	        session = arg0;
	        latch.countDown();
		}

		@Override
		public void onWebSocketError(Throwable arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onWebSocketBinary(byte[] arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onWebSocketText(String arg0) {
	        System.out.println("Message received from server:" + arg0);
		}
	}

}

