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
	// internal state
	protected final String TOUCHPAD_IS_CONNECTED = "TOUCHPAD_IS_CONNECTED"; 


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
	// - Web UI: Add event log on web frontend
	// - Web UI: Why isn't it working on mobile?? Try mobile error alerts
	// - Web UI: Add timeout progress indicator
	// - Web UI: Add session timeout view
	// - Java: Add session & user timeout progress indicator
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
	protected int sessionRecycleInterval =      60 * 1000;
	protected int sessionUserTimeout =          30 * 1000;
	protected int sessionMaxLength =            120 * 1000;
	protected int sessionWarningTime =          10 * 1000;
	
	
	protected void config() {
		Config.setAppSize(1280, 720);
		Config.setProperty(AppSettings.APP_NAME, "SocketClient");
	}
	
	protected void firstFrame() {
//		buildSocketClientPlusSix();
		recycleSocketRoom();
	}
	
	protected void buildSocketClientPlusSix() {
		// set initial state
		P.store.setBoolean(TOUCHPAD_IS_CONNECTED, false);
		
		// replace localhost with IP address
		if(BASE_URL_WS.contains("localhost")) BASE_URL_WS = BASE_URL_WS.replace("localhost", IPAddress.getIP());
		if(BASE_URL_UI.contains("localhost")) BASE_URL_UI = BASE_URL_UI.replace("localhost", IPAddress.getIP());
		
		// works with PlusSix socket server w/authentication and auto-cycling QR codes & room IDs 
		// create new room ID
		roomId = UUID.randomUUID().toString();
		
		// build WebSocket address for the kiosk to create a new room
		serverAddress = BASE_URL_WS + "/ws?roomId="+roomId+"&clientType=kiosk&accountId="+accId+"&accountKey="+accKey;
		socketClient = new SocketClient(serverAddress, this, true);
		
		// build QR code
		if(qr == null) qr = new QRCode();
		uiAddress = BASE_URL_UI + "?t="+DateUtil.epochTime()+"#roomId="+roomId+"&debug=true";
		qr.updateQRCode(uiAddress, 256, 256, p.color(0), p.color( 0, 255, 0));
		
		P.out(serverAddress);
		P.out(uiAddress);
	}
	
	protected void recycleSocketRoom() {
		if(socketClient != null) socketClient.disconnect();
		buildSocketClientPlusSix();
		sessionNewRoom();
	}
	
	protected void drawApp() {
		background(0);
		updateSessionState();
		drawDebug();
		
		// draw qr code (with icon in center to test)
		p.push();
		p.translate(p.width - qr.image().width, p.height - qr.image().height);
		p.image(qr.image(), 0, 0);
		// icon
		p.noStroke();
		p.rect(qr.image().width/2 - 16, qr.image().height/2 - 16, 32, 32);
		p.image(DemoAssets.smallTexture(), qr.image().width/2 - 16, qr.image().height/2 - 16, 32, 32);
		p.pop();

		// send a simple message to clients
		if(FrameLoop.frameModSeconds(1)) sendHeartBeat(); 
		
		// test shutting down & recreating the socket client
		if(KeyboardState.keyTriggered(' ')) recycleSocketRoom();
		if(KeyboardState.keyTriggered('l')) SystemUtil.openWebPage(uiAddress);
	}
	
	protected void drawDebug() {
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
		if(touchpadIsActive()) {
			FontCacher.setFontOnContext(pg, fontSm, p.color(255, 255, 0), 1f, PTextAlign.RIGHT, PTextAlign.TOP);
			pg.text(
					"sessionMaxEndTime: " + msToS(sessionMaxEndTime) + FileUtil.NEWLINE + 
					"lastSessionBroadcastTime: " + msToS(lastSessionBroadcastTime) + FileUtil.NEWLINE + 
					"sessionTimeoutStartTime: " + msToS(sessionTimeoutStartTime) + FileUtil.NEWLINE + 
					"sessionTimeoutEndTime: " + msToS(sessionTimeoutEndTime) + FileUtil.NEWLINE + 
					"sessionDuration: " + msToS(sessionDuration) + FileUtil.NEWLINE + 
					"sessionTimeLeft: " + msToS(sessionTimeLeft) + FileUtil.NEWLINE + 
					"sessionProgress: " + MathUtil.roundToPrecision(sessionProgress, 2) + FileUtil.NEWLINE +
					"sessionCurDuration: " + msToS(sessionCurDuration) + FileUtil.NEWLINE 
					, p.width - 20, 170);
		}
		
		// websockets connected label
		if(socketClient.isConnected()) {
			FontCacher.setFontOnContext(pg, fontSm, p.color(0, 255, 0), 1f, PTextAlign.RIGHT, PTextAlign.TOP);
			pg.text("WebSocket Connected", p.width - 20, 110);
		} else {
			FontCacher.setFontOnContext(pg, fontSm, p.color(255, 0, 0), 1f, PTextAlign.RIGHT, PTextAlign.TOP);
			pg.text("WebSocket Disconnected", p.width - 20, 110);
		}
		
		// touchpad connected label
		if(touchpadIsActive()) {
			FontCacher.setFontOnContext(pg, fontSm, p.color(0, 255, 0), 1f, PTextAlign.RIGHT, PTextAlign.TOP);
			pg.text("Touchpad Connected", p.width - 20, 140);
		} else {
			FontCacher.setFontOnContext(pg, fontSm, p.color(255, 0, 0), 1f, PTextAlign.RIGHT, PTextAlign.TOP);
			pg.text("Touchpad Disconnected", p.width - 20, 140);
		}
		
		// show session progress bar
		pg.push();
		int barColor = 0xff555555;
		if(touchpadIsActive()) barColor = 0xff00bb00;
		if(sessionTimeoutWarning) barColor = 0xffffff00;
		PG.drawProgressBar(pg, pg.width - 220, pg.height - 290, 184, 30, 0xffffffff, 0xff000000, barColor, sessionProgress);
		pg.pop();
		
		pg.endDraw();
		p.image(pg, 0, 0);
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
				if(cmd.equals(CMD_TOUCHPAD_DISCONNECTED)) touchpadDisconnected();
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
	// PlusSix logic
	////////////////////////////////////////////

	protected void touchpadConnected() {
		P.store.setBoolean(TOUCHPAD_IS_CONNECTED, true);
		P.out("Send config to touchpad app: current state, session timeout, etc");
	    touchpadSessionStarted();
	    resetUserInteractionTimeout();
	}
	
	protected boolean canResetSessionTimeout = false;
	protected boolean sessionTimeoutWarning = false;
	protected int sessionMaxEndTime = 0;
	protected int lastSessionBroadcastTime = 0;
	protected int sessionTimeoutStartTime = 0;
	protected int sessionTimeoutEndTime = 0;
	protected int sessionDuration = 0;
	protected int sessionTimeLeft = 0;
	protected float sessionProgress = 0;
	protected int sessionCurDuration = 0;
	
	protected void sessionNewRoom() {
		lastSessionBroadcastTime = P.p.millis();
		sessionTimeoutStartTime = P.p.millis();
		sessionTimeoutEndTime = sessionTimeoutStartTime + sessionRecycleInterval;
		sessionDuration = sessionRecycleInterval;
		sessionTimeLeft = sessionRecycleInterval;
	}

	protected void touchpadSessionStarted() {
		resetTimeoutWarning();
		resetUserInteractionTimeout();
		sessionMaxEndTime = P.p.millis() + sessionMaxLength;
		canResetSessionTimeout = true;
	}

	protected void resetUserInteractionTimeout() {
		int now = P.p.millis();
		if(now < sessionMaxEndTime - sessionUserTimeout) {
			sessionTimeoutStartTime = now;
			sessionTimeoutEndTime = sessionTimeoutStartTime + sessionUserTimeout;
			sessionDuration = sessionUserTimeout;
			sessionTimeLeft = sessionUserTimeout;
			resetTimeoutWarning();
		} else {
			canResetSessionTimeout = false;
		}
	}

	protected void updateSessionState() {
		// how long has the room been open?
		sessionCurDuration = P.p.millis() - sessionTimeoutStartTime;
		sessionProgress = (float) sessionCurDuration / sessionDuration;
		int prevSessionTimeLeft = sessionTimeLeft;
		sessionTimeLeft = sessionTimeoutEndTime - P.p.millis();
		broadcastSessionClock();

		// update progress bar
//		qrTimerEl.style.setProperty('width', `${100 - (sessionProgress * 100)}%`);

		// have we crossed the warning threshold?
		sessionTimeoutWarning = (prevSessionTimeLeft >= sessionWarningTime && sessionTimeLeft < sessionWarningTime);
		if(sessionTimeoutWarning) {
			setTimeoutWarningStyles();
//			emit('widgetSessionTimeoutWarning', {
//				maxSessionDuration: !canResetSessionTimeout,
//			});
		}

		// are we about to close the room?
		boolean closing = (prevSessionTimeLeft >= 500 && sessionTimeLeft < 500);
		if(closing) setRoomClosingStyles();

		// when time's up, move to a new room
		if(sessionProgress >= 1) {
			resetTimeoutWarning();
			recycleSocketRoom();
		}
	}

	protected void broadcastSessionClock() {
		// only emit session clock once per second
		if(P.p.millis() < lastSessionBroadcastTime + 1000) return;
		lastSessionBroadcastTime = P.p.millis();
		
		// send session state to touchpad client
	    JSONObject jsonOut = new JSONObject();
	    jsonOut.setString(KEY_CMD, CMD_KIOSK_SESSION_UPDATED);
	    jsonOut.setInt("sessionCurDuration", sessionCurDuration);
	    jsonOut.setInt("sessionTimeoutStartTime", sessionTimeoutStartTime);
	    jsonOut.setInt("sessionMaxEndTime", sessionMaxEndTime);
	    jsonOut.setInt("sessionTimeoutEndTime", sessionTimeoutEndTime);
	    jsonOut.setInt("sessionDuration", sessionDuration);
	    jsonOut.setInt("sessionTimeLeft", sessionTimeLeft);
	    jsonOut.setFloat("sessionProgress", sessionProgress);
	    sendJSON(jsonOut);
	}

	protected void setTimeoutWarningStyles() {
		P.out("setTimeoutWarningStyles");
//		qrWidgetEl.classList.add('room-almost-closed');
	}

	protected void setRoomClosingStyles() {
//		qrWidgetEl.classList.remove('room-almost-closed');
//		qrWidgetEl.classList.add('room-closing');
	}

	protected void resetTimeoutWarning() {
		P.out("resetTimeoutWarning");
//		qrWidgetEl.classList.remove('room-almost-closed');
//		qrWidgetEl.classList.remove('room-closing');
	}


	protected void sessionResetTimeout() {
		this.resetUserInteractionTimeout();
	}


	
	protected void touchpadDisconnected() {
		P.store.setBoolean(TOUCHPAD_IS_CONNECTED, false);
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
