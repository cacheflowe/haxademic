package com.haxademic.core.debug;

import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;

public class TextEventLog {

	protected String logsDir;
	protected String logFile;
	protected int maxLogFiles = 90;
	
	public TextEventLog() {
		logsDir = FileUtil.getHaxademicOutputPath() + "logs/";
		checkMaxLogFileCount();
	}
	
	public void addEvent(String str) {
		// append or create new log file based on dateStamp (eg. 2019-12-01.txt)
		new Thread(new Runnable() { public void run() {
			String oldLogFile = logFile + "";
			logFile = logsDir + SystemUtil.getDateStamp() + ".txt";
			if(logFile.equals(oldLogFile) == false) checkMaxLogFileCount(); // if date has changed while running, clear old log files if needed 
			FileUtil.createDir(FileUtil.pathForFile(logFile));
			FileUtil.appendTextToFile(logFile, "[" + SystemUtil.getTimestamp() + "] :: " + str + FileUtil.NEWLINE);
		}}).start();
	}
	
	public void setMaxLogFiles(int maxLogFiles) {
		this.maxLogFiles = maxLogFiles;
	}
	
	public void checkMaxLogFileCount() {
		if(FileUtil.fileOrPathExists(logsDir) == false) return;	// don't check if no logs dir
		// get file list, and bail if we haven't crossed the threshold
		String[] filesByDate = FileUtil.getFilesInDirByModifiedDateNewestFirst(logsDir);
		if(filesByDate.length < maxLogFiles) return;
		
		// if we have too many logs, let's delete the oldest
		for (int i = 0; i < filesByDate.length; i++) {
			if(i >= maxLogFiles) {
				FileUtil.deleteFile(filesByDate[i]);
			}
		}
	}
	
	public void appStarted() {
		addEvent("======= App Started =======");
	}
}
