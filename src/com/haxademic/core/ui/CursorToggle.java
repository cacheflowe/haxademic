package com.haxademic.core.ui;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugUtil;

import processing.event.KeyEvent;

public class CursorToggle {

	protected boolean isHidden;
	
	public CursorToggle(boolean defaultHidden) {
		if(P.p == null) DebugUtil.printErr("===========\n Please wait until setup() to init CursorToggle \n===========");
		isHidden = defaultHidden;
		P.p.registerMethod("keyEvent", this);
	}
	
	public void keyEvent(KeyEvent e) {
		if(e.getKey() == 'm') isHidden = !isHidden;
		if(isHidden == true) {
			P.p.noCursor();
		} else {
			P.p.cursor();
		}
	}

}
