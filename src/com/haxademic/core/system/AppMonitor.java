package com.haxademic.core.system;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;

import processing.core.PApplet;

public class AppMonitor {

	// TODO:
	// - Before restarting, attempt to hit a URL that sends an email? Make this configurable
	// - Send an email on poor framerate performance [optional]
	// - Send an email daily with stats?? This should be a different object that includes user sessions, average framerate, system reports, etc. Maybe this negates the need to the drop in performance email 
	
	public AppMonitorWindow monitorApp = null;
	public boolean showWindow;
	public int timeAfterCrash;
	
	public AppMonitor() {
		this(false, 5000);
	}
	
	public AppMonitor(boolean showWindow, int timeAfterCrash) {
		this.showWindow = showWindow;
		this.timeAfterCrash = timeAfterCrash;
		P.p.registerMethod("post", this);	 // update texture to 2nd window after main draw() execution
	}
	
	public void post() {
		if(monitorApp == null && P.p.frameCount >= 2) {
			monitorApp = new AppMonitorWindow(P.p, timeAfterCrash, showWindow);
		}
		if(monitorApp != null) {
//			if(P.p.frameCount % 20 == 0) {
				monitorApp.setUpdateTime(P.p.millis());
//			}
		}
	}
	
	class AppMonitorWindow extends PApplet {

		PAppletHax p;
		public boolean showing;
		public int timeout;
		public int updateTime;
		public int lastUpdateTime;
		public boolean attemptRestart = false;

		public AppMonitorWindow(PAppletHax p) {
			this(p, 3000, false);
		}
		
		public AppMonitorWindow(PAppletHax p, int timeout) {
			this(p, timeout, false);
		}
		
		public AppMonitorWindow(PAppletHax p, int timeout, boolean showing) {
			this.p = p;
			this.timeout = timeout;
			this.showing = showing;
			runSketch(new String[] {"AppMonitorWindow"}, this);
		}

		public void settings() {
			size(200, 128);
			noSmooth();
		}

		public void setup() {
			int windowX = (showing) ? 0 : -220;
			super.surface.setResizable(true);	// needed for setLocation()
			super.surface.setLocation(windowX, 0);
			super.surface.setTitle("App Monitor");
			// super.surface.setVisible(showing);
		}
		
		public void setUpdateTime(int updateTime) {
			this.updateTime = updateTime;
			lastUpdateTime = millis();
		}

		public void draw() {
			if(millis() - lastUpdateTime < timeout) {
				background(0, 127, 0);
			} else {
				background(127, 0, 0);
				if(attemptRestart == false) {
					attemptRestart = true;
					AppRestart.restart(p);
				}
			}
			noStroke();
			// show debug text
			fill(255);
			textAlign(P.CENTER, P.CENTER);
			text("Frame Update:\n"+lastUpdateTime+" / "+millis(), 0, 0, width, height);
		}

		public void exit() {
			dispose();
		}

	}
}
