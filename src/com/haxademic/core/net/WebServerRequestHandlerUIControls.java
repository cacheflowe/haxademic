//package com.haxademic.core.net;
//
//import com.haxademic.core.app.P;
//import com.haxademic.core.app.PAppletHax;
//import com.haxademic.core.data.ConvertUtil;
//import com.haxademic.core.ui.UIButton;
//
//public class WebServerRequestHandlerUIControls 
//extends WebServerRequestHandler {
//	
//	protected PAppletHax delegate;
//	
//	public void setDelegate(PAppletHax delegate) {
//		this.delegate = delegate;
//	}
//
//	@Override
//	protected String handleCustomPaths(String path, String[] pathComponents) {
//		if(WebServer.DEBUG && pathComponents[0].equals("values") == false) P.println("CustomWebRequestHandler path:", path);
//		P.store.setString(WebServer.REQUEST_URL, path);	// pass along all web requests to AppStore
//		
//		if(pathComponents[0].equals("button")) {
//			String buttonIndex = pathComponents[1];
//			float buttonValue = ConvertUtil.stringToFloat(pathComponents[2]);
//			P.p.browserInputState.setControlValue("button"+buttonIndex, buttonValue);
//			if(P.p.ui.has(buttonIndex)) {
//				P.p.ui.setValue(buttonIndex, buttonValue);
//				P.p.uiButtonClicked((UIButton) P.p.ui.get(buttonIndex));	// grab button and set clicked callback
//			}
//			return "{\"log\": \"button: "+buttonIndex+", value: "+buttonValue+"\"}";
//			
//		} else if(pathComponents[0].equals("slider")) {
//			String sliderIndex = pathComponents[1];
//			float sliderValue = ConvertUtil.stringToFloat(pathComponents[2]);
//			P.p.browserInputState.setControlValue("slider"+sliderIndex, sliderValue);
//			if(P.p.ui.has(sliderIndex)) {
//				P.p.ui.setValue(sliderIndex, sliderValue);
//			}
//			return "{\"log\": \"slider: "+sliderIndex+", value: "+sliderValue+"\"}";
//
//		} else if(pathComponents[0].equals("config")) {
//			return P.p.ui.configToJSON();
//			
//		} else if(pathComponents[0].equals("values")) {
//			return P.p.ui.valuesToJSON();
//			
//		} else {
//			return null;
//		}
//	}
//}
