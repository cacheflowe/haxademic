package com.haxademic.app.kacheout.game;

import toxi.geom.Vec3D;
import toxi.geom.mesh.WETriangleMesh;

import com.haxademic.core.draw.util.ThreeDeeUtil;
import com.haxademic.core.math.MathUtil;

public class Shard {
	
	protected WETriangleMesh _mesh; 
	protected Vec3D _origPos; 
	protected Vec3D _curPos;
	protected Vec3D _speed;
	protected Vec3D _rotationSpeed;
	protected Vec3D _curRotation;
	
	public Shard( WETriangleMesh mesh, float x, float y, float z ) {
		_mesh = mesh;
		ThreeDeeUtil.addPositionToMesh( mesh, x, y, z );
		
		_origPos = mesh.computeCentroid();
		_curPos = mesh.computeCentroid();
		_speed = new Vec3D( 0, 0, 0 );
		_curRotation = new Vec3D( 0, 0, 0 );
		_rotationSpeed = new Vec3D( MathUtil.randRangeDecimal( -0.1f, 0.1f ), MathUtil.randRangeDecimal( -0.1f, 0.1f ), MathUtil.randRangeDecimal( -0.1f, 0.1f ) );
	}
	
	public WETriangleMesh mesh() {
		return _mesh;
	}
	
	public void update() {
		_curPos.x += _speed.x;
		_curPos.y += _speed.y;
		_curPos.z += _speed.z;
		
		_mesh.center( new Vec3D( _curPos.x, _curPos.y, _curPos.z ) );

		_curRotation.x = _curRotation.x + _rotationSpeed.x;
		_curRotation.y = _curRotation.y + _rotationSpeed.y;
		_curRotation.x = _curRotation.z + _rotationSpeed.z;
		
//		_mesh.rotateX( _curRotation.x );
//		_mesh.rotateY( _curRotation.y );
//		_mesh.rotateZ( _curRotation.z );
		
//		ThreeDeeUtil.addPositionToMesh( _mesh, _speed.x, _speed.y, _speed.z );
	}
	
	public void setSpeed( float x, float y, float z ) {
		_speed.x = x;
		_speed.y = y;
		_speed.z = z;
	}
	
	public void reset() {
		_speed.x = 0;
		_speed.y = 0;
		_speed.z = 0;
		_curPos.x = _origPos.x;
		_curPos.y = _origPos.y;
		_curPos.z = _origPos.z;
		_curRotation.x = 0;
		_curRotation.y = 0;
		_curRotation.z = 0;
	}
}
