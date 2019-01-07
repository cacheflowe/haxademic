package com.haxademic.demo.system;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.system.SystemUtil;

import controlP5.ControlP5;

public class Demo_SystemUtil_getScreenshot
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected ControlP5 _cp5;
	
	protected int _x = 0;
	protected int _y = 0;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 600 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_NONE );
	}

	public void setup() {
		super.setup();	

		_cp5 = new ControlP5(this);
		_cp5.addSlider("_x").setPosition(20,60).setWidth(200).setRange(0,p.displayWidth - p.width);
		_cp5.addSlider("_y").setPosition(20,100).setWidth(200).setRange(0,p.displayHeight - p.height);
	}

	public void drawApp() {
		background(0);
		p.image( SystemUtil.getScreenshot(_x, _y, p.width, p.height), 0, 0 );
	}

}
