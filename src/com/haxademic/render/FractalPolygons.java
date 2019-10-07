package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.image.MotionBlurPGraphics;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.system.SystemUtil;

import processing.core.PGraphics;

public class FractalPolygons
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

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
	
	protected boolean _shouldPrint = false;

	protected float _frames = 90;
	protected float _numRenderedShapes = 3;
	
	MotionBlurPGraphics _pgMotionBlur;
	PGraphics _pg;
	// PApplet _pg;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "700" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "700" );
//		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, "true" );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, ""+Math.round(_frames + 10) );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, false );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "40" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, "1" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, Math.round(2) );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, Math.round(_frames + 10) );
	}

	public void setup() {
		super.setup();
		buildCanvas();
	}

	protected void buildCanvas() {
		_pg = p.createGraphics( p.width, p.height, P.P3D );
		_pg.smooth(OpenGLUtil.SMOOTH_HIGH);
		_pgMotionBlur = new MotionBlurPGraphics(6);
	}

	public void drawApp() {
		p.background(255);
		_pg.beginDraw();
		_pg.clear();
//		_pg.rotateX(p.mouseY/50f);
		drawGraphics();
		_pg.endDraw();
		_pgMotionBlur.updateToCanvas(_pg.get(), p.g, 0.7f);
	}

	protected void generateVars() {
		_baseRadiusEased.setTarget( MathUtil.randRange( 200, 250 ) );
		_strokeWidth.setTarget( MathUtil.randRangeDecimal( 0.5f, 3f ) );
		_numArms.setTarget( MathUtil.randRange( 3, 10 ) );
//		_levels.setTarget( MathUtil.randRange( 2, 4 ) );
		_levels.setTarget( MathUtil.randRange( 2, P.map(_numArms.target(), 3, 10, 5, 3) ) ); // the higher the arms, the fewer the levels. make it responsive
		_shouldBeFurther = MathUtil.randBoolean();
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
	
	public void keyPressed() {
		if( p.key == ' ' ) generateVars();
		if( p.key == 'p' ) printPDF();
		
		if( p.key == '1' ) _numArms.setTarget( MathUtil.randRange( 3, 10 ) );
		if( p.key == '2' ) _levels.setTarget( MathUtil.randRange( 2, 4 ) );
		if( p.key == '3' ) _recursiveDivisor.setTarget( 0.125f * MathUtil.randRange(2, 8) );
		if( p.key == '4' ) _strokeWidth.setTarget( MathUtil.randRangeDecimal( 0.5f, 2f ) );
		if( p.key == '5' ) _shouldBeFurther = !_shouldBeFurther;
		if( p.key == '6' ) _drawsLinesOut = !_drawsLinesOut;
		if( p.key == '7' ) _nextLevelPushesOut = !_nextLevelPushesOut;
		if( p.key == '8' ) _drawCircles = !_drawCircles;
		if( p.key == '9' ) _armPushesOut = !_armPushesOut;
		if( p.key == '0' ) _everyOtherPoly = !_everyOtherPoly;
		if( p.key == '-' ) _everyOtherPolyVerts = !_everyOtherPolyVerts;
		if( p.key == '=' ) _everyOtherCircle = !_everyOtherCircle;
	}

	public void printPDF() {
		_shouldPrint = true;
	}
	
	public void drawGraphics() {
		
		if(p.frameCount == 2) generateVars();
//		if(p.frameCount % P.round(_frames/_numRenderedShapes) == 0) {
//			if(p.frameCount < _frames) { 
//				generateVars();
//			}
//		}

		if( _shouldPrint ) p.beginRecord( P.PDF,  FileUtil.getHaxademicOutputPath() + "fractal-"+ SystemUtil.getTimestamp() +".pdf" );
		_pg.noFill();
		_pg.stroke(0);
		_pg.strokeWeight(_strokeWidth.value());
		_pg.strokeJoin(P.MITER);
		
		_baseRadiusEased.update();
		_numArms.update();
		_strokeWidth.update();
		_recursiveDivisor.update();
		_levels.update();
		
		_furthestPoint = 0;
		
		if(_baseRadiusEased.value() > 0) {
			new ClusterPolygon( p.width/2, p.height/2, 0, _baseRadiusEased.value(), 0, P.round(_numArms.value()) );
		}
		
//		if(p.frameCount >= _frames) 
//			_baseRadiusEased.setTarget( -180 );
//		else
			_baseRadiusEased.setTarget( p.height/2 - _furthestPoint );

		if( _shouldPrint == true ) {
			p.endRecord();
			_shouldPrint = false;
		}
		
//		if(p.frameCount >= _frames * 2) AppRestart.restart( p ); 

	}

	public class ClusterPolygon {
		
		public PolygonArm arms[];
		
		public ClusterPolygon( float x, float y, float startCircleInc, float radius, int level, int numArms ) {
			int nextArms = (MathUtil.randBoolean() == true) ? numArms + 2 : numArms - 2;

			_curCircleSegment = (float)((Math.PI*2f) / (float)numArms);
			float circleInc = 0;

			arms = new PolygonArm[numArms];
			for( int i=0; i < numArms; i++ ) {
				circleInc = _curCircleSegment * (float)i;
				arms[i] = new PolygonArm( x, y, circleInc + startCircleInc, radius * _recursiveDivisor.value(), level + 1, i, numArms );
			}

			// draw the polygon vertices
			_pg.beginShape();
			for( int i=0; i < numArms; i++ ) {
				if(_everyOtherPoly == false || ((i+level)%2==0 && i < level)) {
					_pg.vertex( arms[i]._x, arms[i]._y);
				}
			}
			_pg.endShape(P.CLOSE);
			for( int i=0; i < numArms; i++ ) {
				if(_everyOtherPoly == false || ((i+level)%2==0 && i < level)) {
//					BoxBetween.draw(_pg, new PVector(arms[i]._x, arms[i]._y), new PVector(arms[(i+1)%arms.length]._x, arms[(i+1)%arms.length]._y), 6);
				}
			}


			// draw lines between polygon center & vertices
			if( _drawsLinesOut == true ) {
				for( int i=0; i < numArms; i++ ) {
					if(_everyOtherPolyVerts == false || ((i+level)%2==0 && i < level)) {
						_pg.beginShape();
						_pg.vertex( x, y );
						_pg.vertex( arms[i]._x, arms[i]._y);
						_pg.endShape();
//						BoxBetween.draw(_pg, new PVector(x, y), new PVector(arms[i]._x, arms[i]._y), 6);
					}
				}
			} else {
				for( int i=0; i < numArms; i++ ) {
					if( arms[i].clusterPolygon != null ) {
						for( int j=0; j < numArms; j++ ) {
							_pg.line( arms[i]._x, arms[i]._y, arms[i].clusterPolygon.arms[j]._x, arms[i].clusterPolygon.arms[j]._y );
//							BoxBetween.draw(_pg, new PVector(arms[i]._x, arms[i]._y), new PVector(arms[i].clusterPolygon.arms[j]._x, arms[i].clusterPolygon.arms[j]._y), 6);
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
			float polyCenterDistToSceneCenter = MathUtil.getDistance( baseX, baseY, p.width/2f, p.height/2f );
			float polyArmDist = MathUtil.getDistance( _x, _y, p.width/2f, p.height/2f );
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
						_pg.ellipse(_x, _y, radius*2f, radius*2f);
					else 
						_pg.ellipse(_x, _y, radius, radius);
				}
			} else {
				float distFromCenter = MathUtil.getDistance(p.width/2, p.height/2, _x, _y);
				if(distFromCenter > _furthestPoint) {
					_furthestPoint = distFromCenter;
				}
			}
		}
	}

}

