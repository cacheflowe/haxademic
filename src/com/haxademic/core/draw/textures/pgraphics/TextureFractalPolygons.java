package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PConstants;

public class TextureFractalPolygons 
extends BaseTexture {

	protected float _rotationTarget;
	protected float _curRotation;
	protected float _rotXTarget;
	protected float _curRotX;
	
	
	protected float easing = 5f;
	protected float _curCircleSegment = 0;
	protected EasingFloat _recursiveDivisor = new EasingFloat(0, easing);
	protected EasingFloat _baseRadiusEased = new EasingFloat(0, 10);
	protected EasingFloat _strokeWidth = new EasingFloat(0, easing);
	protected EasingFloat _numArms = new EasingFloat(3, easing);
	protected EasingFloat _levels = new EasingFloat(1, easing);
	protected boolean _shouldBeFurther;
	protected boolean _drawsLinesOut;
	protected boolean _nextLevelPushesOut;
	protected boolean _armPushesOut;
	protected boolean _drawCircles;
	protected boolean _everyOtherPoly;
	protected boolean _everyOtherPolyVerts;
	protected boolean _everyOtherCircle;
	
	protected float _furthestPoint = 0;

	public TextureFractalPolygons( int width, int height ) {
		super(width, height);

		
		
		// set some defaults
		_curRotation = 0;
		_rotationTarget = 0;
		_curRotX = 0;
		_rotXTarget = 0;
		
		
		// polygon setup
		generateVars();
	}
	
	// INPUTS
	public void setColor( int color ) {
		super.setColor(color);
	}
	
	public void updateTiming() {
		super.updateTiming();
		
		if(MathUtil.randBooleanWeighted(0.2f) == true) _drawsLinesOut = !_drawsLinesOut;
		if(MathUtil.randBooleanWeighted(0.2f) == true) _drawCircles = !_drawCircles;
		if(MathUtil.randBooleanWeighted(0.2f) == true) _everyOtherPoly = !_everyOtherPoly;
		if(MathUtil.randBooleanWeighted(0.2f) == true) _everyOtherPolyVerts = !_everyOtherPolyVerts;
		if(MathUtil.randBooleanWeighted(0.2f) == true) _everyOtherCircle = !_everyOtherCircle;
	}
	
	public void updateTimingSection() {
		super.updateTimingSection();
		
		if(MathUtil.randBooleanWeighted(0.2f) == true) _numArms.setTarget( MathUtil.randRange( 3, 10 ) );
		if(MathUtil.randBooleanWeighted(0.2f) == true) _levels.setTarget( MathUtil.randRange( 2, 4 ) );
		if(MathUtil.randBooleanWeighted(0.2f) == true) _recursiveDivisor.setTarget( 0.125f * MathUtil.randRange(2, 8) );
	}
	
	public void newMode() {
		generateVars();
	}
	
	public void newLineMode() {
		if(MathUtil.randBooleanWeighted(0.2f) == true) _strokeWidth.setTarget( MathUtil.randRangeDecimal( 0.5f, 2f ) );
//		if(MathUtil.randBooleanWeighted(0.2f) == true) _shouldBeFurther = !_shouldBeFurther;
		if(MathUtil.randBooleanWeighted(0.2f) == true) _nextLevelPushesOut = !_nextLevelPushesOut;
		if(MathUtil.randBooleanWeighted(0.2f) == true) _armPushesOut = !_armPushesOut;
	}
	
	public void newRotation() {
		float eighthPi = (float) Math.PI / 4f;
		float target = eighthPi * (float) MathUtil.randRange(-4,4);
		_rotationTarget = target;
		_rotXTarget = target;
	}

	// fractal polygon drawing
	public void updateDraw() {
//		_texture.clear();
		_texture.background(0);
		
//		PG.resetGlobalProps( _texture );
		PG.setBasicLights( _texture );
		PG.setCenterScreen( _texture );
		PG.setDrawCenter(_texture);

		_texture.pushMatrix();
		_texture.rectMode(PConstants.CORNER);
				
		// ease rotations & set center
		_curRotation = MathUtil.easeTo(_curRotation, _rotationTarget, 10);
		_curRotX = MathUtil.easeTo(_curRotX, _rotXTarget, 10);
		_texture.rotate( _curRotation );
		
		drawShapes();
		_texture.popMatrix();
	}	

	protected void generateVars() {
		_baseRadiusEased.setTarget( MathUtil.randRange( 200, 250 ) );
		_strokeWidth.setTarget( MathUtil.randRangeDecimal( 0.5f, 5f ) );
		_numArms.setTarget( MathUtil.randRange( 3, 8 ) );
//		_levels.setTarget( MathUtil.randRange( 2, 4 ) );
		_levels.setTarget( MathUtil.randRange( 2, P.map(_numArms.target(), 3, 8, 5, 2) ) ); // the higher the arms, the fewer the levels. make it responsive
		_shouldBeFurther = false; // MathUtil.randBoolean();
		_drawsLinesOut = MathUtil.randBoolean();
		_nextLevelPushesOut = MathUtil.randBoolean();
		_armPushesOut = MathUtil.randBoolean();
		_drawCircles = MathUtil.randBoolean();
		_everyOtherPoly = MathUtil.randBoolean();
		_everyOtherPolyVerts = MathUtil.randBoolean();
		if(_drawsLinesOut == true) _everyOtherPoly = false;
		_everyOtherCircle = MathUtil.randBoolean();
		_recursiveDivisor.setTarget( 0.125f * MathUtil.randRange(1, 8) );
	}
		
	public void drawShapes() {
		_texture.noFill();
		_texture.stroke(_colorEase.colorInt());
		_texture.strokeWeight(_strokeWidth.value());
		_texture.strokeJoin(P.MITER);
		
		_baseRadiusEased.update();
		_numArms.update();
		_strokeWidth.update();
		_recursiveDivisor.update();
		_levels.update();
		
		_furthestPoint = 0;
		
		if(_baseRadiusEased.value() > 0) {
			new ClusterPolygon( 0, 0, 0, _baseRadiusEased.value(), 0, P.round(_numArms.value()) );
		}		
		_baseRadiusEased.setTarget( width - _furthestPoint );
	}

	public class ClusterPolygon {
		
		public PolygonArm arms[];
		
		public ClusterPolygon( float x, float y, float startCircleInc, float radius, int level, int numArms ) {

			_curCircleSegment = (float)((Math.PI*2f) / (float)numArms);
			float circleInc = 0;

			arms = new PolygonArm[numArms];
			for( int i=0; i < numArms; i++ ) {
				circleInc = _curCircleSegment * (float)i;
				arms[i] = new PolygonArm( x, y, circleInc + startCircleInc, radius * _recursiveDivisor.value(), level + 1, i, numArms );
			}

			// draw the polygon vertices
			_texture.beginShape();
			for( int i=0; i < numArms; i++ ) {
				if(_everyOtherPoly == false || ((i+level)%2==0 && i < level)) {
					_texture.vertex( arms[i]._x, arms[i]._y);
				}
			}
			_texture.endShape(P.CLOSE);
			for( int i=0; i < numArms; i++ ) {
				if(_everyOtherPoly == false || ((i+level)%2==0 && i < level)) {
//					BoxBetween.draw(_texture, new PVector(arms[i]._x, arms[i]._y), new PVector(arms[(i+1)%arms.length]._x, arms[(i+1)%arms.length]._y), 6);
				}
			}


			// draw lines between polygon center & vertices
			if( _drawsLinesOut == true ) {
				for( int i=0; i < numArms; i++ ) {
					if(_everyOtherPolyVerts == false || ((i+level)%2==0 && i < level)) {
						_texture.beginShape();
						_texture.vertex( x, y );
						_texture.vertex( arms[i]._x, arms[i]._y);
						_texture.endShape();
//						BoxBetween.draw(_texture, new PVector(x, y), new PVector(arms[i]._x, arms[i]._y), 6);
					}
				}
			} else {
				for( int i=0; i < numArms; i++ ) {
					if( arms[i].clusterPolygon != null ) {
						for( int j=0; j < numArms; j++ ) {
							_texture.line( arms[i]._x, arms[i]._y, arms[i].clusterPolygon.arms[j]._x, arms[i].clusterPolygon.arms[j]._y );
//							BoxBetween.draw(_texture, new PVector(arms[i]._x, arms[i]._y), new PVector(arms[i].clusterPolygon.arms[j]._x, arms[i].clusterPolygon.arms[j]._y), 6);
						}
					}
				}
			}

		}
	}


	public class PolygonArm {

		public float _x;
		public float _y;
		public ClusterPolygon clusterPolygon;

		public PolygonArm( float baseX, float baseY, float startCircleInc, float radius, int level, int index, int numArms ) {
			
			_x = baseX + (float)Math.sin( startCircleInc ) * radius;
			_y = baseY + (float)Math.cos( startCircleInc ) * radius;


			// try to make sure new polys are further than the center of the current... not the best option here
			float polyCenterDistToSceneCenter = MathUtil.getDistance( baseX, baseY, width/2f, height/2f );
			float polyArmDist = MathUtil.getDistance( _x, _y, width/2f, height/2f );
			boolean furtherFromCenter = (polyCenterDistToSceneCenter < polyArmDist );

			// not helpful
//			if( _armPushesOut == true ) {
//				_x += Math.sin( startCircleInc ) * radius * _recursiveDivisor.value();
//				_y += Math.cos( startCircleInc ) * radius * _recursiveDivisor.value();
//			}
			
			float nextX = _x;
			float nextY = _y;
			if( _nextLevelPushesOut == true ) {
				nextX = _x + (float)Math.sin( startCircleInc ) * radius * 0.5f;
				nextY = _y + (float)Math.cos( startCircleInc ) * radius * 0.5f;
			}
			
			if( level < P.round(_levels.value()) && (furtherFromCenter || _shouldBeFurther == false) ) {
				
				float nextStart = (level%2 == 0 && _everyOtherCircle) ? startCircleInc : startCircleInc + _curCircleSegment;
				
				clusterPolygon = new ClusterPolygon( nextX, nextY, nextStart, radius, level, numArms );
					
				if( _drawCircles == true ) {
					if( _everyOtherCircle == true && level % 2 == 0 ) 
						_texture.ellipse(_x, _y, radius*2f, radius*2f);
					else 
						_texture.ellipse(_x, _y, radius, radius);
				}
			} else {
				float distFromCenter = MathUtil.getDistance(width/2, height/2, _x, _y);
				if(distFromCenter > _furthestPoint) {
					_furthestPoint = distFromCenter;
				}
			}
		}
	}

}
