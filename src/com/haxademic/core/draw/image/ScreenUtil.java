package com.haxademic.core.draw.image;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

import com.haxademic.core.system.SystemUtil;

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

	public static PImage getScreenAsPImage( PApplet p ) {
		return p.get();
	}
	
	public static PImage getScreenShotAsPImage( PApplet p ) {
		try {
			Robot robot = new Robot();
			PImage screenshot = new PImage(robot.createScreenCapture(new Rectangle(0,0,p.width,p.height)));
			return screenshot;
		} catch (AWTException e) { }
		return null;
	}

}

