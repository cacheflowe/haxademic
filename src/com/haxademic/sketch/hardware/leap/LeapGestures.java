package com.haxademic.sketch.hardware.leap;

import com.haxademic.core.app.PAppletHax;

import de.voidplus.leapmotion.CircleGesture;
import de.voidplus.leapmotion.Device;
import de.voidplus.leapmotion.Finger;
import de.voidplus.leapmotion.Hand;
import de.voidplus.leapmotion.KeyTapGesture;
import de.voidplus.leapmotion.LeapMotion;
import de.voidplus.leapmotion.ScreenTapGesture;
import de.voidplus.leapmotion.SwipeGesture;
import de.voidplus.leapmotion.Tool;
import processing.core.PVector;

public class LeapGestures
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	

	LeapMotion leap;

	public void setupFirstFrame() {

//	    leap = new LeapMotion(this).withGestures();
	     leap = new LeapMotion(this).withGestures("circle, swipe, screen_tap, key_tap");
	    // leap = new LeapMotion(this).withGestures("swipe"); // Leap detects only swipe gestures.
	}

	public void drawApp(){
	    background(0);
	    // ...
	    int fps = leap.getFrameRate();

	    // HANDS
	    for(Hand hand : leap.getHands()){

	        hand.draw();
	        int     hand_id          = hand.getId();
	        PVector hand_position    = hand.getPosition();
	        PVector hand_stabilized  = hand.getStabilizedPosition();
	        PVector hand_direction   = hand.getDirection();
	        PVector hand_dynamics    = hand.getDynamics();
	        float   hand_roll        = hand.getRoll();
	        float   hand_pitch       = hand.getPitch();
	        float   hand_yaw         = hand.getYaw();
	        PVector sphere_position  = hand.getSpherePosition();
	        float   sphere_radius    = hand.getSphereRadius();
	        
	        p.noFill();
	        p.stroke(0,255,0);
            p.pushMatrix();
            p.translate(sphere_position.x, sphere_position.y, sphere_position.z);
            p.rotateX(hand_direction.y);
            p.rotateY(hand_direction.x);
            p.rotateZ(hand_direction.z);
            p.box(80,10,80);
            p.popMatrix();


	        // FINGERS
	        for(Finger finger : hand.getFingers()){

	            // Basics
	            finger.draw();
	            int     finger_id         = finger.getId();
	            PVector finger_position   = finger.getPosition();
	            PVector finger_stabilized = finger.getStabilizedPosition();
	            PVector finger_velocity   = finger.getVelocity();
	            PVector finger_direction  = finger.getDirection();
	            
	            // added by justin
	            p.pushMatrix();
	            p.translate(finger_position.x, finger_position.y, finger_position.z);
	            p.rotateX(finger_direction.y);
	            p.rotateY(finger_direction.x * -1);
	            p.rotateZ(finger_direction.z);
	            p.box(10,10,80);
	            p.popMatrix();

	            // Touch Emulation
	            int     touch_zone        = finger.getTouchZone();
	            float   touch_distance    = finger.getTouchDistance();

	            switch(touch_zone){
	                case -1: // None
	                break;
	                case 0: // Hovering
	                    // println("Hovering (#"+finger_id+"): "+touch_distance);
	                break;
	                case 1: // Touching
	                    // println("Touching (#"+finger_id+")");
	                break;
	            }
	        }

	        // TOOLS
	        for(Tool tool : hand.getTools()){

	            // Basics
	            tool.draw();
	            int     tool_id           = tool.getId();
	            PVector tool_position     = tool.getPosition();
	            PVector tool_stabilized   = tool.getStabilizedPosition();
	            PVector tool_velocity     = tool.getVelocity();
	            PVector tool_direction    = tool.getDirection();

	            // Touch Emulation
	            int     touch_zone        = tool.getTouchZone();
	            float   touch_distance    = tool.getTouchDistance();

	            switch(touch_zone){
	                case -1: // None
	                break;
	                case 0: // Hovering
	                    // println("Hovering (#"+tool_id+"): "+touch_distance);
	                break;
	                case 1: // Touching
	                    // println("Touching (#"+tool_id+")");
	                break;
	            }
	        }

	    }

	    // DEVICES
	    for(Device device : leap.getDevices()){
	        float device_horizontal_view_angle = device.getHorizontalViewAngle();
	        float device_verical_view_angle = device.getVerticalViewAngle();
	        float device_range = device.getRange();
	    }
	}
	
	// CIRCLE GESTURE
	public void leapOnCircleGesture(CircleGesture g, int state){
	    int     id                  = g.getId();
	    Finger  finger              = g.getFinger();
	    PVector position_center     = g.getCenter();
	    float   radius              = g.getRadius();
	    float   progress            = g.getProgress();
	    long    duration            = g.getDuration();
	    float   duration_seconds    = g.getDurationInSeconds();

	    switch(state){
	        case 1: // Start
	            break;
	        case 2: // Update
	            break;
	        case 3: // Stop
	            println("CircleGesture: "+id);
	            break;
	    }
	}

	
	// SWIPE GESTURE
	public void leapOnSwipeGesture(SwipeGesture g, int state){
	    int     id                  = g.getId();
	    Finger  finger              = g.getFinger();
	    PVector position            = g.getPosition();
	    PVector position_start      = g.getStartPosition();
	    PVector direction           = g.getDirection();
	    float   speed               = g.getSpeed();
	    long    duration            = g.getDuration();
	    float   duration_seconds    = g.getDurationInSeconds();

	    switch(state){
	        case 1: // Start
	            break;
	        case 2: // Update
	            break;
	        case 3: // Stop
	            println("SwipeGesture: "+id);
	            break;
	    }
	}

	// SCREEN TAP GESTURE
	public void leapOnScreenTapGesture(ScreenTapGesture g){
	    int     id                  = g.getId();
	    Finger  finger              = g.getFinger();
	    PVector position            = g.getPosition();
	    PVector direction           = g.getDirection();
	    long    duration            = g.getDuration();
	    float   duration_seconds    = g.getDurationInSeconds();

	    println("ScreenTapGesture: "+id);
	}

	// KEY TAP GESTURE
	public void leapOnKeyTapGesture(KeyTapGesture g){
	    int     id                  = g.getId();
	    Finger  finger              = g.getFinger();
	    PVector position            = g.getPosition();
	    PVector direction           = g.getDirection();
	    long    duration            = g.getDuration();
	    float   duration_seconds    = g.getDurationInSeconds();

	    println("KeyTapGesture: "+id);
	}

	public void leapOnInit(){
	     println("Leap Motion Init");
	}
	public void leapOnConnect(){
	     println("Leap Motion Connect");
	}
	public void leapOnFrame(){
//	     println("Leap Motion Frame");
	}
	public void leapOnDisconnect(){
	     println("Leap Motion Disconnect");
	}
	public void leapOnExit(){
	     println("Leap Motion Exit");
	}

}
