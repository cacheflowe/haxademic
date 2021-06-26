//package com.haxademic.demo.system;
//
//import javax.swing.JFrame;
//
//import com.haxademic.core.app.P;
//import com.haxademic.core.app.PAppletHax;
//import com.haxademic.core.app.config.AppSettings;
//import com.haxademic.core.app.config.Config;
//import com.haxademic.core.data.constants.PRenderers;
//import com.haxademic.core.net.JsonUtil;
//import com.haxademic.core.system.VirtualCursor;
//
//import javafx.application.Platform;
//import javafx.embed.swing.JFXPanel;
//import javafx.scene.Scene;
//import javafx.scene.paint.Color;
//import javafx.scene.web.WebEngine;
//import javafx.scene.web.WebView;
//import netscape.javascript.JSObject;
//import processing.awt.PSurfaceAWT;
//import processing.data.JSONObject;
//
//public class Demo_VirtualCursor
//extends PAppletHax {
//	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
//
//	protected VirtualCursor cursor;
//
//	protected void config() {
//		Config.setAppSize(750, 900);
//		Config.setProperty(AppSettings.RENDERER, PRenderers.JAVA2D);
////		Config.setProperty(AppSettings.FULLSCREEN, true);
////		Config.setProperty(AppSettings.SCREEN_X, 100);
////		Config.setProperty(AppSettings.SCREEN_Y, 100);
//	}
//
//	protected void firstFrame() {
//		setupQR();
//		cursor = new VirtualCursor();
//	}
//
//	protected void drawApp() {
//		p.background(0);
//		cursor.drawLog(p.g);
//	}
//
//	public void keyPressed() {
//		super.keyPressed();
//		if(p.key == ' ') cursor.executeJavascript("document.body.style.setProperty('background', 'rgba(255,0,0,1)')");
//		if(p.key == 'c') cursor.executeJavascript("native.getData('Hello Native World')");
//		if(p.key == 'q') cursor.executeJavascript("window.plusSix.generateNewRoom()");
//	}
//
//	// QR window
//	
//	protected JFXPanel fxPanel;
//	protected JFrame jframe;
//	protected WebView webView;
//	protected WebEngine webEngine;
//	
//	protected void setupQR() {
//		fxPanel = new JFXPanel();
//		initJFrame();
//		Platform.runLater(new Runnable() { public void run() { initWebView(); }});
//	}
//	
//	protected void initJFrame() {
//		jframe = (JFrame)((PSurfaceAWT.SmoothCanvas) getSurface().getNative()).getFrame();
//		jframe.add(fxPanel);
//	}
//	
//	protected void initWebView() {
//		// build WebView & JavaFX components on JavaFX thread
//		
//		// init WebView component
//		// load page & attach js bridge to call up to java
//		webView = new WebView();  // webView.setDisable(true);	// disable keyboard/mouse
//		webEngine = webView.getEngine();
//		webEngine.load("http://192.168.1.3:3333/touchless/sdk/javascript/#mode=kiosk&customerId=hovercraft&deviceId=1234567890&debug=true");
//		P.println("webEngine.getUserAgent()", webEngine.getUserAgent());
//		
//		// init js bridge with this class as the deletage
//		JSObject win = (JSObject) webEngine.executeScript("window");
//		win.setMember("native", this);
//		
//		// add WebView to FXPanel/JFrame
//		Scene scene = new Scene(webView, jframe.getWidth(), jframe.getHeight(), Color.web("#666970"));
//		fxPanel.setScene(scene);
//		jframe.setContentPane(fxPanel);
//		
//		// set special window properties
//		// fxPanel.setFocusable(false);
//		// fxPanel.setFocusTraversalKeysEnabled(true);
//		// fxPanel.requestFocus();
//		// fxPanel.requestFocusInWindow();
//	}
//
//	///////////////////////////////////////////////
//	// Java -> .js
//	///////////////////////////////////////////////
//	
//	public void executeJavascript(String jsStr) {
//		// run calls into WebView on FX thread
//		Platform.runLater(new Runnable(){ public void run() {
////			P.out("executeJavascript:", jsStr);
//			webEngine.executeScript(jsStr);
//		}});
//	}
//	
//	///////////////////////////////////////////////
//	// .js -> Java
//	///////////////////////////////////////////////
//	
//	public void onJsBridgeConnected(String s) {
//		P.out("onJsBridgeConnected!", s);
//	}
//
//	public void onWsMessage(String jsonStr) {
//		P.out("onWsMessage!", jsonStr);
//		if(JsonUtil.isValid(jsonStr)) {
//			JSONObject json = JsonUtil.jsonFromString(jsonStr);
////			if(json.hasKey("click")) click();
////			if(json.hasKey("keyCode")) keyPress(json.getInt("keyCode"));
//			if(json.hasKey("pointerXDelta") && json.hasKey("pointerYDelta")) {
//				cursor.addLocationDelta(json.getInt("pointerXDelta", 0), json.getInt("pointerYDelta", 0));
//			}
//		}
//
//	}
//	
//	public void pointerDelta(int pointerXDelta, int pointerYDelta) {
//		// P.out("pointerDelta!", pointerXDelta, pointerYDelta);
//		// cursor.addLocationDelta(pointerXDelta, pointerYDelta);
//	}
//	
//	public void getData(String s){
//		P.out("Native callback!", s);
//	}
//	
//}
//
