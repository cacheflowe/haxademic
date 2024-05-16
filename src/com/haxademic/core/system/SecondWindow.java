package com.haxademic.core.system;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.image.ImageUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class SecondWindow {

	public PGraphics srcBuffer;
	public PImage dstBuffer; // - has to be PImage to be shared between window contexts. PGraphics doesn't work
	public SecondWindowWindow viewerWindow = null;
	
	public SecondWindow(PGraphics buffer) {
		srcBuffer = buffer;
		dstBuffer = ImageUtil.newImage(srcBuffer.width, srcBuffer.height);
		P.p.registerMethod(PRegisterableMethods.post, this);	 // update texture to 2nd window after main draw() execution
	}
	
	public void post() {
		if(viewerWindow == null && P.p.frameCount >= 10) {
			viewerWindow = new SecondWindowWindow();
		}
		if(viewerWindow != null) {
			ImageUtil.copyImage(srcBuffer, dstBuffer);
			viewerWindow.setImage(dstBuffer);
		}
	}
	
	class SecondWindowWindow extends PAppletHax {

		SecondWindow parent;
		PImage srcImg;

		public SecondWindowWindow() {
			runSketch(new String[] {"SecondWindow"}, this);
		}

		public void settings() {
			size(dstBuffer.width, dstBuffer.height, PRenderers.P3D);
		}
		
		public void setImage(PImage img) {
			srcImg = img;
		}

		public void drawApp() {
			background(0, 0, 255);
			if(srcImg != null) {
				image(srcImg, 0, 0);
			}
		}

		public void setLocation(int x, int y) {
			surface.setLocation(x, y);
		}

		public void exit() {
			dispose();
		}

		public void moveToTop() {
			surface.setAlwaysOnTop(true);
			surface.setAlwaysOnTop(false);
		}
	}
}
