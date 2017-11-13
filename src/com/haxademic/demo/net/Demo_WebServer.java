package com.haxademic.demo.net;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.net.WebServer;
import com.haxademic.core.net.WebServerRequestHandler;
import com.haxademic.core.system.SystemUtil;

public class Demo_WebServer
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected WebServer server;
	
	protected float sliderVal = 0;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.FPS, 90);
	}

	public void setup() {
		super.setup();	
		buildWebServer();
	}
	
	protected void buildWebServer() {
		server = new WebServer(new CustomWebRequestHandler(), true);
	}
	
	public void drawApp() {
		background(0);
		if(p.frameCount == 200) SystemUtil.openWebPage("http://localhost:8080/web-server-demo/");
		// draw slider val
		p.fill(255);
		p.rect(0, 0, P.map(sliderVal, 0, 1, 0, p.width), p.height);
	}
	
	// Example custom web server responder
	
	public class CustomWebRequestHandler extends WebServerRequestHandler {
		
		@Override
		protected String handleCustomPaths(String path, String[] pathComponents) {
			P.println("CustomWebRequestHandler path:", path);
			
			if(pathComponents[0].equals("button")) {
				int buttonIndex = ConvertUtil.stringToInt(pathComponents[1]);
				return "{\"log\": \"Button Number: "+buttonIndex+"\"}";
				
			} else if(pathComponents[0].equals("slider")) {
				sliderVal = ConvertUtil.stringToFloat(pathComponents[1]);
				return "{\"log\": \"Slider Val: "+sliderVal+"\"}";
				
			} else {
				return null;
			}
		}
	}

}
