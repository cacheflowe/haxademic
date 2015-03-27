package com.haxademic.sketch.hardware.kinect_openni;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

import com.haxademic.core.app.P;
import com.haxademic.core.hardware.kinect.IKinectWrapper;
import com.haxademic.core.hardware.kinect.KinectSize;
import com.haxademic.core.hardware.kinect.KinectWrapperV2;

@SuppressWarnings("serial")
public class KinectDepthWrapper 
extends PApplet {

	IKinectWrapper _kinectWrapper;
	float        zoomF =0.5f;
	float        rotX = radians(0);//radians(180);  // by default rotate the hole scene 180deg around the x-axis, 
	// the data from openni comes upside down
	float        rotY = radians(0);

	public void setup()
	{
		frameRate(300);

		size(1024,768,P3D);  // strange, get drawing error in the cameraFrustum if i use P3D, in opengl there is no problem

		//context = new SimpleOpenNI(this,SimpleOpenNI.RUN_MODE_SINGLE_THREADED);
		//_kinectWrapper = new KinectWrapper( this, true, true, true );
		
		//TODO: This is temporary. Redesign to use dependency injection of the Kinect services
		_kinectWrapper = new KinectWrapperV2( this, true, true, true );
		
		stroke(255,255,255);
		smooth();
		perspective(radians(45),(float)width/(float)height,10,150000);
	}

	public void draw()
	{
		// update the cam
		_kinectWrapper.update();

		background(0,0,0);

		translate(width/2, height/2, 0);
//		rotateX(rotX);
//		rotateY(rotY);
		scale(zoomF);

		int steps = 4;  // to speed up the drawing, draw every third point
		PVector curPoint;
		int curZ;

		translate(0,0,-1000);  // set the rotation center of the scene 1000 infront of the camera

		stroke(255);
		
		PFont debugFont = createFont("Arial",50);
		textFont( debugFont );
		fill(255,255,255);
		int kwidth = KinectSize.WIDTH;
		int kheight = KinectSize.HEIGHT;
		
		for(int y=0; y < kheight; y+=steps) {
			for(int x=0; x < kwidth; x+=steps) {
				// draw raw data in millimeters
				curPoint = _kinectWrapper.getRealWorldDepthForKinectPixel(x, y);
				stroke(255,255,255);
				if( curPoint != null ) { 
					point( curPoint.x,curPoint.y,curPoint.z );  // make realworld z negative, in the 3d drawing coordsystem +z points in the direction of the eye
					pushMatrix();
					translate( curPoint.x,curPoint.y,curPoint.z );
//					if( x % 30 == 0 && y == KinectSize.WIDTH / 2 ) text(""+curPoint.z);
					if( x == kwidth / 2 && y == kheight / 2 ) P.println("real:  x"+curPoint.x+"  y:"+curPoint.y+"  z:"+curPoint.z);
					popMatrix();
				}
				// draw raw data in millimeters
				curZ = _kinectWrapper.getMillimetersDepthForKinectPixel(x, y);
				stroke(0,255,255);
				if( curPoint != null ) point(x,y,curZ);
				pushMatrix();
				translate( x,y,-curZ );
				if( x == kwidth / 2 && y == kheight / 2 ) P.println("depth: "+curZ);
				popMatrix();
			}
		}

		// draw the kinect cam
		_kinectWrapper.drawCamFrustum();
	}


	@SuppressWarnings("deprecation")
	public void keyPressed()
	{
		switch(key)
		{
			case ' ':
				_kinectWrapper.setMirror(!_kinectWrapper.isMirrored());
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
