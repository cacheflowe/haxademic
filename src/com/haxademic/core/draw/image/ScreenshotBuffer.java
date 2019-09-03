package com.haxademic.core.draw.image;

import java.awt.AWTException;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.peer.RobotPeer;

import com.haxademic.core.app.P;
import com.haxademic.core.system.ScreenUtil;

import processing.core.PImage;
import sun.awt.SunToolkit;

public class ScreenshotBuffer {

	// buffer props
	protected PImage image; 
	protected PImage scaledImg; 
	protected Rectangle2D bounds;
	protected int w = 32;
	protected int h = 32;
	protected boolean needsUpdate = false;

	// screenshot-copying props
	protected SunToolkit toolkit;
	protected RobotPeer robot;
	protected BufferedImage screenshot;

	protected DataBufferInt buffer;
	protected WritableRaster raster;
	protected int[] bandmasks = new int[3];
	protected DirectColorModel screenCapCM = new DirectColorModel(24,
			/* red mask */    0x00FF0000,
			/* green mask */  0x0000FF00,
			/* blue mask */   0x000000FF);


	public ScreenshotBuffer() {
		this(null);
	}

	public ScreenshotBuffer(Rectangle2D bound) {
		// default bounds to capture all screens 
		bounds = (bound == null) ? ScreenUtil.getFullScreenBounds() : bound;
		w = (int) bounds.getWidth();
		h = (int) bounds.getHeight();
		image = new PImage(w, h);
		
		// below ported from Java's Robot class
		// I'm trying to just copy pixel data instead of create new BufferedImage instances, and the like
		// init required native image objects
		toolkit = (SunToolkit) Toolkit.getDefaultToolkit();
		try {
			robot = toolkit.createRobot(new Robot(), GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice());
		} catch (HeadlessException e) { e.printStackTrace(); } catch (AWTException e) {	e.printStackTrace(); }
		int pixels[] = robot.getRGBPixels(bounds.getBounds());
		buffer = new DataBufferInt(pixels, pixels.length);
		bandmasks[0] = screenCapCM.getRedMask();
		bandmasks[1] = screenCapCM.getGreenMask();
		bandmasks[2] = screenCapCM.getBlueMask();
		raster = Raster.createPackedRaster(buffer, w, h, w, bandmasks, null);
		screenshot = new BufferedImage(screenCapCM, raster, false, null);

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
	
	public String base64() {
		return Base64Image.encodePImageToBase64(image, "jpg");
	}
	
	public String base64Scaled() {
		return Base64Image.encodePImageToBase64(scaledImg, "jpg");
	}
	
	public void post() {
		if(needsUpdate == false) return;
		// copy screenshot pixels right into screenshot BufferedImage
		screenshot.setRGB(0, 0, w, h, robot.getRGBPixels(bounds.getBounds()), 0, w);
		// save pixels directly to PImage
		ImageUtil.copyBufferedToPImagePixels(screenshot, image);
		// copy scaled copy if needed
		if(scaledImg != null) ImageUtil.copyImage(image, scaledImg);
		// reset screenshot update flag
		needsUpdate = false;
	}
}
