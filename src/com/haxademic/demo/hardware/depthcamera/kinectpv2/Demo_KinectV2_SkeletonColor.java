package com.haxademic.demo.hardware.depthcamera.kinectpv2;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.math.MathUtil;

import KinectPV2.KJoint;
import KinectPV2.KSkeleton;
import KinectPV2.KinectPV2;
import processing.core.PImage;

public class Demo_KinectV2_SkeletonColor
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected KinectPV2 kinectV2;
	protected PImage handImg;
	protected PImage hatImg;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 720 );
		Config.setProperty( AppSettings.PG_WIDTH, 1920 );
		Config.setProperty( AppSettings.PG_HEIGHT, 1080 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {
		// init kinect
		kinectV2 = new KinectPV2(this);
		kinectV2.enableSkeletonColorMap(true);
		kinectV2.enableColorImg(true);
		kinectV2.init();
		
		// load media
		handImg = P.getImage("images/_sketch/foam-finger.png");
		hatImg = P.getImage("images/_sketch/beanie.png");
	}

	protected void drawApp() {
		p.background(0);
		
		// draw to 1080p offscreen canvas to match the inect skeleto coordinates
		pg.beginDraw();
		
		// draw image
		pg.image(kinectV2.getColorImage(), 0, 0, pg.width, pg.height);

		//individual JOINTS
		ArrayList<KSkeleton> skeletonArray =  kinectV2.getSkeletonColorMap();
		for (int i = 0; i < skeletonArray.size(); i++) {
			KSkeleton skeleton = (KSkeleton) skeletonArray.get(i);
			if (skeleton.isTracked()) {
				KJoint[] joints = skeleton.getJoints();

				int col  = skeleton.getIndexColor();
				// col = 255;
				pg.fill(col);
				pg.stroke(col);
				pg.strokeWeight(3);
				drawBody(joints);

				//draw different color for each hand state
				drawHandState(joints[KinectPV2.JointType_HandRight]);
				drawHandState(joints[KinectPV2.JointType_HandLeft]);
				
				// draw foam finger
//				drawHandState(joints[KinectPV2.JointType_HandTipRight]);
				KJoint handJoint = joints[KinectPV2.JointType_HandTipLeft];
				KJoint elbowJoint = joints[KinectPV2.JointType_ElbowLeft];
				float elbowHandDist = P.dist(handJoint.getX(), handJoint.getY(), elbowJoint.getX(), elbowJoint.getY());
				float imgScale = MathUtil.scaleToTarget(handImg.height, elbowHandDist);
				float imgRot = MathUtil.getRadiansToTarget(handJoint.getX(), handJoint.getY(), elbowJoint.getX(), elbowJoint.getY());
				pg.push();
				PG.setDrawCenter(pg);
				pg.noStroke();
				pg.translate(handJoint.getX(), handJoint.getY(), handJoint.getZ());
				pg.rotate(imgRot - P.HALF_PI);
				pg.image(handImg, 0, 0, handImg.width * imgScale, handImg.height * imgScale);
				pg.pop();

				// draw hat
//				drawHandState(joints[KinectPV2.JointType_HandTipRight]);
				KJoint neckJoint = joints[KinectPV2.JointType_Neck];
				KJoint headJoint = joints[KinectPV2.JointType_Head];
				float neckHeadDist = P.dist(headJoint.getX(), headJoint.getY(), neckJoint.getX(), neckJoint.getY());
				imgScale = MathUtil.scaleToTarget(hatImg.height, neckHeadDist) * 1.8f;
				imgRot = MathUtil.getRadiansToTarget(headJoint.getX(), headJoint.getY(), neckJoint.getX(), neckJoint.getY());
				pg.push();
				PG.setDrawCenter(pg);
				pg.noStroke();
				pg.translate(headJoint.getX(), headJoint.getY(), headJoint.getZ());
				pg.rotate(imgRot - P.HALF_PI);
				pg.translate(0, -hatImg.height * imgScale * 0.55f);
				pg.image(hatImg, 0, 0, hatImg.width * imgScale, hatImg.height * imgScale);
				pg.pop();
			}
		}

		pg.fill(255, 0, 0);
		pg.text(frameRate, 50, 50);

		pg.endDraw();
		
		ImageUtil.cropFillCopyImage(pg, p.g, true);
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
		pg.pushMatrix();
		pg.translate(joints[jointType].getX(), joints[jointType].getY(), joints[jointType].getZ());
		pg.ellipse(0, 0, 10, 10);
		pg.popMatrix();
	}

	//draw bone
	void drawBone(KJoint[] joints, int jointType1, int jointType2) {
		pg.pushMatrix();
		pg.translate(joints[jointType1].getX(), joints[jointType1].getY(), joints[jointType1].getZ());
		pg.ellipse(0, 0, 10, 10);
		pg.popMatrix();
		pg.line(joints[jointType1].getX(), joints[jointType1].getY(), joints[jointType1].getZ(), joints[jointType2].getX(), joints[jointType2].getY(), joints[jointType2].getZ());
	}

	//draw hand state
	void drawHandState(KJoint joint) {
		pg.noStroke();
		handState(joint.getState());
		pg.pushMatrix();
		pg.translate(joint.getX(), joint.getY(), joint.getZ());
		pg.ellipse(0, 0, 70, 70);
		pg.popMatrix();
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
			pg.fill(0, 255, 0);
			break;
		case KinectPV2.HandState_Closed:
			pg.fill(255, 0, 0);
			break;
		case KinectPV2.HandState_Lasso:
			pg.fill(0, 0, 255);
			break;
		case KinectPV2.HandState_NotTracked:
			pg.fill(255, 255, 255);
			break;
		}
	}
}
