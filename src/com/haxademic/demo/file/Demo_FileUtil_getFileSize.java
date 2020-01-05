package com.haxademic.demo.file;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.file.FileUtil;

public class Demo_FileUtil_getFileSize
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void firstFrame() {
		P.out(FileUtil.getFileSize(FileUtil.getPath("haxademic/video/fractal-cube.mp4")));
		P.out(FileUtil.getFileSize(FileUtil.getPath("haxademic/images/smiley.png")));
		P.out(FileUtil.getFileSize(FileUtil.getPath("haxademic/svg/x.svg")));
	}

	protected void drawApp() {
		p.background(0);
		p.exit();
	}
	
}