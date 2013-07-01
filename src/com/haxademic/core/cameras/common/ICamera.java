package com.haxademic.core.cameras.common;

public interface ICamera 
{
	public void init();
	public void update();
	public void reset();  
	public void setPosition( float offsetX, float offsetY, float offsetZ );
	public void setTarget( float targetX, float targetY, float targetZ );
}
