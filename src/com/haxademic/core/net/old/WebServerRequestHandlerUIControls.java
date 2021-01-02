package com.haxademic.core.net.old;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.hardware.http.HttpInputState;
import com.haxademic.core.net.WebServer;
import com.haxademic.core.ui.UI;

public class WebServerRequestHandlerUIControls 
extends WebServerRequestHandler {
	
	protected PAppletHax delegate;
	
	public void setDelegate(PAppletHax delegate) {
		this.delegate = delegate;
	}

	@Override
	protected String handleCustomPaths(String path, String[] pathComponents) {
		if(WebServer.DEBUG == true && pathComponents[0].equals("values") == false) P.println("CustomWebRequestHandler path:", path);
		P.store.setString(WebServer.REQUEST_URL, path);	// pass along all web requests to AppStore
		
		if(pathComponents[0].equals("button")) {
			String buttonIndex = pathComponents[1];
			float buttonValue = ConvertUtil.stringToFloat(pathComponents[2]);
			HttpInputState.instance().setControlValue("button"+buttonIndex, buttonValue);
			if(UI.has(buttonIndex)) {
				UI.setValue(buttonIndex, buttonValue);
			}
			return "{\"log\": \"button: "+buttonIndex+", value: "+buttonValue+"\"}";
			
		} else if(pathComponents[0].equals("slider")) {
			String sliderIndex = pathComponents[1];
			float sliderValue = ConvertUtil.stringToFloat(pathComponents[2]);
			HttpInputState.instance().setControlValue("slider"+sliderIndex, sliderValue);
			if(UI.has(sliderIndex)) {
				UI.setValue(sliderIndex, sliderValue);
			}
			return "{\"log\": \"slider: "+sliderIndex+", value: "+sliderValue+"\"}";

		} else if(pathComponents[0].equals("config")) {
			return UI.configToJSON();
			
		} else if(pathComponents[0].equals("values")) {
			return UI.valuesToJSON();
			
		} else {
			return null;
		}
	}
}
