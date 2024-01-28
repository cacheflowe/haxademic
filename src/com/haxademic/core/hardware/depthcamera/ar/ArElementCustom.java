package com.haxademic.core.hardware.depthcamera.ar;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.depthcamera.KinectV2SkeletonsAR;
import com.haxademic.core.math.MathUtil;

import KinectPV2.KJoint;
import KinectPV2.KQuaternion;
import KinectPV2.KinectPV2;
import processing.core.PGraphics;
import processing.core.PVector;

public class ArElementCustom 
extends ArObjectBase {


	public ArElementCustom(float baseScale) {
		super(baseScale, BodyTrackType.CUSTOM);
	}
	
	public void draw(PGraphics pg) {
		pg.push();
		PG.setBetterLights(pg);
		pg.translate(position.x, position.y);
		setRotationOnContext(pg);
		float responsiveHeight = KinectV2SkeletonsAR.CAMERA_HEIGHT * baseScale;
		pg.translate(
				positionOffset.x * userScale * responsiveHeight, 
				positionOffset.y * userScale * responsiveHeight, 
				positionOffset.z * userScale * responsiveHeight
		);
		pg.scale(userScale);
		pg.rotateY(P.p.frameCount * 0.03f);
		pg.box(responsiveHeight);
		pg.noLights();
		pg.pop();
		
		// once we've drawn, we're lerping
		isReset = false;
	}
	
	@Override
	public IArElement setJoints(KJoint[] joints2d, KJoint[] joints3d) {
		// TODO: add tracking of a second joint with lerping like `setPosition()` does
		
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
		KJoint handJoint = joints2d[KinectPV2.JointType_HandTipLeft];
		KJoint elbowJoint = joints2d[KinectPV2.JointType_ElbowLeft];
		float elbowHandDist = P.dist(handJoint.getX(), handJoint.getY(), elbowJoint.getX(), elbowJoint.getY());


		float imgRot = MathUtil.getRadiansToTarget(elbowJoint.getX(), elbowJoint.getY(), handJoint.getX(), handJoint.getY());
		setPosition(handJoint.getX(), handJoint.getY(), handJoint.getZ());
		setRotation(0, 0, 0);//P.HALF_PI * 3f);	// imgRot - 
		
		return this;
	}

}
