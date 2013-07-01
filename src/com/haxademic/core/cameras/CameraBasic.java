package com.haxademic.core.cameras;

import processing.core.PApplet;
import processing.core.PConstants;

import com.haxademic.core.app.P;
import com.haxademic.core.cameras.common.CameraBase;
import com.haxademic.core.cameras.common.ICamera;

public class CameraBasic
extends CameraBase
implements ICamera
{
	int curFrameCount;
	int recycleAfterNumFrames;
	float cameraXSpeed;
	float cameraYSpeed;
	float cameraZSpeed;

	public CameraBasic( PApplet p5, int xoffset, int yoffset, int zoffset )
	{
		super( p5, xoffset, yoffset, zoffset );

		init();
	}

	public void init()
	{
		curFrameCount = 0;
		recycleAfterNumFrames = 300;
		cameraXSpeed = p.random(-1,1);
		cameraYSpeed = p.random(-1,1);
		cameraZSpeed = p.random(-1,1);
	}

	public void reset()
	{
		init();
		curFrameCount = 0;
	}

	public void update()
	{
		// move camera
		_curX = p.width/2.0f + cameraXSpeed * curFrameCount;
		_curY = p.height/2.0f + cameraYSpeed * curFrameCount;
		_curZ = ( p.height/2.0f ) / P.tan( PConstants.PI * 60.0f / 360.0f ) + cameraZSpeed * curFrameCount;
		// aim camera
		super.update();
		//curFrameCount++;
		if( curFrameCount >= recycleAfterNumFrames )
		{
			curFrameCount = 0;
			//init();
		}
	}

}