package com.haxademic.app.haxmapper;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

import com.haxademic.core.math.MathUtil;

public class MappedRectangle
implements IMappedPolygon {

		
		public float x1;
		public float y1;
		public float x2;
		public float y2;
		public float x3;
		public float y3;
		public float x4;
		public float y4;
		
		protected PImage _texture;
		
		protected int mappingOrientation;
		protected boolean _mappingStyleIsFullImage = false;
		
		public MappedRectangle( float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4 ) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
			this.x3 = x3;
			this.y3 = y3;
			this.x4 = x4;
			this.y4 = y4;
			
			mappingOrientation = 0;
		}
		
		public void setTexture( PImage texture ) {
			_texture = texture;
		}
		
		public void setTextureStyle( boolean isFullImage ) {
			_mappingStyleIsFullImage = isFullImage;
		}
		
		public void rotateTexture() {
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

			mappingOrientation = MathUtil.randRange(0, 3); 
		}
		
		public void draw( PGraphics pg ) {
			if( _texture != null ) {
				pg.beginShape(PConstants.QUAD);
				pg.texture(_texture);
				if( _mappingStyleIsFullImage == true ) {
//					if( mappingOrientation == 0 ) {
						pg.vertex(x1, y1, 0, 		0, 0);
						pg.vertex(x2, y2, 0, 		_texture.width, 0);
						pg.vertex(x3, y3, 0, 		_texture.width, _texture.height);
						pg.vertex(x4, y4, 0, 		0, _texture.height);
//					} else if( mappingOrientation == 1 ) {
//						pg.vertex(x1, y1, 0, 		0, 0);
//						pg.vertex(x2, y2, 0, 		_texture.width, 0);
//						pg.vertex(x3, y3, 0, 		_texture.width/2, _texture.height);
//					} else if( mappingOrientation == 2 ) {
//						pg.vertex(x1, y1, 0, 		0, _texture.height/2);
//						pg.vertex(x2, y2, 0, 		_texture.width, 0);
//						pg.vertex(x3, y3, 0, 		_texture.width, _texture.height);
//					} else if( mappingOrientation == 3 ) {
//						pg.vertex(x1, y1, 0, 		0, _texture.height);
//						pg.vertex(x2, y2, 0, 		_texture.width/2, 0);
//						pg.vertex(x3, y3, 0, 		_texture.width, _texture.height);
//					}
				} else {
					// map the screen coordinates to the texture coordinates
					float texScreenRatioW = (float) _texture.width / (float) pg.width;
					float texScreenRatioH = (float) _texture.height / (float) pg.height;
					pg.vertex(x1, y1, 0, 		x1 * texScreenRatioW, y1 * texScreenRatioH);
					pg.vertex(x2, y2, 0, 		x2 * texScreenRatioW, y2 * texScreenRatioH);
					pg.vertex(x3, y3, 0, 		x3 * texScreenRatioW, y3 * texScreenRatioH);
					pg.vertex(x4, y4, 0, 		x4 * texScreenRatioW, y4 * texScreenRatioH);
				}
				pg.endShape();
			}
		}
	}