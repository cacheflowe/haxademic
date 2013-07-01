//package com.haxademic.sketch.hardware.kinect_openkinect_old;
//
//import org.openkinect.processing.Kinect;
//
//import processing.core.PApplet;
//import processing.core.PConstants;
//import processing.core.PImage;
//import processing.core.PVector;
//
//public class KinectBlobCenterPoint extends PApplet {
//
//	KinectTracker tracker;
//	// Kinect Library object
//	Kinect kinect;
//
//	public void setup() {
//		size(640, 520);
//		kinect = new Kinect(this);
//		tracker = new KinectTracker();
//		
//	}
//
//	public void draw() {
//		//background(0);
//
//		// Run the tracking analysis
//		tracker.track();
//		// Show the image
//		tracker.display();
//
//		// Let's draw the raw location
//		PVector v1 = tracker.getPos();
//		fill(50, 100, 250, 200);
//		noStroke();
//		ellipse(v1.x, v1.y, 20, 20);
//
//		// Let's draw the "lerped" location
//		PVector v2 = tracker.getLerpedPos();
//		fill(100, 250, 50, 200);
//		noStroke();
//		ellipse(v2.x, v2.y, 20, 20);
//
//		// Display some info
//		int t = tracker.getThreshold();
//		//fill(0);
//	}
//
//	public void keyPressed() {
//		int t = tracker.getThreshold();
//		if (key == CODED) {
//			if (keyCode == UP) {
//				t += 5;
//				tracker.setThreshold(t);
//			} else if (keyCode == DOWN) {
//				t -= 5;
//				tracker.setThreshold(t);
//			}
//		}
//	}
//
//	public void stop() {
//		kinect.quit();
//		super.stop();
//	}
//
//	class KinectTracker {
//
//		// Size of kinect image
//		int kw = 640;
//		int kh = 480;
//		int threshold = 745;
//
//		// Raw location
//		PVector loc;
//
//		// Interpolated location
//		PVector lerpedLoc;
//
//		// Depth data
//		int[] depth;
//
//		PImage display;
//
//		KinectTracker() {
//			kinect.start();
//			kinect.enableDepth(true);
//
//			// We could skip processing the grayscale image for efficiency
//			// but this example is just demonstrating everything
//			kinect.processDepthImage(true);
//
//			display = createImage(kw, kh, PConstants.RGB);
//
//			loc = new PVector(0, 0);
//			lerpedLoc = new PVector(0, 0);
//		}
//
//		void track() {
//
//			// Get the raw depth as array of integers
//			depth = kinect.getRawDepth();
//
//			// Being overly cautious here
//			if (depth == null)
//				return;
//
//			float sumX = 0;
//			float sumY = 0;
//			float count = 0;
//
//			for (int x = 0; x < kw; x++) {
//				for (int y = 0; y < kh; y++) {
//					// Mirroring the image
//					int offset = kw - x - 1 + y * kw;
//					// Grabbing the raw depth
//					int rawDepth = depth[offset];
//
//					// Testing against threshold
//					if (rawDepth < threshold) {
//						sumX += x;
//						sumY += y;
//						count++;
//					}
//				}
//			}
//			// As long as we found something
//			if (count != 0) {
//				loc = new PVector(sumX / count, sumY / count);
//			}
//
//			// Interpolating the location, doing it arbitrarily for now
//			lerpedLoc.x = PApplet.lerp(lerpedLoc.x, loc.x, 0.3f);
//			lerpedLoc.y = PApplet.lerp(lerpedLoc.y, loc.y, 0.3f);
//		}
//
//		PVector getLerpedPos() {
//			return lerpedLoc;
//		}
//
//		PVector getPos() {
//			return loc;
//		}
//
//		void display() {
//			//fill(0,55);
//			//rect(0,0,width,height);
//			tint(255,30); 
//			PImage img = kinect.getDepthImage();
//
//			// Being overly cautious here
//			if (depth == null || img == null)
//				return;
//			
//			int numPixelsDrawn = 0;
//
//			// Going to rewrite the depth image to show which pixels are in
//			// threshold
//			// A lot of this is redundant, but this is just for demonstration
//			// purposes
//			display.loadPixels();
//			//display.
//			for (int x = 0; x < kw; x++) {
//				for (int y = 0; y < kh; y++) {
//					// mirroring image
//					int offset = kw - x - 1 + y * kw;
//					// Raw depth
//					int rawDepth = depth[offset];
//
//					int pix = x + y * display.width;
//					if (rawDepth < threshold) {
//						// A red color instead
//						display.pixels[pix] = color(150, 50, 50);
//						numPixelsDrawn++;
//					} else {
//						display.pixels[pix] = color(0,0,0,0);//img.pixels[offset];
//					}
//				}
//			}
//			display.updatePixels();
//			
//			//if(numPixelsDrawn > 2000 && numPixelsDrawn < 10000) {
//			// Draw the image
//			image(display, 0, 0);
//			
//		}
//
//		void quit() {
//			kinect.quit();
//		}
//
//		int getThreshold() {
//			return threshold;
//		}
//
//		void setThreshold(int t) {
//			threshold = t;
//		}
//	}
//
//}
