package com.haxademic.demo.net;

import java.util.UUID;

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
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.net.IPAddress;
import com.haxademic.core.net.ISocketClientDelegate;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.net.QRCode;
import com.haxademic.core.net.SocketClient;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.system.DateUtil;
import com.haxademic.core.system.SystemUtil;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.data.JSONObject;

public class Demo_SocketClient_PlusSixKiosk
extends PAppletHax
implements ISocketClientDelegate, IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// Kiosk URLs, QR, and authentication
	// TODO: Add switches for testing locally
	protected String BASE_URL_WS = "ws://localhost:3001";
	protected String BASE_URL_UI = "http://localhost:3000";
	protected String accId = "e448b1bb-a0db-4db9-90c8-55db9c7ec568";
	protected String accKey = "da71f60a-bacb-4666-aa26-4b7d36d4eed3";
	protected String roomId;
	protected QRCode qr;
	protected boolean touchpadActive;
	
	// WebSocket command constants
	protected final String KEY_CMD = "cmd"; 
	// incoming
	protected final String CMD_TOUCHPAD_CONNECTED = "touchpadConnected"; 
	protected final String CMD_TOUCHPAD_DISCONNECTED = "touchpadDisconnected"; 
	protected final String CMD_TOUCHPAD_INTERACTED = "touchpadInteracted"; 
	protected final String CMD_HEARTBEAT = "heartbeat"; 
	// internal / outgoing
	protected final String CMD_KIOSK_SESSION_UPDATED = "widgetSessionUpdated";
	protected final String CMD_KIOSK_ROOM_IS_CLOSING = "CMD_KIOSK_ROOM_IS_CLOSING";
	// internal state
	protected final String TOUCHPAD_IS_CONNECTED = "TOUCHPAD_IS_CONNECTED"; 
	protected final String ROOM_HAD_TOUCHPAD = "ROOM_HAD_TOUCHPAD"; 


	// KEY FEATURES
	// --------------------------------------------------------------------------
	// - A "kiosk" WebSocket client connects with a an ID & key to be an authenticated host.
	//   Authentication crednetials are stored in ./data/customers.json
	//   We don't want to allow anyone with the ws:// URL to be able to create rooms, So
	//   we make sure that a "kiosk" client lives in a situation that doesn't expose these query params.
	//   In this case, it's a Java app, so unless there's a network sniffer, nobody should see them.
	// - A "kiosk" client is allowed to create temporary rooms. These rooms use a UUID as 
	//   the room ID. The kiosk generates a QR to to allow a "touchpad" client visitor 
	//   into the room. There should only be one "kiosk" and one "touchpad" in a room. If another 
	//   "touchpad" QR scan happens, the previous touchpad client will get booted, FIFO
	// - Each room is only open for a max of 2 minutes, then is shut as the Kiosk leaves 
	//   (which triggers a room cleanup on the server side). The Kiosk reconnects 
	//   with a new room ID, creating another temporary room. When a "touchpad" client connects, the 
	//   two-minute max timeout is reset.
	// - As rooms are opened and closed by the kiosk, it generates a QR code to the
	//   UI web app, which receives the room ID as a query param. This is "totally
	//   secure", since the room ID will go away as soon as the kiosk recycles the room
	// - A client will scan the QR code, launching the web app, which connects to the 
	//   ws:// server with the given room ID. As it connects, the kiosk is sent a json
	//   message letting it know the session has started. This triggers a session timeout, so 
	//   clients can't walk off and keep controlling the experience
	// - After a session timeout, the kiosk recycles the room (and QR code), requiring an updated QR code
	//   scan to join a new valid, active room
	// - If a client manually exits the room (by closing/refreshing a tab), this will also 
	//   trigger a room recycling, freeing up the kiosk to recycle the room for another user
	// - While a touchpad session is active, any user interaction should keep the session alive
	//   by sending an event `touchpadInteracted`. This allows for a 30-second user interaction timeout
	//   that continues to reset, but a max session length of two minutes if the touchpad user
	//   keeps interacting
	
	// TODO
	// - Web UI: Add timeout progress indicator
	// - Web UI: Add session timeout view
	// - Kiosk bug - don't restart timer if user refreshes the page!!!
	// - Turn PlusSix into core object
	// - Test Node code on AWS server
	// - Hide the QR code if the kiosk isn't connected to the ws:// server
	
	// WebSockets & QR code config
	protected SocketClient socketClient;
	protected boolean isServerLocalhost = false;
	protected String serverAddress;
	protected String uiAddress;
	protected StringBufferLog socketLog = new StringBufferLog(30);
	
	// Room/session config
	protected int roomRecycleInterval =      	60 * 1000;
	protected int sessionUserTimeout =          30 * 1000;
	protected int sessionMaxLength =            120 * 1000;
	protected int sessionWarningTime =          10 * 1000;
	protected int sessionClosingTime =          3 * 1000;

	// session state & timers
	protected boolean sessionTimeoutWarningFrame = false;
	protected boolean sessionTimeoutWarning = false;
	protected boolean sessionIsClosingFrame = false;
	protected boolean sessionIsClosing = false;
	protected int sessionAbsStartTime = 0;
	protected int sessionAbsEndTime = 0;
	protected int sessionAbsCurTime = 0;
	protected int sessionAbsCurTimeDown = 0;
	protected int lastSessionBroadcastTime = 0;
	protected int sessionWindowStartTime = 0;
	protected int sessionWindowEndTime = 0;
	protected int sessionWindowCurDuration = 0;
	protected int sessionTimeLeft = 0;
	protected float sessionWindowProgress = 0;
	protected float sessionAbsProgress = 0;
	protected int sessionWindowCurTime = 0;
	

	
	protected void config() {
		Config.setAppSize(1280, 720);
		Config.setProperty(AppSettings.APP_NAME, "SocketClient");
	}
	
	protected void firstFrame() {
		newSocketRoom();
	}
	
	protected void drawApp() {
		background(0);
		updateKioskPre();
		p.image(pg, 0, 0);
	}
	
	protected void updateKioskPre() {
		// update kioskstate
		updateSessionState();
		drawKioskDebug();
		
		// send a simple message to clients
		// if the touchpad is active, the session timer messages server as a heartbeat
		if(touchpadIsActive() == false && FrameLoop.frameModSeconds(3)) sendHeartBeat(); 
		
		// test shutting down & recreating the socket client
		if(KeyboardState.keyTriggered(' ')) newSocketRoom();
		if(KeyboardState.keyTriggered('l')) SystemUtil.openWebPage(uiAddress);
	}
	
	protected void newSocketRoom() {
		// close old room
		if(socketClient != null) socketClient.disconnect();
		
		// set initial state
		P.store.setBoolean(TOUCHPAD_IS_CONNECTED, false);
		P.store.setBoolean(ROOM_HAD_TOUCHPAD, false);
		
		// works with PlusSix socket server w/authentication and auto-cycling QR codes & room IDs 
		// create new room ID and reset session timeouts
		roomId = UUID.randomUUID().toString();
		sessionNewRoom();
		
		// replace localhost with IP address
		if(BASE_URL_WS.contains("localhost")) BASE_URL_WS = BASE_URL_WS.replace("localhost", IPAddress.getIP());
		if(BASE_URL_UI.contains("localhost")) BASE_URL_UI = BASE_URL_UI.replace("localhost", IPAddress.getIP());
		
		// build WebSocket address for the kiosk to create a new room
		serverAddress = BASE_URL_WS + "/ws?roomId="+roomId+"&clientType=kiosk&accountId="+accId+"&accountKey="+accKey;
		socketClient = new SocketClient(serverAddress, this, true);
		
		// build QR code
		if(qr == null) qr = new QRCode();
		uiAddress = BASE_URL_UI + "?t="+DateUtil.epochTime()+"#roomId="+roomId+"&debug=true";
		qr.updateQRCode(uiAddress, 256, 256, p.color(0), p.color( 0, 255, 0));
		
		// log addresses for testing
		P.out(serverAddress);
		P.out(uiAddress);
	}
	
	protected void drawKioskDebug() {
		pg.beginDraw();
		pg.background(0);
		
		// header addresses
		String fontFile = DemoAssets.fontOpenSansPath;
		PFont font = FontCacher.getFont(fontFile, 30);
		PFont fontSm = FontCacher.getFont(fontFile, 20);
		FontCacher.setFontOnContext(pg, font, p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		pg.text(serverAddress, 20, 10);
		pg.text(uiAddress, 20, 50);
		pg.rect(20, 100, pg.width - 40, 2);
		
		// socket message event log
		socketLog.printToScreen(pg, 20, 120);

		// create background for session logging
		pg.push();
		pg.fill(0, 180);
		pg.rect(pg.width - 500, 110, 500, 600);
		pg.pop();
		
		// show session info
//		if(touchpadIsActive()) {
			FontCacher.setFontOnContext(pg, fontSm, p.color(255, 255, 0), 1f, PTextAlign.RIGHT, PTextAlign.TOP);
			pg.text(
					"sessionAbsStartTime: " + msToS(sessionAbsStartTime) + FileUtil.NEWLINE + 
					"sessionAbsEndTime: " + msToS(sessionAbsEndTime) + FileUtil.NEWLINE + 
					"sessionAbsCurTime: " + DateUtil.timeFromMilliseconds(sessionAbsCurTime, false) + FileUtil.NEWLINE + 
					"sessionAbsCurTimeDown: " + DateUtil.timeFromMilliseconds(sessionAbsCurTimeDown, false) + FileUtil.NEWLINE + 
					"lastSessionBroadcastTime: " + msToS(lastSessionBroadcastTime) + FileUtil.NEWLINE + 
					"sessionTimeoutStartTime: " + msToS(sessionWindowStartTime) + FileUtil.NEWLINE + 
					"sessionTimeoutEndTime: " + msToS(sessionWindowEndTime) + FileUtil.NEWLINE + 
					"sessionTimeLeft: " + DateUtil.timeFromMilliseconds(sessionTimeLeft, false) + FileUtil.NEWLINE + 
					"sessionProgress: " + MathUtil.roundToPrecision(sessionWindowProgress, 2) + FileUtil.NEWLINE +
					"sessionAbsProgress: " + MathUtil.roundToPrecision(sessionAbsProgress, 2) + FileUtil.NEWLINE +
					"sessionIsClosing: " + sessionIsClosing + FileUtil.NEWLINE +
					"sessionDuration: " + DateUtil.timeFromMilliseconds(sessionWindowCurDuration, false) + FileUtil.NEWLINE + 
					"sessionCurDuration: " + DateUtil.timeFromMilliseconds(sessionWindowCurTime, false) + FileUtil.NEWLINE + 
//					"sessionCurDuration: " + DateUtil.timeFromMilliseconds(sessionCurDuration, false) + FileUtil.NEWLINE + 
					""
					, pg.width - 20, 170);
//		}
		
		// websockets connected label
		if(socketClient.isConnected()) {
			FontCacher.setFontOnContext(pg, fontSm, p.color(0, 255, 0), 1f, PTextAlign.RIGHT, PTextAlign.TOP);
			pg.text("WebSocket Connected", pg.width - 20, 110);
		} else {
			FontCacher.setFontOnContext(pg, fontSm, p.color(255, 0, 0), 1f, PTextAlign.RIGHT, PTextAlign.TOP);
			pg.text("WebSocket Disconnected", pg.width - 20, 110);
		}
		
		// touchpad connected label
		if(touchpadIsActive()) {
			FontCacher.setFontOnContext(pg, fontSm, p.color(0, 255, 0), 1f, PTextAlign.RIGHT, PTextAlign.TOP);
			pg.text("Touchpad Connected", pg.width - 20, 140);
		} else {
			FontCacher.setFontOnContext(pg, fontSm, p.color(255, 0, 0), 1f, PTextAlign.RIGHT, PTextAlign.TOP);
			pg.text("Touchpad Disconnected", pg.width - 20, 140);
		}
		
		// show session progress bar
		pg.push();
		int barColor = 0xff555555;
		if(touchpadIsActive()) barColor = 0xff00bb00;
		if(sessionTimeoutWarning) barColor = 0xffffff00;
		if(sessionIsClosing) barColor = 0xffff0000;
		PG.drawProgressBar(pg, pg.width - 220, pg.height - 290, 184, 30, 0xffffffff, 0xff000000, barColor, sessionWindowProgress);
		pg.pop();
		
		// draw qr code (with icon in center to test)
		pg.push();
		PG.setPImageAlpha(pg, (touchpadIsActive()) ? 0.2f : 1f);
		pg.translate(pg.width - qr.image().width, pg.height - qr.image().height);
		pg.image(qr.image(), 0, 0);
		// icon
		pg.noStroke();
		pg.rect(qr.image().width/2 - 16, qr.image().height/2 - 16, 32, 32);
		pg.image(DemoAssets.smallTexture(), qr.image().width/2 - 16, qr.image().height/2 - 16, 32, 32);
		pg.pop();
		
		pg.endDraw();
	}
	
	protected int msToS(float ms) {
		return P.round(ms / 1000f);
	}

	protected boolean touchpadIsActive() {
		return P.store.getBoolean(TOUCHPAD_IS_CONNECTED);
	}
	
	protected void sendHeartBeat() {
		// Helps ensure the WebSocket connection stays alive. 
		// Depending on the platform, they can be shut down if there's not any activity
	    JSONObject jsonOut = new JSONObject();
	    jsonOut.setString(KEY_CMD, CMD_HEARTBEAT);
	    jsonOut.setInt("frameCount", P.round(FrameLoop.count()));
	    sendJSON(jsonOut);
	}
	
	protected void sendJSON(JSONObject jsonData) {
		String jsonString = JsonUtil.jsonToSingleLine(jsonData);
		socketClient.sendMessage(jsonString);
		socketLog.update("OUT: " + jsonString);
	}
	
	////////////////////////////////////////////
	// ISocketClientDelegate methods
	////////////////////////////////////////////

	public void messageReceived(String message) {
		socketLog.update("IN:     " + message);
		boolean isValidJson = JsonUtil.isValid(message);
		if(isValidJson) {
			JSONObject jsonData = JsonUtil.jsonFromString(message);
			if(jsonData.hasKey(KEY_CMD)) {
				String cmd = jsonData.getString(KEY_CMD);
				DebugView.setValue("CMD", cmd);
				// perform actions based on cmd
				if(cmd.equals(CMD_TOUCHPAD_CONNECTED))    touchpadConnected();
				if(cmd.equals(CMD_TOUCHPAD_DISCONNECTED)) newSocketRoom();
				if(cmd.equals(CMD_TOUCHPAD_INTERACTED))   resetUserInteractionTimeout();
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
	// PlusSix session logic
	////////////////////////////////////////////

	protected void touchpadConnected() {
		P.store.setBoolean(TOUCHPAD_IS_CONNECTED, true);
		if(P.store.getBoolean(ROOM_HAD_TOUCHPAD) == false) {
			touchpadSessionStarted();	// don't restart session if a user refreshes and continues a session they already started
		}
	    updateSessionState();
	    P.store.setBoolean(ROOM_HAD_TOUCHPAD, true);
	}
	
	protected void sessionNewRoom() {
		P.store.setBoolean(ROOM_HAD_TOUCHPAD, false);
		int now = P.p.millis();
		lastSessionBroadcastTime = now;
		sessionWindowStartTime = now;
		sessionWindowEndTime = sessionWindowStartTime + roomRecycleInterval;
		sessionWindowCurDuration = roomRecycleInterval;
		sessionTimeLeft = roomRecycleInterval;
	}

	protected void touchpadSessionStarted() {
		int now = P.p.millis();
		sessionAbsStartTime = now;
		sessionAbsEndTime = now + sessionMaxLength;
		resetUserInteractionTimeout();
	}

	protected void resetUserInteractionTimeout() {
		int now = P.p.millis();
		if(now < sessionAbsEndTime - sessionUserTimeout) {
			sessionWindowStartTime = now;
			sessionWindowEndTime = sessionWindowStartTime + sessionUserTimeout;
			sessionWindowCurDuration = sessionUserTimeout;
			sessionTimeLeft = sessionUserTimeout;
		}
	}

	protected void updateSessionState() {
		// how long has the room been open?
		int now = P.p.millis();
		
		// total max session length timer
		sessionAbsCurTime = now - sessionAbsStartTime;
		sessionAbsCurTimeDown = sessionAbsEndTime - now;
		sessionAbsProgress = (float) (sessionAbsCurTime) / (sessionAbsEndTime - sessionAbsStartTime);

		// user timeout session time
		sessionWindowCurTime = now - sessionWindowStartTime;
		sessionWindowProgress = (float) sessionWindowCurTime / sessionWindowCurDuration;
		int prevSessionTimeLeft = sessionTimeLeft;
		sessionTimeLeft = sessionWindowEndTime - now;
		
		// send state to touchpad client
		if(touchpadIsActive()) {
			broadcastSessionClock();
		}

		// have we crossed the warning threshold?
		sessionTimeoutWarningFrame = (prevSessionTimeLeft >= sessionWarningTime && sessionTimeLeft < sessionWarningTime);
		sessionTimeoutWarning = (sessionTimeLeft < sessionWarningTime);
		// are we about to close the room?
		sessionIsClosingFrame = (prevSessionTimeLeft >= sessionClosingTime && sessionTimeLeft < sessionClosingTime);
		sessionIsClosing = (sessionTimeLeft < sessionClosingTime);

		// when time's up, move to a new room
		if(sessionWindowProgress >= 1) {
			newSocketRoom();
		}
	}

	protected void broadcastSessionClock() {
		// only emit session clock once per second
		if(P.p.millis() < lastSessionBroadcastTime + 1000) return;
		lastSessionBroadcastTime = P.p.millis();
		
		// send session state to touchpad client
	    JSONObject jsonOut = new JSONObject();
	    jsonOut.setString(KEY_CMD, CMD_KIOSK_SESSION_UPDATED);
	    jsonOut.setInt("sessionCurDuration", sessionWindowCurTime);
	    jsonOut.setInt("sessionTimeoutStartTime", sessionWindowStartTime);
	    jsonOut.setInt("sessionTimeoutEndTime", sessionWindowEndTime);
	    jsonOut.setInt("sessionWindowCurDuration", sessionWindowCurDuration);
	    jsonOut.setInt("sessionTimeLeft", sessionTimeLeft);
	    jsonOut.setFloat("sessionWindowProgress", sessionWindowProgress);
	    jsonOut.setFloat("sessionAbsProgress", sessionAbsProgress);
	    jsonOut.setInt("sessionAbsEndTime", sessionAbsEndTime);
	    jsonOut.setInt("sessionAbsStartTime", sessionAbsStartTime);
	    jsonOut.setFloat("sessionAbsCurTime", sessionAbsCurTime);
	    sendJSON(jsonOut);
	}

	///////////////////////////////////////////
	// IAppStoreListener callbacks
	///////////////////////////////////////////
	
	public void updatedNumber(String key, Number val) {}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}
	
	/*
  

  socketClose(e) {
    this.triggerWidgetDisconnected();
  }

  socketError(e) {
    this.triggerWidgetDisconnected();
  }

  triggerWidgetDisconnected() {
    // socket is disconnected
    this.emit('widgetDisconnected', {});
    this.touchpadDisconnected({});  // normally this comes from the server
    delete this.heartbeatTime;

    // // send as JSON so the native side picks it up. this should hide the cursor
    // this.sendCmdToNative({
    //   "cmd": "widgetDisconnected",
    // });
  }

	  
  /////////////////////////////////////
  // QR CODE & Room re-creation
  /////////////////////////////////////


  qrCodeRegenerate() {
    // make sure touchpad is disconnected
    this.triggerWidgetDisconnected();
    this.touchpadIsActive = false;
    // send message to kill old room (this only happens after the 2nd call to this function, since the first time is the first room)
    if(this.roomId) this.sendJSON('closeRoom', {roomId: this.roomId});
    // create new room
    this.roomId = uuid.v4();
    this.touchpadURL = `${PlusSixURLs.TOUCHPAD_URL_BASE}?t=${Date.now()}#roomId=${this.roomId}&debug=true`;
    if(!!window.location.href.match(/-src/)) this.touchpadURL = this.touchpadURL.replace('/touchpad', '/touchpad-src'); // point to dev touchpad if we're widget dev
    this.webSocketURL = `${PlusSixURLs.WS_SERVER_URL}?roomId=${this.roomId}&clientType=kiosk&accountId=${this.accountId}&accountKey=${this.accountKey}`;
    if(this.solidSocket) this.solidSocket.setURL(this.webSocketURL);
    this.generateNewQrImage();
    this.qrCodeEl.setAttribute('href', this.touchpadURL);
    // restart timer
    this.sessionNewRoom();
    // emit
    this.emit('widgetQrRegenerated', {touchpadURL: this.touchpadURL});
  }
	  
	*/
}
