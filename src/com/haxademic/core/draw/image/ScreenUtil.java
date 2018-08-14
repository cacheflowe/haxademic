package com.haxademic.core.draw.image;

import java.awt.AWTException;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.geom.Rectangle2D;

import com.haxademic.core.system.SystemUtil;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class ScreenUtil {

	public static String saveScreenshot( PApplet p, String outputDir ) {
		String filename = outputDir + SystemUtil.getTimestampFine( p ) + ".png";
		p.saveFrame( filename );
		return filename;
	}

	public static void screenshotHiRes( PApplet p, int scaleFactor, String p5Renderer, String outputDir ) {
		// from: http://amnonp5.wordpress.com/2012/01/28/25-life-saving-tips-for-processing/
		PGraphics hires = p.createGraphics(p.width*scaleFactor, p.height*scaleFactor, p5Renderer );
		hires.beginDraw();
		p.beginRecord(hires);
		hires.scale(scaleFactor);
		p.smooth();
		p.draw();
		p.endRecord();
		hires.endDraw();
		hires.save( outputDir + SystemUtil.getTimestamp(p) + "-hires.png" );
		p.noSmooth();
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

}

