package com.haxademic.app.haxmapper.polygons;

import java.awt.Point;
import java.awt.Rectangle;

import com.haxademic.app.haxmapper.textures.BaseTexture;

import processing.core.PGraphics;
import processing.core.PVector;

public interface IMappedPolygon {
	public static final int MAP_STYLE_CONTAIN_TEXTURE = 0;
	public static final int MAP_STYLE_MASK = 1;
	public static final int MAP_STYLE_CONTAIN_RANDOM_TEX_AREA = 2;
	public static final int MAP_STYLE_EQ = 3;
	public void draw( PGraphics pg, Rectangle mappingBounds );
	public void rawDrawPolygon( PGraphics pg );
	public void setColor( int color );
	public void setTexture( BaseTexture baseTtexture );
	public BaseTexture getTexture();
	public void setTextureStyle( int mapStyle );
	public void randomTextureStyle();
	public void rotateTexture();
	public void resetRotation();
	public Point getCenter();
	public PVector[] getVertices();
	public void addNeighbor( IMappedPolygon polygon );
	public IMappedPolygon getRandomNeighbor();
	public void setFlash( int mode, int wireMode );
}
