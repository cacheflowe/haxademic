package com.haxademic.sketch.visualgorithms;

import java.awt.Point;
import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class BlobDetectBlackAndWhite
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
//	protected Movie video;
	protected PImage testImage;
	protected PGraphics videoBuffer;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
	}
	
	public void firstFrame() {
//		video = DemoAssets.movieKinectSilhouette();
//		video.loop();
//		video.speed(0.1f);
		
		testImage = P.getImage("haxademic/images/test-bw-shapes.jpg");
	}

	public void drawApp() {
		PG.setDrawCenter(p);
		p.background(0);
		PG.setPImageAlpha(p, 0.4f);
		p.image(testImage, testImage.width/2, testImage.height/2);
		PG.resetPImageAlpha(p);
		
		// load pixels and iterate over grid, looking for a starting point
		// draw shapes - find launch points
//		if(video.width > 20) {
//			if(videoBuffer == null) videoBuffer = p.createGraphics(video.width, video.height, PRenderers.P2D);
//			ImageUtil.copyImage(video, videoBuffer);
			
			testImage.loadPixels();
			int searchPixelSkip = 10;
			
			recurseCount = 0;
			p.noStroke();
			int objectCheckCount = 0;
//			if(p.frameCount == 10) {
				for (int x = 0; x < testImage.width; x+=searchPixelSkip) {
					for (int y = 0; y < testImage.height; y+=searchPixelSkip) {
						if(pixelWhite(x, y)) {
							p.fill(255, 0, 0);
							p.rect(x, y, 2, 2);
	
							
							objectCheckCount++;
							if(objectCheckCount == 1) { 	// test just a single object for now
								
								searchFromPixel(x, y);
								// reload pixels since we're setting pixels green as checked
		//						testImage.loadPixels();
							}
						}
					}			
				}
//			}
//		}
				
		
		// create averaged points
		ArrayList<Point> averagedPoints = new ArrayList<Point>();
		for (int i = 0; i < storedPoints.size(); i++) {
			if(i % 3 == 0 && i > 0) {
				float avgX = MathUtil.averageOfThree(storedPoints.get(i-2).x, storedPoints.get(i-1).x, storedPoints.get(i).x);// (storedPoints.get(i-1).x + storedPoints.get(i).x) / 2f; 
				float avgY = MathUtil.averageOfThree(storedPoints.get(i-2).y, storedPoints.get(i-1).y, storedPoints.get(i).y);//(storedPoints.get(i-1).y + storedPoints.get(i).y) / 2f; 
				averagedPoints.add(new Point((int) avgX, (int) avgY));
			}
			
		}
		
		// draw stored points
		fill(0, 255, 0);
		stroke(0);
		beginShape();
		for (int i = 1; i < storedPoints.size() - P.round(Mouse.xNorm * 150); i++) {
			p.vertex(storedPoints.get(i-1).x, storedPoints.get(i-1).y, storedPoints.get(i).x, storedPoints.get(i).y);
			// p.ellipse(averagedPoints.get(i).x, averagedPoints.get(i).y, 5, 5);
		}
		endShape();
	}
	
	protected boolean pixelWhite(int x, int y) {
		int pixelColor = ImageUtil.getPixelColor(testImage, x, y);
		float redColor = (float) ColorUtil.redFromColorInt(pixelColor);
		return (redColor > 127);
	}
	
	protected boolean pixelChecked(int x, int y) {
		int pixelColor = ImageUtil.getPixelColor(testImage, x, y);
		float greenColor = (float) ColorUtil.greenFromColorInt(pixelColor);
		return (greenColor > 127 && pixelWhite(x, y) == false);
	}
	
	protected void searchFromPixel(int x, int y) {
		recurseCount = 0;
		searchComplete = false;
		points = new ArrayList<Point>();
		storedPoints = new ArrayList<Point>();
		checkNeighbors(x, y, P.LEFT);
	}

	protected int recurseCount = 0;
	protected int neighborCheckDist = 10; 
	protected ArrayList<Point> points;
	protected ArrayList<Point> storedPoints;
	protected boolean searchComplete = false;
	
	protected void checkNeighbors(int x, int y, int origin) {
		// figure out when to stop recursing
		recurseCount++;
		Point newPoint = new Point(x, y);
//		P.out(recurseCount, newPoint);
		
		if(searchComplete == true) return;
		// check if we've already checked this point
		if(points.size() > 0 && points.get(0).equals(newPoint)) {
//			P.out(recurseCount, "back to start!");
			return;
		}
		if(recurseCount > 2500) {
//			P.out(recurseCount, "too many recursions!");
			return;
		}
		
		// store point if not already stored
		boolean pointStored = false;
		for (int i = 0; i < points.size(); i++) {
			if(points.get(i).equals(newPoint)) {
				pointStored = true;
				break;
			}
		}
		if(pointStored == false) {
			points.add(newPoint);
//			P.out("points.size()", points.size(), newPoint);
		} else {
			return;
		}

		
		boolean neighborUp = pixelWhite(x, y - neighborCheckDist); 
		boolean neighborRight = pixelWhite(x + neighborCheckDist, y); 
		boolean neighborDown = pixelWhite(x, y + neighborCheckDist); 
		boolean neighborLeft = pixelWhite(x - neighborCheckDist, y);

		// draw debug
		int debugGreenSize = 4;
		p.fill(0, 255, 0);
		p.rect(x, y, debugGreenSize, debugGreenSize);
		if(neighborUp) p.rect(x, y - neighborCheckDist, debugGreenSize, debugGreenSize);
		if(neighborRight) p.rect(x + neighborCheckDist, y, debugGreenSize, debugGreenSize);
		if(neighborDown) p.rect(x, y + neighborCheckDist, debugGreenSize, debugGreenSize);
		if(neighborLeft) p.rect(x - neighborCheckDist, y, debugGreenSize, debugGreenSize);
		
		p.stroke(255);
		if(origin == P.UP) p.line(x, y, x, y - 6);
		if(origin == P.DOWN) p.line(x, y, x, y + 6);
		if(origin == P.LEFT) p.line(x, y, x - 6, y);
		if(origin == P.RIGHT) p.line(x, y, x + 6, y);
		p.noStroke();
		
		// are we (and our neighbors) not fully inside or outside?
		boolean allActive = neighborUp && neighborRight && neighborDown && neighborLeft;
		boolean allInactive = !neighborUp && !neighborRight && !neighborDown && !neighborLeft;
		
		// don't reverse and go back to check origin direction
		if(!allActive && !allInactive) {			
			// good pixel, keep going
			p.fill(0, 255, 255);
			p.rect(x, y, debugGreenSize, debugGreenSize);
			
			// store it, since we're right on the line
			storedPoints.add(newPoint);
			
//			P.out(recurseCount, "NOT DONE YET");
			// check neighbors
			if(origin != P.UP)    checkNeighbors(x, y - neighborCheckDist, P.DOWN); 
			if(origin != P.RIGHT) checkNeighbors(x + neighborCheckDist, y, P.LEFT); 
			if(origin != P.DOWN)  checkNeighbors(x, y + neighborCheckDist, P.UP); 
			if(origin != P.LEFT)  checkNeighbors(x - neighborCheckDist, y, P.RIGHT);
		} else {
			p.fill(0, 0, 255);
			p.rect(x, y, debugGreenSize, debugGreenSize);
		}
	}
}
