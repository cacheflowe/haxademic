package com.haxademic.core.image;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;

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
		p.beginRecord(hires);
		hires.scale(scaleFactor);
		p.smooth();
		p.draw();
		p.endRecord();
		hires.save( outputDir + SystemUtil.getTimestamp(p) + "hires.png" );
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




//if(frameCount == -1) {
//p.camera();
//PGraphics screenshot = createGraphics(width, height, P3D );
//p.beginRecord(screenshot);
//p.camera();
//_modules.get( _curModule ).update();
//p.endRecord();
//PImage newImg = screenshot.get();
//
//println("screeshotting!");
////filter(THRESHOLD);
//
////		PImage newImg = createImage(1000, 1000, RGB);
////PImage newImg = ScreenUtil.getScreenAsPImage( p5 );
////		println(newImg.width+"x"+newImg.height);
//p.noStroke();
////		newImg.save( "output/saved_img/test.png" );
//p.tint(255, 0, 255, 200f);
////		image( screenshot, 20, 20 );
////		image( screenshot, 40, 40 );
////		image( screenshot, 60, 60 );
////translate(0,0,-100);
////rotateX(TWO_PI/10);
////rotateZ(TWO_PI/10);
//
//
////textureMode(IMAGE);
////imageMode(CORNERS);
////resetMatrix();
////beginShape(QUADS);
////texture(newImg);
////vertex(0, 0, 0, 0);
////vertex(width, 0, width, 0);
////vertex(width, height, width, height);
////vertex(0, 0, 0, height);
////endShape();
////
////translate(10,10,0);
////beginShape(QUADS);
////texture(newImg);
////vertex(0, 0, 0, 0);
////vertex(width, 0, width, 0);
////vertex(width, height, width, height);
////vertex(0, 0, 0, height);
////endShape();
////
////translate(10,10,0);
////translate(0,0,-200);
////beginShape(QUADS);
////texture(newImg);
////vertex(0, 0, 0, 0);
////vertex(width, 0, width, 0);
////vertex(width, height, width, height);
////vertex(0, 0, 0, height);
////endShape();
//
//imageMode(CORNER);
////translate(100,100,0);
//image(newImg, 0, 0, width, height);
//}