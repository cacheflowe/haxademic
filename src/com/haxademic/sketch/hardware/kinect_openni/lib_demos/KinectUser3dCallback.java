package com.haxademic.sketch.hardware.kinect_openni.lib_demos;

import processing.core.PApplet;
import processing.core.PMatrix3D;
import processing.core.PVector;
import SimpleOpenNI.SimpleOpenNI;

public class KinectUser3dCallback extends PApplet {
	SimpleOpenNI context;
	float        zoomF =0.5f;
	float        rotX = radians(180);  // by default rotate the hole scene 180deg around the x-axis, 
	// the data from openni comes upside down
	float        rotY = radians(0);

	// this object deals with the user callbacks
	UserManager  userManager;

	public void setup()
	{
	  size(1024, 768, P3D);  // strange, get drawing error in the cameraFrustum if i use P3D, in opengl there is no problem
	  context = new SimpleOpenNI(this);

	  // setup the callback helper class
	  userManager = new UserManager(context);
	  
	  // disable mirror
	  context.setMirror(false);

	  // enable depthMap generation 
	  if(context.enableDepth() == false)
	  {
	     println("Can't open the depthMap, maybe the camera is not connected!"); 
	     exit();
	     return;
	  }

	  // enable skeleton generation for all joints, direct all callback to the helper class
	  context.enableUser();

	  stroke(255, 255, 255);
	  smooth();  
	  perspective(radians(45), 
	              (float)width/(float)height, 
	              10, 150000);
	}

	public void draw()
	{
	  // update the cam
	  context.update();

	  background(0, 0, 0);

	  // set the scene pos
	  translate(width/2, height/2, 0);
	  rotateX(rotX);
	  rotateY(rotY);
	  scale(zoomF);

	  int[]   depthMap = context.depthMap();
	  int     steps   = 3;  // to speed up the drawing, draw every third point
	  int     index;
	  PVector realWorldPoint;

	  translate(0, 0, -1000);  // set the rotation center of the scene 1000 infront of the camera

	  stroke(100); 
	  for (int y=0;y < context.depthHeight();y+=steps)
	  {
	    for (int x=0;x < context.depthWidth();x+=steps)
	    {
	      index = x + y * context.depthWidth();
	      if (depthMap[index] > 0)
	      { 
	        // draw the projected point
	        realWorldPoint = context.depthMapRealWorld()[index];
	        point(realWorldPoint.x, realWorldPoint.y, realWorldPoint.z);
	      }
	    }
	  } 

	  // draw the skeleton if it's available
	  int[] userList = context.getUsers();
	  for(int i=0;i<userList.length;i++)
	  {
	    if(context.isTrackingSkeleton(userList[i]))
	      drawSkeleton(userList[i]);
	  }    

	  // draw the kinect cam
	  context.drawCamFrustum();
	}

	// draw the skeleton with the selected joints
	void drawSkeleton(int userId)
	{
	  strokeWeight(3);

	  // to get the 3d joint data
	  drawLimb(userId, SimpleOpenNI.SKEL_HEAD, SimpleOpenNI.SKEL_NECK);

	  drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_LEFT_SHOULDER);
	  drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_LEFT_ELBOW);
	  drawLimb(userId, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_LEFT_HAND);

	  drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_RIGHT_SHOULDER);
	  drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_RIGHT_ELBOW);
	  drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_ELBOW, SimpleOpenNI.SKEL_RIGHT_HAND);

	  drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_TORSO);
	  drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_TORSO);

	  drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_LEFT_HIP);
	  drawLimb(userId, SimpleOpenNI.SKEL_LEFT_HIP, SimpleOpenNI.SKEL_LEFT_KNEE);
	  drawLimb(userId, SimpleOpenNI.SKEL_LEFT_KNEE, SimpleOpenNI.SKEL_LEFT_FOOT);

	  drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_RIGHT_HIP);
	  drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_HIP, SimpleOpenNI.SKEL_RIGHT_KNEE);
	  drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_KNEE, SimpleOpenNI.SKEL_RIGHT_FOOT);  

	  strokeWeight(1);
	}

	void drawLimb(int userId, int jointType1, int jointType2)
	{
	  PVector jointPos1 = new PVector();
	  PVector jointPos2 = new PVector();
	  float  confidence;

	  // draw the joint position
	  confidence = context.getJointPositionSkeleton(userId, jointType1, jointPos1);
	  confidence = context.getJointPositionSkeleton(userId, jointType2, jointPos2);

	  stroke(255, 0, 0, confidence * 200 + 55);
	  line(jointPos1.x, jointPos1.y, jointPos1.z, 
	  jointPos2.x, jointPos2.y, jointPos2.z);

	  drawJointOrientation(userId, jointType1, jointPos1, 50);
	}

	void drawJointOrientation(int userId, int jointType, PVector pos, float length)
	{
	  // draw the joint orientation  
	  PMatrix3D  orientation = new PMatrix3D();
	  float confidence = context.getJointOrientationSkeleton(userId, jointType, orientation);
	  if (confidence < 0.001f) 
	    // nothing to draw, orientation data is useless
	    return;

	  pushMatrix();
	  translate(pos.x, pos.y, pos.z);

	  // set the local coordsys
	  applyMatrix(orientation);

	  // coordsys lines are 100mm long
	  // x - r
	  stroke(255, 0, 0, confidence * 200 + 55);
	  line(0, 0, 0, 
	  length, 0, 0);
	  // y - g
	  stroke(0, 255, 0, confidence * 200 + 55);
	  line(0, 0, 0, 
	  0, length, 0);
	  // z - b    
	  stroke(0, 0, 255, confidence * 200 + 55);
	  line(0, 0, 0, 
	  0, 0, length);
	  popMatrix();
	}

	// -----------------------------------------------------------------
	// Keyboard events

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
	    if (keyEvent.isShiftDown())
	      zoomF += 0.01f;
	    else
	      rotX += 0.1f;
	    break;
	  case DOWN:
	    if (keyEvent.isShiftDown())
	    {
	      zoomF -= 0.01f;
	      if (zoomF < 0.01f)
	        zoomF = 0.01f;
	    }
	    else
	      rotX -= 0.1f;
	    break;
	  }
	}

	// -----------------------------------------------------------------
	// UserManager

	public class UserManager
	{
	  protected SimpleOpenNI  _context;
	  boolean                         _autoCalib=true;
	  
	  public UserManager(SimpleOpenNI context)
	  {
	    _context = context;
	  }
	  
		// -----------------------------------------------------------------
		// SimpleOpenNI user events

		void onNewUser(SimpleOpenNI curContext,int userId)
		{
		  println("onNewUser - userId: " + userId);
		  println("\tstart tracking skeleton");
		  
		  context.startTrackingSkeleton(userId);
		}

		void onLostUser(SimpleOpenNI curContext,int userId)
		{
		  println("onLostUser - userId: " + userId);
		}

		void onVisibleUser(SimpleOpenNI curContext,int userId)
		{
		  //println("onVisibleUser - userId: " + userId);
		}



	  public void onExitUser(int userId)
	  {
	    println("onExitUser - userId: " + userId);
	  }

	  public void onReEnterUser(int userId)
	  {
	    println("onReEnterUser - userId: " + userId);
	  }

	  public void onStartCalibration(int userId)
	  {
	    println("onStartCalibration - userId: " + userId);
	  }

	  public void onEndCalibration(int userId, boolean successfull)
	  {
	    println("onEndCalibration - userId: " + userId + ", successfull: " + successfull);

	    if (successfull) 
	    { 
	      println("  User calibrated !!!");
	      _context.startTrackingSkeleton(userId);
	    } 
	    else 
	    { 
	      println("  Failed to calibrate user !!!");
	      println("  Start pose detection");
//	      _context.startPoseDetection("Psi", userId);
	    }
	  }

	  public void onStartPose(String pose, int userId)
	  {
	    println("onStartdPose - userId: " + userId + ", pose: " + pose);
	    println(" stop pose detection");

//	    _context.stopPoseDetection(userId); 
//	    _context.requestCalibrationSkeleton(userId, true);
	  }

	  public void onEndPose(String pose, int userId)
	  {
	    println("onEndPose - userId: " + userId + ", pose: " + pose);
	  }
	}

}
