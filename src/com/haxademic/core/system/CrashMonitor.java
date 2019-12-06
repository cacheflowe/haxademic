package com.haxademic.core.system;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PRenderers;

import processing.core.PApplet;

public class CrashMonitor {
	
	public AppMonitorWindow monitorApp = null;
	public boolean showWindow;
	public int timeAfterCrash;
	public boolean restarts;
	
	public CrashMonitor() {
		this(false, 5000, true);
	}
	
	public CrashMonitor(boolean showWindow, int timeAfterCrash, boolean restarts) {
		this.showWindow = showWindow;
		this.timeAfterCrash = timeAfterCrash;
		this.restarts = restarts;
		P.p.registerMethod("post", this);	 // update texture to 2nd window after main draw() execution
	}
	
	public void post() {
		if(monitorApp == null && P.p.frameCount >= 2) {
			monitorApp = new AppMonitorWindow(P.p, timeAfterCrash, showWindow);
		}
		if(monitorApp != null) {
			if(P.p.frameCount % 20 == 0) {
				monitorApp.setUpdateTime(P.p.millis());
			}
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
			size(256, 128, PRenderers.P2D);
		}

		public void setupFirstFrame() {
			int windowX = (showing) ? 0 : -220;
			super.surface.setResizable(true);	// needed for setLocation()
			super.surface.setLocation(windowX, 0);
			super.surface.setTitle("App Monitor");
			// super.surface.setVisible(showing);
			frameRate(20);
		}
		
		public void setUpdateTime(int updateTime) {
			this.updateTime = updateTime;
			lastUpdateTime = millis();
		}

		public void draw() {
			if(millis() - lastUpdateTime < timeout) {
				if(millis() - lastUpdateTime < 1000) {
					background(0, 127, 0);
				} else {
					background(200, 200, 0);
				}
			} else {
				background(127, 0, 0);
				if(attemptRestart == false) {
					attemptRestart = true;
					if(restarts) {
						AppRestart.restart(p);
					} else {
						AppRestart.quit(p);
					}
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
