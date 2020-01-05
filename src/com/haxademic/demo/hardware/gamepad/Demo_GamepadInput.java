package com.haxademic.demo.hardware.gamepad;


import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.gamepad.GamepadState;

public class Demo_GamepadInput
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void firstFrame() {
		GamepadState.instance();
		
		DebugView.autoHide(false);
		DebugView.active(true);
	}

	protected void drawApp() {
		p.background(0);
	}

}