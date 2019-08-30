package com.haxademic.core.draw.image;

import java.awt.AWTException;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.haxademic.core.system.SystemUtil;

import processing.core.PApplet;
import processing.core.PImage;

public class ScreenUtil {

	public static String saveScreenshot( PApplet p, String outputDir ) {
		String filename = outputDir + SystemUtil.getTimestampFine() + ".png";
		p.saveFrame( filename );
		return filename;
	}

	public static PImage getScreenShotAsPImage( PApplet p ) {
		try {
			Robot robot = new Robot();
			PImage screenshot = new PImage(robot.createScreenCapture(new Rectangle(0,0,p.width,p.height)));
			return screenshot;
		} catch (AWTException e) { }
		return null;
	}

	public static PImage getScreenShotAllMonitors() {
		return getScreenShotAllMonitors(0,0);
	}
	
	public static PImage getScreenShotAllMonitors(int x, int y) {
		// NOTE! main monitor must be top left, otherwise, override x+y position  
		try {
			Rectangle2D result = new Rectangle2D.Double();
			GraphicsEnvironment localGE = GraphicsEnvironment.getLocalGraphicsEnvironment();
			for (GraphicsDevice gd : localGE.getScreenDevices()) {
				for (GraphicsConfiguration graphicsConfiguration : gd.getConfigurations()) {
					Rectangle2D.union(result, graphicsConfiguration.getBounds(), result);
				}
			}

			Robot robot = new Robot();
			PImage screenshot = new PImage(robot.createScreenCapture(new Rectangle(x, y,(int) result.getWidth(), (int) result.getHeight())));
			return screenshot;
		} catch (AWTException e) { }
		return null;
	}

	public static BufferedImage getScreenShotNativeAllMonitors(int x, int y) {
		// NOTE! main monitor must be top left, otherwise, override x+y position  
		try {
			Rectangle2D result = new Rectangle2D.Double();
			GraphicsEnvironment localGE = GraphicsEnvironment.getLocalGraphicsEnvironment();
			for (GraphicsDevice gd : localGE.getScreenDevices()) {
				for (GraphicsConfiguration graphicsConfiguration : gd.getConfigurations()) {
					Rectangle2D.union(result, graphicsConfiguration.getBounds(), result);
				}
			}
			
			Robot robot = new Robot();
			return robot.createScreenCapture(new Rectangle(x, y,(int) result.getWidth(), (int) result.getHeight()));
		} catch (AWTException e) { }
		return null;
	}
	
}

