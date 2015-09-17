package com.haxademic.app.haxmapper.polygons;

import java.awt.Point;
import java.util.ArrayList;

import com.haxademic.app.haxmapper.textures.BaseTexture;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PVector;

public class BaseMappedPolygon {
	
	protected PVector[] _vertices;
	protected Point _center;

	protected int _color;
	protected int _curColor;

	protected BaseTexture _baseTexture;
	protected PGraphics _texture;
	protected int _eqIndex = MathUtil.randRange(30, 512);

	protected int _numRotations = 0;
	protected int _mappingOrientation;
	protected int _mappingStyle = 0;
	protected ArrayList<IMappedPolygon> _neighbors;
	protected float _isFlash = 1;
	protected float _isFlashMode = 1;
	protected float _isWireMode = 1;
	protected int _gradientFadeDivisor = MathUtil.randRange(30, 512);
	protected float fakeLightAlpha = 40;

	// MUST OVERRIDE
	public void randomMappingArea() {}
	public void rotateTexture() {}
	public void rawDrawPolygon(PGraphics pg) {}

	public PVector[] getVertices() {
		return _vertices;
	}
	
	public Point getCenter() {
		return _center;
	}

	public BaseTexture getTexture() {
		return _baseTexture;
	}
	
	public void addNeighbor(IMappedPolygon neighbor) {
		_neighbors.add(neighbor);
	}
	
	public IMappedPolygon getRandomNeighbor() {
		if(_neighbors.size() > 0) {
			return _neighbors.get(MathUtil.randRange(0, _neighbors.size()-1));
		} else {
			return null;
		}
	}
	
	public void setFlash(int mode, int wireMode) {
		_isFlash = 1;
		_isFlashMode = mode;
		_isWireMode = wireMode;
	}
	
	public void setColor( int color ) {
		_color = color;
		_eqIndex = MathUtil.randRange(30, 512);
	}
	
	public void setTexture( BaseTexture baseTexture ) {
		_baseTexture = baseTexture;
		_texture = baseTexture.texture();
	}
	
	public void setTextureStyle( int mapStyle ) {
		_mappingStyle = mapStyle;
		randomMappingArea();
	}
	
	public void randomTextureStyle() {
		_mappingStyle = MathUtil.randRange(0, 2); 
		randomMappingArea();
	}
	
	public void resetRotation() {
		int numRotationToReset = _vertices.length - ( _numRotations % _vertices.length );
		for( int i=0; i < numRotationToReset; i++ ) {
			rotateTexture();
		}
	}
	
	protected void drawFlashFadeOverlay(PGraphics pg) {
		// run flash fading
		_isFlash *= 0.9f;
		if(_isFlash > 0.01f) {
			if(_isFlashMode == 0) {
				if(_isWireMode == 0) {
					pg.noStroke();
					pg.fill(0, _isFlash * 255f);
				} else {
					pg.stroke(0, _isFlash * 255f);
					pg.strokeWeight(1.1f);
					pg.noFill();
				}
			} else {
				if(_isWireMode == 0) {
					pg.noStroke();
					pg.fill(255, _isFlash * 255f);
				} else {
					pg.stroke(255, _isFlash * 255f);
					pg.strokeWeight(1.1f);
					pg.noFill();
				}
			}
			rawDrawPolygon(pg);
		}
	}
	
}
