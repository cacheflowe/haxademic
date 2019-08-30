package com.haxademic.core.debug;

import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;

public class TextEventLog {

	protected String logFile;
	
	public static String DAILY_LOG() {
		return FileUtil.getHaxademicOutputPath() + "logs/" + SystemUtil.getDateStamp() + ".txt";
	}
	
	public TextEventLog() {
		this(FileUtil.getHaxademicOutputPath() + "_log.txt"); 
	}
	
	public TextEventLog(String filePath) {
		logFile = filePath;
		FileUtil.createDir(FileUtil.pathForFile(logFile));
		if(FileUtil.fileExists(logFile) == false) {
			FileUtil.writeTextToFile(logFile, "");	// create text file if it doesn;t yet exist
		}
	}
	
	public void addEvent(String str) {
		new Thread(new Runnable() { public void run() {
			FileUtil.appendTextToFile(logFile, "[" + SystemUtil.getTimestamp() + "] :: " + str + FileUtil.NEWLINE);
		}}).start();
	}
	
	public void appStarted() {
		addEvent("======= App Started =======");
	}
}
