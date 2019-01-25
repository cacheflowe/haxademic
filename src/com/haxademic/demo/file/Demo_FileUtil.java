package com.haxademic.demo.file;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.file.FileUtil;

public class Demo_FileUtil
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	public void setupFirstFrame() {
		P.out(FileUtil.getFileSize(FileUtil.getFile("haxademic/video/fractal-cube.mp4")));
		P.out(FileUtil.getFileSize(FileUtil.getFile("haxademic/images/smiley.png")));
		P.out(FileUtil.getFileSize(FileUtil.getFile("haxademic/svg/x.svg")));
	}

	public void drawApp() {
		p.background(0);
		p.exit();
	}
	
}