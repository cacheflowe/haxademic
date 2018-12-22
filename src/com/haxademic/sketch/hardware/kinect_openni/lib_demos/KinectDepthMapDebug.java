package com.haxademic.sketch.hardware.kinect_openni.lib_demos;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;
import processing.core.PVector;

public class KinectDepthMapDebug extends PApplet {

	SimpleOpenNI context;
	float        zoomF =0.3f;
	float        rotX = radians(180);  // by default rotate the hole scene 180deg around the x-axis, 
	// the data from openni comes upside down
	float        rotY = radians(0);

	public void setup()
	{
		frameRate(300);

		size(1024,768,P3D);  // strange, get drawing error in the cameraFrustum if i use P3D, in opengl there is no problem

		//context = new SimpleOpenNI(this,SimpleOpenNI.RUN_MODE_SINGLE_THREADED);
		context = new SimpleOpenNI(this);

		// disable mirror
		context.setMirror(false);

		// enable depthMap generation 
		if(context.enableDepth() == false)
		{
			println("Can't open the depthMap, maybe the camera is not connected!"); 
			exit();
			return;
		}

		stroke(255,255,255);
		smooth();
		perspective(radians(45),(float)width/(float)height,10,150000);
	}

	public void draw()
	{
		// update the cam
		context.update();

		background(0,0,0);

		translate(width/2, height/2, 0);
		rotateX(rotX);
		rotateY(rotY);
		scale(zoomF);

		int[]   depthMap = context.depthMap();
		int     steps   = 3;  // to speed up the drawing, draw every third point
		int     index;
		PVector realWorldPoint;

		translate(0,0,-1000);  // set the rotation center of the scene 1000 infront of the camera

		stroke(255);

		PVector[] realWorldMap = context.depthMapRealWorld();
		for(int y=0;y < context.depthHeight();y+=steps)
		{
			for(int x=0;x < context.depthWidth();x+=steps)
			{
				index = x + y * context.depthWidth();
				if(depthMap[index] > 0)
				{ 
					// draw the projected point
					//		         realWorldPoint = context.depthMapRealWorld()[index];
					realWorldPoint = realWorldMap[index];
					point(realWorldPoint.x,realWorldPoint.y,realWorldPoint.z);  // make realworld z negative, in the 3d drawing coordsystem +z points in the direction of the eye
				}
				//println("x: " + x + " y: " + y);
			}
		} 

		// draw the kinect cam
		context.drawCamFrustum();
	}


	public void keyPressed()
	{
		switch(key)
		{
			case ' ':
				context.setMirror(!context.mirror());
				break;
		}

		switch(keyCode)
		{
			case LEFT:
				rotY += 0.1f;
				break;
			case RIGHT:
				// zoom out
				rotY -= 0.1f;
				break;
			case UP:
				if(keyEvent.isShiftDown())
					zoomF += 0.02f;
				else
					rotX += 0.1f;
				break;
			case DOWN:
				if(keyEvent.isShiftDown())
				{
					zoomF -= 0.02f;
					if(zoomF < 0.01)
						zoomF = 0.01f;
				}
				else
					rotX -= 0.1f;
				break;
		}

	}
}
