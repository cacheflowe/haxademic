package com.haxademic.core.system;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PRenderers;

import processing.core.PApplet;

public class CrashMonitor {
	
	public CrashMonitorWindow monitorApp = null;
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
	
	public void setLocation(int x, int y) {
		monitorApp.getSurface().setLocation(x, y);
	}
	
	public void setVisible(boolean visible) {
		monitorApp.getSurface().setVisible(visible);
	}
	
	public void post() {
		if(monitorApp == null && P.p.frameCount >= 2) {
			monitorApp = new CrashMonitorWindow(P.p, timeAfterCrash, showWindow);
		}
		if(monitorApp != null) {
			if(P.p.frameCount % 20 == 0) {
				monitorApp.setUpdateTime(P.p.millis());
			}
		}
	}
	
	public class CrashMonitorWindow extends PApplet {

		PAppletHax p;
		public boolean showing;
		public int timeout;
		public int updateTime;
		public int lastUpdateTime;
		public boolean attemptRestart = false;

		public CrashMonitorWindow(PAppletHax p) {
			this(p, 3000, false);
		}
		
		public CrashMonitorWindow(PAppletHax p, int timeout) {
			this(p, timeout, false);
		}
		
		public CrashMonitorWindow(PAppletHax p, int timeout, boolean showing) {
			this.p = p;
			this.timeout = timeout;
			this.showing = showing;
			runSketch(new String[] {"CrashMonitorWindow"}, this);
		}

		public void settings() {
			size(256, 128, PRenderers.JAVA2D);
		}

		public void setup() {
			int windowX = (showing) ? 0 : -1000;
			super.surface.setResizable(true);	// needed for setLocation()
			super.surface.setLocation(windowX, 0);					// super.surface.setVisible(showing);	<- this seems to disable the restart functionality
			super.surface.setTitle("CrashMonitor");
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
