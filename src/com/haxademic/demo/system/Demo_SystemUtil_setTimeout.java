package com.haxademic.demo.system;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.system.SystemUtil;

public class Demo_SystemUtil_setTimeout
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected int randGray = 0;
	protected ActionListener callback;
	
	public void setupFirstFrame() {

		buildCallbacks();
	}
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.WIDTH, "520" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "120" );
	}
	
	protected void buildCallbacks() {
		callback = new ActionListener() {@Override public void actionPerformed(ActionEvent arg0) {
			randGray = MathUtil.randRange(0, 255);
			SystemUtil.setTimeout(callback, 1000);
		}};
		SystemUtil.setTimeout(callback, 1000);
	}
	
	public void drawApp() {
		p.background(randGray);
	}
}
