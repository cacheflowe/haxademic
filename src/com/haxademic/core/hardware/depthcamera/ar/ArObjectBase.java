package com.haxademic.core.hardware.depthcamera.ar;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.depthcamera.KinectV2SkeletonsAR;
import com.haxademic.core.math.MathUtil;

import KinectPV2.KJoint;
import KinectPV2.KQuaternion;
import KinectPV2.KSkeleton;
import KinectPV2.KinectPV2;
import processing.core.PGraphics;
import processing.core.PVector;


public class ArObjectBase
	implements IArElement {
		
	protected PVector position;
	protected PVector positionOffset;
	protected PVector pivotOffset;
	protected PVector rotation;
	protected PVector rotationOffset;
	protected float userScale;
	protected float baseScale;
	protected KSkeleton skeleton2d;
	protected KSkeleton skeleton3d;
	protected boolean isActive = false;
	protected boolean isReset = true;
	protected BodyTrackType bodyTrackType;
	
	public ArObjectBase(float baseScale, BodyTrackType bodyTrackType) {
		this.baseScale = baseScale;
		this.bodyTrackType = bodyTrackType;
		
		// init position
		this.position = new PVector();
		this.positionOffset = new PVector(0, 0, 0);
		this.pivotOffset = new PVector();
		this.rotation = new PVector();
		this.rotationOffset = new PVector();
		this.userScale = 1f;
		this.isReset = true;
	}
	
	public IArElement setBaseScale(float baseScale) {
		if(!floatIsSafe(baseScale)) return this;
		this.baseScale = safeFloat(baseScale, 1);
		return this;
	}
	
	public void setActive(boolean isActive) {
		this.isActive = isActive;
		if(isActive) this.isReset = true;
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public PVector position() {
		return position;
	}
	
	public IArElement setPosition(float x, float y, float z) {
		if(!floatIsSafe(x) || !floatIsSafe(y) || !floatIsSafe(z)) return this;
		x = safeFloat(x, 0);
		y = safeFloat(y, 0);
		z = safeFloat(z, 0);
		if(isReset) {
			position.set(x, y, z);
		} else {
			position.lerp(x, y, z, 0.3f);
		}
		return this;
	}
	
	public IArElement setPositionOffset(float x, float y, float z) {
		if(!floatIsSafe(x) || !floatIsSafe(y) || !floatIsSafe(z)) return this;
		x = safeFloat(x, 0);
		y = safeFloat(y, 0);
		z = safeFloat(z, 0);
		positionOffset.set(x, y, z);
		return this;
	}
	
	public IArElement setPivotOffset(float x, float y, float z) {
		if(!floatIsSafe(x) || !floatIsSafe(y) || !floatIsSafe(z)) return this;
		x = safeFloat(x, 0);
		y = safeFloat(y, 0);
		z = safeFloat(z, 0);
		pivotOffset.set(x, y, z);
		return this;
	}
	
	public IArElement setRotation(float x, float y, float z) {
		if(!floatIsSafe(x) || !floatIsSafe(y) || !floatIsSafe(z)) return this;
		x = safeFloat(x, 0);
		y = safeFloat(y, 0);
		z = safeFloat(z, 0);
		x += rotationOffset.x;
		y += rotationOffset.y;
		z += rotationOffset.z;
		if(isReset) {
			rotation.set(x, y, z);
		} else {
			// handle lerping around 360 degrees - 
			// change current rotation vector to match the updated wrapped number
			if(Math.abs(rotation.z - z) > P.PI) {
				if(z < rotation.z) rotation.z -= P.TWO_PI;
				else rotation.z += P.TWO_PI;
			}
			rotation.lerp(x, y, z, 0.3f);
		}
		return this;
	}
	
	public IArElement setRotationOffset(float x, float y, float z) {
		if(!floatIsSafe(x) || !floatIsSafe(y) || !floatIsSafe(z)) return this;
		x = safeFloat(x, 0);
		y = safeFloat(y, 0);
		z = safeFloat(z, 0);
		rotationOffset.set(x, y, z);
		return this;
	}
	
	public IArElement setScale(float scale) {
		if(!floatIsSafe(scale)) return this;
		scale = safeFloat(scale, 1);
		// this is set from KinectSkeletons as skeleton data is processed
		if(isReset) {
			this.userScale = scale;
		} else {
			this.userScale = P.lerp(this.userScale, scale, 0.3f);
		}
		return this;
	}
	
	public IArElement setJoints(KJoint[] joints2d, KJoint[] joints3d) {
		// some predefined joint tracking. 
		// you can override this and do something totally custom 
		switch (bodyTrackType) {
			case HEAD: 						setPositionForHead(joints2d, joints3d); break;
			case HAND_LEFT: 				setPositionHand(joints2d, joints3d, false, false); break;
			case HAND_POINT_LEFT: 			setPositionHand(joints2d, joints3d, true, false); break;
			case HAND_RIGHT: 				setPositionHand(joints2d, joints3d, false, true); break;
			case HAND_POINT_RIGHT: 			setPositionHand(joints2d, joints3d, true, true); break;
			case HANG_ON_SHOULDERS: 		setPositionHangFromShoulders(joints2d, joints3d); break;
			case HAND_FLAG: 				setPositionForHandFlag(joints2d, joints3d); break;
			case WAIST: 					setPositionWaist(joints2d, joints3d); break;
			default: break;
		}
		return this;
	}
	
	protected float safeFloat(float num, float defaultNum) {
		if(floatIsSafe(num)) return num;
		else return defaultNum;
	}
	
	protected boolean floatIsSafe(float num) {
		return num > Float.NEGATIVE_INFINITY && num < Float.POSITIVE_INFINITY && Float.isNaN(num) == false;
	}
	
	public void updatePre(PGraphics pg) {
		
	}
	
	public void drawOrigin(PGraphics pg) {
		PG.setDrawCenter(pg);
		pg.push();
		pg.fill(0, 255, 0);
		pg.noStroke();
		pg.translate(position.x, position.y);
		pg.ellipse(0, 0, 60, 60);
		pg.pop();
		PG.setDrawCorner(pg);
	}
	
	public void draw(PGraphics pg) {
		// override this!
	}
	
	protected void setRotationOnContext(PGraphics pg) {
		pg.rotateX(rotation.x);
		pg.rotateY(rotation.y);
		pg.rotateZ(rotation.z);
	}
	
	protected void setPositionForHead(KJoint[] joints2d, KJoint[] joints3d) {
		// get position & rotation of head
		KJoint headJoint3 = joints3d[KinectPV2.JointType_SpineShoulder];
		KQuaternion q1 = headJoint3.getOrientation();
		double q1x = q1.getX();
		double q1y = q1.getY();
		double q1z = q1.getZ();
		double q1w = q1.getW();
		double sqw = q1w*q1w;
		double sqx = q1x*q1x;
		double sqy = q1y*q1y;
		double sqz = q1z*q1z;
		float attitudeX = (float) Math.asin(-2.0 * (q1x*q1z - q1y*q1w)/(sqx + sqy + sqz + sqw));
		// float headingY = (float) Math.atan2(2.0 * (q1x*q1y + q1z*q1w),(sqx - sqy - sqz + sqw));
		float bankZ = (float) Math.atan2(2.0 * (q1y*q1z + q1x*q1w),(-sqx - sqy + sqz + sqw));
		bankZ = (bankZ + P.PI); // wraps around -HALF_PI when standing straight up, but base is HALF_PI< so rotate a lil more
		if(bankZ > P.PI) bankZ = bankZ - P.TWO_PI;
		DebugView.setValue("bankZ", bankZ);
		// scale based on spine-should to spine-mid
		KJoint headJoint = joints2d[KinectPV2.JointType_Head];
		KJoint spineShoulderJoint = joints2d[KinectPV2.JointType_SpineShoulder];
		KJoint spineMidJoint = joints2d[KinectPV2.JointType_SpineMid];
		float imgRot =  -P.HALF_PI + MathUtil.getRadiansToTarget(spineShoulderJoint.getX(), spineShoulderJoint.getY(), spineMidJoint.getX(), spineMidJoint.getY());
		float rotY = (this instanceof ArElementObj) ? attitudeX * 0.5f : 0;	// only rotate on y-axis is it's a 3d model
		float rotX = (this instanceof ArElementObj) ? bankZ * -0.5f : 0;	    // only rotate on x-axis is it's a 3d model
		float rotZAmp = 1.f;
		
		// set position
		setPosition(headJoint.getX(), headJoint.getY(), headJoint.getZ());
		setRotation(rotX, rotY, imgRot * rotZAmp);
	}

	protected void setPositionWaist(KJoint[] joints2d, KJoint[] joints3d) {
		// get joint positions
		KJoint spineMidJoint = joints2d[KinectPV2.JointType_SpineMid];
		KJoint spineShoulderJoint = joints2d[KinectPV2.JointType_SpineShoulder];
		KJoint spineBaseJoint = joints2d[KinectPV2.JointType_SpineBase];
		
		// get position & rotation of waist
		KQuaternion q1 = spineMidJoint.getOrientation();
		double q1x = q1.getX();
		double q1y = q1.getY();
		double q1z = q1.getZ();
		double q1w = q1.getW();
		double sqw = q1w*q1w;
		double sqx = q1x*q1x;
		double sqy = q1y*q1y;
		double sqz = q1z*q1z;
		float attitudeX = (float) Math.asin(-2.0 * (q1x*q1z - q1y*q1w)/(sqx + sqy + sqz + sqw));
		
		// scale based on spine-should to spine-mid
		float imgRot =  -P.HALF_PI + MathUtil.getRadiansToTarget(spineShoulderJoint.getX(), spineShoulderJoint.getY(), spineBaseJoint.getX(), spineBaseJoint.getY());
		float rotY = (this instanceof ArElementObj) ? attitudeX * 0.5f : 0;
		float rotZAmp = 1.f;
		
		// set position
		setPosition(spineMidJoint.getX(), spineMidJoint.getY(), spineMidJoint.getZ());
		setRotation(0, rotY, imgRot * rotZAmp);
	}
	
	protected void setPositionHangFromShoulders(KJoint[] joints2d, KJoint[] joints3d) {
		// get position & rotation of head
		KJoint headJoint3 = joints3d[KinectPV2.JointType_SpineShoulder];
		KQuaternion q1 = headJoint3.getOrientation();
		double q1x = q1.getX();
		double q1y = q1.getY();
		double q1z = q1.getZ();
		double q1w = q1.getW();
		double sqw = q1w*q1w;
		double sqx = q1x*q1x;
		double sqy = q1y*q1y;
		double sqz = q1z*q1z;
		float attitudeX = (float) Math.asin(-2.0 * (q1x*q1z - q1y*q1w)/(sqx + sqy + sqz + sqw));
		float headingY = (float) Math.atan2(2.0 * (q1x*q1y + q1z*q1w),(sqx - sqy - sqz + sqw));
		float bankZ = (float) Math.atan2(2.0 * (q1y*q1z + q1x*q1w),(-sqx - sqy + sqz + sqw));
		
		// scale based on spine-should to spine-mid
		KJoint neckJoint = joints2d[KinectPV2.JointType_Neck];
		KJoint headJoint = joints2d[KinectPV2.JointType_Head];
		KJoint spineShoulderJoint = joints2d[KinectPV2.JointType_SpineShoulder];
		KJoint spineMidJoint = joints2d[KinectPV2.JointType_SpineMid];
		float halfSpineDist = P.dist(spineShoulderJoint.getX(), spineShoulderJoint.getY(), spineMidJoint.getX(), spineMidJoint.getY());
		
		// use distance between spine-mid and head as the base body-size distance to put objects above head
		// - Multiply by the camera scale based on resizing camera to fit vertically at the current resolution
		// - Multiply by a constant, then additional custom multiplication
		float shoulderHeadDist = P.dist(spineMidJoint.getX(), spineMidJoint.getY(), headJoint.getX(), headJoint.getY());
		shoulderHeadDist *= KinectV2SkeletonsAR.CAMERA_DISPLAY_SCALE;
		shoulderHeadDist *= 1.3f;
		shoulderHeadDist *= positionOffset.y;

		float imgRot = MathUtil.getRadiansToTarget(spineShoulderJoint.getX(), spineShoulderJoint.getY(), spineMidJoint.getX(), spineMidJoint.getY());
		
		// set position
		setPosition(spineShoulderJoint.getX(), spineShoulderJoint.getY() - shoulderHeadDist, spineShoulderJoint.getZ());
		setRotation(0, attitudeX * 0.4f, imgRot - P.HALF_PI);
	}
	
	protected void setPositionForHandFlag(KJoint[] joints2d, KJoint[] joints3d) {
		KJoint leftElbowJoint = joints2d[KinectPV2.JointType_ElbowLeft];
		KJoint leftHandJoint = joints2d[KinectPV2.JointType_WristLeft];
		KJoint leftElbowJoint3 = joints3d[KinectPV2.JointType_ElbowLeft];
		KJoint leftHandJoint3 = joints3d[KinectPV2.JointType_WristLeft];
		PVector elbowV = new PVector(leftElbowJoint.getX(), leftElbowJoint.getY(), leftElbowJoint3.getZ() * 100f);
		PVector handV = new PVector(leftHandJoint.getX(), leftHandJoint.getY(), leftHandJoint3.getZ() * 100f);

		// get 3d hand rotation???
		KJoint handJoint3 = joints3d[KinectPV2.JointType_HandLeft];
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

		//				drawHandState(joints[KinectPV2.JointType_HandTipRight]);
		KJoint handJoint = joints2d[KinectPV2.JointType_HandLeft];
		KJoint elbowJoint = joints2d[KinectPV2.JointType_ElbowLeft];
		float elbowHandDist = P.dist(handJoint.getX(), handJoint.getY(), elbowJoint.getX(), elbowJoint.getY());
//		float imgScale = MathUtil.scaleToTarget(handImg.height, elbowHandDist);
		float imgRot = MathUtil.getRadiansToTarget(handJoint.getX(), handJoint.getY(), elbowJoint.getX(), elbowJoint.getY());

		setPosition(handJoint.getX(), handJoint.getY(), handJoint.getZ());
		setRotation(0, 0, P.constrain(imgRot - P.HALF_PI, -0.4f, 0.4f));
	}
	
	protected void setPositionHand(KJoint[] joints2d, KJoint[] joints3d, boolean rotates, boolean isRightHand) {
		KJoint elbowJoint = 		joints2d[(isRightHand) ? KinectPV2.JointType_ElbowRight : KinectPV2.JointType_ElbowLeft];
//		KJoint elbowJoint3 = 		joints3d[(isRightHand) ? KinectPV2.JointType_ElbowRight : KinectPV2.JointType_ElbowLeft];
		KJoint handJoint3 = 		joints3d[(isRightHand) ? KinectPV2.JointType_HandRight : KinectPV2.JointType_HandLeft];
		KJoint handJoint = 			joints2d[(isRightHand) ? KinectPV2.JointType_HandRight : KinectPV2.JointType_HandLeft];
//		KJoint leftHandJoint3 = 	joints3d[(isRightHand) ? KinectPV2.JointType_WristRight : KinectPV2.JointType_WristLeft];
//		KJoint handTipJoint = 		joints2d[(isRightHand) ? KinectPV2.JointType_HandTipRight : KinectPV2.JointType_HandTipLeft];
//		PVector elbowV = new PVector(elbowJoint.getX(), elbowJoint.getY(), elbowJoint3.getZ() * 100f);
//		PVector handV = new PVector(handJoint.getX(), handJoint.getY(), leftHandJoint3.getZ() * 100f);
		
		// get 3d hand rotation???
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
		
		//				drawHandState(joints[KinectPV2.JointType_HandTipRight]);
		float elbowHandDist = P.dist(handJoint.getX(), handJoint.getY(), elbowJoint.getX(), elbowJoint.getY());
		float rotY = (this instanceof ArElementObj) ? attitudeX * 0.5f : 0;


		float imgRot = MathUtil.getRadiansToTarget(elbowJoint.getX(), elbowJoint.getY(), handJoint.getX(), handJoint.getY());
		if(rotates == false) imgRot *= 0.1f;
		
		float x = handJoint.getX();
		float y = handJoint.getY();
		float z = handJoint.getZ();

		setPosition(x, y, z);
		setRotation(0, rotY, imgRot);
	}
	
}