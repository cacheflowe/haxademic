package com.haxademic.app.haxmapper.polygons;

import java.awt.Point;

import processing.core.PGraphics;

public interface IMappedPolygon {
	public static final int MAP_STYLE_CONTAIN_TEXTURE = 0;
	public static final int MAP_STYLE_MASK = 1;
	public static final int MAP_STYLE_CONTAIN_RANDOM_TEX_AREA = 2;
	public static final int MAP_STYLE_EQ = 3;
	public void draw( PGraphics pg );
	public void setColor( int color );
	public void setTexture( PGraphics texture );
	public PGraphics getTexture();
	public void setTextureStyle( int mapStyle );
	public void randomTextureStyle();
	public void rotateTexture();
	public void resetRotation();
	public Point getCenter();
}
