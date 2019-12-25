package com.haxademic.demo.file;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.file.FileUtil;

public class Demo_FileUtil_wordsFromTextFile
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	public void firstFrame() {
		P.out(FileUtil.wordsFromTextFile(FileUtil.getPath("haxademic/text/neuromancer.txt")));
	}

	public void drawApp() {
		p.background(0);
		p.exit();
	}
	
}