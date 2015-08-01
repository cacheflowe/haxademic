package com.haxademic.app.haxmapper.polygons;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

import com.haxademic.core.app.P;
import com.haxademic.core.image.ImageUtil;
import com.haxademic.core.math.MathUtil;

public class MappedTriangle
implements IMappedPolygon {
	
	public float x1;
	public float y1;
	public float x2;
	public float y2;
	public float x3;
	public float y3;
	protected PVector[] _vertices;
	protected Point _center;
	protected int _eqIndex = MathUtil.randRange(30, 512);

	protected int _color;
	protected int _curColor;
	protected PGraphics _texture;
	
	protected int _numRotations = 0;
	protected int _mappingOrientation;
	protected int _mappingStyle = 0;
	protected ArrayList<IMappedPolygon> _neighbors;
	protected float _isFlash = 1;
	protected float _isFlashMode = 1;
	protected float _isWireMode = 1;
	protected int _gradientFadeDivisor = MathUtil.randRange(30, 512);

	protected PVector[] _randTriangle = {new PVector(), new PVector(), new PVector()};

	
	public MappedTriangle( float x1, float y1, float x2, float y2, float x3, float y3 ) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.x3 = x3;
		this.y3 = y3;
		_vertices = new PVector[3];
		_vertices[0] = new PVector(x1, y1);
		_vertices[1] = new PVector(x2, y2);
		_vertices[2] = new PVector(x3, y3);
		_center = MathUtil.computeTriangleCenter(x1, y1, x2, y2, x3, y3);
		_neighbors = new ArrayList<IMappedPolygon>();
		_mappingOrientation = 0;
	}
	
	public PVector[] getVertices() {
		return _vertices;
	}
	
	public Point getCenter() {
		return _center;
	}

	public PGraphics getTexture() {
		return _texture;
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
	
	public void setTexture( PGraphics texture ) {
		_texture = texture;
	}
	
	public void setTextureStyle( int mapStyle ) {
		_mappingStyle = mapStyle;
		randomMappingArea();
	}
	
	public void randomTextureStyle() {
		_mappingStyle = MathUtil.randRange(0, 3); 
		randomMappingArea();
	}
	
	public void resetRotation() {
		int numRotationToReset = 3 - ( _numRotations % 3 );
		for( int i=0; i < numRotationToReset; i++ ) {
			rotateTexture();
		}
	}
	
	public void randomMappingArea() {
		if( _texture != null ) {
			_randTriangle[0].set( MathUtil.randRange(0, _texture.width ), MathUtil.randRange(0, _texture.width ) );
			_randTriangle[1].set( MathUtil.randRange(0, _texture.width ), MathUtil.randRange(0, _texture.width ) );
			_randTriangle[2].set( MathUtil.randRange(0, _texture.width ), MathUtil.randRange(0, _texture.width ) );
		}
	}
	
	public void rotateTexture() {
		if(MathUtil.randRange(0, 4) > 1) return;
		float xTemp = x1;
		float yTemp = y1;
		x1 = x2;
		y1 = y2;
		x2 = x3;
		y2 = y3;
		x3 = xTemp;
		y3 = yTemp;

		_mappingOrientation = MathUtil.randRange(0, 3); 
	}
	
	public void draw( PGraphics pg, Rectangle mappingBounds ) {
		if( _texture != null ) {
			if( _mappingStyle == MAP_STYLE_CONTAIN_TEXTURE ) {
				pg.beginShape(PConstants.TRIANGLE);
				pg.texture(_texture);
				if( _mappingOrientation == 0 ) {
					pg.vertex(x1, y1, 0, 		0, 0);
					pg.vertex(x2, y2, 0, 		_texture.width, _texture.height/2);
					pg.vertex(x3, y3, 0, 		0, _texture.height);
				} else if( _mappingOrientation == 1 ) {
					pg.vertex(x1, y1, 0, 		0, 0);
					pg.vertex(x2, y2, 0, 		_texture.width, 0);
					pg.vertex(x3, y3, 0, 		_texture.width/2, _texture.height);
				} else if( _mappingOrientation == 2 ) {
					pg.vertex(x1, y1, 0, 		0, _texture.height/2);
					pg.vertex(x2, y2, 0, 		_texture.width, 0);
					pg.vertex(x3, y3, 0, 		_texture.width, _texture.height);
				} else if( _mappingOrientation == 3 ) {
					pg.vertex(x1, y1, 0, 		0, _texture.height);
					pg.vertex(x2, y2, 0, 		_texture.width/2, 0);
					pg.vertex(x3, y3, 0, 		_texture.width, _texture.height);
				}
				pg.endShape();
			} else if( _mappingStyle == MAP_STYLE_MASK ) {
				pg.beginShape(PConstants.TRIANGLE);
				pg.texture(_texture);
				// map the screen coordinates to the texture coordinates
				// crop to fill the mapped area with the current texture
				float[] cropPosOffset = ImageUtil.getOffsetAndSizeToCrop(mappingBounds.width, mappingBounds.height, _texture.width, _texture.height, true);
				float left = mappingBounds.x + cropPosOffset[0];
				float top = mappingBounds.y + cropPosOffset[1];
				float right = left + cropPosOffset[2];
				float bottom = top + cropPosOffset[3];
				pg.vertex(x1, y1, 0, 		P.map(x1, left, right, 0f, _texture.width), P.map(y1, top, bottom, 0f, _texture.height));
				pg.vertex(x2, y2, 0, 		P.map(x2, left, right, 0f, _texture.width), P.map(y2, top, bottom, 0f, _texture.height));
				pg.vertex(x3, y3, 0, 		P.map(x3, left, right, 0f, _texture.width), P.map(y3, top, bottom, 0f, _texture.height));
				pg.endShape();
			} else if( _mappingStyle == MAP_STYLE_CONTAIN_RANDOM_TEX_AREA ) {
				pg.beginShape(PConstants.TRIANGLE);
				pg.texture(_texture);
				// map the polygon coordinates to the random sampling coordinates
				pg.vertex(x1, y1, 0, 		_randTriangle[0].x, _randTriangle[0].y);
				pg.vertex(x2, y2, 0, 		_randTriangle[1].x, _randTriangle[1].y);
				pg.vertex(x3, y3, 0, 		_randTriangle[2].x, _randTriangle[2].y);
				pg.endShape();
			} else if( _mappingStyle == MAP_STYLE_EQ ) {
				_curColor = P.p.lerpColor(_curColor, _color, 0.1f);
				pg.beginShape(PConstants.TRIANGLE);
				pg.fill(pg.color(_curColor, P.constrain( P.p.audioIn.getEqBand((_eqIndex)) * 255, 0, 255 )));
				pg.vertex(x1, y1, 0);
				pg.vertex(x2, y2, 0);				
				pg.fill(pg.color(_curColor, P.constrain( P.p.audioIn.getEqBand((_eqIndex)) * 100, 0, 190 )));
				pg.vertex(x3, y3, 0);
				pg.endShape();
			}
			
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
				pg.beginShape(PConstants.TRIANGLE);
				pg.vertex(x1, y1, 0);
				pg.vertex(x2, y2, 0);				
				pg.vertex(x3, y3, 0);
				pg.endShape();
			}
			
			// overlay with gradient, oscillating from white to black over time
			float whiteFade = P.sin(P.p.frameCount / _gradientFadeDivisor); //P.constrain( P.p.audioIn.getEqBand((_eqIndex)) * 200 * _isFlash, 0, 50 );
			pg.noStroke();
			pg.beginShape(PConstants.TRIANGLE);
			pg.fill(255*whiteFade,100);
			pg.vertex(x1, y1, 0);
			pg.fill(255*whiteFade,0);
			pg.vertex(x2, y2, 0);				
			pg.vertex(x3, y3, 0);
			pg.endShape();

			
		}
	}
}