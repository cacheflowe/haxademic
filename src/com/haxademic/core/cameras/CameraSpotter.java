package com.haxademic.core.cameras;

import processing.core.PApplet;

import com.haxademic.core.cameras.common.CameraBase;
import com.haxademic.core.cameras.common.ICamera;

public class CameraSpotter
extends CameraBase
implements ICamera
{
	int _curFrameCount;
	
	public CameraSpotter( PApplet p5, int xoffset, int yoffset, int zoffset )
	{
		super( p5, xoffset, yoffset, zoffset );

		init();
	}

	public void init()
	{
		_curX = p.width/2 + p.random(-p.width/5,p.width/5);
	    _curY = p.height/2 + p.random(-p.width/5,p.width/5);
	    _curZ = p.random(100,500);
	    // aim camera
	    _targetZ = p.random(-100,10);
	    // apply camera properties
	    super.update();
	}

	public void reset()
	{
		init();
		_curFrameCount = 0;
	}

	public void update()
	{
		// aim camera
		super.update();
	}

}