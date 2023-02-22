package com.haxademic.core.net;

import java.util.UUID;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.data.store.AppStoreDistributed;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.debug.StringBufferLog;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.system.DateUtil;
import com.haxademic.core.system.SystemUtil;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.data.JSONObject;

public class PlusSixKiosk
implements ISocketClientDelegate, IAppStoreListener {

	// Kiosk URLs, QR, and authentication
	protected String urlWs;
	protected String urlUi;
	protected String accId;
	protected String accKey;
	
	protected int qrFgColor = 0xff000000;
	protected int qrBgColor = 0xffffffff;
	
	// internal
	protected JSONObject jsonConfig;
	protected String systemChannelId = "system";
	protected String roomId;
	protected QRCode qr;
	protected boolean touchpadActive;
	
	// WebSocket command constants
	public static final String KEY_CMD = "cmd"; 
	// incoming
	public static final String CMD_TOUCHPAD_CONNECTED = "touchpadConnected"; 
	public static final String CMD_TOUCHPAD_DISCONNECTED = "touchpadDisconnected"; 
	public static final String CMD_TOUCHPAD_INTERACTED = "touchpadInteracted"; 
	public static final String CMD_TOUCHPAD_CUSTOM = "touchpadCustom"; 
	// internal / outgoing
	public static final String CMD_HEARTBEAT = "heartbeat"; 
	public static final String CMD_KIOSK_SESSION_UPDATED = "kioskSessionUpdated";
	public static final String CMD_KIOSK_SESSION_CONFIG = "kioskSessionConfig";
	// internal AppStore state
	public static final String TOUCHPAD_IS_CONNECTED = "TOUCHPAD_IS_CONNECTED"; 
	public static final String ROOM_HAD_TOUCHPAD = "ROOM_HAD_TOUCHPAD"; 
	public static final String INCOMING_JSON_DATA = "INCOMING_JSON_DATA"; 
	public static final String INCOMING_JSON_CMD = "INCOMING_JSON_CMD"; 


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
	protected boolean isTimeoutWarning = false;
	protected boolean sessionIsClosingFrame = false;
	protected boolean sessionIsClosing = false;
	protected int sessionStartTime = 0;
	protected int sessionEndTime = 0;
	protected int sessionElapsedTime = 0;
	protected int sessionMaxTimeCountdown = 0;
	protected int lastSessionBroadcastTime = 0;
	protected int sessionWindowStartTime = 0;
	protected int sessionWindowEndTime = 0;
	protected int sessionWindowCurDuration = 0;
	protected int activityTimeoutCountdown = 0;
	protected float activityTimeoutProgress = 0;
	protected float sessionProgress = 0;
	protected int activityWindowCurTime = 0;
	protected int lastTimeoutWarning = 0;
	protected int timeSinceLastWarning = 0;
	
	
	public PlusSixKiosk(String accId, String accKey, String urlWs, String urlUi) {
		this.accId = accId;
		this.accKey = accKey;
		this.urlWs = urlWs;
		this.urlUi = urlUi;
		replaceLocalhostWithIp();
		
		P.store.addListener(this);
		P.store.setBoolean(ROOM_HAD_TOUCHPAD, false);
		P.store.setBoolean(TOUCHPAD_IS_CONNECTED, false);
				
		// build config object. outside objects can add properties that will be sent to the touchpad on connection
		jsonConfig = new JSONObject();
		jsonConfig.setString(KEY_CMD, CMD_KIOSK_SESSION_CONFIG);
				
		// init first cycling socket connection as kiosk host
		newSocketRoom();
	}
	
	protected void replaceLocalhostWithIp() {
		if(urlWs.contains("localhost")) urlWs = urlWs.replace("localhost", IPAddress.getIP());
		if(urlUi.contains("localhost")) urlUi = urlUi.replace("localhost", IPAddress.getIP());
	}
	
	public void setColors(int bg, int fg) {
		qrBgColor = bg;
		qrFgColor = fg;
		newSocketRoom(); // needs to regenerate QR code 
	}

	public void setRoomRecycleIntervalSeconds(int seconds) { roomRecycleInterval = seconds * 1000; }
	public void setSessionUserTimeoutSeconds(int seconds) { sessionUserTimeout = seconds * 1000; }
	public void setSessionMaxLengthSeconds(int seconds) { sessionMaxLength = seconds * 1000; }
	public void setSessionWarningTimeSeconds(int seconds) { sessionWarningTime = seconds * 1000; }
	public void setSessionClosingTimeSeconds(int seconds) { sessionClosingTime = seconds * 1000; }
	
	public JSONObject getSessionConfigJson() {
		return jsonConfig;
	}
	
	public boolean touchpadIsActive() {
		return P.store.getBoolean(TOUCHPAD_IS_CONNECTED);
	}
	
	public boolean socketIsConnected() {
		return socketClient.isConnected();
	}
	
	public PImage qrImage() {
		return qr.image();
	}
	
	public void launchWebBrowser() {
		SystemUtil.openWebPage(uiAddress);
	}
	
	public String touchpadAddress() {
		return uiAddress;
	}
	
	public void update() {
		// update kiosk state
		// should happen in `pre` since we're drawing
		updateSessionState();
		
		// send a simple message to clients
		// if the touchpad is active, the session timer messages server as a heartbeat
		if(touchpadIsActive() == false && FrameLoop.frameModSeconds(3)) sendHeartBeat();
	}
	
	public void connectToSystemChannel() {
		// for syncing with other machines, we'll do that with AppStoreDistributed
		String systemWsAddress = urlWs + "/ws?roomId="+systemChannelId+"&clientType=kiosk&accountId="+accId+"&accountKey="+accKey;
		SocketClient socketSystem = new SocketClient(systemWsAddress, null, true);
		P.storeDistributed = AppStoreDistributed.instance();
		P.storeDistributed.start(socketSystem);
	}
	
	public void newSocketRoom() {
		// close old room
		if(socketClient != null) socketClient.disconnect();
		
		// set initial state
		if(P.store.getBoolean(ROOM_HAD_TOUCHPAD)) {
			P.store.setBoolean(TOUCHPAD_IS_CONNECTED, false);
		}
		P.store.setBoolean(ROOM_HAD_TOUCHPAD, false);
		
		// works with PlusSix socket server w/authentication and auto-cycling QR codes & room IDs 
		// create new room ID and reset session timeouts
		roomId = UUID.randomUUID().toString();
		sessionNewRoom();
		
		// build WebSocket address for the kiosk to create a new room
		serverAddress = urlWs + "/ws?roomId="+roomId+"&clientType=kiosk&accountId="+accId+"&accountKey="+accKey;
		socketClient = new SocketClient(serverAddress, this, true);
		
		// build QR code
		if(qr == null) qr = new QRCode();
		uiAddress = urlUi + "?t="+DateUtil.epochTime()+"#roomId="+roomId+"&debug=true";
		qr.updateQRCode(uiAddress, 256, 256, qrBgColor, qrFgColor);
		
		// log addresses for testing
		// P.out(serverAddress);
		// P.out(uiAddress);
	}
	
	public void drawKioskDebug(PGraphics pg) {
		pg.background(0);
		
		// header addresses
		String fontFile = DemoAssets.fontOpenSansPath;
		PFont font = FontCacher.getFont(fontFile, 30);
		PFont fontSm = FontCacher.getFont(fontFile, 20);
		FontCacher.setFontOnContext(pg, font, P.p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
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
			FontCacher.setFontOnContext(pg, fontSm, P.p.color(255, 255, 0), 1f, PTextAlign.RIGHT, PTextAlign.TOP);
			pg.text(
					"sessionStartTime: " + msToS(sessionStartTime) + FileUtil.NEWLINE + 
					"sessionEndTime: " + msToS(sessionEndTime) + FileUtil.NEWLINE + 
					"sessionElapsedTime: " + DateUtil.timeFromMilliseconds(sessionElapsedTime, false) + FileUtil.NEWLINE + 
					"sessionMaxTimeCountdown: " + DateUtil.timeFromMilliseconds(sessionMaxTimeCountdown, false) + FileUtil.NEWLINE + 
					"lastSessionBroadcastTime: " + msToS(lastSessionBroadcastTime) + FileUtil.NEWLINE + 
					"sessionTimeoutStartTime: " + msToS(sessionWindowStartTime) + FileUtil.NEWLINE + 
					"sessionTimeoutEndTime: " + msToS(sessionWindowEndTime) + FileUtil.NEWLINE + 
					"activityTimeoutCountdown: " + DateUtil.timeFromMilliseconds(activityTimeoutCountdown, false) + FileUtil.NEWLINE + 
					"activityTimeoutProgress: " + MathUtil.roundToPrecision(activityTimeoutProgress, 2) + FileUtil.NEWLINE +
					"sessionProgress: " + MathUtil.roundToPrecision(sessionProgress, 2) + FileUtil.NEWLINE +
					"isTimeoutWarning: " + isTimeoutWarning + FileUtil.NEWLINE +
					"sessionIsClosing: " + sessionIsClosing + FileUtil.NEWLINE +
					"sessionElapsedTime: " + DateUtil.timeFromMilliseconds(sessionWindowCurDuration, false) + FileUtil.NEWLINE + 
					"activityWindowCurTime: " + DateUtil.timeFromMilliseconds(activityWindowCurTime, false) + FileUtil.NEWLINE + 
					"timeSinceLastWarning: " + timeSinceLastWarning + FileUtil.NEWLINE + 
					""
					, pg.width - 20, 170);
//		}
		
		// websockets connected label
		if(socketClient.isConnected()) {
			FontCacher.setFontOnContext(pg, fontSm, P.p.color(0, 255, 0), 1f, PTextAlign.RIGHT, PTextAlign.TOP);
			pg.text("WebSocket Connected", pg.width - 20, 110);
		} else {
			FontCacher.setFontOnContext(pg, fontSm, P.p.color(255, 0, 0), 1f, PTextAlign.RIGHT, PTextAlign.TOP);
			pg.text("WebSocket Disconnected", pg.width - 20, 110);
		}
		
		// touchpad connected label
		if(touchpadIsActive()) {
			FontCacher.setFontOnContext(pg, fontSm, P.p.color(0, 255, 0), 1f, PTextAlign.RIGHT, PTextAlign.TOP);
			pg.text("Touchpad Connected", pg.width - 20, 140);
		} else {
			FontCacher.setFontOnContext(pg, fontSm, P.p.color(255, 0, 0), 1f, PTextAlign.RIGHT, PTextAlign.TOP);
			pg.text("Touchpad Disconnected", pg.width - 20, 140);
		}
		
		// show session progress bar
		pg.push();
		int barColor = 0xff555555;
		if(touchpadIsActive()) barColor = 0xff00bb00;
		if(isTimeoutWarning) barColor = 0xffffff00;
		if(sessionIsClosing) barColor = 0xffff0000;
		PG.drawProgressBar(pg, pg.width - 220, pg.height - 290, 184, 30, 0xffffffff, 0xff000000, barColor, activityTimeoutProgress);
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
	}
	
	protected int msToS(float ms) {
		return P.round(ms / 1000f);
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
				// perform actions based on cmd from touchpad
				if(cmd.equals(CMD_TOUCHPAD_CONNECTED))    touchpadConnected();
				else if(cmd.equals(CMD_TOUCHPAD_DISCONNECTED)) newSocketRoom();
				else if(cmd.equals(CMD_TOUCHPAD_INTERACTED))   resetUserInteractionTimeout();
				else {
					P.store.setString(INCOMING_JSON_DATA, message);
					P.store.setString(INCOMING_JSON_CMD, cmd);
				}
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
			touchpadSessionStarted();	// don't restart session timer if a user refreshes and continues a session they already started
		}
	    updateSessionState();
	    P.store.setBoolean(ROOM_HAD_TOUCHPAD, true);
	    sendSessionConfig();
	}
	
	protected void sessionNewRoom() {
		P.store.setBoolean(ROOM_HAD_TOUCHPAD, false);
		int now = P.p.millis();
		lastSessionBroadcastTime = now;
		sessionWindowStartTime = now;
		sessionWindowEndTime = sessionWindowStartTime + roomRecycleInterval;
		sessionWindowCurDuration = roomRecycleInterval;
		activityTimeoutCountdown = roomRecycleInterval;
	}

	protected void touchpadSessionStarted() {
		int now = P.p.millis();
		sessionStartTime = now;
		sessionEndTime = now + sessionMaxLength;
		resetUserInteractionTimeout();
	}

	protected void resetUserInteractionTimeout() {
		int now = P.p.millis();
		if(now < sessionEndTime - sessionUserTimeout) {
			sessionWindowStartTime = now;
			sessionWindowEndTime = sessionWindowStartTime + sessionUserTimeout;
			sessionWindowCurDuration = sessionUserTimeout;
			activityTimeoutCountdown = sessionUserTimeout;
		}
	}

	protected void updateSessionState() {
		// how long has the room been open?
		int now = P.p.millis();
		
		// total max session length timer
		sessionElapsedTime = now - sessionStartTime;
		sessionMaxTimeCountdown = sessionEndTime - now;
		sessionProgress = (float) (sessionElapsedTime) / (sessionEndTime - sessionStartTime);

		// user timeout session time
		activityWindowCurTime = now - sessionWindowStartTime;
		activityTimeoutProgress = (float) activityWindowCurTime / sessionWindowCurDuration;
		int prevSessionTimeLeft = activityTimeoutCountdown;
		activityTimeoutCountdown = sessionWindowEndTime - now;
		
		// send state to touchpad client
		if(touchpadIsActive()) {
			broadcastSessionClock();
		}

		// have we crossed the warning threshold?
	    timeSinceLastWarning = P.p.millis() - lastTimeoutWarning;
		sessionTimeoutWarningFrame = (prevSessionTimeLeft >= sessionWarningTime && activityTimeoutCountdown < sessionWarningTime);
		isTimeoutWarning = (activityTimeoutCountdown < sessionWarningTime) && timeSinceLastWarning > (sessionWarningTime - 1000);
		if(isTimeoutWarning) {
		    lastTimeoutWarning = P.p.millis();
		}
		// are we about to close the room?
		sessionIsClosingFrame = (prevSessionTimeLeft >= sessionClosingTime && activityTimeoutCountdown < sessionClosingTime);
		sessionIsClosing = (activityTimeoutCountdown < sessionClosingTime);

		// when time's up, move to a new room
		if(activityTimeoutProgress >= 1) {
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
	    jsonOut.setInt("sessionStartTime", sessionStartTime);
	    jsonOut.setInt("sessionEndTime", sessionEndTime);
	    jsonOut.setFloat("sessionElapsedTime", sessionElapsedTime);
	    jsonOut.setFloat("sessionMaxTimeCountdown", sessionMaxTimeCountdown);
	    jsonOut.setFloat("sessionProgress", sessionProgress);
	    jsonOut.setInt("activityTimeoutCountdown", activityTimeoutCountdown);
	    jsonOut.setFloat("activityTimeoutProgress", activityTimeoutProgress);
	    jsonOut.setBoolean("isTimeoutWarning", isTimeoutWarning);
	    jsonOut.setBoolean("sessionIsClosing", sessionIsClosing);
	    sendJSON(jsonOut);
	}

	protected void sendSessionConfig() {
		sendJSON(jsonConfig);
	}
	
	///////////////////////////////////////////
	// IAppStoreListener callbacks
	///////////////////////////////////////////
	
	public void updatedNumber(String key, Number val) {}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}

}
