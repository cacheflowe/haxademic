package com.haxademic.demo.hardware.depthcamera.shared;

import java.net.UnknownHostException;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.debug.StringBufferLog;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.hardware.depthcamera.DepthCameraRegion;
import com.haxademic.core.hardware.depthcamera.DepthCameraSize;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingBoolean;
import com.haxademic.core.math.easing.EasingBoolean.IEasingBooleanCallback;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.net.IPAddress;
import com.haxademic.core.net.ISocketClientDelegate;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.net.SocketServer;
import com.haxademic.core.net.SocketServerHandler;
import com.haxademic.core.net.UIControlsHandler;
import com.haxademic.core.net.WebServer;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.data.JSONObject;

public class Demo_DepthCameraRegion_SocketServer
extends PAppletHax
implements IAppStoreListener, ISocketClientDelegate, IEasingBooleanCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// base components
	protected DepthCameraRegion region;
	protected PGraphics regionDebug;		// updated by the `region` object
	protected PGraphics regionFlatDebug;	// updated by the `region` object
	protected PGraphics joystickDebug;		// updated by the `region`
	
	// smoothed output
	protected EasingBoolean userActive;
	protected EasingFloat userX = new EasingFloat(0, 0.1f);
	protected EasingFloat userY = new EasingFloat(0, 0.1f);
	protected EasingFloat userZ = new EasingFloat(0, 0.1f);

	// UI
	protected String CAMERA_debug_flat = "CAMERA_debug_flat";

	
	protected void config() {
		Config.setAppSize(1340, 720);
		Config.setProperty(AppSettings.SHOW_UI, true);
		Config.setProperty(AppSettings.DEPTH_CAM_RGB_ACTIVE, false);
		Config.setProperty(AppSettings.SHOW_UI, true);
		Config.setProperty(AppSettings.SHOW_DEBUG, false);
	}
	
	protected void firstFrame() {
		buildCamera();
		buildSocketServer();
	}

	protected void drawApp() {
		// check depth camera stability
		if (FrameLoop.frameModMinutes(10)) P.out("Still running:", DebugView.uptimeStr());
		int bgColor = (numConnections() == 0) ? 0xff990000 : 0;
		p.background(bgColor);
		updateCamera();
		drawDebug();
	}

	//////////////////////////
	// CAMERA
	//////////////////////////

	protected void buildCamera() {
		// RealSenseWrapper.setSmallStream();
		DepthCamera.instance(DepthCameraType.Realsense);
		
		// add ui sliders to tweak at runtime
		UI.addTitle("DepthCamera Debug");
		UI.addToggle(CAMERA_debug_flat, true, false);

		// build CAMERA region and debug buffer
		regionDebug = PG.newPG(DepthCameraSize.WIDTH, DepthCameraSize.HEIGHT);
		regionFlatDebug = PG.newPG(DepthCameraSize.WIDTH, DepthCameraSize.HEIGHT);
		joystickDebug = PG.newPG(128, 128);
		region = new DepthCameraRegion("cam1", false);
		userActive = new EasingBoolean(false, 20, this);
	}
	
	protected void updateDepthRegion() {
		PGraphics debugPG = UI.valueToggle(CAMERA_debug_flat) ? null : regionDebug;
		// update depth data
		// draw old 3d debug view at the same time if toggled 
		if(debugPG != null) {
			regionDebug.beginDraw();
			regionDebug.background(0);
			region.update(regionDebug);
			regionDebug.endDraw();
		} else {
			region.update();
		}
		// draw newer flat depth data debug view
		// good for overhead views & laying on top of RGB stream
		if(UI.valueToggle(CAMERA_debug_flat)) {
			region.drawDebugFlat(regionFlatDebug);
		}
		// draw x/y coords grid debug view
		DepthCameraRegion.drawDebugCoords(joystickDebug, userX.value(), userY.value(), userActive.value());
	}
	
	protected void updateSmoothedJoystickResults() {
		userActive.target(region.isActive()).update();
		userX.setTarget(region.controlX()).update();
		userY.setTarget(region.controlY()).update();
		userZ.setTarget(region.controlZ()).update();
		DebugView.setValue("userActive", userActive.value());
		DebugView.setValue("userX", userX.value());
		DebugView.setValue("userY", userY.value());
		DebugView.setValue("userZ", userZ.value());

		// send json
		// we're constructing a string instead of JSONObject, since doing so kills the smaller floating-point precision that reduces overall message size
		float userXOut = MathUtil.roundToPrecision(userX.value(), 3);
		float userYOut = MathUtil.roundToPrecision(userY.value(), 3);
		float userZOut = MathUtil.roundToPrecision(userZ.value(), 3);
		String jsonOut = "{\"cmd\":\"position\",\"valueX\":" + userXOut + ",\"valueY\":" + userYOut + ",\"valueZ\":" + userZOut + "}";
		String jsonString = JsonUtil.jsonToSingleLine(jsonOut);
		if(userActive.value() && FrameLoop.frameModLooped(1)) {
			boolean shouldLog = FrameLoop.frameModLooped(30);
			sendJson(jsonString, true);
		}
	}
	
	protected void addDebugTextures() {
		if(DepthCamera.instance().camera.getDepthImage() != null) DebugView.setTexture("depthCamera.getDepthImage", DepthCamera.instance().camera.getDepthImage());
	}
	
	protected void drawCameraDebug() {
		int smallCamW = 1280/2;
		int smallCamH = 720/2;

		// draw bg for small camera views
		p.fill(0);
		p.rect(p.width - smallCamW, 0, smallCamW, p.height);

		// draw Realsense depth data
		p.image(DepthCamera.instance().camera.getDepthImage(), p.width - smallCamW, 0, smallCamW, smallCamH);

		// toggle between debug views
		if(UI.valueToggle(CAMERA_debug_flat)) {
			p.image(regionFlatDebug, p.width - smallCamW, p.height/2, smallCamW, smallCamH);
		} else {
			p.image(regionDebug, p.width - smallCamW, p.height/2, smallCamW, smallCamH);
		}

		// draw joystick x/y debug in upper corner
		// use userY if overhead vs mirror
		p.g.image(joystickDebug, p.width - joystickDebug.width - 20, 20);
	}

	protected void updateCamera() {
		updateDepthRegion();
		updateSmoothedJoystickResults();
		addDebugTextures();
		drawCameraDebug();
	}
	
	/////////////////////////
	// IEasingBooleanCallback methods 
	/////////////////////////

	public void booleanSwitched(EasingBoolean booleanSwitch, boolean value) {
		// send json
		JSONObject jsonOut = new JSONObject();
		jsonOut.setString("cmd", "active");
		jsonOut.setBoolean("active", value);
		String jsonString = JsonUtil.jsonToSingleLine(jsonOut);
		sendJson(jsonString, true);
	}
		
	/////////////////////////
	// WebSocket Server
	/////////////////////////

	protected boolean SOCKET_DEBUG = false;
	protected SocketServerHandler socketServerHandler;
	protected SocketServer socketServer;
	protected String wsServerAddress;
	protected WebServer webServer;
	protected String webServerAddress;
	
	protected StringBufferLog socketLog = new StringBufferLog(30);
	protected StringBufferLog webServerLog = new StringBufferLog(10);
		
	protected void buildSocketServer() {
		// init state
		P.store.addListener(this);
		
		// build screens / objects
		initSocketServer();
		initWebServer();
		
		// extra setup
		addKeyCommandInfo();
	}	
	
	protected void addKeyCommandInfo() {
		// DebugView.setHelpLine(DebugView.TITLE_PREFIX + "Custom Key Commands", "");
		// DebugView.setHelpLine("[b] |", "Launch Socket Test URL");
	}
	
	protected void initWebServer() {
		WebServer.PORT = 8080;
		webServer = new WebServer(new UIControlsHandler(), false);
		webServerAddress = WebServer.getServerAddress();
	}
	
	protected void initSocketServer() {
		try {
			SocketServer.PORT = 3001;
			socketServerHandler = new SocketServerHandler(SocketServer.PORT, this);
			socketServer = new SocketServer(socketServerHandler, SOCKET_DEBUG);
			wsServerAddress = "ws://" + IPAddress.getIP() + ":" + SocketServer.PORT;
			DebugView.setValue("WS Server", wsServerAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace(); 
		}
	}
	
	/////////////////////////////////
	// SERVER DEBUG VIEW
	/////////////////////////////////
	
	protected void drawDebug() {
		// main app canvas context setup
		// p.background(0);

		p.push();

		p.noStroke();
		PG.setDrawCorner(p);

		// MAIN DRAW STEPS:
		drawServerLocation(p.g);
		drawLogs(p.g);
		p.image(pg, 0, 0);
		
		// debug
		if(p.frameCount % 100 == 0) sendHeartbeat();
		
		p.pop();
	}
	
	protected void drawServerLocation(PGraphics pg) {
		if(wsServerAddress != null && webServerAddress != null) {
			String fontFile = DemoAssets.fontOpenSansPath;
			PFont font = FontCacher.getFont(fontFile, 30);
			FontCacher.setFontOnContext(pg, font, p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
			pg.text(wsServerAddress, 20, 10);
			pg.rect(20, 60, 640, 2);
			pg.text(webServerAddress, 20, 460);
			pg.rect(20, 510, 640, 2);
			
			PFont fontSm = FontCacher.getFont(fontFile, 16);
			FontCacher.setFontOnContext(pg, fontSm, p.color(0, 255, 0), 1f, PTextAlign.RIGHT, PTextAlign.TOP);
			pg.text(numConnections() + " connections", 540, 24);
		}
	}

	protected int numConnections() {
		return socketServerHandler.getConnections().size();
	}

	protected void drawLogs(PGraphics pg) {
		socketLog.printToScreen(pg, 20, 80);
		webServerLog.printToScreen(pg, 20, 530);
	}
	
	/////////////////////////////////
	// SOCKET MESSAGING
	/////////////////////////////////	
	
	protected void sendHeartbeat() {
		JSONObject jsonOut = new JSONObject();
		jsonOut.setString("cmd", "heartbeat");
		jsonOut.setInt("count", p.frameCount);
		String jsonString = JsonUtil.jsonToSingleLine(jsonOut);
		sendJson(jsonString, true);
	}

	protected void sendJson(String jsonString, boolean log) {
		socketServer.sendMessage(jsonString);
		if(log) socketLog.update("OUT:     " + jsonString);
	}
	
	/////////////////////////////////
	// APPSTORE LISTENERS
	/////////////////////////////////

	public void updatedNumber(String key, Number val) {}
	public void updatedString(String key, String val) {
		if(key.equals(WebServer.REQUEST_URL)) {
			P.out("WebServer.REQUEST_URL", val);
			webServerLog.update(val);
		}
	}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}
		
	/////////////////////////////////
	// ISocketClientDelegate methods
	/////////////////////////////////
	
	public void messageReceived(String message) {
		socketLog.update("IN:     " + message);
		
		// this check is specifically listening for events from `www/web-socket-demo/`
		/*
		if(message.indexOf("WEB_EVENT") != -1) {
			JSONObject eventData = JSONObject.parse(message);
				String event = eventData.getString("event");	
				String command = eventData.getString("command");	
				DebugUtil.printBig("Incoming WS command: " + event + " / " + command);
		}
		*/
	}

	public void socketConnected(String connection) {
		socketLog.update("CONNECT: " + connection);
	}
	
	public void socketDisconnected(String connection) {
		socketLog.update("DISCONNECT: " + connection);
	}

}
