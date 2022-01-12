package com.haxademic.demo.media.video;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.net.UIControlsHandler;
import com.haxademic.core.net.WebServer;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.system.shell.IScriptCallback;
import com.haxademic.core.system.shell.ScriptRunner;

public class Demo_ChromeKioskVideo_Multiple
extends PAppletHax
implements IScriptCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// web server to host static html
	protected WebServer webServer;
	protected ScriptRunner killChrome;
	protected ScriptRunner runKiosk1;
	protected ScriptRunner runKiosk2;
	protected ScriptRunner killJava;
	protected String baseURL = "http://localhost:8080/chrome-kiosk/#video%3D";	// url encoded "="
	
	protected void firstFrame() {
		webServer = new WebServer(new UIControlsHandler(), false);
		SystemUtil.setTimeout(launchKiosks, 1000);
	}
	
	protected void drawApp() {
		if(p.frameCount == 1) p.background(FrameLoop.osc(0.05f, 0, 80));
		p.noStroke();
	}
	
	protected ActionListener launchKiosks = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			launchChromeKiosk();
		}
	};
	
	protected void launchChromeKiosk() {
		// kill chrome in case one is already running. script exits on its own
		killChrome = new ScriptRunner("chrome-kill", this);
		killChrome.runWithParams();
	
		// kiosk scripts need to be on own threads
		runKiosk1 = new ScriptRunner("chrome-kiosk-multi", this);
		new Thread(new Runnable() { public void run() {
			runKiosk1.runWithParams(baseURL + "video/kinect-silhouette.mp4", "0", "1");
		}}).start();
		
		runKiosk2 = new ScriptRunner("chrome-kiosk-multi", this);
		new Thread(new Runnable() { public void run() {
			runKiosk2.runWithParams(baseURL + "video/kinect-silhouette.mp4", "1920", "2");
		}}).start();
	}

	
	public void killKiosks() {
		// kill chrome kiosks
		runKiosk1.process().destroy();
		runKiosk2.process().destroy();
		killChrome.runWithParams();
		
		// kill java. script exits on its own. Java process was hanging after launching Chrome kiosks
		killJava = new ScriptRunner("kill-java", this);
		killJava.runWithParams();
	}

	public void scriptComplete() {
		P.out("FullscreenKiosks :: SCRIPT COMPLETE");
	}

	public void exit() {
		killKiosks();
		super.exit();
		
		// if using in a custom object w/AppStoreListener, use this:
		/*
			public void updatedBoolean(String key, Boolean val) {
				if(key.equals(PEvents.EXIT)) killKiosks();
			}
		*/
	}
}
