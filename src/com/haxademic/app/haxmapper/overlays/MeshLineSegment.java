package com.haxademic.app.haxmapper.overlays;

import com.haxademic.app.haxmapper.overlays.MeshLines.MODE;
import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;

public class MeshLineSegment {

	protected PVector _point1;
	protected PVector _point2;
	protected PVector _utilVec;
	protected PVector _utilLastVec;
	protected float _angle;
	protected float _radians;
	protected float _length;
	public static PShape waveformShape;
	public static int waveformShapeFrameDrew = 0;
	protected float _progress = 0;
	protected float _progressDir = 1;

	public MeshLineSegment( float x1, float y1, float x2, float y2 ) {
		_point1 = new PVector(x1, y1);
		_point2 = new PVector(x2, y2);
		_angle = MathUtil.getAngleToTarget(x1, y1, x2, y2);
		_radians = MathUtil.degreesToRadians(_angle + 90);
		_length = MathUtil.getDistance(x1, y1, x2, y2);
		_utilVec = new PVector();
		_utilLastVec = new PVector();
		
		if( waveformShape == null ) {
			int width = 1000;
			waveformShape = P.p.createShape();
			waveformShape.beginShape();
			waveformShape.noFill();
			waveformShape.stroke(255);
			waveformShape.strokeWeight(1);
			float spacing = (float) width / 10;//P.p._waveformData._waveform.length;
			float startX = 0f;
			for (int i = 0; i < P.p._waveformData._waveform.length-23; i+=30 ) {	
				waveformShape.vertex(startX + i * spacing, 0);
			}
			waveformShape.endShape();
		}
	}
	
	public PVector contains(PVector vec) {
//		P.println(vec, _point1, _point2);
		if(vec.x == _point1.x && vec.y == _point1.y) return _point1;
		if(vec.x == _point2.x && vec.y == _point2.y) return _point2;
		return null;
	}

	public PVector otherPoint(PVector vec) {
		if(vec.x == _point1.x && vec.y == _point1.y) return _point2;
		if(vec.x == _point2.x && vec.y == _point2.y) return _point1;
		return null;
	}

	public PVector randomPoint() {
		if(MathUtil.randBoolean(P.p) == true) return _point1;
		else return _point2;
	}

	public void update( PGraphics pg, MODE mode, int color, float ampTotal, float amp ) {
//		if( mode == MODE.MODE_EQ_TOTAL ) {
//			pg.strokeWeight( P.constrain( ampTotal * .5f, 0, 1 ) );
//			pg.stroke(color);
//			pg.line( _point1.x, _point1.y, _point2.x, _point2.y );
//		} else 
		if( mode == MODE.MODE_EQ_BARS_BLACK ) {
			pg.strokeWeight( P.constrain( ampTotal * 1.0f, 1, 3 ) );
			pg.stroke(0);
			pg.line( _point1.x, _point1.y, _point2.x, _point2.y );
		} else if( mode == MODE.MODE_DOTS ) {
			pg.noStroke();
			pg.fill(color, P.constrain(amp * 10, 0, 255));
			float ampNormalized = P.constrain( amp * 2.f, 0, 4 );
			pg.ellipse( _point1.x, _point1.y, ampNormalized, ampNormalized );
		} 
//		else if( mode == MODE.MODE_WAVEFORMS ) {
//			DrawUtil.setDrawCorner(pg);
//			
//			amp = 5;
//			
//			if(waveformShapeFrameDrew != P.p.frameCount) {
//				for (int i = 0; i < waveformShape.getVertexCount()-2; i+=2) {
//					waveformShape.setVertex( i, waveformShape.getVertexX(i), P.p._waveformData._waveform[i] * amp );
//				}
//				waveformShapeFrameDrew = P.p.frameCount;
//			}
//						
//			waveformShape.disableStyle();
//			pg.noFill();
//			pg.stroke(color);
//			pg.strokeWeight(1);
//			pg.pushMatrix();
//			pg.translate( _point2.x, _point2.y );
//			pg.rotate(_radians);
//			pg.shape(waveformShape, 0, 0, _length, waveformShape.height);
//			
////			pg.translate( (_point1.x + _point2.x)/2f, (_point1.y + _point2.y)/2f );
////			for (int i = 1; i < waveformData._waveform.length; i++ ) {			
////				pg.line( 
////						startX + (i-1) * _spacing,
////						waveformData._waveform[(i-1)] * amp,
////						startX + i * _spacing,
////						waveformData._waveform[i] * amp 
////				);
////			}
//			pg.popMatrix();
//		} 
//		else if( mode == MODE.MODE_EQ_BARS ) {
//			pg.strokeWeight( P.constrain( amp * 0.3f, 0, 1 ) );
//			pg.stroke(color);
//			pg.line( _point1.x, _point1.y, _point2.x, _point2.y );
//		} 
		else if( mode == MODE.MODE_LINE_EXTEND ) {
			pg.strokeWeight( 1f );
			pg.stroke(color, 147);

			_utilVec.set( _point2 );
			_utilVec.lerp( _point1, P.constrain(amp/10f, 0f, 1f) );
			pg.line( _utilVec.x, _utilVec.y, _point2.x, _point2.y );

			_utilVec.set( _point1 );
			_utilVec.lerp( _point2, P.constrain(amp/10f, 0f, 1f) );
			pg.line( _utilVec.x, _utilVec.y, _point1.x, _point1.y );
		} else if( mode == MODE.MODE_NONE ) {
			// do nothing
		} else if( mode == MODE.MODE_PARTICLES ) {
			// do nothing
		} else if( mode == MODE.MODE_PERLIN) {
			float noise = P.p.noise(
					_point1.x/5f + P.p.noise(P.p.frameCount/100f), 
					_point1.y/10f + P.p.noise(P.p.frameCount/50f) 
			);
			pg.strokeWeight( 1f );
			pg.stroke(color, noise * 255f);
			pg.line( _point1.x, _point1.y, _point2.x, _point2.y );
//		} else if( mode == MODE.MODE_PROGRESS_BAR ) {
//			_progress += _progressDir * ( amp/100f );
//			if(_progressDir == 1 && _progress > 1) {
//				_progressDir = -1;
//				_progress = 1;
//			} else if(_progressDir == -1 && _progress < 0) {
//				_progressDir = 1;
//				_progress = 0;
//			} 
//			_utilLastVec.set(_utilVec);
//			
////			pg.noStroke();
////			pg.fill(color);
//			pg.noFill();
//			pg.stroke(color, 210);
//			pg.strokeWeight(1.3f);
//			_utilVec.set( _point2 );
//			_utilVec.lerp( _point1, _progress );
//			// pg.ellipse( _utilVec.x, _utilVec.y, 2, 2 );
//			pg.line( _utilVec.x, _utilVec.y, _utilLastVec.x, _utilLastVec.y );
//
		}
	}

	// helps make sure that we don't add existing segments
	public boolean matches( float x1, float y1, float x2, float y2 ) {
		if( _point1.x == x1 && _point1.y == y1 && _point2.x == x2 && _point2.y == y2 ) {
			return true;
		} else if( _point1.x == x2 && _point1.y == y2 && _point2.x == x1 && _point2.y == y1 ) {
			return true;
		} else {
			return false;
		}
	}
}
