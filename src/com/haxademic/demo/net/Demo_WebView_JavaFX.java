package com.haxademic.demo.net;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.net.UIControlsHandler;
import com.haxademic.core.net.WebServer;
import com.haxademic.core.net.WebViewWindow;
import com.haxademic.core.net.WebViewWindow.IWebViewDelegate;
import com.haxademic.core.render.FrameLoop;

public class Demo_WebView_JavaFX
extends PAppletHax
implements IWebViewDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// TODO:
	// - Add hooks from AppStore to pass to WebView javascript. Like AppStoreDistributed
	// - Why won't launching work after all webviews are closed?
	// - What's up with JFXPanel from VirtualCursor? Does this help us?
	// Hopeful:
	// - Can we get an image of the current frame of execution? Use a webview as a texture?
	// - WebView - how to support WebGL??
	
	protected WebViewWindow webView;
	protected WebServer webServer;
	protected boolean hasBridge = false;
	
	protected void config() {
	}

	protected void firstFrame() {
		WebViewWindow.UNDECORATED = false;
//		launchWebView("http://www.cacheflowe.com/");
		
		// show off demo for UI
//		UI.addWebInterface(false);
//		WebViewWrapper.launchWebView(WebServer.getServerAddress() + "ui/");
		
		// show off webview .js bridge
		webServer = new WebServer(new UIControlsHandler());
		WebViewWindow.launchWebView(WebServer.getServerAddress() + "webview-demo/", this);

		// test GA demo
//		webServer = new WebServer(new UIControlsHandler());
//		WebViewWindow.launchWebView("http://localhost:" + WebServer.PORT + "/webview-ga-demo/", this);
	}
	
	///////////////////////////////////////////////
	// IWebViewDelegate callbacks
	///////////////////////////////////////////////
	
	public void webViewCreated(WebViewWindow webView) {
		this.webView = webView;
		webView.setWindowTitle("WebViewWindow test title!");
		webView.setSize(200, 200);
		webView.setLocation(10, 10);
		webView.hide();
	}
	
	public void webViewLoaded(WebViewWindow webView) {
		P.out("WEB VIEW LOADED!");
	}
	
	public void webViewBridged(WebViewWindow webView) {
		P.out("WEB VIEW BRIDGED!");
		hasBridge = true;
	}
	
	public void webViewClosed(WebViewWindow webView) {
		P.out("WEB VIEW CLOSED!");
		webView.stop();
	}

	///////////////////////////////////////////////
	// End IWebViewDelegate callbacks
	///////////////////////////////////////////////

	public void keyPressed() {
		super.keyPressed();
//		if(p.key == '1') webView.printSomething();
		if(p.key == '2') webView.executeJavascript("document.body.innerHTML = ''");
		if(p.key == '3') webView.stage().centerOnScreen();
		if(p.key == '4') webView.stage().setWidth(200);
		if(p.key == '5') webView.reload();
		if(p.key == '6') webView.setLocation(100, 100);
		if(p.key == '7') webView.setSize(800, 800);
		if(p.key == '8') webView.setFullscreen(true);
		if(p.key == '9') webView.setFullscreen(false);
		if(p.key == '0') WebViewWindow.launchWebView("http://localhost", this);
		if(p.key == '-') webView.hide();
		if(p.key == '+') webView.loadURL("http://localhost:" + WebServer.PORT + "/webview-ga-demo/");
		if(p.key == '=') webView.loadURL("http://localhost:" + WebServer.PORT + "/webview-ga-demo/no-track.html");
	}
	
	protected void drawApp() {
		P.store.showStoreValuesInDebugView();

		p.background(0);
		PG.setCenterScreen(p);
		
		if(WebViewWindow.numWebViews > 0) {
			// receive streaming number from webview
			float red = P.store.hasNumber("time") ? P.store.getFloat("time") * 30f : 255;
			p.background(0, red % 255, 0);
			
			DebugView.setTexture("webView.getImage()", webView.getImage());
		} else {
			p.background(255,0,0);
		}
		
		// stream calls into the web view if the bridge has been built
		int r = (int) FrameLoop.count(0.45f) % 255;
		int g = (int) FrameLoop.count(0.39f) % 255;
		int b = (int) FrameLoop.count(0.24f) % 255;
		String jsOut = "if(!!window.app) window.app.setBGColor("+r+", "+g+", "+b+")";
		DebugView.setValue("js out", jsOut);
		if(hasBridge) webView.executeJavascript(jsOut);
	}

}

