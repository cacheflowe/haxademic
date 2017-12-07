package com.haxademic.core.net;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.ConvertUtil;

public class WebServerRequestHandlerUIControls extends WebServerRequestHandler {
	
	protected PAppletHax delegate;
	
	public void setDelegate(PAppletHax delegate) {
		this.delegate = delegate;
	}

	@Override
	protected String handleCustomPaths(String path, String[] pathComponents) {
		if(WebServer.DEBUG) P.println("CustomWebRequestHandler path:", path);
		
		if(pathComponents[0].equals("button")) {
			int buttonIndex = ConvertUtil.stringToInt(pathComponents[1]);
			float buttonValue = ConvertUtil.stringToFloat(pathComponents[2]);
			P.p.browserInputState.setControlValue("button"+buttonIndex, buttonValue);
			return "{\"log\": \"Button Number: "+buttonIndex+", value: "+buttonValue+"\"}";
			
		} else if(pathComponents[0].equals("slider")) {
			int sliderIndex = ConvertUtil.stringToInt(pathComponents[1]);
			float sliderValue = ConvertUtil.stringToFloat(pathComponents[2]);
			P.p.browserInputState.setControlValue("slider"+sliderIndex, sliderValue);
			
			return "{\"log\": \"Slider number: "+sliderIndex+", value: "+sliderValue+"\"}";
			
		} else {
			return null;
		}
	}
}
