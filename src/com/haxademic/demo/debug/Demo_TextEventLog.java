package com.haxademic.demo.debug;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.TextEventLog;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;

public class Demo_TextEventLog
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected TextEventLog eventLog;
	
	public void setupFirstFrame() {
//		eventLog = new TextEventLog();
		eventLog = new TextEventLog(FileUtil.getHaxademicOutputPath() + "logs/" + SystemUtil.getDateStamp() + ".txt");
		eventLog.appStarted();
	}

	public void drawApp() {
		background(0);
	}
	
	public void keyPressed() {
		super.keyPressed();
		eventLog.addEvent("keyPressed() - " + p.key);
	}

}
