package com.haxademic.app.haxmapper.polygons;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;

public class MappedQuad
implements IMappedPolygon {

		
		public float x1;
		public float y1;
		public float x2;
		public float y2;
		public float x3;
		public float y3;
		public float x4;
		public float y4;
		protected PVector[] _vertices;
		protected Point _center;

		protected int _color;
		protected PGraphics _texture;
		protected int _eqIndex = MathUtil.randRange(30, 512);

		protected int _numRotations = 0;
		protected int _mappingOrientation;
		protected int _mappingStyle = 0;
		protected ArrayList<IMappedPolygon> _neighbors;
		protected float _isFlash = 1;
		protected float _isFlashMode = 1;
		protected float _isWireMode = 0;
		protected int _gradientFadeDivisor = MathUtil.randRange(30, 512);


		protected Rectangle _randRect = new Rectangle(0,0,100,100);
		
		public MappedQuad( float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4 ) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
			this.x3 = x3;
			this.y3 = y3;
			this.x4 = x4;
			this.y4 = y4;
			_vertices = new PVector[4];
			_vertices[0] = new PVector(x1, y1);
			_vertices[1] = new PVector(x2, y2);
			_vertices[2] = new PVector(x3, y3);
			_vertices[3] = new PVector(x4, y4);
			_center = MathUtil.computeQuadCenter(x1, y1, x2, y2, x3, y3, x4, y4);
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
			int numRotationToReset = 4 - ( _numRotations % 4 );
			for( int i=0; i < numRotationToReset; i++ ) {
				rotateTexture();
			}
		}
		
		public void randomMappingArea() {
			if( _texture != null ) {
				_randRect.x = MathUtil.randRange(0, _texture.width / 2f );
				_randRect.y = MathUtil.randRange(0, _texture.height / 2f );
				_randRect.width = MathUtil.randRange(_randRect.x, _texture.width / 2f );
				_randRect.height = MathUtil.randRange(_randRect.y, _texture.height / 2f );
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
			x3 = x4;
			y3 = y4;
			x4 = xTemp;
			y4 = yTemp;

			_numRotations++;
			_mappingOrientation = MathUtil.randRange(0, 3); 
		}
		
		public void draw( PGraphics pg ) {
			if( _texture != null ) {
				if( _mappingStyle == MAP_STYLE_CONTAIN_TEXTURE ) {
//					if( _mappingOrientation == 0 ) {
						pg.beginShape(PConstants.QUAD);
						pg.texture(_texture);
						pg.vertex(x1, y1, 0, 		0, 0);
						pg.vertex(x2, y2, 0, 		_texture.width, 0);
						pg.vertex(x3, y3, 0, 		_texture.width, _texture.height);
						pg.vertex(x4, y4, 0, 		0, _texture.height);
						pg.endShape();
//					} else if( _mappingOrientation == 1 ) {
//						pg.vertex(x1, y1, 0, 		0, 0);
//						pg.vertex(x2, y2, 0, 		_texture.width, 0);
//						pg.vertex(x3, y3, 0, 		_texture.width/2, _texture.height);
//					} else if( _mappingOrientation == 2 ) {
//						pg.vertex(x1, y1, 0, 		0, _texture.height/2);
//						pg.vertex(x2, y2, 0, 		_texture.width, 0);
//						pg.vertex(x3, y3, 0, 		_texture.width, _texture.height);
//					} else if( _mappingOrientation == 3 ) {
//						pg.vertex(x1, y1, 0, 		0, _texture.height);
//						pg.vertex(x2, y2, 0, 		_texture.width/2, 0);
//						pg.vertex(x3, y3, 0, 		_texture.width, _texture.height);
//					}
				} else if( _mappingStyle == MAP_STYLE_MASK ) {
					pg.beginShape(PConstants.QUAD);
					pg.texture(_texture);
					// map the screen coordinates to the texture coordinates
					float texScreenRatioW = (float) _texture.width / (float) pg.width;
					float texScreenRatioH = (float) _texture.height / (float) pg.height;
					pg.vertex(x1, y1, 0, 		x1 * texScreenRatioW, y1 * texScreenRatioH);
					pg.vertex(x2, y2, 0, 		x2 * texScreenRatioW, y2 * texScreenRatioH);
					pg.vertex(x3, y3, 0, 		x3 * texScreenRatioW, y3 * texScreenRatioH);
					pg.vertex(x4, y4, 0, 		x4 * texScreenRatioW, y4 * texScreenRatioH);
					pg.endShape();
				} else if( _mappingStyle == MAP_STYLE_CONTAIN_RANDOM_TEX_AREA ) {
					pg.beginShape(PConstants.QUAD);
					pg.texture(_texture);
					// map the polygon coordinates to the random sampling coordinates
					pg.vertex(x1, y1, 0, 		_randRect.x, _randRect.y);
					pg.vertex(x2, y2, 0, 		_randRect.x + _randRect.width, _randRect.y);
					pg.vertex(x3, y3, 0, 		_randRect.x + _randRect.width, _randRect.y + _randRect.height);
					pg.vertex(x4, y4, 0, 		_randRect.x, _randRect.y + _randRect.height);
					pg.endShape();
				} else if( _mappingStyle == MAP_STYLE_EQ ) {
					pg.beginShape(PConstants.QUAD);
					pg.fill(pg.color(_color, P.constrain( P.p.audioIn.getEqBand((_eqIndex)) * 255, 0, 255 )));
					pg.vertex(x1, y1, 0);
					pg.vertex(x2, y2, 0);
					pg.vertex(x3, y3, 0);
					pg.fill(pg.color(_color, P.constrain( P.p.audioIn.getEqBand((_eqIndex)) * 100, 0, 190 )));
					pg.vertex(x4, y4, 0);
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
					pg.beginShape(PConstants.QUAD);
					pg.vertex(x1, y1, 0);
					pg.vertex(x2, y2, 0);				
					pg.vertex(x3, y3, 0);
					pg.vertex(x4, y4, 0);
					pg.endShape();
				}
				
				// overlay with gradient, oscillating from white to black over time
				float whiteFade = P.sin(P.p.frameCount / _gradientFadeDivisor); //P.constrain( P.p.audioIn.getEqBand((_eqIndex)) * 200 * _isFlash, 0, 50 );
				pg.noStroke();
				pg.beginShape(PConstants.QUAD);
				pg.fill(255*whiteFade,100);
				pg.vertex(x2, y2, 0);				
				pg.vertex(x3, y3, 0);
				pg.fill(255*whiteFade,0);
				pg.vertex(x4, y4, 0);
				pg.vertex(x1, y1, 0);
				pg.endShape();
			}
			

		}
	}