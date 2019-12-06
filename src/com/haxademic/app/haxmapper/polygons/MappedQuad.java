package com.haxademic.app.haxmapper.polygons;

import java.awt.Rectangle;
import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

public class MappedQuad
extends BaseMappedPolygon
implements IMappedPolygon {

	public float x1;
	public float y1;
	public float x2;
	public float y2;
	public float x3;
	public float y3;
	public float x4;
	public float y4;

	protected Rectangle _randRect = new Rectangle(0,0,100,100);
	protected PVector[] _maskRect = {new PVector(), new PVector(), new PVector(), new PVector()};

	
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
		_centerX = _center.x;
		_centerY = _center.y;
		_neighbors = new ArrayList<IMappedPolygon>();
		_mappingOrientation = 0;
	}
			
	public void randomMappingArea() {
		if( _texture != null ) {
			_randRect.x = MathUtil.randRange(0, _texture.width / 2f );
			_randRect.y = MathUtil.randRange(0, _texture.height / 2f );
			_randRect.width = MathUtil.randRange(_randRect.x, _texture.width / 2f );
			_randRect.height = MathUtil.randRange(_randRect.y, _texture.height / 2f );
		}
	}
	
	public void setMaskPolygon(Rectangle mappingBounds) {
		if( _texture == null ) return;
		float[] cropPosOffset = ImageUtil.getOffsetAndSizeToCrop(mappingBounds.width, mappingBounds.height, _texture.width, _texture.height, true);
		float left = mappingBounds.x + cropPosOffset[0];
		float top = mappingBounds.y + cropPosOffset[1];
		float right = left + cropPosOffset[2];
		float bottom = top + cropPosOffset[3];
		_maskRect[0].set( P.map(x1, left, right, 0f, _texture.width), P.map(y1, top, bottom, 0f, _texture.height) );
		_maskRect[1].set( P.map(x2, left, right, 0f, _texture.width), P.map(y2, top, bottom, 0f, _texture.height) );
		_maskRect[2].set( P.map(x3, left, right, 0f, _texture.width), P.map(y3, top, bottom, 0f, _texture.height) );
		_maskRect[3].set( P.map(x4, left, right, 0f, _texture.width), P.map(y4, top, bottom, 0f, _texture.height) );

//			pg.vertex(x1, y1, 0, 		P.map(x1, left, right, 0f, _texture.width), P.map(y1, top, bottom, 0f, _texture.height));
//			pg.vertex(x2, y2, 0, 		P.map(x2, left, right, 0f, _texture.width), P.map(y2, top, bottom, 0f, _texture.height));
//			pg.vertex(x3, y3, 0, 		P.map(x3, left, right, 0f, _texture.width), P.map(y3, top, bottom, 0f, _texture.height));
//			pg.vertex(x4, y4, 0, 		P.map(x4, left, right, 0f, _texture.width), P.map(y4, top, bottom, 0f, _texture.height));
		
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
	
	public void rawDrawPolygon( PGraphics pg ) {
		pg.beginShape(PConstants.QUAD);
		pg.vertex(x1, y1, 0);
		pg.vertex(x2, y2, 0);
		pg.vertex(x3, y3, 0);
		pg.vertex(x4, y4, 0);
		pg.endShape();
	}
	
	public void draw( PGraphics pg ) {
		if( _texture != null ) {
			if( _mappingStyle == MAP_STYLE_CONTAIN_TEXTURE ) {
				if( _mappingOrientation == 0 ) {
					pg.beginShape(PConstants.QUAD);
					pg.texture(_texture);
					pg.vertex(x1, y1, 0, 		0, 0);
					pg.vertex(x2, y2, 0, 		_texture.width, 0);
					pg.vertex(x3, y3, 0, 		_texture.width, _texture.height);
					pg.vertex(x4, y4, 0, 		0, _texture.height);
					pg.endShape();
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
			} else if( _mappingStyle == MAP_STYLE_MASK ) {
				pg.beginShape(PConstants.QUAD);
				pg.texture(_texture);
				// map the screen coordinates to the texture coordinates
				// crop to fill the mapped area with the current texture
				pg.vertex(x1, y1, 0, 		_maskRect[0].x, _maskRect[0].y);
				pg.vertex(x2, y2, 0, 		_maskRect[1].x, _maskRect[1].y);
				pg.vertex(x3, y3, 0, 		_maskRect[2].x, _maskRect[2].y);
				pg.vertex(x4, y4, 0, 		_maskRect[3].x, _maskRect[3].y);
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
				pg.fill(pg.color(_color, P.constrain( AudioIn.audioFreq(_eqIndex) * 255, 0, 255 )));
				pg.vertex(x1, y1, 0);
				pg.vertex(x2, y2, 0);
				pg.vertex(x3, y3, 0);
				pg.fill(pg.color(_color, P.constrain( AudioIn.audioFreq(_eqIndex) * 100, 0, 190 )));
				pg.vertex(x4, y4, 0);
				pg.endShape();
			}
			
			// flash fade overlay
			drawFlashFadeOverlay(pg);
			
			// overlay with gradient, oscillating from white to black over time
			float whiteFade = P.sin(P.p.frameCount / _gradientFadeDivisor); //P.constrain( AudioIn.getEqBand((_eqIndex)) * 200 * _isFlash, 0, 50 );
			pg.noStroke();
			pg.beginShape(PConstants.QUAD);
			pg.fill(255*whiteFade,fakeLightAlpha);
			pg.vertex(x2, y2, 0);				
			pg.vertex(x3, y3, 0);
			pg.fill(255*whiteFade,0);
			pg.vertex(x4, y4, 0);
			pg.vertex(x1, y1, 0);
			pg.endShape();
		} 
		

		// show debug info
//		pg.fill(255);
//		pg.textSize(20);
//		pg.text(_mappingStyle+"", _centerX, _centerY);
	}
}