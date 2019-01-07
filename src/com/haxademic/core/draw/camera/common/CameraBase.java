package com.haxademic.core.draw.camera.common;

import processing.core.PApplet;

// Base Camera object - common camera functionalities are here to subclass
public class CameraBase
implements ICamera
{
	public PApplet p;
	
	// position coordinates
	public float _offsetX;
	public float _offsetY;
	public float _offsetZ;
	// position coordinates
	public float _curX;
	public float _curY;
	public float _curZ;
	// target coordinates
	public float _targetX = 0;
	public float _targetY = 0;
	public float _targetZ = 0;
	// target easing helpers
	public float _tempTargetX = 0;
	public float _tempTargetY = 0;
	public float _tempTargetZ = 0;


	public CameraBase( PApplet p5, float offsetX, float offsetY, float offsetZ ) {
		p = p5;
		
		setPosition( offsetX, offsetY, offsetZ );

		// default position
		_curX = 0;
		_curY = 0;
		_curZ = 0;

		// default target
		_targetX = (float)p.width/2.0f;
		_targetY = (float)p.height/2.0f;
		_targetZ = 0;

		init();
	}

	public void init()
	{
	}

	public void reset()
	{

	}

	// subclass to update position
	public void update()
	{
		// apply camera properties
		p.camera( _curX + _offsetX, _curY + _offsetY, _curZ + _offsetZ, _targetX, _targetY, _targetZ, 0, 1, 0);
		//p.frustum(-p.width/2, p.width/2, -p.height/2, p.height/2, -100, 200);
//		p.frustum(-p.width/2, p.width, 0, p.height, -100, 200);
		
//		p.perspective(1.0f,1.5f,1f,2000000f);
		p.perspective( (float)Math.PI/3f, (float)p.width / (float)p.height, 1f, 2000000f );
	}

	// moves the camera
	public void setPosition( float offsetX, float offsetY, float offsetZ )
	{
		_offsetX = offsetX;
		_offsetY = offsetY;
		_offsetZ = offsetZ;
	}

	// points the camera
	public void setTarget( float targetX, float targetY, float targetZ )
	{
		
		_tempTargetX = targetX;
		_tempTargetY = targetY;
		_tempTargetZ = targetZ;
		// ease 
		_targetX = _targetX + ( _tempTargetX - _targetX ) / 8;
		_targetY = _targetY + ( _tempTargetY - _targetY ) / 8;
		_targetZ = _targetZ + ( _tempTargetZ - _targetZ ) / 8;
	}

}