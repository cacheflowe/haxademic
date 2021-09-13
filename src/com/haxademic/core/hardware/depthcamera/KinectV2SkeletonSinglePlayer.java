package com.haxademic.core.hardware.depthcamera;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PEvents;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.data.store.AppState;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingBoolean;
import com.haxademic.core.math.easing.EasingBoolean.IEasingBooleanCallback;
import com.haxademic.core.math.easing.FloatBuffer;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.ui.UI;

import KinectPV2.KJoint;
import KinectPV2.KSkeleton;
import KinectPV2.KinectPV2;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class KinectV2SkeletonSinglePlayer 
implements IAppStoreListener, IEasingBooleanCallback {
	
	// from infinite runner video game
	protected PAppletHax p;
	protected PGraphics pg;
	
	// background camera image
	protected PGraphics debugPG;
	protected PImage rgbImage;
	
	// kinect & skeletons
	protected KinectPV2 kinectV2;
	protected ArrayList<KSkeleton> skeletons2d = new ArrayList<KSkeleton>();
	protected ArrayList<KSkeleton> skeletons3d = new ArrayList<KSkeleton>();
	
	// active skeleton
	protected KSkeleton activeSkeleton2d;
	protected KSkeleton activeSkeleton3d;
	protected float activeX = 0;
	protected float activeZ = 0;
	
	// input state
	protected PVector worldCenter = new PVector();
	protected FloatBuffer playerX = new FloatBuffer(10);
	protected EasingBoolean playerActive;
	
	// AppStore
	public static String USER_ACTIVE = "USER_ACTIVE";
	public static String USER_X = "USER_X";
	public static String USER_Z = "USER_Z";
	
	
	// UI
	public static String SKELETONS_THRESH_NEAR = "SKELETONS_THRESH_NEAR";
	public static String SKELETONS_THRESH_FAR = "SKELETONS_THRESH_FAR";
	public static String SKELETONS_THRESH_X = "SKELETONS_THRESH_X";
	public static String SKELETON_MAX_X = "SKELETON_MAX_X";
	public static String SKELETON_X_AMP = "SKELETON_X_AMP";
	public static String CAMERA_DEBUG_SHOW = "CAMERA_DEBUG_SHOW";
	public static String MOUSE_CONTROLS = "MOUSE_CONTROLS";
	
	
	public KinectV2SkeletonSinglePlayer() {
		p = P.p;
		pg = p.pg;
		
		// debug buffer 
		debugPG = PG.newPG(1920, 1080, true, false);
		DebugView.setTexture("pgBG", debugPG);
		
		P.store.addListener(this);
		initCamera();
		playerActive = new EasingBoolean(false, 120, this);
		buildUI();
		
		// set initial store values
		P.store.setBoolean(USER_ACTIVE, false);
		P.store.setNumber(USER_X, 0);
		P.store.setNumber(USER_Z, 0);
	}
	
	protected void initCamera() {
		// init kinect
		kinectV2 = new KinectPV2(p);
		kinectV2.enableSkeletonColorMap(true);
		kinectV2.enableSkeleton3DMap(true);
		kinectV2.enableColorImg(true);
		kinectV2.enableDepthMaskImg(true);
		kinectV2.enableSkeletonDepthMap(true);
		kinectV2.init();
	}
	
	protected void buildUI() {
		// kinect config
		UI.addTitle("Kinect Skeletons");
		UI.addSlider(SKELETONS_THRESH_NEAR, 0, 0, 15, 0.01f, true);
		UI.addSlider(SKELETONS_THRESH_FAR, 5, 0, 20, 0.01f, true);
		UI.addSlider(SKELETONS_THRESH_X, 0.8f, 0, 3f, 0.01f, true);
		UI.addSlider(SKELETON_MAX_X, 3.5f, 0, 10, 0.01f, true);
		UI.addSlider(SKELETON_X_AMP, 1.5f, 0, 10, 0.01f, true);
		UI.addToggle(CAMERA_DEBUG_SHOW, false, false);
		UI.addTitle("Mouse Override");
		UI.addToggle(MOUSE_CONTROLS, false, false);
	}
	
	// Kinect user tracking
	
	protected int numUsers() {
		return kinectV2.getNumOfUsers();
	}
	
	protected void detectUserActive() {
		// update easing boolean switch
//		boolean userActive = numUsers() > 0;
		DebugView.setValue("NUM KINECT USERS", numUsers());
	}
	
	protected void checkUsersChanged() {
		// get current skeletons and compare to persistent array
		ArrayList<KSkeleton> skeletonsCur2d = kinectV2.getSkeletonColorMap();
		ArrayList<KSkeleton> skeletonsCur3d = kinectV2.getSkeleton3d();
		boolean usersChanged = numUsersChanged(skeletonsCur2d, skeletonsCur3d);
		DebugView.setValue("usersChanged", p.frameCount);
		
		// then store current skeletons
		skeletons2d = skeletonsCur2d;
		skeletons3d = skeletonsCur3d;
	}
	
	protected void drawDebugSkeleton(KSkeleton skeleton2d, KJoint[] joints2d) {
		int col  = skeleton2d.getIndexColor();
		debugPG.push();
		debugPG.fill(col);
		debugPG.stroke(col);
		debugPG.strokeWeight(5);
		drawBody(joints2d);

		//draw different color for each hand state
		drawHandState(joints2d[KinectPV2.JointType_HandRight]);
		drawHandState(joints2d[KinectPV2.JointType_HandLeft]);
		debugPG.pop();
	}
	
	// draw loop

	protected void drawPre(int frameCount) {
		if(UI.valueToggle(MOUSE_CONTROLS) == true) {
			P.store.setNumber(USER_X, (-0.5f + Mouse.xNorm) * 10f);
			boolean isActive = (Mouse.x != Mouse.xLast);
			playerActive.target(isActive);
		} else {
			detectUserActive();
			checkUsersChanged();
			updateSkeletons();
			drawDebugSkeletons();
		}
		playerActive.update();
	}
	
	protected void draw(int frameCount) {
		if(UI.valueToggle(CAMERA_DEBUG_SHOW)) {
			ImageUtil.drawImageCropFill(debugPG, pg, false, false, false);
		}
	}
	
	protected void updateSkeletons() {
		// track skeletons between frames. 
		// protect against bad skeleton data
		
		// default false in case we bail from this function
		playerActive.target(false);	
		activeSkeleton2d = null;
		activeSkeleton3d = null;
		
		// bail if data is bad - this was a very occasional issue in the AFI Fan Cam
		DebugView.setValue("skeletons2d.size()", skeletons2d.size());
		DebugView.setValue("skeletons3d.size()", skeletons3d.size());
		if(skeletons2d.size() == 0 || skeletons3d.size() == 0) return;
		if(skeletons2d.size() != skeletons3d.size()) return;
		
		// loop through skeletons
		boolean foundSkeleton = false;
		int indexClosest = 0;
		float maxDist = 999999;
		for (int i = 0; i < skeletons2d.size(); i++) {
			// get 2d & 3d skeletons for this user
			KSkeleton skeleton2d = (KSkeleton) skeletons2d.get(i);
			KSkeleton skeleton3d = (KSkeleton) skeletons3d.get(i);
			
			// if skeletons are good, draw things!
//			if (skeleton2d.isTracked() && skeleton3d.isTracked()) {
				// get joints arrays
				KJoint[] joints2d = skeleton2d.getJoints();
				KJoint[] joints3d = skeleton3d.getJoints();
				
				float skeletonX = joints3d[KinectPV2.JointType_SpineShoulder].getX();
				float skeletonY = joints3d[KinectPV2.JointType_SpineShoulder].getY();
				float skeletonZ = joints3d[KinectPV2.JointType_SpineShoulder].getZ();

				// log body positions
				DebugView.setValue("skel_"+i+"_x", skeletonX);
				DebugView.setValue("skel_"+i+"_y", skeletonY);
				DebugView.setValue("skel_"+i+"_z", skeletonZ);
				
				// only draw skeletons if we're in the right depth range
				// skeletonZ > UI.value(SKELETONS_THRESH_NEAR) && 
				boolean skeletonInRange = (skeletonZ < UI.value(SKELETONS_THRESH_FAR)) && P.abs(skeletonX) < UI.value(SKELETONS_THRESH_X);
				if(skeletonInRange) {
					// found a good skeleton
					float userDstance = PVector.dist(worldCenter, joints3d[KinectPV2.JointType_SpineShoulder].getPosition());
					if(userDstance < maxDist) {
						maxDist = userDstance;
						indexClosest = i;
						
						foundSkeleton = true;
						activeSkeleton2d = skeleton2d;
						activeSkeleton3d = skeleton3d;
						activeX = skeletonX;
						activeZ = skeletonZ;
					}
				}
//			}
		}
		// set user active for other screens
		playerActive.target(foundSkeleton);
		
		// send closest valid skeleton
		if(foundSkeleton) {
			// send controls
			KSkeleton skeleton2d = (KSkeleton) skeletons2d.get(indexClosest);
			KSkeleton skeleton3d = (KSkeleton) skeletons3d.get(indexClosest);
			KJoint[] joints3d = skeleton3d.getJoints();
			
			float skeletonX = joints3d[KinectPV2.JointType_SpineShoulder].getX();
			float skeletonY = joints3d[KinectPV2.JointType_SpineShoulder].getY();
			float skeletonZ = joints3d[KinectPV2.JointType_SpineShoulder].getZ();

			// smooth player x
			playerX.update(skeletonX * UI.value(SKELETON_X_AMP));	
			float newUserX = playerX.average();
			
			// store player position
			DebugView.setValue("newUserX", newUserX);
			newUserX = P.constrain(newUserX, -UI.value(SKELETON_MAX_X), UI.value(SKELETON_MAX_X));
			P.store.setNumber(USER_X, newUserX);
			P.store.setNumber(USER_Z, skeletonZ);
		}
	}
	
	protected void drawDebugSkeletons() {
		if(DebugView.active() == false && UI.valueToggle(CAMERA_DEBUG_SHOW) == false) return;
		
		debugPG.beginDraw();
		debugPG.background(0);
		
		// draw camera
		debugPG.image(kinectV2.getColorImage(), 0, 0);
		
		// draw all skeletons
		/*
		for (int i = 0; i < skeletons2d.size(); i++) {
			// get 2d & 3d skeletons for this user
			KSkeleton skeleton2d = (KSkeleton) skeletons2d.get(i);
			KSkeleton skeleton3d = (KSkeleton) skeletons3d.get(i);
			
			// if skeletons are good, draw things!
			if (skeleton2d.isTracked() && skeleton3d.isTracked()) {
				// get joints arrays
				KJoint[] joints2d = skeleton2d.getJoints();
				KJoint[] joints3d = skeleton3d.getJoints();
				
				// draw debug skeleton lines
				drawDebugSkeleton(skeleton2d, joints2d);
			}
		}
		*/
		// just draw active skeleton
		if(activeSkeleton2d != null) {
			// get joints arrays
			KJoint[] joints2d = activeSkeleton2d.getJoints();
			drawDebugSkeleton(activeSkeleton2d, joints2d);
		}
		
		// draw user coords text
		String fontFile = DemoAssets.fontOpenSansPath;
		PFont font = FontCacher.getFont(fontFile, 40);
		FontCacher.setFontOnContext(debugPG, font, p.color(255), 1.5f, PTextAlign.LEFT, PTextAlign.TOP);
		debugPG.text(
				"PlayerX: " + MathUtil.roundToPrecision(activeX, 3) + FileUtil.NEWLINE + 
				"PlayerZ: " + MathUtil.roundToPrecision(activeZ, 3) + FileUtil.NEWLINE +  
				"x multiply: " + MathUtil.roundToPrecision(UI.value(SKELETON_X_AMP), 3) + FileUtil.NEWLINE + 
				"SKELETONS_THRESH_X: " + MathUtil.roundToPrecision(UI.value(SKELETONS_THRESH_X), 3) + FileUtil.NEWLINE + 
				"SKELETONS_THRESH_Z: " + MathUtil.roundToPrecision(UI.value(SKELETONS_THRESH_FAR), 3) + FileUtil.NEWLINE + 
				"App.USER_ACTIVE: " + P.store.getBoolean(USER_ACTIVE) + FileUtil.NEWLINE + 
				"playerActive.progress(): " + MathUtil.roundToPrecision(playerActive.progress(), 3) + FileUtil.NEWLINE,
				50, debugPG.height - font.getSize() * 13f, debugPG.width, debugPG.height);
		
		
		
		debugPG.endDraw();
	}
	
	protected boolean numUsersChanged(ArrayList<KSkeleton> skeletonsCur2d, ArrayList<KSkeleton> skeletonsCur3d) {
		if(skeletons2d.size() != skeletonsCur2d.size()) return true;
		for (int i = 0; i < skeletons2d.size(); i++) {
			if(skeletons2d.get(i) != skeletonsCur2d.get(i)) return true;
		}
		return false;
	}
	
	//DRAW BODY
	
	protected int[] allJointIds = new int[] {
		KinectPV2.JointType_Head,
		KinectPV2.JointType_SpineShoulder,
		KinectPV2.JointType_SpineMid,
		KinectPV2.JointType_ShoulderRight,
		KinectPV2.JointType_ShoulderLeft,
		KinectPV2.JointType_ElbowRight,
		KinectPV2.JointType_ElbowLeft,
		KinectPV2.JointType_HandRight,
		KinectPV2.JointType_HandLeft,
		KinectPV2.JointType_SpineBase,
		KinectPV2.JointType_HipRight,
		KinectPV2.JointType_HipLeft,
		KinectPV2.JointType_KneeRight,
		KinectPV2.JointType_KneeLeft,
		KinectPV2.JointType_AnkleRight,
		KinectPV2.JointType_AnkleLeft,
		KinectPV2.JointType_FootRight,
		KinectPV2.JointType_FootLeft,
	};
	
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
		debugPG.pushMatrix();
		debugPG.translate(joints[jointType].getX(), joints[jointType].getY(), joints[jointType].getZ());
		debugPG.ellipse(0, 0, 10, 10);
		debugPG.popMatrix();
	}

	//draw bone
	void drawBone(KJoint[] joints, int jointType1, int jointType2) {
		debugPG.pushMatrix();
		debugPG.translate(joints[jointType1].getX(), joints[jointType1].getY(), joints[jointType1].getZ());
		debugPG.ellipse(0, 0, 10, 10);
		debugPG.popMatrix();
		debugPG.line(joints[jointType1].getX(), joints[jointType1].getY(), joints[jointType1].getZ(), joints[jointType2].getX(), joints[jointType2].getY(), joints[jointType2].getZ());
	}

	void drawHandState(KJoint joint) {
		debugPG.noStroke();
		setColorForHandState(joint.getState());
		debugPG.pushMatrix();
		debugPG.translate(joint.getX(), joint.getY(), joint.getZ());
		debugPG.ellipse(0, 0, 70, 70);
		debugPG.popMatrix();
	}

	void setColorForHandState(int handState) {
		switch(handState) {
		case KinectPV2.HandState_Open:
			debugPG.fill(0, 255, 0);
			break;
		case KinectPV2.HandState_Closed:
			debugPG.fill(255, 0, 0);
			break;
		case KinectPV2.HandState_Lasso:
			debugPG.fill(0, 0, 255);
			break;
		case KinectPV2.HandState_NotTracked:
			debugPG.fill(255, 255, 255);
			break;
		}
	}


	/////////////////////////////////////
	// AppStore listeners
	/////////////////////////////////////

	public void updatedNumber(String key, Number val) {
		if(key.equals(AppState.ANIMATION_FRAME_PRE)) drawPre(val.intValue());
		if(key.equals(AppState.ANIMATION_FRAME)) draw(val.intValue());
	}
	public void updatedString(String key, String val) {
		if(key.equals(PEvents.KEY_PRESSED) && val.equals("k")) {
			UI.setValueToggle(CAMERA_DEBUG_SHOW, !UI.valueToggle(CAMERA_DEBUG_SHOW));
		}
	}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}

	/////////////////////////////////////
	// EasingBoolean listeners
	/////////////////////////////////////

	public void booleanSwitched(EasingBoolean booleanSwitch, boolean value) {
		if(booleanSwitch == playerActive) {
			P.store.setBoolean(USER_ACTIVE, value);
		}
	}

}
