package com.haxademic.core.draw.image;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.haxademic.core.app.P;
import com.haxademic.core.system.ScreenshotUtil;

import processing.core.PImage;

public class ScreenshotBuffer {

	// buffer props
	protected PImage image; 
	protected PImage scaledImg; 
	protected Rectangle2D bounds;
	protected int w = 32;
	protected int h = 32;
	protected boolean needsUpdate = false;

	// screenshot-copying props
	protected Robot robot;
	protected BufferedImage screenshot;


	public ScreenshotBuffer() {
		this(null);
	}

	public ScreenshotBuffer(Rectangle2D bound) {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// default bounds to capture all screens 
		bounds = (bound == null) ? ScreenshotUtil.getFullScreenBounds() : bound;
		w = (int) bounds.getWidth();
		h = (int) bounds.getHeight();
		image = new PImage(w, h);
		
		// get new screenshot...
		// it's okay because garbage collection :-/
				Rectangle capture = new Rectangle((int)bounds.getX(), (int)bounds.getY(), (int)bounds.getWidth(), (int)bounds.getHeight());
				screenshot = robot.createScreenCapture(capture);
		
		// listen for Processing post command for threaded drawing 
		P.p.registerMethod("post", this);
	}
	
	public void addScaledImage(float scale) {
		if(scaledImg == null) scaledImg = new PImage(P.round(w * scale), P.round(h * scale));
	}

	public void needsUpdate(boolean needsUpdate) {
		this.needsUpdate = needsUpdate;
	}

	public PImage image() {
		return image;
	}

	public PImage scaledImg() {
		return scaledImg;
	}
	
	public String base64(float quality) {
		return Base64Image.encodeImageToBase64Jpeg(image, quality);
	}
	
	public String base64Scaled(float quality) {
		return Base64Image.encodeImageToBase64Jpeg(scaledImg, quality);
	}
	
	public void updateScreenshot() {
		// copy screenshot pixels right into screenshot BufferedImage
		// screenshot.setRGB(0, 0, w, h, robot.getRGBPixels(bounds.getBounds()), 0, w);
		
		// do old fashioned way
		Rectangle capture = new Rectangle((int)bounds.getX(), (int)bounds.getY(), (int)bounds.getWidth(), (int)bounds.getHeight());
		screenshot = robot.createScreenCapture(capture);

		// save pixels directly to PImage
		ImageUtil.copyBufferedToPImagePixels(screenshot, image);
		// copy scaled copy if needed
		if(scaledImg != null) ImageUtil.copyImage(image, scaledImg);
	}
	
	public void post() {
		if(needsUpdate == false) return;	// only take screenshot if requested
		updateScreenshot();					// update on UI thread
		needsUpdate = false;				// reset screenshot update flag
	}
}
