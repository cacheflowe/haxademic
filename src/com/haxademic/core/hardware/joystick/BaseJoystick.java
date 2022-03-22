package com.haxademic.core.hardware.joystick;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;

import processing.core.PGraphics;

public class BaseJoystick
implements IJoystickControl {
	
	protected float _oscSpeedX = 0;
	protected float _oscSpeedY = 0;
	protected float _oscSpeedZ = 0;
	protected float _oscCurX = 0;
	protected float _oscCurY = 0;
	protected float _oscCurZ = 0;
	
	protected boolean _isActive = true;
	protected float _controlX = 0;
	protected float _controlY = 0;
	protected float _controlZ = 0;
	
	public BaseJoystick() {}
	
	public float controlX() {
		return _controlX;
	}
	
	public void controlX( float value ) {
		_controlX = value;
	}
	
	public float controlY() {
		return _controlY;
	}
	
	public void controlY( float value ) {
		_controlY = value;
	}
	
	public float controlZ() {
		return _controlZ;
	}

	public void controlZ( float value ) {
		_controlZ = value;
	}
	
	public boolean isActive() {
		return _isActive;
	}
	
	public void isActive( boolean value ) {
		_isActive = value;
	}
	
	public void drawDebug(PGraphics debugGraphics) {}
	
	public static void drawDebugCoords(PGraphics debugPG, float userX, float userY, boolean userActive) {
		float debugSize = debugPG.width;
		debugPG.beginDraw();
		debugPG.background(0);
		debugPG.push();
		// draw debug bg
		PG.setDrawCorner(debugPG);
		PG.drawGrid(debugPG, 0xff111111, 0xff999999, 10, 10, 2, false);
		debugPG.rect(debugPG.width/2 - 2, 0, 4, debugPG.height);
		debugPG.rect(0, debugPG.height/2 - 2, debugPG.width, 4);
		// draw point
		PG.setDrawCenter(debugPG);
		debugPG.fill((userActive) ? P.p.color(0,255,0) : P.p.color(255,0,0));
		debugPG.stroke(0);
		debugPG.strokeWeight(2);
		debugPG.ellipse(debugSize/2 + userX * debugSize/2, debugSize/2 + userY * debugSize/2, 20, 20);
		debugPG.pop();
		debugPG.endDraw();
	}

	
	public void update(PGraphics debugGraphics) {}
}
