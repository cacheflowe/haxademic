package com.haxademic.demo.file;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;

public class Demo_FileUtil_getFilesInDirOfTypes
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PImage[] pImages;
	
	public void firstFrame() {
		ArrayList<String> images = FileUtil.getFilesInDirOfTypes(FileUtil.getFile("haxademic/images"), "jpg,png");
		pImages = new PImage[images.size()];
		for (int i = 0; i < images.size(); i++) {
			P.out(images.get(i));
			pImages[i] = p.loadImage(images.get(i));
		}
	}

	public void drawApp() {
		p.background(0);
		for (int i = 0; i < pImages.length; i++) {
			p.image(pImages[i], i * 100, 0);
		}
	}
	
}