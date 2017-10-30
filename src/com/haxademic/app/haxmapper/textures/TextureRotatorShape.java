package com.haxademic.app.haxmapper.textures;

import toxi.geom.Vec3D;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat3d;

public class TextureRotatorShape 
extends BaseTexture {

	final int POINTS_MIN = 6;
	final int POINTS_MAX = 12;
	final int COORD_MIN = 0;
	int COORD_MAX;

	protected PointGroup _curPointGroup = null;
	protected int _numRotations;
	protected int _pointsPerGroup;
	
	protected float _baseRotZAdd = 0;
	protected float _baseRotZTarget = 0;
	protected float _rotDir = 0;
	
//	protected TColorBlendBetween _color;

	public TextureRotatorShape( int width, int height ) {
		super();

		buildGraphics( width, height );
		
		COORD_MAX = _texture.width / 3;
		_pointsPerGroup = POINTS_MAX;
		_numRotations = POINTS_MAX;//_texture.round( _texture.random( ROTATION_MIN, ROTATION_MAX ) );
		if( _numRotations % 2 != 0 ) _numRotations++;	// keep even numbers for proper reflection
				
		_curPointGroup = new PointGroup( _pointsPerGroup );
		_curPointGroup.reset();

		newLineMode();
	}
	
	public void newLineMode() {
		_pointsPerGroup = POINTS_MAX; //P.round( p.random( POINTS_MIN, POINTS_MAX ) );
		if( _curPointGroup != null ) _curPointGroup.reset();
	}
	
	public void newRotation() {
		_baseRotZAdd = ( MathUtil.randBoolean( P.p ) ) ? 0.8f : -0.8f;
		_rotDir = MathUtil.randRangeDecimal( -4, 4 )/ 1000f;
	}

	public void newMode() {
	}

	public void updateTimingSection() {
	}

	public void updateDraw() {
		_texture.clear();
		
		DrawUtil.setCenterScreen( _texture );
		_texture.translate(0, 0, -600);
		// rotate beginning z
		_baseRotZAdd = MathUtil.easeTo( _baseRotZAdd, _baseRotZTarget, 20 );
		_texture.rotateZ( _rotDir * P.p.frameCount - _baseRotZAdd );

		// update the single shape
		_curPointGroup.update();
		
		// draw repeated shape 
		float rotationIncrement = P.TWO_PI / _numRotations;
		float spectrumData;
		_texture.noStroke();
//		DrawUtil.setColorForPImage(_texture);
		for( int i = 0; i < _numRotations; i++ ) {
			spectrumData = P.p._audioInput.getFFT().spectrum[ 20 + (int) (i * (255f/_numRotations)) ];
			_texture.fill( _color, 255f * spectrumData );
//			_texture.fill( _baseColor.lighten( spectrumData * 255 * 30 ).toARGB() );
//			_texture.stroke( spectrumData * 255, spectrumData * 127 );
			_texture.pushMatrix();
			_texture.rotateZ( rotationIncrement * i );
			_curPointGroup.drawPointsTriangles();
			_texture.popMatrix();
		}
	}
	
	
	
	public class Point {
		
		public Vec3D _pos;
		public EasingFloat3d _posBase;
		public Vec3D _speed;
		protected float _inc;
		protected float _incSpeed;
		protected float _radius;

		public Point(){
			_pos = new Vec3D(0,0,0);
			_posBase = new EasingFloat3d( 0, 0, 0, 5f );
			_speed = new Vec3D(0,0,0);
			reset();
		}
		
		public void reset() {
			// reset positions
			_posBase.setTargetX( -COORD_MAX + P.round( MathUtil.randRangeDecimal( -COORD_MAX, COORD_MAX ) ) );
			_posBase.setTargetY( -COORD_MAX + P.round( MathUtil.randRangeDecimal( -COORD_MAX, COORD_MAX ) ) );
			_posBase.setTargetZ( -COORD_MAX + P.round( MathUtil.randRangeDecimal( -COORD_MAX, COORD_MAX ) ) );
			_inc = MathUtil.randRangeDecimal( 0, 1f );
			_incSpeed = MathUtil.randRangeDecimal( -0.005f, 0.005f );
			_radius = MathUtil.randRangeDecimal( _texture.width, _texture.width * 3 );
		}
		
		public void update() {
			_inc += _incSpeed;
			
			_posBase.update();
			_pos.x = _posBase.x() + P.sin( _inc ) * _radius;
			_pos.y = _posBase.y() + P.sin( _inc ) * _radius;
			_pos.z = _posBase.z() + P.cos( _inc ) * _radius * 0.3f;
		}

		public void dispose() {
			_pos = null;
			_posBase = null;
			_speed = null;
		}
	}	
	
	public class PointGroup {
		
		public Point[] _points;
		public int _numPoints;

		public PointGroup( int numPoints ){
			_numPoints = numPoints;
			_points = new Point[ _numPoints ];
			for( int i = 0; i < _numPoints; i++ ) {
				_points[ i ] = new Point();
			}
			reset();
		}
		
		public void reset() {
			for( int i = 0; i < _numPoints; i++ ) {
				_points[ i ].reset();
			}
		}

		public void update() {
			for( int i = 0; i < _numPoints; i++ ) {
				_points[ i ].update();
			}
		}

		public void drawPointsTriangles() {
			for( int i = 1; i < _numPoints; i++ ) {
				// draw a line - currently disabled from noStroke()
				
//				P.p.toxi.line( line );
				_texture.beginShape(P.LINE);
				_texture.vertex(_points[ i - 1 ]._pos.x, _points[ i - 1 ]._pos.y, _points[ i - 1 ]._pos.z);
				_texture.vertex(_points[ i - 0 ]._pos.x, _points[ i - 0 ]._pos.y, _points[ i - 0 ]._pos.z);
				_texture.endShape();

				
				// from 3rd point on, start connecting triangles
				if( i >= 2 ) {
//					tri = new Triangle3D( _points[ i - 2 ]._pos, _points[ i - 1 ]._pos, _points[ i ]._pos ); 
//					P.p.toxi.triangle( tri );
					
					_texture.beginShape(P.TRIANGLE);
					// map the screen coordinates to the texture coordinates
					_texture.vertex(_points[ i - 2 ]._pos.x, _points[ i - 2 ]._pos.y, _points[ i - 2 ]._pos.z);
					_texture.vertex(_points[ i - 1 ]._pos.x, _points[ i - 1 ]._pos.y, _points[ i - 1 ]._pos.z);
					_texture.vertex(_points[ i - 0 ]._pos.x, _points[ i - 0 ]._pos.y, _points[ i - 0 ]._pos.z);
					_texture.endShape();

				}
			}
		}

//		public void drawMesh() {
////			if( toxi != null && _mesh != null && _mesh.faces.size() > 0 && _mesh.getNumVertices() > 0 ) 
////				toxi.mesh( _mesh.copy() );
//			for( int i=0; i < _mesh.vertices)
//		}
		
		public void dispose() {
			for( int i = 0; i < _numPoints; i++ ) {
				_points[ i ].dispose();
			}
			_points = null;
		}
	}	
}
