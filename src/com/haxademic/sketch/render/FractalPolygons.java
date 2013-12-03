package com.haxademic.sketch.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.system.FileUtil;
import com.haxademic.core.system.SystemUtil;

@SuppressWarnings("serial")
public class FractalPolygons
extends PAppletHax{

	protected float _baseRadius;
	protected float _curCircleSegment = 0;
	protected float _recursiveDivisor;
	protected float _rootRot = 0;
	protected float _strokeWidth = 0;
	protected int _numArms = 3;
	protected int _levels = 1;
	protected boolean _shouldBeFurther;
	protected boolean _drawsLinesOut;
	protected boolean _nextLevelPushesOut;
	protected boolean _drawCircles;
	
	
	protected boolean _shouldPrint = false;

	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "1000" );
		_appConfig.setProperty( "height", "1000" );
	}

	public void setup() {
		super.setup();

		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		generateVars();
	}

	protected void generateVars() {
		_baseRadius = MathUtil.randRange( 300, 400 );
		_strokeWidth = MathUtil.randRangeDecimel( 0.5f, 3f );
		_numArms = MathUtil.randRange( 3, 12 );
		_levels = MathUtil.randRange( 2, 3 );
		_shouldBeFurther = MathUtil.randBoolean(p);
		_drawsLinesOut = MathUtil.randBoolean(p);
		_nextLevelPushesOut = MathUtil.randBoolean(p);
		_drawCircles = MathUtil.randBoolean(p);
//		_recursiveDivisor = 0.125f * MathUtil.randRange(1, 7);
		_recursiveDivisor = 0.25f * MathUtil.randRange(2, 4);
//		_recursiveDivisor = (1f/(float)_numArms) * (float)MathUtil.randRange(1, _numArms-1);
}
	
	public void keyPressed() {
		if( p.key == ' ' ) generateVars();
		if( p.key == 'p' ) printPDF();
		
		if( p.key == '1' ) _numArms = MathUtil.randRange( 3, 12 );
		if( p.key == '2' ) _levels = MathUtil.randRange( 2, 3 );
		if( p.key == '3' ) _numArms = MathUtil.randRange( 3, 8 );
		if( p.key == '4' ) _shouldBeFurther = !_shouldBeFurther;
		if( p.key == '5' ) _drawsLinesOut = !_drawsLinesOut;
		if( p.key == '6' ) _nextLevelPushesOut = !_nextLevelPushesOut;
		if( p.key == '7' ) _baseRadius = MathUtil.randRange( 300, 400 );
		if( p.key == '8' ) _recursiveDivisor = 0.125f * MathUtil.randRange(3, 8);
		if( p.key == '9' ) _drawCircles = !_drawCircles;
	}

	public void printPDF() {
		_shouldPrint = true;
	}
	
	public void drawApp() {
		
		p.background(255);

		if( _shouldPrint ) p.beginRecord( P.PDF,  FileUtil.getHaxademicOutputPath() + "fractal-"+ SystemUtil.getTimestamp(p) +".pdf" );
		p.noFill();
		p.stroke(0);
		p.strokeWeight(3);
		p.strokeJoin(P.MITER);

		float startRadius = _baseRadius;
		new ClusterPolygon( p.width/2, p.height/2, 0, startRadius, 0 );
		
		if( _shouldPrint == true ) {
			p.endRecord();
			_shouldPrint = false;
		}
	}

	public class ClusterPolygon {
		
		public PolygonArm arms[];
		
		public ClusterPolygon( float x, float y, float startCircleInc, float radius, int level ) {

			_curCircleSegment = (float)((Math.PI*2f) / (float)_numArms);
			float circleInc = 0;

			arms = new PolygonArm[_numArms];
			for( int i=0; i < _numArms; i++ ) {
				circleInc = _curCircleSegment * (float)i;
				arms[i] = new PolygonArm( x, y, circleInc + startCircleInc, radius * _recursiveDivisor, level + 1, i );
			}

			// draw the polygon vertices
			p.beginShape();
			for( int i=0; i < _numArms; i++ ) {
//				if((i+level)%2==0 && i < level) {
					p.vertex( arms[i]._x, arms[i]._y);
//				}
			}
			p.endShape(P.CLOSE);

			// draw lines between polygon center & vertices
			if( _drawsLinesOut == true ) {
				for( int i=0; i < _numArms; i++ ) {
//					if((i+level)%2==0 && i < level) {
						p.beginShape();
						p.vertex( x, y );
						p.vertex( arms[i]._x, arms[i]._y);
						p.endShape();
//					}
				}
			} else {
				for( int i=0; i < _numArms; i++ ) {
					if( arms[i].clusterPolygon != null ) {
						for( int j=0; j < _numArms; j++ ) {
							p.line( arms[i]._x, arms[i]._y, arms[i].clusterPolygon.arms[j]._x, arms[i].clusterPolygon.arms[j]._y );
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

		public PolygonArm( float baseX, float baseY, float startCircleInc, float radius, int level, int index ) {
			
			_x = baseX + (float)Math.sin( startCircleInc ) * radius;
			_y = baseY + (float)Math.cos( startCircleInc ) * radius;


			// try to make sure new polys are further than the center of the current... not the best option here
			float polyCenterDistToSceneCenter = MathUtil.getDistance( baseX, baseY, p.width/2f, p.height/2f );
			float polyArmDist = MathUtil.getDistance( _x, _y, p.width/2f, p.height/2f );
			boolean furtherFromCenter = (polyCenterDistToSceneCenter < polyArmDist );

//			_x += Math.sin( startCircleInc ) * radius * _recursiveDivisor;
//			_y += Math.cos( startCircleInc ) * radius * _recursiveDivisor;

			float nextX = _x;
			float nextY = _y;
			if( _nextLevelPushesOut == true ) {
				nextX = _x + (float)Math.sin( startCircleInc ) * radius * 0.5f;
				nextY = _y + (float)Math.cos( startCircleInc ) * radius * 0.5f;
			}
			
			if( level < _levels && (furtherFromCenter || _shouldBeFurther == false) ) {
				
				float nextStart = (level%2 == 0) ? startCircleInc : startCircleInc + _curCircleSegment;
				
				clusterPolygon = new ClusterPolygon( nextX, nextY, nextStart, radius, level );
					
				if( _drawCircles == true ) {
//					if( level % 2 == 0 ) 
//						p.ellipse(_x, _y, radius*2f, radius*2f);
//					else 
						p.ellipse(_x, _y, radius, radius);
				}
			}
		}
	}

}

