package com.haxademic.demo.hardware.gamepad;


import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;

public class Demo_GamepadInput
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void config() {
		Config.setProperty( AppSettings.GAMEPADS_ACTIVE, true );
	}

	public void firstFrame() {
	}

	public void drawApp() {
		p.background(0);
		p.gamepadState.printControls();
	}

}