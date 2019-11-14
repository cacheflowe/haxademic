package com.haxademic.demo.debug;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.TextEventLog;

public class Demo_TextEventLog
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected TextEventLog eventLog;
	
	public void setupFirstFrame() {
		eventLog = new TextEventLog();
		eventLog.appStarted();
		eventLog.setMaxLogFiles(5);
	}

	public void drawApp() {
		background(0);
	}
	
	public void keyPressed() {
		super.keyPressed();
		eventLog.addEvent("keyPressed() - " + p.key);
	}

}
