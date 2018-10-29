package com.haxademic.app.haxvisual.viz.elements;

import java.util.ArrayList;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat3d;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import toxi.processing.ToxiclibsSupport;

public class StarField
extends ElementBase 
implements IVizElement {
	
	protected float _width;
	protected float _height;
	protected int _numStars;
	protected ArrayList<Star> _stars;
	protected boolean _wireframe = false;
	
	public StarField( PApplet p, ToxiclibsSupport toxi ) {
		super( p, toxi );
		init();
	}

	public void init() {
		// set some defaults
		float scaleMult = 0.5f;
		setDrawProps(p.width*scaleMult, p.height*scaleMult);
		
		_numStars = P.p.audioData.frequencies().length / 8;
		_stars = new ArrayList<Star>();
		for( int i = 0; i < _numStars; i++ ) {
			_stars.add( new Star() );
		}
	}
	
	public void setDrawProps(float width, float height) {
		_width = width;
		_height = height;
	}

	public void updateColorSet( ColorGroup colors ) {
		for( int i = 0; i < _numStars; i++ ) {
			_stars.get( i ).updateColorSet( colors );
		}
	}

	public void update() {
		DrawUtil.resetGlobalProps( p );
		p.pushMatrix();
		
		p.translate( 0f, -p.height/2, -p.height * 0.5f );
		
		p.rectMode(PConstants.CENTER);
		p.noStroke();
		
		// slow-rotate the entire element
		p.rotateX( p.frameCount/1000f );
		
		for( int i = 0; i < _numStars; i++ ) {
			_stars.get( i ).update( P.p.audioFreq(i) );
		}
		
		
		p.popMatrix();
	}
	
	public void reset() {
		updateLineMode();
		updateCamera();
		for( int i = 0; i < _numStars; i++ ) {
			_stars.get( i ).reset();
		}
	}

	public void updateLineMode() {
		_wireframe = MathUtil.randBoolean( p );
//		_is3D = ( p.random(0f,2f) >= 1 ) ? false : true;
	}
	
	public void updateCamera() {
//		float circleSegment = (float) ( Math.PI * 2f ) / 16f;
//		_rotationTarget.x = -circleSegment * 3f + p.random( -circleSegment * 2, 0 );
//		_rotationTarget.y = p.random( -circleSegment, circleSegment );
//		_rotationTarget.z = p.random( -circleSegment, circleSegment );
	}
	
	public void dispose() {
	}
	
	class Star {
		protected float _size, _speed;
		protected EasingFloat3d _loc;
		protected ArrayList<PVector> _trailPoints;
		protected int _trailIndex = 0;	// helps recycle the trails by constantly incrementing
		protected int _numTrails = 20;
		protected int _framesTillMove = 0;
		protected Boolean _isStrafing = false;
		protected int _zRange = 1000;
		protected ColorGroup _colors = null;
		protected EasingColor _curColor = null;
		protected EasingColor _white = null;
		
		public Star() {
			_trailPoints = new ArrayList<PVector>();
			for( int i = 0; i < _numTrails; i++ ) {
				_trailPoints.add( new PVector( 0, 0, 0 ) );
			}
			
			_loc = new EasingFloat3d( 0, 0, 0, 5 );
			_curColor = new EasingColor( 0, 0, 0 );
			_white = new EasingColor( 255, 255, 255 );
			reset();
		}
		
		public void updateColorSet( ColorGroup colors ) {
			_colors = colors;
//			_curColor.setColors( TColor.BLACK.copy(), _colors.getRandomColor().copy() );
//			float lighten = 0.3f;
//			_baseColor.adjustRGB( lighten, lighten, lighten );
		}
		
		public void reset() {
			if( _colors != null ) updateColorSet( _colors );
			_loc.setCurrentX( MathUtil.randRangeDecimal( -_width, _width ) );
			_loc.setTargetX( MathUtil.randRangeDecimal( -_width, _width ) );
			_loc.setCurrentY( MathUtil.randRangeDecimal( -_height, _height ) );
			_loc.setTargetY( MathUtil.randRangeDecimal( -_height, _height ) );
			_loc.setCurrentZ( _zRange );
			_loc.setTargetZ( _zRange );
			
			for( int i = _trailIndex + _numTrails; i > _trailIndex; i-- ) {
				int indx = i % _numTrails;
				_trailPoints.get(indx).x = _loc.x();
				_trailPoints.get(indx).y = _loc.y();
				_trailPoints.get(indx).z = _loc.z();
			}

			
			_size = 60 + (int) (Math.sin( p.frameCount / 100f ) * 50);
			_speed = -_size + MathUtil.randRangeDecimal( -10, 10 );
			_framesTillMove = MathUtil.randRange( 0, 30 );
		}
		
		public void update( float amp ) {
			_framesTillMove--;
			if( _framesTillMove <= 0 ) {
				_framesTillMove = MathUtil.randRange( 0, 30 );
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

			if( _isStrafing == true ) {
				
			} else {
				// keep moving forward
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
			int fillColor = _curColor.colorIntMixedWith(_white, amp );

			
//			p.fill( 255, 255 );
//			p.pushMatrix();
//			p.translate( _loc.valueX(), _loc.valueY(), _loc.valueZ() );
//			p.box( baseSize );
//			baseSize *= 0.9f;
//			p.popMatrix();

			if( _wireframe == false ) {
				// loop backwards through history of locations
				p.noStroke();
				for( int i = _numTrails + _trailIndex; i > _trailIndex; i-- ) {
					indx = i % _numTrails;
					p.pushMatrix();
					p.translate( _trailPoints.get(indx).x, _trailPoints.get(indx).y, _trailPoints.get(indx).z );
					p.fill( fillColor, alpha );
					p.box( baseSize );
					baseSize *= 0.97f;
					alpha -= 12.5;
					p.popMatrix();
				}
			} else {
				// draw lines between locations
	//			p.noFill();
				p.pushMatrix();
				p.stroke( fillColor, 255 );
				p.noFill();
				p.strokeWeight( 4f );
				p.beginShape(P.TRIANGLES);
				for( int i = _trailIndex + _numTrails; i > _trailIndex; i-- ) {
					indx = i % _numTrails;
					if( i == 0 ) p.translate( _trailPoints.get(indx).x, _trailPoints.get(indx).y, _trailPoints.get(indx).z );
					p.vertex( _trailPoints.get(indx).x + _size, _trailPoints.get(indx).y, _trailPoints.get(indx).z );
					alpha -= 12.5;
				}
				p.endShape();
				p.popMatrix();
			}
			
			
			
			if( _loc.z() < -_zRange ) {
				reset();
			}
			
			_trailIndex++;
			if( _trailIndex >= _trailPoints.size() ) _trailIndex = 0; 
			
		}
	}
}
