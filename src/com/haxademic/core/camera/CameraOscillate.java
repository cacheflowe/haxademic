package com.haxademic.core.camera;

import com.haxademic.core.app.P;
import com.haxademic.core.camera.common.CameraBase;
import com.haxademic.core.camera.common.ICamera;

import processing.core.PApplet;
import processing.core.PConstants;

public class CameraOscillate
extends CameraBase
implements ICamera
{
	int _curFrameCount;
	int _recycleAfterNumFrames;
	float _incX;
	float _incY;
	float _incZ;
	float _incXSpeed;
	float _incYSpeed;
	float _incZSpeed;
	int _amplitude;
	
	public CameraOscillate( PApplet p5, int xoffset, int yoffset, int zoffset, int amplitude )
	{
		super( p5, xoffset, yoffset, zoffset );
		
		_amplitude = amplitude;
		
		init();
	}

	public void init()
	{
		reset();
	}

	public void reset()
	{
		_curFrameCount = 0;
		_recycleAfterNumFrames = 300;
		_incX = p.random(-2*PConstants.PI,2*PConstants.PI);
		_incY = p.random(-2*PConstants.PI,2*PConstants.PI);
		_incZ = p.random(-2*PConstants.PI,2*PConstants.PI);
		_incXSpeed = p.random(-.01f,.01f);
		_incYSpeed = p.random(-.1f,.1f);
		_incZSpeed = p.random(-.1f,.1f);
	}
	
	public void update()
	{
		// increment position
		_incX += _incXSpeed * .25;
		_incY += _incYSpeed * .25;
		_incZ += _incZSpeed * .25;
		// move camera
		if( P.abs( _offsetZ ) < 100 ) _offsetZ = 100;
		_curX = p.width/2 + P.sin( _incX ) * _amplitude * (_offsetZ/100);
		_curY = p.height/2 + P.cos( _incY ) * _amplitude * (_offsetZ/100);
		_curZ = 500 + P.cos( _incZ ) * _amplitude;

		// apply camera properties
		super.update();
	}

}