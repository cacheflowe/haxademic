package com.haxademic.sketch.hardware.leap;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;

import de.voidplus.leapmotion.Device;
import de.voidplus.leapmotion.Finger;
import de.voidplus.leapmotion.Hand;
import de.voidplus.leapmotion.LeapMotion;
import de.voidplus.leapmotion.Tool;
import processing.core.PVector;

public class LeapBasic 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	public LeapMotion leapMotion = null;

	public void firstFrame() {
		leapMotion = new LeapMotion(this);
	}

	public void drawApp(){
	    background(255);
	    // ...
	    int fps = leapMotion.getFrameRate();

	    // HANDS
	    for(Hand hand : leapMotion.getHands()){

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

	        P.println("hand_position = "+hand_position.x+", "+hand_position.y+", "+hand_position.z);
	        
	        // FINGERS
	        for(Finger finger : hand.getFingers()){

	            // Basics
	            finger.draw();
	            int     finger_id         = finger.getId();
	            PVector finger_position   = finger.getPosition();
	            PVector finger_stabilized = finger.getStabilizedPosition();
	            PVector finger_velocity   = finger.getVelocity();
	            PVector finger_direction  = finger.getDirection();

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
	    for(Device device : leapMotion.getDevices()){
	        float device_horizontal_view_angle = device.getHorizontalViewAngle();
	        float device_verical_view_angle = device.getVerticalViewAngle();
	        float device_range = device.getRange();
	    }
	}

	void leapOnInit(){
	    // println("Leap Motion Init");
	}
	void leapOnConnect(){
	    // println("Leap Motion Connect");
	}
	void leapOnFrame(){
	    // println("Leap Motion Frame");
	}
	void leapOnDisconnect(){
	    // println("Leap Motion Disconnect");
	}
	void leapOnExit(){
	    // println("Leap Motion Exit");
	}
	
}
