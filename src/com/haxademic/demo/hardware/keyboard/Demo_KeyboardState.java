package com.haxademic.demo.hardware.keyboard;


import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.keyboard.KeyboardState;

public class Demo_KeyboardState
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public void firstFrame() {
		DebugView.autoHide(false);
		DebugView.active(true);
	}

	public void drawApp() {
		p.background(0);
		if(KeyboardState.keyOn('b')) {
			p.background(0, 0, 255);
		}
		if(KeyboardState.keyTriggered(' ')) {
			P.out("SPACE");
		}
	}

}