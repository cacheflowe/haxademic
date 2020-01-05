package com.haxademic.demo.file;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.file.FileUtil;

public class Demo_FileUtil_getFilesInDirByModifiedDate
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void firstFrame() {
		String baseDir = FileUtil.getPath("haxademic/images/");
		
		P.out("Files by modified date (newest first):");
		String[] filesByDate = FileUtil.getFilesInDirByModifiedDateNewestFirst(baseDir);
		for (int i = 0; i < filesByDate.length; i++) {
			P.out(filesByDate[i]);
		}

		P.out("Files by modified date (oldest first):");
		String[] filesByDateRev = FileUtil.getFilesInDirByModifiedDateOldestFirst(baseDir);
		for (int i = 0; i < filesByDateRev.length; i++) {
			P.out(filesByDateRev[i]);
		}
	}

	protected void drawApp() {
		p.background(0);
		p.exit();
	}
	
}