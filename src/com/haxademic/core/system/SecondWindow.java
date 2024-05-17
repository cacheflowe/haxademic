package com.haxademic.core.system;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.draw.image.ImageUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class SecondWindow {

	public PGraphics srcBuffer;
	public PImage displayImage; // - has to be PImage to be shared between window contexts. PGraphics doesn't work
	public SecondWindowWindow appWindow;
	protected int defaultX;
	protected int defaultY;
	
	public SecondWindow(PGraphics buffer, int x, int y) {
		srcBuffer = buffer;
		defaultX = x;
		defaultY = y;
		displayImage = ImageUtil.newImage(srcBuffer.width, srcBuffer.height);
		P.p.registerMethod(PRegisterableMethods.post, this);	 // update texture to 2nd window after main draw() execution
	}
	
	public void post() {
		if(appWindow == null && P.p.frameCount >= 1) {
			appWindow = new SecondWindowWindow(this);
		}
		ImageUtil.copyImage(srcBuffer, displayImage);
	}

	public void setLocation(int x, int y) {
		AppUtil.setLocation(appWindow, x, y);
	}
	
	public void setSize(int w, int h) {
		AppUtil.setSize(appWindow, w, h);
	}

	public void moveToTop() {
		appWindow.getSurface().setAlwaysOnTop(true);
		appWindow.getSurface().setAlwaysOnTop(false);
	}

	////////////////////////////////////////////////////
	// PAppletHax subclass for second window
	////////////////////////////////////////////////////
	
	class SecondWindowWindow extends PAppletHax {

		protected SecondWindow parentObj;

		public SecondWindowWindow(SecondWindow parentObj) {
			this.parentObj = parentObj;
			runSketch(new String[] {"SecondWindowWindow"}, this);
		}

		public void settings() {
			// THIS OVERRIDES PAppletHax normal initialization... So we're cutting off what it can do, however, this breaks super hard if not
			// size(dstBuffer.width, dstBuffer.height, PRenderers.P3D);
			fullScreen(P.P3D);
		}

		public void setup() {
			P.out("SETUP");
		}
		
		protected void initWindowProps() {
			AppUtil.setLocation(this, defaultX, defaultY);
			AppUtil.setSize(this, displayImage.width, displayImage.height);
			AppUtil.setResizable(this, true);
		}

		public void drawApp() {
			if(frameCount == 5) initWindowProps();
			background(0);
			image(displayImage, 0, 0);
		}

	}
}
