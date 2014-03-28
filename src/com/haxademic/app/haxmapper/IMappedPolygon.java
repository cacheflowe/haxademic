package com.haxademic.app.haxmapper;

import processing.core.PGraphics;
import processing.core.PImage;

public interface IMappedPolygon {
	public void draw( PGraphics pg );
	public void setTexture( PImage texture );
	public void setTextureStyle( boolean isFullImage );
	public void rotateTexture();
}
