package com.haxademic.app.haxmapper.polygons;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PVector;

public class BaseMappedPolygon {
	
	protected PVector[] _vertices;
	protected Point _center;
	protected int _centerX;
	protected int _centerY;

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
	public void setMaskPolygon(Rectangle mappingBounds) {}
	public void rotateTexture() {}
	public void rawDrawPolygon(PGraphics pg) {}

	public PVector[] getVertices() {
		return _vertices;
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
	
	public void setTexture( BaseTexture baseTexture, Rectangle mappingBounds ) {
		_baseTexture = baseTexture;
		_texture = baseTexture.texture();
		randomTextureStyle();
		resetRotation();
		setMaskPolygon(mappingBounds);
	}
	
	public void setTextureStyle( int mapStyle ) {
		_mappingStyle = mapStyle;
//		_mappingStyle = IMappedPolygon.MAP_STYLE_CONTAIN_RANDOM_TEX_AREA; // FOR TESTING
		randomMappingArea();
		resetRotation();
	}
	
	public void randomTextureStyle() {
		_mappingStyle = MathUtil.randRange(0, 3); 
//		P.println("randomTextureStyle()", _mappingStyle);
//		_mappingStyle = IMappedPolygon.MAP_STYLE_CONTAIN_RANDOM_TEX_AREA;  // FOR TESTING
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
	
	
	
	
	
	
	// Find a random polygon inside a texture that matches the shape of the original mapped polygon
	
	public void setRandomTexturePolygonToDestPolygon(PVector[] source, Point center, PVector[] destination, int textureW, int textureH) {
		// make rotated triangle
		copyPolygon(source, destination);
		rotatePolygon(destination, center, MathUtil.randRangeDecimal(0, P.TWO_PI));
		Rectangle randomPolyRotatedBB = createBoundingBox(destination);
		
		// fit rotated version in texture box & set to top/left corner
		float ratioW = (float)textureW / (float)randomPolyRotatedBB.width;
		float ratioH = (float)textureH / (float)randomPolyRotatedBB.height;
		float containRatio = (ratioW < ratioH) ? ratioW : ratioH;
		translatePolygon(destination, -randomPolyRotatedBB.x, -randomPolyRotatedBB.y);

		containRatio *= MathUtil.randRangeDecimal(0.5f, 1.0f);
		scalePolygon(destination, containRatio);
		Rectangle destinationBB = createBoundingBox(destination);
		
		// find random position within texture and move triangle & bb
		float moveX = (textureW - destinationBB.width) * MathUtil.randRangeDecimal(0, 1);
		float moveY = (textureH - destinationBB.height) * MathUtil.randRangeDecimal(0, 1);
		destinationBB.x = (int) moveX;
		destinationBB.y = (int) moveY;
		translatePolygon(destination, (int) moveX, (int) moveY);
	}
	
	protected Rectangle createBoundingBox(PVector[] points) {
		Rectangle rect = new Rectangle(new Point((int)points[0].x, (int)points[0].y));
		for (int i = 1; i < points.length; i++) {
			rect.add(points[i].x, points[i].y);
		}
		return rect;
	}
		
	protected void rotatePolygon(PVector[] points, Point center, float rotateRadians) {
		for (int i = 0; i < points.length; i++) {
			double cosAngle = Math.cos(rotateRadians);
			double sinAngle = Math.sin(rotateRadians);
			double dx = (points[i].x-center.x);
			double dy = (points[i].y-center.y);
			
			points[i].x = center.x + (int) (dx*cosAngle-dy*sinAngle);
			points[i].y = center.y + (int) (dx*sinAngle+dy*cosAngle);
		}
	}
		
	protected void translatePolygon(PVector[] points, int moveX, int moveY) {
		for (int i = 0; i < points.length; i++) {
			points[i].x = points[i].x + moveX;
			points[i].y = points[i].y + moveY;
		}
	}
	
	protected void copyPolygon(PVector[] source, PVector[] dest) {
		for (int i = 0; i < source.length; i++) {
			dest[i].x = source[i].x;
			dest[i].y = source[i].y;
		}
	}
	
	protected void scalePolygon(PVector[] points, float scale) {
		for (int i = 0; i < points.length; i++) {
			points[i].x = points[i].x * scale;
			points[i].y = points[i].y * scale;
		}
	}
	
}
