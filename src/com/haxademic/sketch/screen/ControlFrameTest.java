package com.haxademic.sketch.screen;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.system.ControlFrame;

import controlP5.ControlElement;
import processing.core.PApplet;

public class ControlFrameTest 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected ControlFrameRGB controlFrameLauncher;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FPS, "30" );
		p.appConfig.setProperty( AppSettings.WIDTH, "500" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "500" );
	}

	public void setup() {
		super.setup();	
		controlFrameLauncher = new ControlFrameRGB(this);
	}

	public void drawApp() {
		controlFrameLauncher.update();
	}
	
	
	
	public class ControlFrameRGB {
		// from: https://gist.github.com/eskimoblood/10414654

		private final PApplet p;

		@ControlElement(properties = {"min=0", "max=255"}, x = 10, y = 10)
		private float red = 0;

		@ControlElement(properties = {"min=0", "max=255"}, x = 10, y = 30)
		private float green = 0;

		@ControlElement(properties = {"min=0", "max=255"}, x = 10, y = 50)
		private float blue = 0;

		public ControlFrameRGB(PApplet p) {
			this.p = p;
			new ControlFrame(this, 200, 200, p.width, 0);
		}

		public void update() {
			p.background(red, green, blue);
		}
	}

}
