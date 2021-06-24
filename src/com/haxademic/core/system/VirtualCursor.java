package com.haxademic.core.system;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.cage.zxing4p3.ZXING4P;
import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.debug.StringBufferLog;
import com.haxademic.core.draw.image.Base64Image;
import com.haxademic.core.hardware.keyboard.Keyboard;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.net.IPAddress;
import com.haxademic.core.net.ISocketClientDelegate;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.net.SocketClient;
import com.haxademic.core.net.SocketServer;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import processing.awt.PSurfaceAWT;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONObject;

public class VirtualCursor
implements ISocketClientDelegate {

	// cursor window
	public float windowScale;
	public VirtualCursorWindow cursorWindow = null;
	public QrWidgetWindow qrWindow = null;

	// socket client
	protected SocketClient socketClient;
	protected boolean isServerLocalhost = true;
	protected String serverAddress;
	protected StringBufferLog socketLog = new StringBufferLog(30);

	// qr code
	protected PImage qrImage;


	public VirtualCursor() {
		cursorWindow = new VirtualCursorWindow();
//		qrWindow = new QrWidgetWindow();
//		initSocketClient();
//		initQR();
//		P.p.registerMethod("post", this);	 // update texture to 2nd window after main draw() execution
	}

	protected void initQR() {
		String uiAddress = "http://" + IPAddress.getIP() + ":3333/haxademic.js/demo/#solid-socket-touchpad";
		ZXING4P qr = new ZXING4P();
		qrImage = qr.generateQRCode(uiAddress, 128, 128);
		DebugView.setTexture("qrImage", qrImage);
	}

	protected void initSocketClient() {
		serverAddress = (isServerLocalhost) ?
				"ws://" + IPAddress.getIP() + ":" + SocketServer.PORT + "?roomId=987654321" :
				"ws://192.168.1.3:3001?roomId=987654321";
		socketClient = new SocketClient(serverAddress, this, true);
	}

	public void drawLog(PGraphics pg) {
		socketLog.printToScreen(pg, 30, 30);
	}

	public void post() {
		//		if(cursorWindow == null && P.p.frameCount >= 10) 
		//		if(active == true && cursorWindow != null) {
		////			cursorWindow.setLocation();
		//		}
		DebugView.setValue("socketClient.isConnected()", socketClient.isConnected());
	}

	////////////////////////////////////////////
	// ISocketClientDelegate methods
	////////////////////////////////////////////

	public void messageReceived(String message) {
		socketLog.update("IN:     " + message);
		if(JsonUtil.isValid(message)) {
			JSONObject json = JsonUtil.jsonFromString(message);
			if(json.hasKey("click")) click();
			if(json.hasKey("keyCode")) keyPress(json.getInt("keyCode"));
			if(json.hasKey("pointerX") && json.hasKey("pointerY")) {
				setLocation(
					P.round(json.getFloat("pointerX", 0.5f) * P.p.displayWidth),
					P.round(json.getFloat("pointerY", 0.5f)* P.p.displayHeight)
				);
			}
		}
	}

	public void socketConnected(String connection) {
		socketLog.update("CONNECT: " + connection);
	}

	public void socketDisconnected(String connection) {
		socketLog.update("DISCONNECT: " + connection);
	}

	////////////////////////////////////////////
	// API mask
	////////////////////////////////////////////

	public void setLocation(int x, int y) {
		if(cursorWindow == null) return;
		DebugView.setValue("cursorLocation.x", x);
		DebugView.setValue("cursorLocation.y", y);
		cursorWindow.setLocation(x, y);
	}

	public void addLocationDelta(int x, int y) {
		if(cursorWindow == null) return;
//		DebugView.setValue("delta.x", x);
//		DebugView.setValue("delta.y", y);
		cursorWindow.addLocationDelta(x, y);
	}
	
	public void click() {
		Mouse.mouseClickAt((int) cursorWindow.cursorLocation.x, (int) cursorWindow.cursorLocation.y);
	}

	public void executeJavascript(String jsStr) {
		qrWindow.executeJavascript(jsStr);
	}

	public void keyPress(int keyCode) {
		Keyboard.keyPress(keyCode);
	}

	////////////////////////////////////////////
	// Pointer window class
	////////////////////////////////////////////

	public class VirtualCursorWindow 
	extends PApplet {

		protected PImage cursor;
		protected PGraphics pg;
		protected JFrame jframe;
		protected JPanel panel;
		protected PApplet applet = this;
		protected PVector cursorLocation = new PVector();
		protected PVector cursorTarget = new PVector();

		public VirtualCursorWindow() {
			runSketch(new String[] {"VirtualCursorWindow"}, this);
		}

		public void settings() {
			super.settings();
			size(128, 128, PRenderers.JAVA2D);
		}

		public void setup() {
			super.setup();
			// set special window properties
			jframe = (JFrame)((PSurfaceAWT.SmoothCanvas) getSurface().getNative()).getFrame();
			jframe.removeNotify();
			jframe.setUndecorated(true);
			jframe.setLayout(null);
			jframe.addNotify();
			jframe.setAlwaysOnTop(true);

			pg = createGraphics(width, height);
			cursor = Base64Image.decodePImageFromBase64("iVBORw0KGgoAAAANSUhEUgAAAGoAAAC1CAMAAACAl5pfAAAAYFBMVEVHcEyRkZEQEBAAAAB1dXVISEgmJib///8cHBwGBgYoKCj9/f3////T09O8vLzl5eX19fVubm4yMjJCQkJXV1eJiYmhoaEvLy+YmJjY2Ng6Ojqurq7Hx8f19fXo6OheXl4dpwGJAAAAIHRSTlMAbe//ibfYBOP5//7/7u3x9vP9+fbv7s/kJcVRNwoVoJWYs8AAAAPzSURBVHgBvM2FiURRAASw+679t3uGDSwamJcG8jXUNC/LvI6YtuXfftSnaVlGXecy7LqWvPpVXv0qr36VV7vKq1/l1a7yald5tau82lVe7SqvdpVXuypcUd3P3b2iettXVO0rq/KVVfnKqnxlVb6yKl9Zla+syldW5Sur8pVV+cqqfGVVvrIqX58VX17xBRVfUNkFFVxQwQUVXFDBpRVcWsGlFVxawcWVX1755ZVfXvkFFV9Q8QUVX1DxBRVfUPEFFV9Q8QUVX1DRBRVcUMEFFVxQwQUVXFDBBRVfUPEFFV9Q8QUVX1DxBRVfUPEFFV9Q8QUVX1DxBRVfUPEFFV9Q8QUVX1DxBRVfUPEFFV9Q8QUVX1DxBRVfUPEFFV9Q8WWVX1799HIeWo6DMBTF3cBx7/3/v3L7xsqGvMXImXd62gWPuLKmZXmWFUVZam1kBc8sBqpSj3Urkrpu2raquq6Xryw3VPsA4VAWf+y2zOCIGoPr8dxQlQMqdUPl6jrKByiUL0S111GRI6q8vq3pvyiddxm+gvMQx2nq+1LK9yC5gGI/z6pSGSqMVTxl26d1WRbPC8PjiKL550riY0e2oFZQJdjWLOyCUdXfD6xNT/3NdgOqO5WKthXyUT352rcvz9bkxHBRVAmGhRTn0wsTlVFSoHpwBWMGiiza6vpO11FAB6oA24rcUdrQ+2ogQplcRwFzK9SLQ2eUMqGqf0mk3lNXFCWhbWVUhE6ompKiE5XzREhQZpJITxYwV7BfQRESXawXWInwuI5qKGmgrzCJsDnr/TKqVS+32sf7K6jJSfcuoipKSn83ot1OhOk1VEdJ/t+WN4Bt5UiEANU/kR41tTiKEKBySpJkkT4QIbmn3qxR4zuSCM9taYYIZUBjFs0WPFIxGr8EpEciTuMHKMN7pvci1KQwBmsUOI1IhKOtCKUFiSdCgArxyxrU+O1QeGV8EVIUPPNEhJ2rCKUFCYgQTEAAhct1PVEZbvwQhe6IsQjxBARQaQJQSIR4AjKgfKjmRHInIJ82KBS+CA/SNnAm7gS0+YSEE3MnoP3XJ6SAdGPjX8NwFTbx7USYCGawCOnsFzIQQISg3nmZGRMQCEOEw71/nl3jCYgf68bPT8KcgHDsRQgaP4iLCDVo/CBuIgQTEIiLCDUUoUP4ExCOXePXmi9C/gT0UREu94ow/zoRKnAFPytC3SoV3CNCPAFlNeHgW2Ve4+/Ihnhywo2/bAiIJww8AeVPHND2mSLsKhOI6oIvQpx0ETclxKB5FbdlAxx57OLORO9AsSduzmTeUAS6L1uEJH6YfPDfEJEMi/hQEskvBYd6T28tBWCnaBUfT+j/LIVNWOY7PppBCtOtd0oAAAAASUVORK5CYII=");

			JPanel panel = new JPanel() {
				private static final long serialVersionUID = 1L;

				@Override
				protected void paintComponent(Graphics graphics) {
					if (graphics instanceof Graphics2D) {
						Graphics2D g2d = (Graphics2D) graphics;
						g2d.drawImage(pg.image, 0, 0, null);
					}
				}
			};

			jframe.setContentPane(panel);
			panel.setFocusable(true);
			panel.setFocusTraversalKeysEnabled(false);
			panel.requestFocus();
			panel.requestFocusInWindow();
		}

		public void draw() {
			// update dynamic cursor texture
			pg.beginDraw();
			pg.background(0, 0);
			pg.scale(0.25f);
			pg.image(cursor, 0, 0);
			pg.endDraw();

			// calculate next location
			cursorLocation.lerp(cursorTarget, 0.3f);

			// update jframe
			if(frameCount < 100) {
				jframe.setBackground(new java.awt.Color(0, 0, 0, 0));
				jframe.repaint();
			}
			jframe.setLocation((int)cursorLocation.x, (int) cursorLocation.y);
		}

		public void setLocation(int x, int y) {
			cursorTarget.set(x, y);
		}

		public void addLocationDelta(int x, int y) {
			cursorTarget.set(cursorTarget.x + x, cursorTarget.y + y);
		}
		
		public PVector cursorLocation() {
			return cursorLocation;
		}

	}

	////////////////////////////////////////////
	// QR window class
	////////////////////////////////////////////

	public class QrWidgetWindow extends PApplet {

		protected PApplet applet = this;
		protected JFXPanel fxPanel;
		protected PGraphics pg;
		protected JFrame jframe;
		protected Browser browser;

		public QrWidgetWindow() {
			runSketch(new String[] {"QrWidgetWindow"}, this);
		}

		public void settings() {
			super.settings();
			size(750, 900, PRenderers.JAVA2D);
		}

		public void setup() {
			super.setup();

			fxPanel = new JFXPanel();

			jframe = (JFrame)((PSurfaceAWT.SmoothCanvas) getSurface().getNative()).getFrame();
			jframe.removeNotify();
			jframe.setUndecorated(true);
			jframe.setLayout(null);
			jframe.addNotify();
			jframe.setAlwaysOnTop(true);
			jframe.add(fxPanel);
			jframe.setSize(750, 900);
			jframe.setVisible(true);
			jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					initFX(fxPanel);
				}
			});
			// set special window properties
			jframe.setContentPane(fxPanel);
			fxPanel.setFocusable(false);
			fxPanel.setFocusTraversalKeysEnabled(false);
			fxPanel.requestFocus();
			fxPanel.requestFocusInWindow();
		}

		private void initFX(JFXPanel fxPanel) {
			// This method is invoked on the JavaFX thread
			browser = new Browser(jframe.getWidth(), jframe.getHeight());
			Scene scene = new Scene(browser, jframe.getWidth(), jframe.getHeight(), Color.web("#666970"));
			fxPanel.setScene(scene);
		}

		public void draw() {
			// update jframe
			jframe.setBackground(new java.awt.Color(0, 0, 0, 0));
			jframe.repaint();
			jframe.setLocation(100, 100);
		}

		public void setLocation(int x, int y) {
			jframe.setLocation(x, y);
		}

		public void executeJavascript(String jsStr) {
			Platform.runLater(new Runnable(){public void run() {
				P.out("executeJavascript:", jsStr);
				browser.webEngine.executeScript(jsStr);
			}});
		}
	}

	public class Browser extends Region {

		public WebView webView;
		public WebEngine webEngine;
		public Browser thiss;
		public int w;
		public int h;

		public Browser(int w, int h) {
			this.w = w;
			this.h = h;
			thiss = this;
			webView = new WebView();
			webEngine = webView.getEngine();
			getChildren().add(webView);
			//apply the styles
//			getStyleClass().add("browser");
			// load the web page
			
			// load page & attach js bridge to call up to java
			P.println(webEngine.getUserAgent());
			webEngine.load("http://192.168.1.3:3333/touchless/sdk/javascript/#mode=kiosk&customerId=hovercraft&deviceId=1234567890");

			P.out("Adding js callback!");
			JSObject win = (JSObject) webEngine.executeScript("window");
			win.setMember("native", thiss);
		}
		
	    public void getData(String s){
	        P.out("Native callback!", s);
	    }

		@Override protected void layoutChildren() {
			double w = getWidth();
			double h = getHeight();
			layoutInArea(webView,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
		}

		@Override protected double computePrefWidth(double height) {
			return height;
		}

		@Override protected double computePrefHeight(double width) {
			return width;
		}
	}
	
}
