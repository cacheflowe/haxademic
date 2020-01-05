package com.haxademic.demo.hardware.mouse;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.system.AppUtil;

public class Demo_Mouse
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void config() {
		Config.setProperty( AppSettings.APP_NAME, "Demo_AppUtil" );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
		Config.setProperty( AppSettings.FULLSCREEN, true );
		Config.setProperty( AppSettings.SCREEN_X, 0 );
		Config.setProperty( AppSettings.SCREEN_Y, 100 );
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 600 );
		Config.setProperty( AppSettings.ALWAYS_ON_TOP, true );
	}

	protected void firstFrame() {
		// AppUtil.setGLWindowChromeless(p);
//		AppUtil.setLocation(p, 0, 30);
	}

	protected void drawApp() {
		p.background(0);
		
		//get native window object
		DebugView.setValue("window width", P.window.getBounds().getWidth());
		DebugView.setValue("window height", P.window.getBounds().getHeight());
		DebugView.setValue("window hasFocus", P.window.hasFocus());

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