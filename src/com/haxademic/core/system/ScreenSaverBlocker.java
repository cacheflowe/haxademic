package com.haxademic.core.system;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRegisterableMethods;

public class ScreenSaverBlocker {

	protected Robot robot;
	public static int FRAME_INTERVAL = 60 * 60 * 2;	// ~2 minutes
	
	// Singleton instance
	
	public static ScreenSaverBlocker instance;
	
	public static ScreenSaverBlocker instance() {
		if(instance != null) return instance;
		instance = new ScreenSaverBlocker();
		return instance;
	}

	public ScreenSaverBlocker() {
		try {
			robot = new Robot();
			P.p.registerMethod(PRegisterableMethods.post, this);
		} catch( Exception error ) {
			P.out("couldn't init Robot for screensaver disabling");
		}
	}
	
	protected void post() {
		// simulate SHIFT key press on interval
		if(P.p.frameCount % FRAME_INTERVAL == 10) robot.keyPress(KeyEvent.VK_SHIFT);
		if(P.p.frameCount % FRAME_INTERVAL == 11) robot.keyRelease(KeyEvent.VK_SHIFT);
	}
	
}
