package com.haxademic.demo.file;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.demo.file.DropFileWindow.IDropFileDelegate;

import processing.core.PImage;

public class Demo_DropFileWindow 
extends PAppletHax
implements IDropFileDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected DropFileWindow dropFileWindow;
	protected String fileDropped = "Please drop a file on the app window";
	protected PImage img;
	
	protected void firstFrame() {
		dropFileWindow = new DropFileWindow(this);
	}
	
	protected void drawApp() {
		p.background(0);
		p.text(fileDropped, 20, 20);
		if(img != null) p.image(img, 10, 30);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') fileDropped = SystemUtil.getClipboardContents();
	}
	
	public void fileDropped(String filePath) {
		fileDropped = filePath;	// set displayed debug text 
		if(FileUtil.fileExists(filePath)) {
			fileDropped = filePath;
			if(FileUtil.getPathExtension(filePath).equals("png") ||
			   FileUtil.getPathExtension(filePath).equals("jpg")) {
				img = p.loadImage(filePath);
			}
		}
	}

}
