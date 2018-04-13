package com.haxademic.app.haxvisual.viz.elements;

import processing.core.PApplet;
import toxi.color.TColor;
import toxi.geom.Line3D;
import toxi.geom.Triangle3D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.WETriangleMesh;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.app.P;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.color.TColorBlendBetween;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat3d;

public class RotatorShape 
extends ElementBase 
implements IVizElement {
	
	final int POINTS_MIN = 6;
	final int POINTS_MAX = 12;	// 2;//
	final int COORD_MIN = 0;
	final int COORD_MAX = 500;

	protected PointGroup _curPointGroup = null;
	protected int _numRotations;
	protected int _pointsPerGroup;
	
	protected float _baseRotZAdd = 0;
	protected float _baseRotZTarget = 0;
	protected float _rotDir = 0;
	
	protected TColorBlendBetween _color;
	
	
	public RotatorShape( PApplet p, ToxiclibsSupport toxi, AudioInputWrapper audioData, int numRotations ) {
		super( p, toxi, audioData );

		_numRotations = numRotations;//p.round( p.random( ROTATION_MIN, ROTATION_MAX ) );
		if( _numRotations % 2 != 0 ) _numRotations++;	// keep even numbers for proper reflection
				
		init();
	}
	
	public void init() {
		reset();
		_curPointGroup = new PointGroup( _pointsPerGroup );
		_curPointGroup.reset();
		_color = new TColorBlendBetween( TColor.BLACK.copy(), TColor.BLACK.copy() );
	}

	public void updateColorSet( ColorGroup colors ) {
//		if( p.frameCount % 2 == 0 )
		_color.setColors( TColor.BLACK.copy(), colors.getRandomColor() );
//		else
//			_color.setColors( colors.getRandomColor(), TColor.WHITE.copy() );
		_color.lightenColor( 0.3f );
	}

	public void update() {
		// rotate beginning z
		_baseRotZAdd = MathUtil.easeTo( _baseRotZAdd, _baseRotZTarget, 20 );
		p.rotateZ( _rotDir * p.frameCount - _baseRotZAdd );

		// update the single shape
		_curPointGroup.update();
		_curPointGroup.createPointsTrianglesSmoothed();
		
		// draw repeated shape 
		float rotationIncrement = p.TWO_PI / _numRotations;
		float spectrumData;
		p.noStroke();
		DrawUtil.setColorForPImage(p);
		for( int i = 0; i < _numRotations; i++ ) {
			spectrumData = 0.8f;// p._audioData.getFFT().spectrum[ 20 + (int) (i * (255f/_numRotations)) ];
			p.fill( _color.argbWithPercent( spectrumData ) );
//			p.fill( _baseColor.lighten( spectrumData * 255 * 30 ).toARGB() );
//			p.stroke( spectrumData * 255, spectrumData * 127 );
			p.pushMatrix();
			p.rotateZ( rotationIncrement * i );
			_curPointGroup.drawPointsTriangles();
			p.popMatrix();
		}
	}
	
	public void reset() {
		// come up with random numbers
		_pointsPerGroup = POINTS_MAX; //P.round( p.random( POINTS_MIN, POINTS_MAX ) );
		if( _curPointGroup != null ) _curPointGroup.reset();
	}
	
	public void updateLineMode() {
		reset();
	}
	
	public void updateCamera() {
		_baseRotZAdd = ( MathUtil.randBoolean( p ) ) ? 0.8f : -0.8f;
		_rotDir = p.random( -4, 4 )/ 1000;
	}


	
	public void dispose() {
		_curPointGroup.dispose();
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
			_posBase.setTargetX( p.round( p.random( -COORD_MAX, COORD_MAX ) ) );
			_posBase.setTargetY( p.round( p.random( -COORD_MAX, COORD_MAX ) ) );
			_posBase.setTargetZ( p.round( p.random( -COORD_MAX, COORD_MAX ) ) );
			_inc = p.random( 0, 1f );
			_incSpeed = p.random( -0.005f, 0.005f );
			_radius = p.random( 200f, 400f );
		}
		
		public void update() {
			_inc += _incSpeed;
			
			_posBase.update();
			_pos.x = _posBase.x() + p.sin( _inc ) * _radius;
			_pos.y = _posBase.y() + p.sin( _inc ) * _radius;
			_pos.z = _posBase.z() + p.cos( _inc ) * _radius;
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
		protected WETriangleMesh _mesh;

		public PointGroup( int numPoints ){
			_numPoints = numPoints;
			_points = new Point[ _numPoints ];
			for( int i = 0; i < _numPoints; i++ ) {
				_points[ i ] = new Point();
			}
			reset();
			createPointsTrianglesSmoothed();
		}
		
		public void reset() {
			createPointsTrianglesSmoothed()	;		
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
			Line3D line;
			Triangle3D tri;
			for( int i = 1; i < _numPoints; i++ ) {
				// draw a line - currently disabled from noStroke()
				line = new Line3D( _points[ i - 1 ]._pos, _points[ i ]._pos ); 
				toxi.line( line );
				
				// from 3rd point on, start connecting triangles
				if( i >= 2 ) {
					tri = new Triangle3D( _points[ i - 2 ]._pos, _points[ i - 1 ]._pos, _points[ i ]._pos ); 
					toxi.triangle( tri );
				}
			}
		}

		public void createPointsTrianglesSmoothed() {
			if( _mesh != null ) _mesh.clear();
			if( _mesh == null ) _mesh = new WETriangleMesh();
			for( int i = 1; i < _numPoints; i++ ) {
				// from 3rd point on, start connecting triangles
				if( i >= 2 ) {
					_mesh.addFace( _points[ i - 2 ]._pos, _points[ i - 1 ]._pos, _points[ i ]._pos );
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

//SubdivisionStrategy subdiv = new MidpointDisplacementSubdivision( _mesh.computeCentroid(), -0.22f );
//SubdivisionStrategy subdiv = new NormalDisplacementSubdivision(0.25f);
//SubdivisionStrategy subdiv = new NormalDisplacementSubdivision(0.3f);
//_mesh.subdivide( subdiv );
//SubdivisionStrategy subdiv = new NormalDisplacementSubdivision(0.2f);
//_mesh.subdivide( subdiv );
//subdiv = new NormalDisplacementSubdivision(0.17f);
//_mesh.subdivide( subdiv );
//subdiv = new NormalDisplacementSubdivision(0.13f);
//_mesh.subdivide( subdiv );
//subdiv = new NormalDisplacementSubdivision(0.1f);
//_mesh.subdivide( subdiv );
//subdiv = new NormalDisplacementSubdivision(0.075f);
//_mesh.subdivide( subdiv );
//subdiv = new NormalDisplacementSubdivision(0.05f);
//_mesh.subdivide( subdiv );
////subdiv = new NormalDisplacementSubdivision(0.03f);
////_mesh.subdivide( subdiv );
//subdiv = new NormalDisplacementSubdivision(0.01f);
//_mesh.subdivide( subdiv );
//_mesh.subdivide( subdiv, 10 );
//_mesh.subdivide( subdiv, 10 );
//_mesh.subdivide( subdiv, 10 );

//SubdivisionStrategy subdiv = new TriSubdivision();
//_mesh.subdivide( subdiv );
//_mesh.subdivide( subdiv );
////_mesh.subdivide( subdiv );
////_mesh.subdivide( subdiv );
//ThreeDeeUtil.SmoothToxiMesh( p, _mesh, 10 );

