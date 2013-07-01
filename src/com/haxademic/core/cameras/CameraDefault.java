package com.haxademic.core.cameras;

import processing.core.PApplet;

import com.haxademic.core.cameras.common.CameraBase;
import com.haxademic.core.cameras.common.ICamera;

public class CameraDefault
extends CameraBase
implements ICamera
{

	public CameraDefault( PApplet p5, int xoffset, int yoffset, int zoffset )
	{
		super( p5, xoffset, yoffset, zoffset );

		init();
	}

	public void init()
	{
		p.camera();
	}
	
	/**
	 * Reset camera parameters
	 */
	public void reset()
	{
		init();
	}
	
	/**
	 * Aim camera
	 */
	public void update()
	{
		super.update();
	}

}