package com.haxademic.core.system;

import java.awt.AWTException;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.haxademic.core.app.P;

import processing.core.PApplet;
import processing.core.PImage;

public class ScreenUtil {

	// singleton robot
	public static Robot robot;
	public static Robot robot() {
		if(robot == null) {
			try { 
				robot = new Robot(); 
			} catch( Exception error ) { 
				P.out("couldn't init Robot for screensaver disabling"); 
			}
		}
		return robot;
	}

	public static String saveScreenshot( PApplet p, String outputDir ) {
		String filename = outputDir + SystemUtil.getTimestampFine() + ".png";
		p.saveFrame( filename );
		return filename;
	}

	public static PImage getScreenShotAsPImage(int x, int y, int w, int h) {
		return new PImage(robot().createScreenCapture(new Rectangle(x, y, w, h)));
	}

	public static PImage getScreenShotAllMonitors() {
		return getScreenShotAllMonitors(0,0, 1);
	}

	public static PImage getScreenShotAllMonitors(int x, int y, float scale) {
		return new PImage(getScreenShotNativeAllMonitors(x, y, scale));
	}

	public static BufferedImage getScreenShotNativeAllMonitors() {
		return getScreenShotNativeAllMonitors(0, 0, 1);
	}

	
	public static Rectangle fullscreenBounds;
	
	public static Rectangle2D getFullScreenBounds() {
		// NOTE! main monitor must be top left, otherwise, override x+y position  
		Rectangle2D result = new Rectangle2D.Double();
		GraphicsEnvironment localGE = GraphicsEnvironment.getLocalGraphicsEnvironment();
		for (GraphicsDevice gd : localGE.getScreenDevices()) {
			for (GraphicsConfiguration graphicsConfiguration : gd.getConfigurations()) {
				Rectangle2D.union(result, graphicsConfiguration.getBounds(), result);
			}
		}
		return result;
	}
	
	public static BufferedImage getScreenShotNativeAllMonitors(int x, int y, float scale) {
		// get full screencap bounds
		Rectangle2D bounds = getFullScreenBounds();
		BufferedImage screenCap = robot().createScreenCapture(new Rectangle(x, y, (int) bounds.getWidth(), (int) bounds.getHeight()));
		
		// scale down if scale is set
		if(scale != 1) {
			int scaledScreenshotW = P.round(screenCap.getWidth() * scale);
			int scaledScreenshotH = P.round(screenCap.getHeight() * scale);
			BufferedImage scaledImg = new BufferedImage(scaledScreenshotW, scaledScreenshotH, BufferedImage.TYPE_INT_ARGB);
			scaledImg.getGraphics().drawImage(screenCap.getScaledInstance(scaledScreenshotW, scaledScreenshotH, 0), 0, 0, null);
			screenCap = scaledImg; 
		}

		return screenCap;
	}

	@Deprecated
	public static PImage getScreenshotMainMonitor(int x, int y, int width, int height) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		//DisplayMode mode = gs[0].getDisplayMode();
		Rectangle bounds = new Rectangle(x, y, width, height);
		BufferedImage desktop = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		try {
			desktop = new Robot(gs[0]).createScreenCapture(bounds);
		}
		catch(AWTException e) {
			System.err.println("Screen capture failed.");
		}

		return new PImage(desktop);
	}
	
}

