package com.haxademic.demo.hardware.kinect.pv2;

import java.util.ArrayList;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;

import KinectPV2.KJoint;
import KinectPV2.KSkeleton;
import KinectPV2.KinectPV2;

public class Demo_KinectV2_SkeletonColor
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected KinectPV2 kinect;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true );
		p.appConfig.setProperty( AppSettings.KINECT_V2_WIN_ACTIVE, true );
		//		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, true );
	}

	public void setupFirstFrame() {
		kinect = new KinectPV2(this);

		kinect.enableSkeletonColorMap(true);
		kinect.enableColorImg(true);

		kinect.init();

	}

	public void drawApp() {
		p.background(0);
		
		// draw image
//		image(kinect.getColorImage(), 0, 0, width, height);

		ArrayList<KSkeleton> skeletonArray =  kinect.getSkeletonColorMap();

		//individual JOINTS
		for (int i = 0; i < skeletonArray.size(); i++) {
			KSkeleton skeleton = (KSkeleton) skeletonArray.get(i);
			if (skeleton.isTracked()) {
				KJoint[] joints = skeleton.getJoints();

				int col  = skeleton.getIndexColor();
				// col = 255;
				fill(col);
				stroke(col);
				strokeWeight(3);
				drawBody(joints);

				//draw different color for each hand state
				drawHandState(joints[KinectPV2.JointType_HandRight]);
				drawHandState(joints[KinectPV2.JointType_HandLeft]);
			}
		}

		fill(255, 0, 0);
		text(frameRate, 50, 50);

	}


	//DRAW BODY
	protected void drawBody(KJoint[] joints) {
		drawBone(joints, KinectPV2.JointType_Head, KinectPV2.JointType_Neck);
		drawBone(joints, KinectPV2.JointType_Neck, KinectPV2.JointType_SpineShoulder);
		drawBone(joints, KinectPV2.JointType_SpineShoulder, KinectPV2.JointType_SpineMid);
		drawBone(joints, KinectPV2.JointType_SpineMid, KinectPV2.JointType_SpineBase);
		drawBone(joints, KinectPV2.JointType_SpineShoulder, KinectPV2.JointType_ShoulderRight);
		drawBone(joints, KinectPV2.JointType_SpineShoulder, KinectPV2.JointType_ShoulderLeft);
		drawBone(joints, KinectPV2.JointType_SpineBase, KinectPV2.JointType_HipRight);
		drawBone(joints, KinectPV2.JointType_SpineBase, KinectPV2.JointType_HipLeft);

		// Right Arm
		drawBone(joints, KinectPV2.JointType_ShoulderRight, KinectPV2.JointType_ElbowRight);
		drawBone(joints, KinectPV2.JointType_ElbowRight, KinectPV2.JointType_WristRight);
		drawBone(joints, KinectPV2.JointType_WristRight, KinectPV2.JointType_HandRight);
		drawBone(joints, KinectPV2.JointType_HandRight, KinectPV2.JointType_HandTipRight);
		drawBone(joints, KinectPV2.JointType_WristRight, KinectPV2.JointType_ThumbRight);

		// Left Arm
		drawBone(joints, KinectPV2.JointType_ShoulderLeft, KinectPV2.JointType_ElbowLeft);
		drawBone(joints, KinectPV2.JointType_ElbowLeft, KinectPV2.JointType_WristLeft);
		drawBone(joints, KinectPV2.JointType_WristLeft, KinectPV2.JointType_HandLeft);
		drawBone(joints, KinectPV2.JointType_HandLeft, KinectPV2.JointType_HandTipLeft);
		drawBone(joints, KinectPV2.JointType_WristLeft, KinectPV2.JointType_ThumbLeft);

		// Right Leg
		drawBone(joints, KinectPV2.JointType_HipRight, KinectPV2.JointType_KneeRight);
		drawBone(joints, KinectPV2.JointType_KneeRight, KinectPV2.JointType_AnkleRight);
		drawBone(joints, KinectPV2.JointType_AnkleRight, KinectPV2.JointType_FootRight);

		// Left Leg
		drawBone(joints, KinectPV2.JointType_HipLeft, KinectPV2.JointType_KneeLeft);
		drawBone(joints, KinectPV2.JointType_KneeLeft, KinectPV2.JointType_AnkleLeft);
		drawBone(joints, KinectPV2.JointType_AnkleLeft, KinectPV2.JointType_FootLeft);

		drawJoint(joints, KinectPV2.JointType_HandTipLeft);
		drawJoint(joints, KinectPV2.JointType_HandTipRight);
		drawJoint(joints, KinectPV2.JointType_FootLeft);
		drawJoint(joints, KinectPV2.JointType_FootRight);

		drawJoint(joints, KinectPV2.JointType_ThumbLeft);
		drawJoint(joints, KinectPV2.JointType_ThumbRight);

		drawJoint(joints, KinectPV2.JointType_Head);
	}

	//draw joint
	void drawJoint(KJoint[] joints, int jointType) {
		pushMatrix();
		translate(joints[jointType].getX(), joints[jointType].getY(), joints[jointType].getZ());
		ellipse(0, 0, 10, 10);
		popMatrix();
	}

	//draw bone
	void drawBone(KJoint[] joints, int jointType1, int jointType2) {
		pushMatrix();
		translate(joints[jointType1].getX(), joints[jointType1].getY(), joints[jointType1].getZ());
		ellipse(0, 0, 10, 10);
		popMatrix();
		line(joints[jointType1].getX(), joints[jointType1].getY(), joints[jointType1].getZ(), joints[jointType2].getX(), joints[jointType2].getY(), joints[jointType2].getZ());
	}

	//draw hand state
	void drawHandState(KJoint joint) {
		noStroke();
		handState(joint.getState());
		pushMatrix();
		translate(joint.getX(), joint.getY(), joint.getZ());
		ellipse(0, 0, 10, 10);
		popMatrix();
	}

	/*
		Different hand state
		 KinectPV2.HandState_Open
		 KinectPV2.HandState_Closed
		 KinectPV2.HandState_Lasso
		 KinectPV2.HandState_NotTracked
	 */
	void handState(int handState) {
		switch(handState) {
		case KinectPV2.HandState_Open:
			fill(0, 255, 0);
			break;
		case KinectPV2.HandState_Closed:
			fill(255, 0, 0);
			break;
		case KinectPV2.HandState_Lasso:
			fill(0, 0, 255);
			break;
		case KinectPV2.HandState_NotTracked:
			fill(255, 255, 255);
			break;
		}
	}
}
