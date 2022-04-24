package com.haxademic.core.render;

import java.io.File;

import com.haxademic.core.app.P;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.text.StringUtil;

import processing.core.PGraphics;

public class ImageSequenceRenderer {

	protected PGraphics pg;
	protected String savePath;
	protected int framesRendered = 0;
	public static String fileType = "png";

	public ImageSequenceRenderer(PGraphics pg) {
		this.pg = pg;
	}
	
	public void setPG(PGraphics pg) {
		this.pg = pg;
	}
	
	public void startImageSequenceRender() {
		savePath = FileUtil.haxademicOutputPath() + SystemUtil.getTimestamp();
		FileUtil.createDir(savePath);
		P.println("== started rendering gif ==");
	}
		
	public void renderImageFrame() {
		if(framesRendered == -1) return;
		framesRendered++;
		P.println("== rendering image frame: "+framesRendered+" ==");
		pg.save(savePath + File.separator + StringUtil.paddedNumberString(10, framesRendered) + "." + fileType);
	}

	public void finish() {
		framesRendered = -1;
		P.println("== finished rendering image sequence ==");
	}
}
