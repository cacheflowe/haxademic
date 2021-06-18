package com.haxademic.core.system;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PEvents;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.net.DashboardCheckinPoller;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.system.AppSizeWatcher.IAppSizeWatcherDelegate;
import com.haxademic.core.system.VideoOutputsWatcher.IVideoOutputsWatcherDelegate;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;

public class Uptime 
implements IAppStoreListener, IVideoOutputsWatcherDelegate, IAppSizeWatcherDelegate {
	
	protected PAppletHax p;
	protected PGraphics pg;
	
	protected int appExpectedW;
	protected int appExpectedH;
	protected int debugW = 1024;
	protected int debugH = 0;
	
	protected PGraphics debugBuffer;
	protected DashboardCheckinPoller dashboardCheckinPoller;
	protected CrashMonitor crashMonitor;
	protected VideoOutputsWatcher videoOutputsWatcher;
	protected AppSizeWatcher appSizeWatcher;
	
	public Uptime(int appW, int appH) {
		p = (PAppletHax) P.p;
		pg = p.pg;
		P.store.addListener(this);
		
		this.appExpectedW = appW;
		this.appExpectedH = appH;
		
		// init
		addReporting();
		addVideoOutputMonitor();
	}
	
	////////////////////////////////////
	// Init uptime components
	////////////////////////////////////
	
	protected void addReporting() {
		DashboardCheckinPoller.DEBUG = false;
		float pgScale = MathUtil.scaleToTarget(pg.width, debugW);
		debugH = P.floor(pg.height * pgScale);
		debugBuffer = PG.newPG(debugW, debugH);
		int hourInSeconds = 3600;
		int tenMinutesInSeconds = 600;
		dashboardCheckinPoller = new DashboardCheckinPoller("test-project", "Test Project", "localhost/haxademic/www/dashboard/", tenMinutesInSeconds, hourInSeconds, 0.5f);
		dashboardCheckinPoller.setExtraImage(debugBuffer, hourInSeconds);	// Upload debugBuffer every hour
		crashMonitor = new CrashMonitor(false, 30000, false);				// quit (restart via cmd run script) after 30 seconds if crashed
		DebugView.setTexture("debugBuffer", debugBuffer);
	}	
	
	protected void addVideoOutputMonitor() {
		int fiveMinutesFrames = 60 * 60 * 5;
		videoOutputsWatcher = new VideoOutputsWatcher(this, fiveMinutesFrames);
		appSizeWatcher = new AppSizeWatcher(this, appExpectedW, appExpectedH, fiveMinutesFrames);
	}

	////////////////////////////////////
	// Update uptime components
	////////////////////////////////////
	
	protected void drawPost(int frameCount) {
		hidePanels();
		restartNightly();
		clickScreenOnInterval();
		logUptime();
		drawDebug();
	}
	
	protected void hidePanels() {
		// hide UI & DebugView every 5 minutes in case they're accidentally left enabled
		if(FrameLoop.frameModMinutes(5)) { 
			if(UI.active()) UI.active(false);
			if(DebugView.active()) DebugView.active(false);
		}
	}
	
	protected void restartNightly() {
		// every half hour, check if we've been up for over a day, and we're between 4-5am
		if(FrameLoop.frameModMinutes(30)) {
			if(DateUtil.uptimeHours() > 21f && DateUtil.timeIsBetweenHours(4, 5)) {
				p.exit();	// use CrashMonitor & run.cmd to restart from cmd loop
			}
		}
	}
	
	protected void clickScreenOnInterval() {
		// click screen to focus away from crash monitor or other popups
		// do it once after startup, then every half hour
		if(FrameLoop.count() == 120 || FrameLoop.frameModMinutes(30)) { 
			Mouse.mouseClickAt(300, 100);
			Mouse.setPointerLocation(p, 9999, 60);
		}
	}
	
	protected void drawDebug() {
		if(debugBuffer == null) return;
		// update debug buffer every [x] minutes, or always when debug view is up
		if(FrameLoop.frameModMinutes(15) || DebugView.active()) {
			debugBuffer.beginDraw();
			debugBuffer.noStroke();
			// draw main output under debug images
			debugBuffer.image(pg, 0, 0, debugW, debugH);
			// draw anything else?
			
			debugBuffer.endDraw();
		}
	}
	
	protected void logUptime() {
		if(FrameLoop.frameModMinutes(60)) P.out("Still running:", DebugView.uptimeStr());
	}
	
	/////////////////////////////////
	// IVideoOutputsWatcherDelegate methods
	/////////////////////////////////

	public void videoOutputsChanged(int screensWidth, int screensHeight) {
		dashboardCheckinPoller.setCustomValue("videoOutputsChanged()", screensWidth + ", " + screensHeight);
		P.out("videoOutputsChanged() resized to: ", appExpectedW, appExpectedH);
		AppUtil.setSize(P.p, appExpectedW, appExpectedH);
		AppUtil.setLocation(p, 0, 0);
	}

	/////////////////////////////////
	// IAppSizeWatcherDelegate methods
	/////////////////////////////////
	
	public void appSizeChanged(int appWidth, int appHeight) {
		if(p.width != appExpectedW || p.height != appExpectedH) {
			dashboardCheckinPoller.setCustomValue("appSizeChanged()", p.width + ", " + p.height);
			P.out("appSizeChanged() resized to: ", appExpectedW, appExpectedH);
			AppUtil.setSize(p, appExpectedW, appExpectedH);
			AppUtil.setLocation(p, 0, 0);
		}
	}
	
	public void appSizeIncorrect(int appWidth, int appHeight) {
		if(p.width != appExpectedW || p.height != appExpectedH) {
			dashboardCheckinPoller.setCustomValue("appSizeIncorrect()", p.width + ", " + p.height);
			P.out("appSizeIncorrect() resized to: ", appExpectedW, appExpectedH);
			AppUtil.setSize(p, appExpectedW, appExpectedH);
			AppUtil.setLocation(p, 0, 0);
		}
	}
	

	/////////////////////////////////////
	// AppStore listeners
	/////////////////////////////////////

	@Override
	public void updatedNumber(String key, Number val) {
//		if(key.equals(PEvents.DRAW_PRE)) drawPre(val.intValue());
		if(key.equals(PEvents.DRAW_POST)) drawPost(val.intValue());
	}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}

}

