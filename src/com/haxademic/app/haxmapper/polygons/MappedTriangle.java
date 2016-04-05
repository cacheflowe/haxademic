package com.haxademic.app.haxmapper.polygons;

import java.awt.Rectangle;
import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.image.ImageUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

public class MappedTriangle
extends BaseMappedPolygon
implements IMappedPolygon {
	
	public float x1;
	public float y1;
	public float x2;
	public float y2;
	public float x3;
	public float y3;
	
	public EasingFloat _x1 = new EasingFloat(0, 5f);
	public EasingFloat _y1 = new EasingFloat(0, 5f);
	public EasingFloat _x2 = new EasingFloat(0, 5f);
	public EasingFloat _y2 = new EasingFloat(0, 5f);
	public EasingFloat _x3 = new EasingFloat(0, 5f);
	public EasingFloat _y3 = new EasingFloat(0, 5f);

	public EasingFloat _UVx1 = new EasingFloat(0, 5f);
	public EasingFloat _UVy1 = new EasingFloat(0, 5f);
	public EasingFloat _UVx2 = new EasingFloat(0, 5f);
	public EasingFloat _UVy2 = new EasingFloat(0, 5f);
	public EasingFloat _UVx3 = new EasingFloat(0, 5f);
	public EasingFloat _UVy3 = new EasingFloat(0, 5f);
	
	protected PVector[] _randTriangle = {new PVector(), new PVector(), new PVector()};
	protected PVector[] _maskTriangle = {new PVector(), new PVector(), new PVector()};
	
	public MappedTriangle( float x1, float y1, float x2, float y2, float x3, float y3 ) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.x3 = x3;
		this.y3 = y3;
		
		_x1.setCurrent(x1);
		_y1.setCurrent(y1);
		_x2.setCurrent(x2);
		_y2.setCurrent(y2);
		_x3.setCurrent(x3);
		_y3.setCurrent(y3);
		
		_vertices = new PVector[3];
		_vertices[0] = new PVector(x1, y1);
		_vertices[1] = new PVector(x2, y2);
		_vertices[2] = new PVector(x3, y3);
		_center = MathUtil.computeTriangleCenter(x1, y1, x2, y2, x3, y3);
		_centerX = _center.x;
		_centerY = _center.y;
		
		_neighbors = new ArrayList<IMappedPolygon>();
		_mappingOrientation = 0;
	}
	

	public void randomMappingArea() {
		if( _texture != null ) {
			setRandomTexturePolygonToDestPolygon(_vertices, _center, _randTriangle, _texture.width, _texture.height);
		}
	}
	
	public void setMaskPolygon(Rectangle mappingBounds) {
		if( _texture == null ) return;
		float[] cropPosOffset = ImageUtil.getOffsetAndSizeToCrop(mappingBounds.width, mappingBounds.height, _texture.width, _texture.height, true);
		float left = mappingBounds.x + cropPosOffset[0];
		float top = mappingBounds.y + cropPosOffset[1];
		float right = left + cropPosOffset[2];
		float bottom = top + cropPosOffset[3];
		_maskTriangle[0].set( P.map(x1, left, right, 0f, _texture.width), P.map(y1, top, bottom, 0f, _texture.height) );
		_maskTriangle[1].set( P.map(x2, left, right, 0f, _texture.width), P.map(y2, top, bottom, 0f, _texture.height) );
		_maskTriangle[2].set( P.map(x3, left, right, 0f, _texture.width), P.map(y3, top, bottom, 0f, _texture.height) );
	}
	
	public void randomTextureStyle() {
		_mappingStyle = MathUtil.randRange(0, 3); 
		if(_mappingStyle == MAP_STYLE_CONTAIN_RANDOM_TEX_AREA) randomMappingArea();
	}
	
	public void rotateTexture() {
		_numRotations++;
//		if(MathUtil.randRange(0, 4) > 1) return; // don't rotate most of the time
		_x1.setTarget(_vertices[_numRotations % _vertices.length].x); 
		_y1.setTarget(_vertices[_numRotations % _vertices.length].y);
		_x2.setTarget(_vertices[(_numRotations + 1) % _vertices.length].x); 
		_y2.setTarget(_vertices[(_numRotations + 1) % _vertices.length].y);
		_x3.setTarget(_vertices[(_numRotations + 2) % _vertices.length].x); 
		_y3.setTarget(_vertices[(_numRotations + 2) % _vertices.length].y);
//		if(_numRotations == 0) {
//			_x1.setTarget(x1); _y1.setTarget(y1);
//			_x2.setTarget(x2); _y2.setTarget(y2);
//			_x3.setTarget(x3); _y3.setTarget(y3);
//		} else if(_numRotations == 1) {
//			_x1.setTarget(x2); _y1.setTarget(y2);
//			_x2.setTarget(x3); _y2.setTarget(y3);
//			_x3.setTarget(x1); _y3.setTarget(y1);
//		} else if(_numRotations == 2) {
//			_x1.setTarget(x3); _y1.setTarget(y3);
//			_x2.setTarget(x1); _y2.setTarget(y1);
//			_x3.setTarget(x2); _y3.setTarget(y2);
//		}  
		_mappingOrientation = MathUtil.randRange(0, 3); 
	}
	
	public void rawDrawPolygon( PGraphics pg ) {
		pg.beginShape(PConstants.TRIANGLE);
		pg.vertex(_x1.value(), _y1.value());
		pg.vertex(_x2.value(), _y2.value());
		pg.vertex(_x3.value(), _y3.value());
		pg.endShape();
	}
	
	protected float getZ(float x1, float y1) {
		return 0; // 50f * P.sin(x1+y1 + P.p.frameCount/20f);
	}
	
	protected void setUVCoordinates(float uvx1, float uvy1, float uvx2, float uvy2, float uvx3, float uvy3) {
		_UVx1.setTarget(uvx1);
		_UVy1.setTarget(uvy1);
		_UVx2.setTarget(uvx2);
		_UVy2.setTarget(uvy2);
		_UVx3.setTarget(uvx3);
		_UVy3.setTarget(uvy3);

		_UVx1.update();
		_UVy1.update();
		_UVx2.update();
		_UVy2.update();
		_UVx3.update();
		_UVy3.update();
	}
	
	protected void updateVertices() {
		_x1.update();
		_y1.update();
		_x2.update();
		_y2.update();
		_x3.update();
		_y3.update();
	}
	
	public void draw( PGraphics pg ) {
		if( _texture != null ) {
			updateVertices();
			if( _mappingStyle == MAP_STYLE_CONTAIN_TEXTURE ) {
				pg.beginShape(PConstants.TRIANGLE);
				pg.texture(_texture);
				if( _mappingOrientation == 0 ) {
					setUVCoordinates(0, 0, _texture.width, _texture.height/2, 0, _texture.height);
				} else if( _mappingOrientation == 1 ) {
					setUVCoordinates(0, 0, _texture.width, 0, _texture.width/2, _texture.height);
				} else if( _mappingOrientation == 2 ) {
					setUVCoordinates(0, _texture.height/2, _texture.width, 0, _texture.width, _texture.height);
				} else if( _mappingOrientation == 3 ) {
					setUVCoordinates(0, _texture.height, _texture.width/2, 0, _texture.width, _texture.height);
				}
				pg.vertex(_x1.value(), _y1.value(), getZ(x1, y1), 		_UVx1.value(), _UVy1.value());
				pg.vertex(_x2.value(), _y2.value(), getZ(x2, y2), 		_UVx2.value(), _UVy2.value());
				pg.vertex(_x3.value(), _y3.value(), getZ(x3, y3), 		_UVx3.value(), _UVy3.value());
				pg.endShape();
			} else if( _mappingStyle == MAP_STYLE_MASK ) {
				pg.beginShape(PConstants.TRIANGLE);
				pg.texture(_texture);
				// map the screen coordinates to the texture coordinates
				// crop to fill the mapped area with the current texture
				setUVCoordinates(_maskTriangle[0].x, _maskTriangle[0].y, _maskTriangle[1].x, _maskTriangle[1].y, _maskTriangle[2].x, _maskTriangle[2].y);
				pg.vertex(_x1.value(), _y1.value(), getZ(x1, y1), 		_UVx1.value(), _UVy1.value());
				pg.vertex(_x2.value(), _y2.value(), getZ(x2, y2), 		_UVx2.value(), _UVy2.value());
				pg.vertex(_x3.value(), _y3.value(), getZ(x3, y3), 		_UVx3.value(), _UVy3.value());
				pg.endShape();
			} else if( _mappingStyle == MAP_STYLE_CONTAIN_RANDOM_TEX_AREA ) {
				pg.beginShape(PConstants.TRIANGLE);
				pg.texture(_texture);
				// map the polygon coordinates to the random sampling coordinates
				setUVCoordinates(_randTriangle[0].x, _randTriangle[0].y, _randTriangle[1].x, _randTriangle[1].y, _randTriangle[2].x, _randTriangle[2].y);
				pg.vertex(_x1.value(), _y1.value(), getZ(x1, y1), 		_UVx1.value(), _UVy1.value());
				pg.vertex(_x2.value(), _y2.value(), getZ(x2, y2), 		_UVx2.value(), _UVy2.value());
				pg.vertex(_x3.value(), _y3.value(), getZ(x3, y3), 		_UVx3.value(), _UVy3.value());
				pg.endShape();
			} else if( _mappingStyle == MAP_STYLE_EQ ) {
				_curColor = P.p.lerpColor(_curColor, _color, 0.1f);
				pg.beginShape(PConstants.TRIANGLE);
				pg.fill(pg.color(_curColor, P.constrain( P.p.audioIn.getEqBand((_eqIndex)) * 255, 0, 255 )));
				pg.vertex(_x1.value(), _y1.value(), getZ(x1, y1));
				pg.vertex(_x2.value(), _y2.value(), getZ(x2, y2));				
				pg.fill(pg.color(_curColor, P.constrain( P.p.audioIn.getEqBand((_eqIndex)) * 100, 0, 190 )));
				pg.vertex(_x3.value(), _y3.value(), getZ(x3, y3));
				pg.endShape();
			}
			
			
			// flash fade overlay
			drawFlashFadeOverlay(pg);
			
			// overlay with gradient, oscillating from white to black over time
			float whiteFade = P.sin(P.p.frameCount / _gradientFadeDivisor); //P.constrain( P.p.audioIn.getEqBand((_eqIndex)) * 200 * _isFlash, 0, 50 );
			pg.noStroke();
			pg.beginShape(PConstants.TRIANGLE);
			pg.fill(255*whiteFade,fakeLightAlpha);
			pg.vertex(_x1.value(), _y1.value(), getZ(x1, y1));
			pg.fill(255*whiteFade,0);
			pg.vertex(_x2.value(), _y2.value(), getZ(x2, y2));				
			pg.vertex(_x3.value(), _y3.value(), getZ(x3, y3));
			pg.endShape();

//			// show debug info
//			pg.fill(255);
//			pg.textSize(20);
//			pg.text(_mappingStyle+"", _centerX, _centerY);
		}
	}
	
	
	
	
	
	
//	public void drawDebug() {
//	// testing output =========
//	if(_texture == null) return;
//	if(_mappingStyle != MAP_STYLE_CONTAIN_RANDOM_TEX_AREA) return;
//	int debugX = P.p.width - _texture.width;
//	int debugY = P.p.height - _texture.height;
//	P.p.fill(255);
////	P.println(debugX, debugY, _texture.width, _texture.height);
////	P.p.rect(debugX, debugY, _texture.width, _texture.height);
//	P.p.image(_texture, debugX, debugY, _texture.width, _texture.height);
//	for (int i = 0; i < _randTriangle.length; i++) {
//		// draw vertices
//		PVector point = _randTriangle[i];
//		P.p.fill(0,0,0);
//		P.p.noStroke();
//		P.p.ellipse(debugX + point.x, debugY + point.y, 5, 5);
//		
//		// connect vertices with lines
//		PVector nextPoint = _randTriangle[(i+1) % _randTriangle.length];
//		P.p.stroke(0,0,0);
//		P.p.noFill();
//		P.p.line(debugX + point.x, debugY + point.y, debugX + nextPoint.x, debugY + nextPoint.y);
//	}
//}


}