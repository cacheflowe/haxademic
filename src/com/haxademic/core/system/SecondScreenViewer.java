package com.haxademic.core.system;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.image.ImageUtil;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class SecondScreenViewer {

	PImage srcBuffer;
	PImage scaledBuffer;
	SecondScreenViewerWindow viewerWindow;
	
	public SecondScreenViewer(PGraphics buffer, float scale) {
		srcBuffer = buffer;
		scaledBuffer = P.p.createImage(P.round(buffer.width * scale), P.round(buffer.height * scale), P.ARGB);
		P.p.registerMethod("post", this);	 // update texture to 2nd window after main draw() execution
	}
	
	public void post() {
		if(P.p.frameCount == 10) viewerWindow = new SecondScreenViewerWindow();
		if(viewerWindow != null) {
			scaledBuffer.copy(srcBuffer, 0, 0, srcBuffer.width, srcBuffer.height, 0, 0, scaledBuffer.width, scaledBuffer.height);
			viewerWindow.setImage(scaledBuffer);
		}
	}
	
	class SecondScreenViewerWindow extends PApplet {

		SecondScreenViewer parent;
		public PImage parentScreen;

		public SecondScreenViewerWindow() {
			runSketch(new String[] {"SecondScreenViewerWindow"}, this);
		}

		public void settings() {
			size(scaledBuffer.width, scaledBuffer.height);
			noSmooth();
		}

		public void setup() {
			super.surface.setResizable(true);
		}
		
		public void setImage(PImage img) {
			parentScreen = img;
		}

		public void draw() {
			fill(255);
			if(parentScreen != null) {
				ImageUtil.cropFillCopyImage(parentScreen, g, true);
			}
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
