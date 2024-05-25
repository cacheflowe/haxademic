package com.haxademic.core.system;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRegisterableMethods;

import processing.core.PApplet;
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
		displayImage = newImage(srcBuffer.width, srcBuffer.height);
		P.p.registerMethod(PRegisterableMethods.post, this); // update texture to 2nd window after main draw() execution
	}

	public static PImage newImage(int w, int h) {
		return P.p.createImage(w, h, P.ARGB);
	}

	public void post() {
		// create window if it doesn't exist
		if (appWindow == null && P.p.frameCount >= 1) {
			appWindow = new SecondWindowWindow(this);
		}

		// copy display image pixels
		// - this was super slow:
		// ImageUtil.copyImage(srcBuffer, displayImage);
		// - ...so we copy the pixel array instead. This is also not great - maybe we should use Spout instead?
		srcBuffer.loadPixels();
		if (srcBuffer.pixels != null && displayImage.pixels != null) {
			System.arraycopy(srcBuffer.pixels, 0, displayImage.pixels, 0, srcBuffer.pixels.length);
			displayImage.updatePixels();
		}
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

	class SecondWindowWindow extends PApplet {

		protected SecondWindow parentObj;

		public SecondWindowWindow(SecondWindow parentObj) {
			this.parentObj = parentObj;
			runSketch(new String[] { "SecondWindowWindow" }, this);
		}

		public void settings() {
			// size(dstBuffer.width, dstBuffer.height, PRenderers.P3D);
			fullScreen(P.P3D); // use fullscreen to remove window chrome
		}

		public void setup() {
		}

		protected void initWindowProps() {
			AppUtil.setSize(this, displayImage.width, displayImage.height);
			AppUtil.setLocation(this, defaultX, defaultY);
			AppUtil.setResizable(this, true);
		}

		public void draw() {
			if (frameCount == 5) initWindowProps();
			image(displayImage, 0, 0);
		}
	}
}