package com.haxademic.demo.hardware.leapmotion;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;

import de.voidplus.leapmotion.Arm;
import de.voidplus.leapmotion.Bone;
import de.voidplus.leapmotion.CircleGesture;
import de.voidplus.leapmotion.Device;
import de.voidplus.leapmotion.Finger;
import de.voidplus.leapmotion.Hand;
import de.voidplus.leapmotion.Image;
import de.voidplus.leapmotion.KeyTapGesture;
import de.voidplus.leapmotion.LeapMotion;
import de.voidplus.leapmotion.ScreenTapGesture;
import de.voidplus.leapmotion.SwipeGesture;
import de.voidplus.leapmotion.Tool;
import processing.core.PVector;

public class Demo_LeapMotion 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	public LeapMotion leapMotion = null;

	protected void config() {
		Config.setAppSize(1280, 720);
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}
	
	protected void firstFrame() {
		leapMotion = new LeapMotion(this).allowGestures("SWIPE,CIRCLE,SCREEN_TAP,KEY_TAP").allowImages().allowHdm();
	}

	protected void drawApp() {
	    background(255);
	    DebugView.setValue("LeapMotion FPS", leapMotion.getFrameRate());
		DebugView.setValue("leapMotion.getHands()", leapMotion.getHands().size());

		if (leapMotion.hasImages()) {
			for (Image camera : leapMotion.getImages()) {
				if (camera.isLeft()) {
					// Left camera
					image(camera, 0, 0);
				} else {
					// Right camera
					image(camera, 0, camera.getHeight());
				}
			}
		}

	    for (Hand hand : leapMotion.getHands ()) {


	        // ==================================================
	        // 2. Hand

	        int     handId             = hand.getId();
	        PVector handPosition       = hand.getPosition();
	        PVector handStabilized     = hand.getStabilizedPosition();
	        PVector handDirection      = hand.getDirection();
	        PVector handDynamics       = hand.getDynamics();
	        float   handRoll           = hand.getRoll();
	        float   handPitch          = hand.getPitch();
	        float   handYaw            = hand.getYaw();
	        boolean handIsLeft         = hand.isLeft();
	        boolean handIsRight        = hand.isRight();
	        float   handGrab           = hand.getGrabStrength();
	        float   handPinch          = hand.getPinchStrength();
	        float   handTime           = hand.getTimeVisible();
	        PVector spherePosition     = hand.getSpherePosition();
	        float   sphereRadius       = hand.getSphereRadius();

	        DebugView.setValue("hand_position " + hand.getId(), (int) handPosition.x+", "+(int) handPosition.y+", "+(int) handPosition.z);

	        // --------------------------------------------------
	        // Drawing
	        hand.draw();


	        // ==================================================
	        // 3. Arm

	        if (hand.hasArm()) {
	          Arm     arm              = hand.getArm();
	          float   armWidth         = arm.getWidth();
	          PVector armWristPos      = arm.getWristPosition();
	          PVector armElbowPos      = arm.getElbowPosition();
	        }


	        // ==================================================
	        // 4. Finger

	        Finger  fingerThumb        = hand.getThumb();
	        // or                        hand.getFinger("thumb");
	        // or                        hand.getFinger(0);

	        Finger  fingerIndex        = hand.getIndexFinger();
	        // or                        hand.getFinger("index");
	        // or                        hand.getFinger(1);

	        Finger  fingerMiddle       = hand.getMiddleFinger();
	        // or                        hand.getFinger("middle");
	        // or                        hand.getFinger(2);

	        Finger  fingerRing         = hand.getRingFinger();
	        // or                        hand.getFinger("ring");
	        // or                        hand.getFinger(3);

	        Finger  fingerPink         = hand.getPinkyFinger();
	        // or                        hand.getFinger("pinky");
	        // or                        hand.getFinger(4);


	        for (Finger finger : hand.getFingers()) {
	          // or              hand.getOutstretchedFingers();
	          // or              hand.getOutstretchedFingersByAngle();

	          int     fingerId         = finger.getId();
	          PVector fingerPosition   = finger.getPosition();
	          PVector fingerStabilized = finger.getStabilizedPosition();
	          PVector fingerVelocity   = finger.getVelocity();
	          PVector fingerDirection  = finger.getDirection();
	          float   fingerTime       = finger.getTimeVisible();

	          // ------------------------------------------------
	          // Drawing

	          // Drawing:
	          // finger.draw();  // Executes drawBones() and drawJoints()
	          // finger.drawBones();
	          // finger.drawJoints();

	          // ------------------------------------------------
	          // Selection

	          switch(finger.getType()) {
	          case 0:
	            // System.out.println("thumb");
	            break;
	          case 1:
	            // System.out.println("index");
	            break;
	          case 2:
	            // System.out.println("middle");
	            break;
	          case 3:
	            // System.out.println("ring");
	            break;
	          case 4:
	            // System.out.println("pinky");
	            break;
	          }


	          // ================================================
	          // 5. Bones
	          // --------
	          // https://developer.leapmotion.com/documentation/java/devguide/Leap_Overview.html#Layer_1

	          Bone    boneDistal       = finger.getDistalBone();
	          // or                      finger.get("distal");
	          // or                      finger.getBone(0);

	          Bone    boneIntermediate = finger.getIntermediateBone();
	          // or                      finger.get("intermediate");
	          // or                      finger.getBone(1);

	          Bone    boneProximal     = finger.getProximalBone();
	          // or                      finger.get("proximal");
	          // or                      finger.getBone(2);

	          Bone    boneMetacarpal   = finger.getMetacarpalBone();
	          // or                      finger.get("metacarpal");
	          // or                      finger.getBone(3);

	          // ------------------------------------------------
	          // Touch emulation

	          int     touchZone        = finger.getTouchZone();
	          float   touchDistance    = finger.getTouchDistance();

	          switch(touchZone) {
	          case -1: // None
	            break;
	          case 0: // Hovering
	            // println("Hovering (#" + fingerId + "): " + touchDistance);
	            break;
	          case 1: // Touching
	            // println("Touching (#" + fingerId + ")");
	            break;
	          }
	        }


	        // ==================================================
	        // 6. Tools

	        for (Tool tool : hand.getTools()) {
	          int     toolId           = tool.getId();
	          PVector toolPosition     = tool.getPosition();
	          PVector toolStabilized   = tool.getStabilizedPosition();
	          PVector toolVelocity     = tool.getVelocity();
	          PVector toolDirection    = tool.getDirection();
	          float   toolTime         = tool.getTimeVisible();

	          // ------------------------------------------------
	          // Drawing:
	          // tool.draw();

	          // ------------------------------------------------
	          // Touch emulation

	          int     touchZone        = tool.getTouchZone();
	          float   touchDistance    = tool.getTouchDistance();

	          switch(touchZone) {
	          case -1: // None
	            break;
	          case 0: // Hovering
	            // println("Hovering (#" + toolId + "): " + touchDistance);
	            break;
	          case 1: // Touching
	            // println("Touching (#" + toolId + ")");
	            break;
	          }
	        }
	      }


	      // ====================================================
	      // 7. Devices

	      for (Device device : leapMotion.getDevices()) {
	        float deviceHorizontalViewAngle = device.getHorizontalViewAngle();
	        float deviceVericalViewAngle = device.getVerticalViewAngle();
	        float deviceRange = device.getRange();
	      }
}
	
	// ======================================================
	// 1. Swipe Gesture

	public void leapOnSwipeGesture(SwipeGesture g, int state){
	  int     id               = g.getId();
	  Finger  finger           = g.getFinger();
	  PVector position         = g.getPosition();
	  PVector positionStart    = g.getStartPosition();
	  PVector direction        = g.getDirection();
	  float   speed            = g.getSpeed();
	  long    duration         = g.getDuration();
	  float   durationSeconds  = g.getDurationInSeconds();

	  switch(state){
	    case 1: // Start
	      break;
	    case 2: // Update
	      break;
	    case 3: // Stop
	      P.out("SwipeGesture: " + id);
	      break;
	  }
	}


	// ======================================================
	// 2. Circle Gesture

	public void leapOnCircleGesture(CircleGesture g, int state){
	  int     id               = g.getId();
	  Finger  finger           = g.getFinger();
	  PVector positionCenter   = g.getCenter();
	  float   radius           = g.getRadius();
	  float   progress         = g.getProgress();
	  long    duration         = g.getDuration();
	  float   durationSeconds  = g.getDurationInSeconds();
	  int     direction        = g.getDirection();

	  switch(state){
	    case 1: // Start
	      break;
	    case 2: // Update
	      break;
	    case 3: // Stop
	      P.out("CircleGesture: " + id);
	      break;
	  }

	  switch(direction){
	    case 0: // Anticlockwise/Left gesture
	      break;
	    case 1: // Clockwise/Right gesture
	      break;
	  }
	}


	// ======================================================
	// 3. Screen Tap Gesture

	public void leapOnScreenTapGesture(ScreenTapGesture g){
	  int     id               = g.getId();
	  Finger  finger           = g.getFinger();
	  PVector position         = g.getPosition();
	  PVector direction        = g.getDirection();
	  long    duration         = g.getDuration();
	  float   durationSeconds  = g.getDurationInSeconds();

	  P.out("ScreenTapGesture: " + id);
	}


	// ======================================================
	// 4. Key Tap Gesture

	public void leapOnKeyTapGesture(KeyTapGesture g){
	  int     id               = g.getId();
	  Finger  finger           = g.getFinger();
	  PVector position         = g.getPosition();
	  PVector direction        = g.getDirection();
	  long    duration         = g.getDuration();
	  float   durationSeconds  = g.getDurationInSeconds();

	  P.out("KeyTapGesture: " + id);
	}	
	
	
	// ======================================================
	// 1. Callbacks

	void leapOnInit() {
	  // println("Leap Motion Init");
	}
	void leapOnConnect() {
	  // println("Leap Motion Connect");
	}
	void leapOnFrame() {
	  // println("Leap Motion Frame");
	}
	void leapOnDisconnect() {
	  // println("Leap Motion Disconnect");
	}
	void leapOnExit() {
	  // println("Leap Motion Exit");
	}

}
