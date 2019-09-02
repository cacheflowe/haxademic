package com.haxademic.core.system;

import java.awt.AWTException;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.peer.RobotPeer;

import com.haxademic.core.app.P;

import processing.core.PApplet;
import processing.core.PImage;
import sun.awt.SunToolkit;

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

	public static BufferedImage getScreenShotNativeAllMonitors(int x, int y, float scale) {
		// NOTE! main monitor must be top left, otherwise, override x+y position  
		Rectangle2D result = new Rectangle2D.Double();
		GraphicsEnvironment localGE = GraphicsEnvironment.getLocalGraphicsEnvironment();
		for (GraphicsDevice gd : localGE.getScreenDevices()) {
			for (GraphicsConfiguration graphicsConfiguration : gd.getConfigurations()) {
				Rectangle2D.union(result, graphicsConfiguration.getBounds(), result);
			}
		}
		// get full screencap
		BufferedImage screenCap = robot().createScreenCapture(new Rectangle(x, y, (int) result.getWidth(), (int) result.getHeight()));
		
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
	
	
	// turn into a fast screenshot class?
	public static SunToolkit toolkit;
	public static RobotPeer robb;
	public static Rectangle bounds;
	public static BufferedImage screenshot;
	
	public static BufferedImage robotPeerScreenshot(int x, int y, int width, int height) {
		try {
			if(toolkit == null) {
				toolkit = (SunToolkit) Toolkit.getDefaultToolkit();
				robb = toolkit.createRobot(new Robot(), GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice());
				bounds = new Rectangle(x, y, width, height);
				screenshot = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			}
			screenshot.setRGB(0, 0, width, height, robb.getRGBPixels(bounds), 0, width);
			return screenshot;
		} catch (HeadlessException e) {
			e.printStackTrace();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		return null;
	}

}

