package com.haxademic.sketch.hardware.kinect_openkinect;

//Daniel Shiffman
//Thomas Sanchez Lengeling
//Kinect Point Cloud example

//https://github.com/shiffman/OpenKinect-for-Processing
//http://shiffman.net/p5/kinect/

import org.openkinect.processing.Kinect2;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;

import processing.core.PVector;
//import processing.video.Movie;

public class Kinect2DepthPointCloud
extends PAppletHax {

	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	//Kinect Library object
	Kinect2 kinect2;

	//Angle for rotation
	float a = 3.1f;

	//change render mode between openGL and CPU
	int renderMode = 1;

	//for openGL render
	int  vertLoc;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 600 );
	}


	public void setup() {
		super.setup();
		kinect2 = new Kinect2(this);
		kinect2.initDepth();
		kinect2.initDevice();
	}

	public void drawApp() {
		background(0);


		// Translate and rotate
		pushMatrix();
		translate(width/2, height/2, 50);
		rotateY(a);
		hint(DISABLE_DEPTH_MASK);


		// We're just going to calculate and draw every 2nd pixel
		int skip = 4;

		// Get the raw depth as array of integers
		int[] depth = kinect2.getRawDepth();

		float maxDist = 9999999;//map(mouseX, 0, width, 0, 3000);

		stroke(255);
		beginShape(POINTS);
		for (int x = 0; x < kinect2.depthWidth; x+=skip) {
			for (int y = 0; y < kinect2.depthHeight; y+=skip) {
				int offset = x + y * kinect2.depthWidth;
				if(depth[offset] < maxDist) {
					//calculte the x, y, z camera position based on the depth information
					PVector point = depthToPointCloudPos(x, y, depth[offset]);

					// Draw a point
//					vertex(point.x, point.y, point.z);
					vertex(x, y, depth[offset]);
				}
			}
		}
		endShape();
		popMatrix();

		fill(255, 0, 0);
		text(frameRate, 50, 50);

		// Rotate
		a += 0.0015f;	
	}

	//calculte the xyz camera position based on the depth data
	PVector depthToPointCloudPos(int x, int y, float depthValue) {
		PVector point = new PVector();
		point.z = (depthValue);// / (1.0f); // Convert from mm to meters
		point.x = (x - CameraParams.cx) * point.z / CameraParams.fx;
		point.y = (y - CameraParams.cy) * point.z / CameraParams.fy;
		return point;
	}
	
	//camera information based on the Kinect v2 hardware
	public static class CameraParams {
	  static float cx = 254.878f;
	  static float cy = 205.395f;
	  static float fx = 365.456f;
	  static float fy = 365.456f;
	  static float k1 = 0.0905474f;
	  static float k2 = -0.26819f;
	  static float k3 = 0.0950862f;
	  static float p1 = 0.0f;
	  static float p2 = 0.0f;
	}
}



