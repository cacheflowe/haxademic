package com.haxademic.core.net;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.haxademic.core.app.P;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.hardware.http.HttpInputState;
import com.haxademic.core.ui.UI;
import com.haxademic.core.ui.UIButton;

public class UIControlsHandler 
extends AbstractHandler {	
	
	@Override
	public void handle( String target,
			Request baseRequest,
			HttpServletRequest request,
			HttpServletResponse response ) throws IOException, ServletException
	{
		// Get request path and check to see if it looks like a file
		String requestPath = baseRequest.getPathInfo();
		String requestPathNoSlash = requestPath.substring(1);
		if(WebServer.DEBUG == true) P.println("requestPath", requestPath);
		
		String[] pathComponents = requestPathNoSlash.split("/");
		String result = handleCustomPaths(requestPath, pathComponents);
		if(result != null) {
			// Set response props
			response.addHeader("Access-Control-Allow-Origin", "*"); 	// Disable CORS
			response.setStatus(HttpServletResponse.SC_OK);				// set 200
			response.setContentType("text/json; charset=utf-8");		// default to json
			
			// Inform jetty that this request has now been handled
			response.getWriter().println(result);
			baseRequest.setHandled(true);
		} 
	}
	
	protected String handleCustomPaths(String path, String[] pathComponents) {
		if(WebServer.DEBUG && pathComponents[0].equals("values") == false) P.println("CustomWebRequestHandler path:", path);
		P.store.setString(WebServer.REQUEST_URL, path);	// pass along all web requests to AppStore

		if(pathComponents[0].equals("button")) {
			String buttonIndex = pathComponents[1];
			float buttonValue = ConvertUtil.stringToFloat(pathComponents[2]);
			HttpInputState.instance().setControlValue("button"+buttonIndex, buttonValue);
			if(UI.has(buttonIndex)) {
				UI.setValue(buttonIndex, buttonValue);
				P.p.uiButtonClicked((UIButton) UI.get(buttonIndex));	// grab button and set clicked callback
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
