package com.haxademic.app.haxmapper.polygons;

import java.awt.Point;

import processing.core.PGraphics;
import processing.core.PImage;

public interface IMappedPolygon {
	public static final int MAP_STYLE_CONTAIN_TEXTURE = 0;
	public static final int MAP_STYLE_MASK = 1;
	public static final int MAP_STYLE_EQ = 2;
	public void draw( PGraphics pg );
	public void setColor( int color );
	public void setTexture( PImage texture );
	public void setTextureStyle( int mapStyle );
	public void randomTextureStyle();
	public void rotateTexture();
	public Point getCenter();
}
