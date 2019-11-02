package com.haxademic.core.debug;

import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;

public class TextEventLog {

	protected String logFile;
	
	public TextEventLog() {}
	
	public void addEvent(String str) {
		new Thread(new Runnable() { public void run() {
			logFile = FileUtil.getHaxademicOutputPath() + "logs/" + SystemUtil.getDateStamp() + ".txt";;
			FileUtil.createDir(FileUtil.pathForFile(logFile));
			FileUtil.appendTextToFile(logFile, "[" + SystemUtil.getTimestamp() + "] :: " + str + FileUtil.NEWLINE);
		}}).start();
	}
	
	public void appStarted() {
		addEvent("======= App Started =======");
	}
}
