package com.haxademic.demo.system;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.system.SystemUtil;

public class Demo_SystemUtil_copyStringToClipboard
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void firstFrame() {

		SystemUtil.copyStringToClipboard("copied!");	
	}

	protected void drawApp() {
		p.background(0);
	}

}