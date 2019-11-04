package com.haxademic.core.hardware.leap;

import com.haxademic.core.app.P;
import com.haxademic.core.hardware.joystick.BaseJoystick;
import com.haxademic.core.hardware.joystick.IJoystickControl;
import com.haxademic.core.math.MathUtil;

import de.voidplus.leapmotion.Hand;
import processing.core.PGraphics;
import processing.core.PVector;

class LeapRegion
extends BaseJoystick
implements IJoystickControl {
	
	protected int _left = 0;
	protected int _right = 0;
	protected int _near = 0;
	protected int _far = 0;
	protected int _top = 0;
	protected int _bottom = 0;
	protected int _blockColor = -1;
	
	public LeapRegion( int left, int right, int top, int bottom, int near, int far, int blockColor ) {
		_left = left;
		_right = right;
		_near = near;
		_far = far;
		_top = top;
		_bottom = bottom;
		_blockColor = blockColor;
	}
		
	public void drawDebug(PGraphics debugGraphics) {
		if( _blockColor == -1 ) return;
		
		// set box color for (in)active states
		debugGraphics.strokeWeight(5f);
		if(_isActive == true) {
			debugGraphics.stroke(_blockColor, 255);
			debugGraphics.noFill();
		} else {
			debugGraphics.stroke(255, 127);
			debugGraphics.noFill();
		}
		debugGraphics.pushMatrix();
		
		// move to center of box location & draw box
		debugGraphics.translate(P.lerp(_right, _left, 0.5f), P.lerp(_top, _bottom, 0.5f), -1f * P.lerp(_far, _near, 0.5f));
		debugGraphics.box(_right - _left, _top - _bottom, _far - _near);
		
		// draw text control values
		if(_isActive == true) {
			debugGraphics.noStroke();
			debugGraphics.fill(255);
			debugGraphics.textSize(24);
			debugGraphics.text(MathUtil.roundToPrecision(_controlX, 2)+", "+MathUtil.roundToPrecision(_controlY, 2)+", "+MathUtil.roundToPrecision(_controlZ, 2), -50, 0);
		}
		
		debugGraphics.popMatrix();
	}
	
	public void update(PGraphics debugGraphics) {
		// find kinect readings in the region
		_isActive = false;
		if( P.p.leapMotion != null ) {
		    for(Hand hand : P.p.leapMotion.getHands()){
		        PVector hand_position    = hand.getPosition();
		        // PVector hand_stabilized  = hand.getStabilizedPosition();
		        
		        // draw debug hand position
		        if(debugGraphics != null) {
		        	debugGraphics.noStroke();
			        debugGraphics.fill(255);
			        debugGraphics.pushMatrix();
			        debugGraphics.translate(hand_position.x, hand_position.y, -1f * hand_position.z);
			        debugGraphics.box(40, 40, 40);
			        debugGraphics.popMatrix();
		        }
				
		        // set position if hand is in region
		        if(
		        	hand_position.x > _left && 
		        	hand_position.x < _right && 
		        	hand_position.y > _top && 
		        	hand_position.y < _bottom && 
		        	hand_position.z > _near && 
		        	hand_position.z < _far 
		        	) {
		        	_isActive = true;
		        	_controlX = (MathUtil.getPercentWithinRange(_left, _right, hand_position.x) - 0.5f) * 2f;
		        	_controlY = (MathUtil.getPercentWithinRange(_top, _bottom, hand_position.y) - 0.5f) * 2f;
		        	_controlZ = (MathUtil.getPercentWithinRange(_near, _far, hand_position.z) - 0.5f) * 2f;
		        }
		    }
		}
		
		// if none found, reset values
		if(_isActive == false) {
			_controlX = 0;
			_controlY = 0;
			_controlZ = 0;
		}
	}
}
