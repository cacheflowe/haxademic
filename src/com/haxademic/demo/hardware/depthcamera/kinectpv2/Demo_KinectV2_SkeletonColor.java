package com.haxademic.demo.hardware.depthcamera.kinectpv2;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.OrientationUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;

import KinectPV2.KJoint;
import KinectPV2.KQuaternion;
import KinectPV2.KSkeleton;
import KinectPV2.KinectPV2;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

public class Demo_KinectV2_SkeletonColor
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected KinectPV2 kinectV2;
	protected PImage handImg;
	protected PImage hatImg;
	protected PImage shirtImg;
	protected PImage logoImg;
	protected PShape shape1;
	protected PShape shape2;
	protected PShape shape3;
	protected PShape shape4;
	protected PShape shape5;
	protected PShape shape6;

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
		kinectV2.enableSkeleton3DMap(true);
		kinectV2.enableColorImg(true);
		kinectV2.init();

		// load media
		//		handImg = P.getImage("images/_sketch/foam-finger.png");
		//		hatImg = P.getImage("images/_sketch/beanie.png");
		handImg = P.getImage("haxademic/images/cursor-hand.png");
		hatImg = P.getImage("haxademic/images/cursor-finger-trans.png");
		logoImg = P.getImage("images/_sketch/atl/atl-logo-circle.png");
		shirtImg = P.getImage("images/_sketch/atl/atl-jersey.png");
		shape1 = DemoAssets.objSkullRealistic();
		PShapeUtil.scaleVertices(shape1, 50f);
		shape2 = p.loadShape( FileUtil.getPath("svg/diamond.svg")).getTessellation();
		// center and scale
		PShapeUtil.centerShape(shape2);
		PShapeUtil.scaleShapeToExtent(shape2, p.height * 0.2f);
		PShapeUtil.repairMissingSVGVertex(shape2);
		shape2 = PShapeUtil.createExtrudedShape( shape2, 100 );

		shape3 = PShapeUtil.shapeFromImage(P.getImage("images/_sketch/pixel-objects/skull-mini.png"));
		PShapeUtil.scaleShapeToHeight(shape3, pg.height * 0.28f);
		PShapeUtil.scaleVertices(shape3, 1, 1, 3);

		shape5 = PShapeUtil.shapeFromImage(P.getImage("images/_sketch/pixel-objects/sunglasses-deal-with-it.png"));
		PShapeUtil.scaleShapeToWidth(shape5, pg.width * 0.14f);
		PShapeUtil.scaleVertices(shape5, 1, 1, 3);

		shape4 = PShapeUtil.shapeFromImage(P.getImage("images/_sketch/pixel-objects/hand-peace.png"));
		PShapeUtil.scaleShapeToHeight(shape4, pg.height * 0.28f);
		PShapeUtil.scaleVertices(shape4, 1, 1, 1);

		shape6 = PShapeUtil.shapeFromImage(P.getImage("images/_sketch/pixel-objects/hand-point.png"));
		PShapeUtil.scaleShapeToHeight(shape6, pg.height * 0.28f);
		PShapeUtil.scaleVertices(shape6, 1, 1, 1);
	}

	protected void drawApp() {
		p.background(0);

		// draw to 1080p offscreen canvas to match the inect skeleto coordinates
		pg.beginDraw();
		PG.setDrawFlat2d(pg, false);
		pg.background(kinectV2.getColorImage());

		// draw image
		//		pg.image(kinectV2.getColorImage(), 0, 0, pg.width, pg.height);
		pg.lights();
		pg.ortho();
		pg.perspective();

		//individual JOINTS
		// TODO: track skeletons between frames. would a KSkeleton object be the same as the previous frame? check this first!
		ArrayList<KSkeleton> skeletonArray =  kinectV2.getSkeletonColorMap();
		ArrayList<KSkeleton> skeletonArray3 =  kinectV2.getSkeleton3d();
		DebugView.setValue("skeletonArray.size()", skeletonArray.size());
		DebugView.setValue("skeletonArray3.size()", skeletonArray3.size());
		for (int i = 0; i < skeletonArray.size(); i++) {
			KSkeleton skeleton = (KSkeleton) skeletonArray.get(i);
			KSkeleton skeleton3 = (KSkeleton) skeletonArray3.get(i);
			if (skeleton.isTracked() && skeleton3.isTracked()) {
				KJoint[] joints = skeleton.getJoints();
				KJoint[] joints3 = skeleton3.getJoints();

				int col  = skeleton.getIndexColor();
				// col = 255;
				pg.fill(col);
				pg.stroke(col);
				pg.strokeWeight(3);
				drawBody(joints);

				//draw different color for each hand state
				drawHandState(joints[KinectPV2.JointType_HandRight]);
				drawHandState(joints[KinectPV2.JointType_HandLeft]);

				// draw left hand
				{
					KJoint leftElbowJoint = joints[KinectPV2.JointType_ElbowLeft];
					KJoint leftHandJoint = joints[KinectPV2.JointType_WristLeft];
					KJoint leftElbowJoint3 = joints3[KinectPV2.JointType_ElbowLeft];
					KJoint leftHandJoint3 = joints3[KinectPV2.JointType_WristLeft];
					PVector elbowV = new PVector(leftElbowJoint.getX(), leftElbowJoint.getY(), leftElbowJoint3.getZ() * 100f);
					PVector handV = new PVector(leftHandJoint.getX(), leftHandJoint.getY(), leftHandJoint3.getZ() * 100f);

					// get 3d hand rotation???
					KJoint handJoint3 = joints3[KinectPV2.JointType_HandLeft];
					KQuaternion q1 = handJoint3.getOrientation();
					double q1x = q1.getX();
					double q1y = q1.getY();
					double q1z = q1.getZ();
					double q1w = q1.getW();
					double sqw = q1w*q1w;
					double sqx = q1x*q1x;
					double sqy = q1y*q1y;
					double sqz = q1z*q1z;
					float headingY = (float) Math.atan2(2.0 * (q1x*q1y + q1z*q1w),(sqx - sqy - sqz + sqw));
					float bankZ = (float) Math.atan2(2.0 * (q1y*q1z + q1x*q1w),(-sqx - sqy + sqz + sqw));
					float attitudeX = (float) Math.asin(-2.0 * (q1x*q1z - q1y*q1w)/(sqx + sqy + sqz + sqw));
					DebugView.setValue("HandLeft headingY", headingY);
					DebugView.setValue("HandLeft bankZ", bankZ);
					DebugView.setValue("HandLeft attitudeX", attitudeX);

					//				drawHandState(joints[KinectPV2.JointType_HandTipRight]);
					KJoint handJoint = joints[KinectPV2.JointType_HandLeft];
					DebugView.setValue("hand", handJoint.getX() + ", " + handJoint.getY());
					KJoint elbowJoint = joints[KinectPV2.JointType_ElbowLeft];
					float elbowHandDist = P.dist(handJoint.getX(), handJoint.getY(), elbowJoint.getX(), elbowJoint.getY());
					float imgScale = MathUtil.scaleToTarget(handImg.height, elbowHandDist);
					float imgRot = MathUtil.getRadiansToTarget(handJoint.getX(), handJoint.getY(), elbowJoint.getX(), elbowJoint.getY());
					pg.push();
					PG.setDrawCenter(pg);
					pg.noStroke();
					pg.translate(handJoint.getX(), handJoint.getY(), handJoint.getZ());
					pg.rotate(imgRot - P.HALF_PI);
					//					pg.image(handImg, 0, 0, handImg.width * imgScale, handImg.height * imgScale);
					pg.pop();


					// draw extra shape on hand
					pg.pushMatrix();
					pg.translate(handJoint.getX(), handJoint.getY(), 100);
					imgRot = MathUtil.getRadiansToTarget(elbowJoint.getX(), elbowJoint.getY(), handJoint.getX(), handJoint.getY());
					//					pg.rotateX(-bankZ);// - P.HALF_PI);
					pg.rotateZ(imgRot + P.HALF_PI);
					pg.rotateY(attitudeX + P.PI);
					//					OrientationUtil.setRotationTowards(pg, elbowV, handV);
					//					pg.rotateY(-attitudeX + 0.2f);
					//					pg.rotateX(-bankZ + P.PI);// - P.HALF_PI);
					//					pg.rotateZ(imgRot - P.HALF_PI);
					//					pg.shape(shape2, 0, 0);
					pg.shape(shape4, 0, 0);
					pg.popMatrix(); 

					// box between
					//					Shapes.boxBetween(pg, elbowV, handV, 50);
					pg.pushMatrix();
					// set orientation 
					OrientationUtil.setMidPoint(pg, elbowV, handV);
					OrientationUtil.setRotationTowards(pg, elbowV, handV);
					pg.rotateY(attitudeX);
					// draw box
					//					pg.box( 50, elbowV.dist(handV), 50 );
					pg.popMatrix(); 
				}

				// draw right hand
				{
					KJoint rightElbowJoint = joints[KinectPV2.JointType_ElbowRight];
					KJoint rightHandJoint = joints[KinectPV2.JointType_WristRight];
					KJoint rightElbowJoint3 = joints3[KinectPV2.JointType_ElbowRight];
					KJoint rightHandJoint3 = joints3[KinectPV2.JointType_WristRight];
					PVector elbowV = new PVector(rightElbowJoint.getX(), rightElbowJoint.getY(), rightElbowJoint3.getZ() * 100f);
					PVector handV = new PVector(rightHandJoint.getX(), rightHandJoint.getY(), rightHandJoint3.getZ() * 100f);


					// get 3d hand rotation???
					KJoint handJoint3 = joints3[KinectPV2.JointType_HandRight];
					KQuaternion q1 = handJoint3.getOrientation();
					double q1x = q1.getX();
					double q1y = q1.getY();
					double q1z = q1.getZ();
					double q1w = q1.getW();
					double sqw = q1w*q1w;
					double sqx = q1x*q1x;
					double sqy = q1y*q1y;
					double sqz = q1z*q1z;
					float headingY = (float) Math.atan2(2.0 * (q1x*q1y + q1z*q1w),(sqx - sqy - sqz + sqw));
					float bankZ = (float) Math.atan2(2.0 * (q1y*q1z + q1x*q1w),(-sqx - sqy + sqz + sqw));
					float attitudeX = (float) Math.asin(-2.0 * (q1x*q1z - q1y*q1w)/(sqx + sqy + sqz + sqw));
					DebugView.setValue("HandRight headingY", headingY);
					DebugView.setValue("HandRight bankZ", bankZ);
					DebugView.setValue("HandRight attitudeX", attitudeX);

					//				drawHandState(joints[KinectPV2.JointType_HandTipRight]);
					KJoint handJoint = joints[KinectPV2.JointType_HandRight];
					DebugView.setValue("hand", handJoint.getX() + ", " + handJoint.getY());
					KJoint elbowJoint = joints[KinectPV2.JointType_ElbowRight];
					float elbowHandDist = P.dist(handJoint.getX(), handJoint.getY(), elbowJoint.getX(), elbowJoint.getY());
					float imgScale = MathUtil.scaleToTarget(handImg.height, elbowHandDist);
					float imgRot = MathUtil.getRadiansToTarget(handJoint.getX(), handJoint.getY(), elbowJoint.getX(), elbowJoint.getY());
					//					pg.push();
					//					PG.setDrawCenter(pg);
					//					pg.noStroke();
					//					pg.translate(handJoint.getX(), handJoint.getY(), handJoint.getZ());
					//					pg.rotate(imgRot - P.HALF_PI);
//										pg.image(handImg, 0, 0, handImg.width * imgScale, handImg.height * imgScale);
					//					pg.pop();


					// draw extra shape on hand
					pg.pushMatrix();
					pg.translate(handJoint.getX(), handJoint.getY(), 100);
					imgRot = MathUtil.getRadiansToTarget(elbowJoint.getX(), elbowJoint.getY(), handJoint.getX(), handJoint.getY());
					//					pg.rotateX(-bankZ);// - P.HALF_PI);
					pg.rotateZ(imgRot + P.HALF_PI);
					pg.rotateY(attitudeX);
					pg.shape(shape6, 0, 0);
					pg.popMatrix(); 
				}

				{
					// get 3d head rotation
					KJoint headJoint3 = joints3[KinectPV2.JointType_SpineShoulder];
					KQuaternion q1 = headJoint3.getOrientation();
					DebugView.setValue("headRotY", q1.getY());
					DebugView.setValue("headRotX", q1.getX());
					double q1x = q1.getX();
					double q1y = q1.getY();
					double q1z = q1.getZ();
					double q1w = q1.getW();
					double sqw = q1w*q1w;
					double sqx = q1x*q1x;
					double sqy = q1y*q1y;
					double sqz = q1z*q1z;
					float headingY = (float) Math.atan2(2.0 * (q1x*q1y + q1z*q1w),(sqx - sqy - sqz + sqw));
					float bankZ = (float) Math.atan2(2.0 * (q1y*q1z + q1x*q1w),(-sqx - sqy + sqz + sqw));
					float attitudeX = (float) Math.asin(-2.0 * (q1x*q1z - q1y*q1w)/(sqx + sqy + sqz + sqw));

					// draw hat
					//				drawHandState(joints[KinectPV2.JointType_HandTipRight]);
					KJoint neckJoint = joints[KinectPV2.JointType_Neck];
					KJoint headJoint = joints[KinectPV2.JointType_Head];
					float neckHeadDist = P.dist(headJoint.getX(), headJoint.getY(), neckJoint.getX(), neckJoint.getY());
					float imgScale = MathUtil.scaleToTarget(hatImg.height, neckHeadDist) * 1.8f;
					float imgRot = MathUtil.getRadiansToTarget(headJoint.getX(), headJoint.getY(), neckJoint.getX(), neckJoint.getY());
					pg.push();
					PG.setDrawCorner(pg);
					pg.noStroke();
					pg.translate(headJoint.getX(), headJoint.getY(), 0);
					//				pg.rotate(imgRot - P.HALF_PI);
					//				pg.rotateX(headingY);
					pg.rotateY(attitudeX);
					pg.rotateX(-bankZ + P.PI);// - P.HALF_PI);
					pg.rotateZ(imgRot - P.HALF_PI);
					//				pg.translate(0, -hatImg.height * imgScale * 0.55f);
					//				pg.image(hatImg, 0, 0, hatImg.width * imgScale, hatImg.height * imgScale);
					pg.push();
					shape1.disableStyle();
					pg.fill(255);
					//				pg.shape(shape1, 0, 0);
					pg.shape(shape3, 0, 0);
					//				pg.shape(shape5, 0, 0);
					pg.pop();
					pg.pop();
				}
				
				// add a shirt 
				{
					// get 3d head rotation
					KJoint headJoint3 = joints3[KinectPV2.JointType_SpineShoulder];
					KQuaternion q1 = headJoint3.getOrientation();
					DebugView.setValue("headRotY", q1.getY());
					DebugView.setValue("headRotX", q1.getX());
					double q1x = q1.getX();
					double q1y = q1.getY();
					double q1z = q1.getZ();
					double q1w = q1.getW();
					double sqw = q1w*q1w;
					double sqx = q1x*q1x;
					double sqy = q1y*q1y;
					double sqz = q1z*q1z;
					float headingY = (float) Math.atan2(2.0 * (q1x*q1y + q1z*q1w),(sqx - sqy - sqz + sqw));
					float bankZ = (float) Math.atan2(2.0 * (q1y*q1z + q1x*q1w),(-sqx - sqy + sqz + sqw));
					float attitudeX = (float) Math.asin(-2.0 * (q1x*q1z - q1y*q1w)/(sqx + sqy + sqz + sqw));

					// draw hat
					//				drawHandState(joints[KinectPV2.JointType_HandTipRight]);
					KJoint neckJoint = joints[KinectPV2.JointType_Neck];
					KJoint headJoint = joints[KinectPV2.JointType_Head];
					KJoint spineShoulderJoint = joints[KinectPV2.JointType_SpineShoulder];
					KJoint spineMidJoint = joints[KinectPV2.JointType_SpineMid];
					float halfSpineDist = P.dist(spineShoulderJoint.getX(), spineShoulderJoint.getY(), spineMidJoint.getX(), spineMidJoint.getY());
					float imgScale = MathUtil.scaleToTarget(hatImg.height, halfSpineDist) * 0.325f;
					float imgRot = MathUtil.getRadiansToTarget(spineShoulderJoint.getX(), spineShoulderJoint.getY(), spineMidJoint.getX(), spineMidJoint.getY());
					pg.push();
					PG.setDrawCenter(pg);
					pg.noStroke();
					pg.translate(spineMidJoint.getX(), spineMidJoint.getY(), 0);
					//				pg.rotate(imgRot - P.HALF_PI);
					//				pg.rotateX(headingY);
					pg.rotateY(attitudeX);
					pg.rotateX(-bankZ + P.PI);// - P.HALF_PI);
					pg.rotateZ(imgRot - P.HALF_PI);
					//				pg.translate(0, -hatImg.height * imgScale * 0.55f);
					pg.fill(255);
					float shirtYOffset = shirtImg.height * imgScale/10f;
					pg.image(shirtImg, 0, shirtYOffset, shirtImg.width * imgScale, shirtImg.height * imgScale);
					pg.pop();
				}
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
