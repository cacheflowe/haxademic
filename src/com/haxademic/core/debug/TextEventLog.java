package com.haxademic.core.debug;

import com.haxademic.core.app.P;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;

public class TextEventLog {

	protected String logFile;
	
	public TextEventLog() {
		String logPath = FileUtil.getHaxademicOutputPath(); 
		FileUtil.createDir(logPath);
		logFile = logPath + "_log.txt";
		if(FileUtil.fileExists(logFile) == false) {
			FileUtil.writeTextToFile(logFile, "");
		}
	}
	
	public void addEvent(String str) {
		new Thread(new Runnable() { public void run() {
			FileUtil.appendTextToFile(logFile, "[" + SystemUtil.getTimestamp(P.p) + "] :: " + str + FileUtil.NEWLINE);
		}}).start();
	}
	
	public void appStarted() {
		addEvent("======= App Started =======");
	}
}
