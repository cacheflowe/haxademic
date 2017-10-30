package com.haxademic.sketch.volume;

import java.io.PrintWriter;
import java.util.ArrayList;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.shapes.MarchingCubes;
import com.haxademic.core.hardware.kinect.KinectSize;

import processing.core.PImage;
import processing.core.PVector;
import toxi.geom.Vec3D;

public class CubeMarchTest 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// from: http://iwearshorts.com/blog/bubble-boy2/

	MarchingCubes mc;
	Vec3D rotationAxis;

	Boolean bUseFill;

	// kinect
	float a = 0;

	float deg = 8; // Start at 15 degrees
	PImage depthImg;
	int minDepth =  40;
	int maxDepth = 1860;
	// set initial record to false
	boolean record = false;
	int counter = 0;
	// print custom file
	boolean printFile = false;
	ArrayList points;
	PrintWriter output;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, "true" );
		p.appConfig.setProperty( AppSettings.WIDTH, "1024" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "768" );
	}


	public void setup() {
		super.setup();

		Vec3D aabbMin = new Vec3D(-width*2, -height*2, -1500);
		Vec3D aabbMax = new Vec3D(width*2, height*2, 1500);
		Vec3D numPoints = new Vec3D(50,50,50);
		float isoLevel = 2;
		mc = new MarchingCubes(this, aabbMin, aabbMax, numPoints, isoLevel);

		rotationAxis = new Vec3D();

		bUseFill = false;
		points = new ArrayList();
	}

	public void drawApp() {
		p.background(0);
		
		int skip = 20;
		float pixelDepth = 0;
		//translate(width/750,height/750,-50);
		mc.reset();

		// original for loop
//		println("entering loop");
		int nBalls = 0;
		for ( int x = 0; x < KinectSize.WIDTH; x += skip ) {
			for ( int y = 0; y < KinectSize.HEIGHT; y += skip ) {
				pixelDepth = p.kinectWrapper.getMillimetersDepthForKinectPixel( x, y );
				if( pixelDepth != 0 && pixelDepth > minDepth && pixelDepth < maxDepth ) {

					PVector v = new PVector(x,y,pixelDepth);
					Vec3D metaBallPos = new Vec3D(v.x, v.y, -v.z);
					mc.addMetaBall(metaBallPos, 30, 3);
					nBalls++;

				}
			}
		}


//		println("done with loop, " + nBalls + " balls");
		// end original for loop

		mc.createMesh();
		if(bUseFill){
			fill(0,255,0);
			noStroke();
		}
		else {
			noFill();
			stroke(127);
		}

		pushMatrix();
//		translate(width/2, height/2, -500);
		rotateX(rotationAxis.x);
		rotateY(rotationAxis.y);
		mc.renderMesh();
		popMatrix();
	}
	
	public void keyPressed(){
		if(key == CODED){
			if(keyCode == LEFT) rotationAxis.y += 0.05;
			if(keyCode == RIGHT) rotationAxis.y -= 0.05;
			if(keyCode == UP) rotationAxis.x -= 0.05;
			if(keyCode == DOWN) rotationAxis.x += 0.05;
		}
		else {
			if(key == ' '){
				bUseFill = !bUseFill;
			}
			if(key == 'r' || key == 'R'){
				mc.reset();
				rotationAxis.set(0,0,0);
			}
		}
	}
}
