package com.haxademic.demo.hardware.keyboard;


import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PKeys;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.system.SystemUtil;

public class Demo_KeyboardState
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void firstFrame() {
		DebugView.autoHide(false);
		DebugView.active(true);
		KeyboardState.instance().updatesDebugView(true);
	}

	protected void drawApp() {
		p.background(0);
		if(KeyboardState.keyOn('b')) {
			p.background(0, 0, 255);
		}
		if(KeyboardState.keyTriggered(' ')) {
			P.out("SPACE");
		}
		if(KeyboardState.keyTriggered(PKeys.VK_SHIFT)) {
			P.out("VK_SHIFT");
		}
		if(KeyboardState.keyTriggered(PKeys.VK_LEFT)) {
			P.out("VK_LEFT");
		}
		if(KeyboardState.keyTriggered(10)) {
			P.out("ENTER");
		}
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.keyCode == 86 && KeyboardState.instance().isKeyOn(17)) { // ctrl + v
			P.out("PASTED", SystemUtil.getClipboardContents());
		}
	}

}