package com.haxademic.core.draw.textures.pgraphics;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.color.TColorBlendBetween;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat3d;

import processing.core.PVector;
import toxi.color.TColor;

public class TextureStarTrails 
extends BaseTexture {

	protected float _width;
	protected float _height;
	protected int _numStars;
	protected ArrayList<Star> _stars;
	protected boolean _wireframe = true;
	
	public TextureStarTrails( int width, int height ) {
		super();
		buildGraphics( width, height );

		// init stars
		_width = _texture.width;
		_height = _texture.height;
		_numStars = 150;// P.p._audioInput.getFFT().spectrum.length / 4;
		_stars = new ArrayList<Star>();
		for( int i = 0; i < _numStars; i++ ) {
			_stars.add( new Star() );
		}
	}
	
	public BaseTexture setActive(boolean isActive) {
//		if(isActive == false) {
//			for( int i = 0; i < _numStars; i++ ) {
//				_stars.get( i ).reset();
//			}
//		}
		super.setActive(isActive);
		return this;
	}
	
	public void newLineMode() {
//		_wireframe = MathUtil.randBoolean(P.p);
	}

	public void updateDraw() {
		_texture.clear();
		feedback(1f, 0.15f);
		
		DrawUtil.setCenterScreen( _texture );
		_texture.pushMatrix();
		
		DrawUtil.setDrawCenter(_texture);
		_texture.noStroke();
		
		for( int i = 0; i < _numStars; i++ ) {
			_stars.get( i ).update( P.p.audioFreq(i) );
		}
		_texture.popMatrix();
	}
	
	class Star {
		protected float _size, _speed;
		protected EasingFloat3d _loc;
		protected ArrayList<PVector> _trailPoints;
		protected int _trailIndex = 0;	// helps recycle the trails by constantly incrementing
		protected int _numTrails = 20;
		protected int _framesTillMove = 0;
		protected Boolean _isStrafing = false;
		protected int _zRange = 800;
		protected ColorGroup _colors = null;
		protected TColorBlendBetween _curColor = null;
		
		public Star() {
			_trailPoints = new ArrayList<PVector>();
			for( int i = 0; i < _numTrails; i++ ) {
				_trailPoints.add( new PVector( 0, 0, 0 ) );
			}
			
			_loc = new EasingFloat3d( 0, 0, 0, 5 );
			_curColor = new TColorBlendBetween( TColor.BLACK.copy(), TColor.WHITE.copy() );
			reset();
		}
		
		public void updateColorSet( ColorGroup colors ) {
			_colors = colors;
			_curColor.setColors( TColor.BLACK.copy(), _colors.getRandomColor().copy() );
		}
		
		public void reset() {
			if( _colors != null ) updateColorSet( _colors );
			_loc.setCurrentX( MathUtil.randRangeDecimal( -_width/2, _width/2 ) );
			_loc.setTargetX( MathUtil.randRangeDecimal( -_width/2, _width/2 ) );
			_loc.setCurrentY( MathUtil.randRangeDecimal( -_height/2, _height/2 ) );
			_loc.setTargetY( MathUtil.randRangeDecimal( -_height/2, _height/2 ) );
			_loc.setCurrentZ( MathUtil.randRangeDecimal( 0, 300 ) );
			_loc.setTargetZ( MathUtil.randRangeDecimal( 0, 300 ) );
			
			for( int i = _trailIndex + _numTrails; i > _trailIndex; i-- ) {
				int indx = i % _numTrails;
				_trailPoints.get(indx).x = _loc.x();
				_trailPoints.get(indx).y = _loc.y();
				_trailPoints.get(indx).z = _loc.z();
			}

			_size = 20 + (int) (Math.sin( P.p.frameCount / 100f ) * 50);
			_speed = -_size + MathUtil.randRangeDecimal( -10f, 10f );
			_framesTillMove = MathUtil.randRange( 0, 30 );
		}
		
		public void update( float amp ) {
			_framesTillMove--;
			if( _framesTillMove <= 0 ) {
				_framesTillMove = MathUtil.randRange( 4, 30 );
				_isStrafing = !_isStrafing;
				
				if( _isStrafing == true ) {
					float strafeDist = _speed * _framesTillMove / 10f;
					int randDir = MathUtil.randRange( 0, 3 );
					if( randDir == 0 ) {
						_loc.setTargetY( _loc.y() + strafeDist );
					} else if( randDir == 1 ) {
						_loc.setTargetY( _loc.y() - strafeDist );
					} else if( randDir == 2 ) {
						_loc.setTargetX( _loc.x() + strafeDist );
					} else if( randDir == 3 ) {
						_loc.setTargetX( _loc.x() - strafeDist );
					}
				} else {
//					_loc.setTargetX( _loc.valueX() );
//					_loc.setTargetY( _loc.valueY() );
				}
			}
			
			// keep moving forward
			if( _isStrafing == false ) {
				_loc.setTargetZ( _loc.z() + _speed );
			}
			_loc.update();
			
			// every increment references the newest point and loops backwards 
			_trailPoints.get( _trailIndex ).x = _loc.x();
			_trailPoints.get( _trailIndex ).y = _loc.y();
			_trailPoints.get( _trailIndex ).z = _loc.z();
			
			float baseSize = _size * amp;
			int indx = _trailIndex;
			int alpha = 255;
			int fillColor = _curColor.argbWithPercent( amp );


			if( _wireframe == false ) {
				// loop backwards through history of locations
				_texture.noStroke();
				for( int i = _numTrails + _trailIndex; i > _trailIndex; i-- ) {
					indx = i % _numTrails;
					_texture.pushMatrix();
					_texture.translate( _trailPoints.get(indx).x, _trailPoints.get(indx).y, _trailPoints.get(indx).z );
					_texture.fill( fillColor, alpha );
					_texture.box( baseSize );
					baseSize *= 0.97f;
					alpha -= 12.5;
					_texture.popMatrix();
				}
			} else {
				// draw lines between locations
	//			_texture.noFill();
				_texture.pushMatrix();
				_texture.stroke( fillColor, 255 );
				_texture.noFill();
				_texture.strokeWeight( 7f );
				_texture.beginShape(P.LINES);
//				_texture.beginShape(P.TRIANGLES);
				for( int i = _trailIndex + _numTrails; i > _trailIndex; i-- ) {
					indx = i % _numTrails;
					if( i == 0 ) _texture.translate( _trailPoints.get(indx).x, _trailPoints.get(indx).y, _trailPoints.get(indx).z );
					_texture.vertex( _trailPoints.get(indx).x + _size, _trailPoints.get(indx).y, _trailPoints.get(indx).z );
					alpha -= 12.5;
				}
				_texture.endShape();
				_texture.popMatrix();
			}
			
			
			// reset when out of bounds
			if(P.abs(_loc.z()) > _zRange || P.abs(_loc.x()) > _texture.width || P.abs(_loc.y()) > _texture.height) {
				reset();
			}
			
			_trailIndex++;
			if( _trailIndex >= _trailPoints.size() ) _trailIndex = 0; 
			
		}
	}
	
}
