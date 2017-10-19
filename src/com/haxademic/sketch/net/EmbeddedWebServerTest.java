package com.haxademic.sketch.net;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.net.WebServer;
import com.haxademic.core.net.WebServerRequestHandler;
import com.haxademic.core.system.SystemUtil;

public class EmbeddedWebServerTest
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
		if(p.frameCount == 200) SystemUtil.openWebPage("http://localhost:8080/web-server-demo/index.html");
		// draw slider val
		p.fill(255);
		p.rect(0, 0, P.map(sliderVal, 0, 1, 0, p.width), p.height);
	}
	
	// Example 
	
	public class CustomWebRequestHandler extends WebServerRequestHandler {
		
		@Override
		protected String handleCustomPaths(String path, String[] pathComponents) {
			P.println(path, path.indexOf("button"));
			if(path.indexOf("button") != -1) {
				int buttonIndex = ConvertUtil.stringToInt(pathComponents[1]);
				return "{\"log\": \"Button Number: "+buttonIndex+"\"}";
			} else if(path.indexOf("slider") != -1) {
				sliderVal = ConvertUtil.stringToFloat(pathComponents[1]);
				return "{\"log\": \"Slider Val: "+sliderVal+"\"}";
			} else {
				return null;
			}
		}
	}

}
