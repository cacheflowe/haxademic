package com.haxademic.core.hardware.depthcamera;

import java.util.ArrayList;
import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.depthcamera.ar.ArElementPool;
import com.haxademic.core.hardware.depthcamera.ar.IArElement;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.ui.UI;

import KinectPV2.KJoint;
import KinectPV2.KSkeleton;
import KinectPV2.KinectPV2;
import processing.core.PGraphics;
import processing.core.PImage;

public class KinectV2SkeletonsAR {

	public interface IKinectV2SkeletonsARDelegate {
		public void arElementShowing(IArElement arElement, KSkeleton skeleton2d, KSkeleton skeleton3d);
		public void arElementHidden(IArElement arElement, KSkeleton skeleton2d, KSkeleton skeleton3d);
	}
	
	// TODO
	// - Refactor positioning in ArObjectBase - this should happen more discretely - not sure what this means anymore :(
	// - AR elements scale: Figure out app scaledown for future use
	//   - Handle scaling depending on app resolution - it changes if the app size changes...
	//   - Why the scale difference between AFI app and Haxademic demo? Not sure yet, but probably this global scaling
	//   - [DONE, TEST! Will Need to fix AFI app by maybe setting CAMERA_HEIGHT to pg.height] POSSIBLE FIX: in the `depthcamera.ar` package, replace `pg.height` with `KinectV2SkeletonsAR.CAMERA_HEIGHT` - AFTER we launch. this would mess up our scales in AFI app
	// - Kinect user detection
	//   - Sort drawing by `z` using `skeletonZ` - or change z-position and switch to ortho() camera?

	// buffers
	protected PGraphics pg;
	protected PGraphics pgBG;
	protected PImage rgbImage;
	protected PImage depthImage;
	protected boolean updatesDepthImage = false;
	
	// kinect & skeletons
	protected KinectPV2 kinectV2;
	protected ArrayList<KSkeleton> skeletons2d = new ArrayList<KSkeleton>();
	protected ArrayList<KSkeleton> skeletons3d = new ArrayList<KSkeleton>();
	protected boolean usersActive = false;
	
	// ar elements
	protected HashMap<KSkeleton, IArElement> skellyToAr = new HashMap<KSkeleton, IArElement>();
	protected ArElementPool arPool;
	
	// UI
	public static final String DRAW_SKELETONS = "DRAW_SKELETONS";
	public static final String DRAW_AR_ELEMENTS = "DRAW_AR_ELEMENTS";
	public static final String SKELETONS_THRESH_NEAR = "SKELETONS_THRESH_NEAR";
	public static final String SKELETONS_THRESH_FAR = "SKELETONS_THRESH_FAR";
	public static final String SKELETONS_THRESH_X_PAD = "SKELETONS_THRESH_X_PAD";

	// AppStore
	public static float CAMERA_DISPLAY_SCALE = 1f;
	public static float CAMERA_HEIGHT = 1080;
	public static final String KINECT_RGB_IMAGE = "KINECT_RGB_IMAGE";
	public static final String KINECT_DEPTH_IMAGE = "KINECT_DEPTH_IMAGE";
	
	// Delegate
	protected IKinectV2SkeletonsARDelegate delegate;
	
	public KinectV2SkeletonsAR(PGraphics pg, ArElementPool arPool) {
		this(pg, arPool, false);
	}
	
	public KinectV2SkeletonsAR(PGraphics pg, ArElementPool arPool, boolean updatesDepthImage) {
		this.pg = pg;
		this.arPool = arPool;
		this.updatesDepthImage = updatesDepthImage;
		
		pgBG = PG.newPG(pg.width, pg.height, true, false);
		DebugView.setTexture("pgBG", pgBG);
		
		CAMERA_DISPLAY_SCALE = pg.height / CAMERA_HEIGHT;
		
		initCamera();
		buildUI();
	}
	
	protected void initCamera() {
		// init kinect
		kinectV2 = new KinectPV2(P.p);
		kinectV2.enableSkeletonColorMap(true);
		kinectV2.enableSkeleton3DMap(true);
		kinectV2.enableColorImg(true);
		kinectV2.enableDepthMaskImg(true);
		kinectV2.enableSkeletonDepthMap(true);
		kinectV2.init();
	}
	
	protected void buildUI() {
		// kinect config
		UI.addTitle("KinectSkeletonsAR");
		UI.addToggle(DRAW_SKELETONS, false, false);
		UI.addToggle(DRAW_AR_ELEMENTS, true, false);
		UI.addSlider(SKELETONS_THRESH_NEAR, 0, 0, 15, 0.01f, true);
		UI.addSlider(SKELETONS_THRESH_FAR, 25, 0, 20, 0.01f, true);
		UI.addSlider(SKELETONS_THRESH_X_PAD, 100, 0, 1920/2, 1, true);
	}
	
	public KinectV2SkeletonsAR setDelegate(IKinectV2SkeletonsARDelegate delegate) {
		this.delegate = delegate;
		return this;
	}
	
	public KinectPV2 kinectV2() { return kinectV2; }
	public ArElementPool arPool() { return arPool; }
	public ArrayList<KSkeleton> skeletons2d() { return skeletons2d; }
	public ArrayList<KSkeleton> skeletons3d() { return skeletons3d; }
	public HashMap<KSkeleton, IArElement> skellyToAr() { return skellyToAr; }

	public PGraphics bufferAR() {
		return pg;
	}
	
	public PGraphics bufferBG() {
		return pgBG;
	}
	
	// Kinect user tracking
	
	public int numUsers() {
		return kinectV2.getNumOfUsers();
	}
	
	public boolean usersActive() {
		return usersActive;
	}
	
	protected void detectUserActive() {
		// update easing boolean switch
		usersActive = numUsers() > 0;
		DebugView.setValue("NUM KINECT USERS", numUsers());
	}
	
	protected boolean skeletonsDidChange(ArrayList<KSkeleton> skeletonsCur2d, ArrayList<KSkeleton> skeletonsCur3d) {
		// check whether the skeleton array size has changed
		// handle very rare occurance that crashes inside `if(usersChanged)` below if 2d & 3d arrays are different sizes
		boolean arraysAreSafe = skeletonsCur2d.size() == skeletonsCur3d.size();
		boolean arrayDiff2d = skeletons2d.size() != skeletonsCur2d.size();
		boolean arrayDiff3d = skeletons3d.size() != skeletonsCur3d.size();
		if(arrayDiff2d && arrayDiff3d && arraysAreSafe) return true;
		// if the skeletons have reordered, we've changed. we know the old & new arrays are the same size at this point, 
		// so we don't need to protect against index out of bounds
		for (int i = 0; i < skeletons2d.size(); i++) {
			if(i > skeletons2d.size() || i > skeletonsCur2d.size()) return true;
			if(skeletons2d.get(i) != skeletonsCur2d.get(i)) return true;
			if(i > skeletons3d.size() || i > skeletons3d.size()) return true;
			if(i > skeletonsCur3d.size()) return false; // trying to solve error below bbut also ot have things go weird in checkUsersChanged() because it's expecting the arrays to be the same size
			if(skeletons3d.get(i) != skeletonsCur3d.get(i)) return true;  // ERROR HAPPENED HERE - now we're checking on the line above to see if we're in bounds
		}
		// arrays are the same as before!
		return false;
	}
	
	protected void checkUsersChanged() {
		// get current skeletons and compare to persistent array
		ArrayList<KSkeleton> skeletonsCur2d = kinectV2.getSkeletonColorMap();
		ArrayList<KSkeleton> skeletonsCur3d = kinectV2.getSkeleton3d();
		boolean usersChanged = skeletonsDidChange(skeletonsCur2d, skeletonsCur3d);
		// DebugView.setValue("usersChanged", usersChanged);
		
		// If users changed, store an ArElement along with the Skeleton, and remove olds
		if(usersChanged && skeletonsCur2d.size() == skeletonsCur3d.size()) { // occasional error could happen when internal array sizes are different
			// check to see if old skeletons still exist? maybe unnecessary
			// check to see if new skeletons have shown up (not in old array) - assign them an ar element
			for (int i = 0; i < skeletonsCur2d.size(); i++) {
				int iSafe = P.min(i, skeletonsCur3d.size() - 1);
				KSkeleton skel = skeletonsCur2d.get(i);
				KSkeleton skel3d = skeletonsCur3d.get(iSafe); // ERROR HAPPENED HERE, now we're protecting against out-of-bounds, even though the conditional above all of this should've protected us
				if(skeletons2d.indexOf(skel) == -1) {
					IArElement nextArElement = arPool.nextArElement();
					if(nextArElement != null) {
						skellyToAr.put(skel, nextArElement);
						nextArElement.setActive(true);
						if(delegate != null) delegate.arElementShowing(nextArElement, skel, skel3d);
					}
				}
			}
			// check to see if old skeletons are gone (not in new array) - clean these out & release ar element
			for (int i = 0; i < skeletons2d.size(); i++) {
				KSkeleton skel = skeletons2d.get(i);
				if(skeletonsCur2d.indexOf(skel) == -1) {
					if(skellyToAr.containsKey(skel)) {
						IArElement hidingArElement = skellyToAr.get(skel);
						hidingArElement.setActive(false);		// was true and worked fine...
						skellyToAr.remove(skel);
						if(delegate != null) delegate.arElementHidden(hidingArElement, skel, null);
					}
				}
			}
			
			// then store current skeletons if changed
			skeletons2d = skeletonsCur2d;
			skeletons3d = skeletonsCur3d;
		}
	}
	
	protected void drawDebugSkeleton(KSkeleton skeleton2d, KJoint[] joints2d) {
		int col  = skeleton2d.getIndexColor();
		pg.push();
		pg.fill(col);
		pg.stroke(col);
		pg.strokeWeight(3);
		PG.setDrawCenter(pg);
		drawBody(joints2d);

		//draw different color for each hand state
		drawHandState(joints2d[KinectPV2.JointType_HandRight]);
		drawHandState(joints2d[KinectPV2.JointType_HandLeft]);
		pg.pop();
	}
	
	// draw loop

	protected void drawPre() {
		drawCameraBackground();
		detectUserActive();
		checkUsersChanged();
		arElementsUpdatePre();
	}
	
	public void update() {
		drawPre();
		
		pg.beginDraw();
		pg.clear();
		pg.push();
		setCameraRegistrationOffset();
		pg.scale(MathUtil.scaleToTarget(CAMERA_HEIGHT, pg.height));
		setCameraType(pg);
		drawXPad();
		drawSkeletonsAndAElements();
		pg.pop();
		pg.endDraw();
	}
	
	protected void drawXPad() {
		if(UI.valueToggle(DRAW_SKELETONS) == false) return;
		int xPad = UI.valueInt(SKELETONS_THRESH_X_PAD);
		if(xPad == 0) return;
		pg.push();
		pg.fill(255,0,0);
		pg.noStroke();
		pg.rect(xPad - 2, 0, 4, pg.height);
		pg.rect(pg.width - xPad - 2, 0, 4, pg.height);
		pg.pop();
	}
	
	protected void drawSkeletonsAndAElements() {
		// track skeletons between frames. protect against bad skeleton data 
		DebugView.setValue("skeletons2d.size()", skeletons2d.size());
		DebugView.setValue("skeletons3d.size()", skeletons3d.size());
		if(skeletons2d.size() == 0 || skeletons3d.size() == 0) return;
		if(skeletons2d.size() != skeletons3d.size()) return;
		
		// loop through skeletons
		for (int i = 0; i < skeletons2d.size(); i++) {
			// get 2d & 3d skeletons for this user
			KSkeleton skeleton2d = (KSkeleton) skeletons2d.get(i);
			KSkeleton skeleton3d = (KSkeleton) skeletons3d.get(i);
			
			// if skeletons are good, draw things!
			if (skeleton2d.isTracked() && skeleton3d.isTracked()) {
				// get joints arrays
				KJoint[] joints2d = skeleton2d.getJoints();
				KJoint[] joints3d = skeleton3d.getJoints();
				float skeletonZ = joints3d[KinectPV2.JointType_SpineShoulder].getZ();
				float skeletonX = joints2d[KinectPV2.JointType_SpineShoulder].getX();
//				DebugView.setValue("skeleton_"+i+"_z", skeletonZ);
//				DebugView.setValue("skeleton_"+i+"_x", skeletonX);
				
				// get skeleton base scale based on 3d distance between neck and mid-spine
				// this is the size of a person
				KJoint neck = joints3d[KinectPV2.JointType_Neck];
				KJoint spineMid = joints3d[KinectPV2.JointType_SpineMid];
				float skeletonBaseScale = MathUtil.distance3d(neck.getX(), neck.getY(), neck.getZ(), spineMid.getX(), spineMid.getY(), spineMid.getZ()) / 0.24f;	// 0.24 is base size for me
				DebugView.setValue("skeletonBaseScale", skeletonBaseScale);
				
				// get skeleton 2d scale - this would change as a user gets closer & further from camera
				KJoint neck2d = joints2d[KinectPV2.JointType_Neck];
				KJoint spineMid2d = joints2d[KinectPV2.JointType_SpineMid];
				float skeletonUserScale = MathUtil.distance3d(neck2d.getX(), neck2d.getY(), neck2d.getZ(), spineMid2d.getX(), spineMid2d.getY(), spineMid2d.getZ()) / 135f;	// 0.24 is base size for me
				DebugView.setValue("skeletonUserScale", skeletonUserScale);
				
				// only draw skeletons if we're in the right depth range
				boolean skeletonInRange = skeletonZ > UI.value(SKELETONS_THRESH_NEAR) && skeletonZ < UI.value(SKELETONS_THRESH_FAR);
				int xPad = UI.valueInt(SKELETONS_THRESH_X_PAD);
				if(skeletonX < xPad || skeletonX > 1920 - xPad) skeletonInRange = false;
				if(skeletonInRange) {
					
					// get ar element for this skeleton and draw it
					if(skellyToAr.containsKey(skeleton2d) && UI.valueToggle(DRAW_AR_ELEMENTS)) {
						IArElement arEl = skellyToAr.get(skeleton2d);
						arEl.setScale(skeletonBaseScale * skeletonUserScale);
						arEl.setJoints(joints2d, joints3d);
						if(UI.valueToggle(DRAW_SKELETONS)) arEl.drawOrigin(pg);
						arEl.draw(pg);
					}
					
					// draw debug skeleton lines
					if(UI.valueToggle(DRAW_SKELETONS)) drawDebugSkeleton(skeleton2d, joints2d);
				}
			}
		}
	}
	
	protected void setCameraRegistrationOffset() {
		// add translate offset to draw skeletons on top of camera portrait crop
		// this is pre-scaled before Kinect skeletons are drawn (post global scale) at their natural 1080p positions
		float scaledRgbW = rgbImage.width * CAMERA_DISPLAY_SCALE;
		float croppedW = (pg.width - scaledRgbW);
		float xOffset = croppedW / 2;
		pg.translate(xOffset, 0);
	}
	
	protected void drawCameraBackground() {
		// draw camera image into app background PGraphics layer
		rgbImage = kinectV2.getColorImage();
		P.store.setImage(KINECT_RGB_IMAGE, rgbImage);
		DebugView.setTexture(KINECT_RGB_IMAGE, rgbImage);
		
		// update depth image for other uses
		if(updatesDepthImage) {
			depthImage = kinectV2.getDepthImage();
			DebugView.setTexture(KINECT_DEPTH_IMAGE, depthImage);
			P.store.setImage(KINECT_DEPTH_IMAGE, depthImage);
		}

		// draw camera pinned to pg height
		pgBG.beginDraw();
		PG.setDrawCenter(pgBG);
		PG.setCenterScreen(pgBG);
		pgBG.image(rgbImage, 0, 0, rgbImage.width * CAMERA_DISPLAY_SCALE, rgbImage.height * CAMERA_DISPLAY_SCALE);
		pgBG.endDraw();
	}
	
	protected void setCameraType(PGraphics pg) {
		// pg.ortho();
		pg.perspective();
	}
	
	protected void arElementsUpdatePre() {
		// preload image sequences before they're ever drawn
		for (int i = 0; i < arPool.elements().size(); i++) {
			IArElement arEl = arPool.elements().get(i);
			arEl.updatePre(pg);
		}
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
		pg.pushMatrix();
		pg.translate(joints[jointType].getX(), joints[jointType].getY(), joints[jointType].getZ());
		pg.ellipse(0, 0, 10, 10);
		pg.popMatrix();
	}

	//draw bone
	void drawBone(KJoint[] joints, int jointType1, int jointType2) {
		pg.pushMatrix();
		pg.translate(joints[jointType1].getX(), joints[jointType1].getY(), joints[jointType1].getZ());
		pg.ellipse(0, 0, 20, 20);
		pg.popMatrix();
		pg.line(joints[jointType1].getX(), joints[jointType1].getY(), joints[jointType1].getZ(), joints[jointType2].getX(), joints[jointType2].getY(), joints[jointType2].getZ());
	}

	void drawHandState(KJoint joint) {
		pg.noStroke();
		setColorForHandState(joint.getState());
		pg.pushMatrix();
		pg.translate(joint.getX(), joint.getY(), joint.getZ());
		pg.ellipse(0, 0, 70, 70);
		pg.popMatrix();
	}

	void setColorForHandState(int handState) {
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
