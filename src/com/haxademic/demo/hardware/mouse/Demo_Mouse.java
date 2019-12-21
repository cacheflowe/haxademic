package com.haxademic.demo.hardware.mouse;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.system.AppUtil;

public class Demo_Mouse
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.APP_NAME, "Demo_AppUtil" );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, true );
		p.appConfig.setProperty( AppSettings.SCREEN_X, 0 );
		p.appConfig.setProperty( AppSettings.SCREEN_Y, 100 );
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 600 );
		p.appConfig.setProperty( AppSettings.ALWAYS_ON_TOP, true );
	}

	public void setupFirstFrame() {
		// AppUtil.setGLWindowChromeless(p);
//		AppUtil.setLocation(p, 0, 30);
	}

	public void drawApp() {
		p.background(0);
		
		//get native window object
		p.debugView.setValue("window width", window.getBounds().getWidth());
		p.debugView.setValue("window height", window.getBounds().getHeight());
		p.debugView.setValue("window hasFocus", window.hasFocus());

		// move window in circle
		AppUtil.setLocation(p, P.round(100 + 50f * P.cos(p.frameCount * 0.01f)), P.round(100 + 50f * P.sin(p.frameCount * 0.01f)));
//		if(p.frameCount == 200) AppUtil.setSize(p, 200, 200);
		
		// click mouse & move
		if(p.frameCount % 180 == 0) {
			p.noCursor();
			Mouse.mouseClickAt(300, 300);
			Mouse.movePointerTo(99999, 0);
		}
	}

}