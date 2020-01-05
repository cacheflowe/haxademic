package com.haxademic.sketch.hardware.kinect_openni.lib_demos;

import com.haxademic.core.app.PAppletHax;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PVector;

public class KinectSkeletonDemo 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }



	public SimpleOpenNI  context;

	protected void firstFrame()
	{
		// context = new SimpleOpenNI(this);
		context = new SimpleOpenNI(this,SimpleOpenNI.RUN_MODE_MULTI_THREADED);

		// enable depthMap generation 
		context.enableDepth();

		// enable skeleton generation for all joints
		context.enableUser(0);


		stroke(0,0,255);
		strokeWeight(3);
	}

	protected void drawApp()
	{
		background(200,0,0);
		// update the cam
		context.update();

		// draw depthImageMap
		image(context.depthImage(),0,0);
		
		println("context.getNumberOfUsers() :: "+context.getNumberOfUsers());
		println("context.getUsers() :: "+context.getUsers());

		// draw the skeleton if it's available
		if(context.isTrackingSkeleton(1))
			drawSkeleton(1);
		if(context.isTrackingSkeleton(2))
			drawSkeleton(2);
		if(context.isTrackingSkeleton(3))
			drawSkeleton(3);
	}

	// draw the skeleton with the selected joints
	public void drawSkeleton(int userId)
	{
		// find and project the hand positions to 2d space
		PVector jointPos = new PVector();
		PVector jointPosProjected = new PVector();
		float confidence = 0f;
		fill(0,255,0);

		confidence = context.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_LEFT_HAND,jointPos);
		if (confidence > 0.001f) {			
			context.convertRealWorldToProjective(jointPos,jointPosProjected);
			ellipse(jointPosProjected.x, jointPosProjected.y, 35, 35);
		}
		
		confidence = context.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_RIGHT_HAND,jointPos);
		if (confidence > 0.001f) {			
			context.convertRealWorldToProjective(jointPos,jointPosProjected);
			ellipse(jointPosProjected.x, jointPosProjected.y, 35, 35);
		}

		// default limb drawing
		context.drawLimb(userId, SimpleOpenNI.SKEL_HEAD, SimpleOpenNI.SKEL_NECK);

		context.drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_LEFT_SHOULDER);
		context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_LEFT_ELBOW);
		context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_LEFT_HAND);

		context.drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_RIGHT_SHOULDER);
		context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_RIGHT_ELBOW);
		context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_ELBOW, SimpleOpenNI.SKEL_RIGHT_HAND);

		context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_TORSO);
		context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_TORSO);

		context.drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_LEFT_HIP);
		context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_HIP, SimpleOpenNI.SKEL_LEFT_KNEE);
		context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_KNEE, SimpleOpenNI.SKEL_LEFT_FOOT);

		context.drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_RIGHT_HIP);
		context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_HIP, SimpleOpenNI.SKEL_RIGHT_KNEE);
		context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_KNEE, SimpleOpenNI.SKEL_RIGHT_FOOT);  
	}

	// -----------------------------------------------------------------
	// SimpleOpenNI events

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
			context.startTrackingSkeleton(userId); 
		} 
		else 
		{ 
			println("  Failed to calibrate user !!!");
			println("  Start pose detection");
//			context.startPoseDetection("Psi",userId);
		}
	}

	public void onStartPose(String pose,int userId)
	{
		println("onStartPose - userId: " + userId + ", pose: " + pose);
		println(" stop pose detection");

//		context.stopPoseDetection(userId); 
//		context.requestCalibrationSkeleton(userId, true);
	}

	public void onEndPose(String pose,int userId)
	{
		println("onEndPose - userId: " + userId + ", pose: " + pose);
	}
}
