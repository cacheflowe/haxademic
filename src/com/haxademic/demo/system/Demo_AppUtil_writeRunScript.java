package com.haxademic.demo.system;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.system.AppUtil;

public class Demo_AppUtil_writeRunScript
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	public void setupFirstFrame() {

		// Don't include run script file extension - platform detection inside will do that
		AppUtil.writeRunScript("scripts/write-test-run");	
	}

	public void drawApp() {
		p.background(0);
	}

}