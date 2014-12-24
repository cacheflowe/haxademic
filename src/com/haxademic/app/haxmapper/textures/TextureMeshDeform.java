package com.haxademic.app.haxmapper.textures;

import toxi.geom.mesh.WETriangleMesh;

import com.haxademic.core.app.P;
import com.haxademic.core.data.Point3D;
import com.haxademic.core.draw.mesh.DrawMesh;
import com.haxademic.core.draw.mesh.MeshPool;
import com.haxademic.core.draw.mesh.MeshUtil;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.system.FileUtil;

public class TextureMeshDeform 
extends BaseTexture {

	protected MeshPool _meshPool, _meshPoolDeformed;
	protected WETriangleMesh _curMesh, _curMeshDeformed;
	protected int _meshIndex = -1;
	protected boolean isWireFrame;
	protected float _colorGradientDivider = 1;

	protected boolean _isWireframe = true;

	protected Point3D _rotSpeed = new Point3D( 0, 0, 0 );
	protected Point3D _rotation = new Point3D( 0, 0, 0 );
	protected Point3D _rotationTarget = new Point3D( 0, 0, 0 );

	public TextureMeshDeform( int width, int height ) {
		super();

		buildGraphics( width, height );
		
		init();
	}

	public void init() {
		float scaleMult = 1.0f;
		_meshPool = new MeshPool( P.p );
		_meshPoolDeformed = new MeshPool( P.p );
		
		_meshPool.addMesh( "DISCOVERY", MeshUtil.meshFromOBJ( P.p, FileUtil.getHaxademicDataPath() + "models/the-discovery-multiplied-seied.obj", 1f ), 900 * scaleMult );
		_meshPool.addMesh( "TOPSECRET", MeshUtil.meshFromOBJ( P.p, FileUtil.getHaxademicDataPath() + "models/topsecret-seied.obj", 1f ), 400 * scaleMult );
		_meshPool.addMesh( "SKULL", MeshUtil.meshFromOBJ( P.p, FileUtil.getHaxademicDataPath() + "models/skull.obj", 1f ), 75 * scaleMult );
//		_meshPool.addMesh( "ELLO", MeshUtil.getExtrudedMesh( MeshUtil.meshFromSVG( P.p, FileUtil.getHaxademicDataPath() + "svg/ello.svg", -1, 20, 0.5f ), 20), scaleMult );
		_meshPool.addMesh( "POLY_HOLE_PENT", MeshUtil.meshFromOBJ( P.p, FileUtil.getHaxademicDataPath() + "models/poly-hole-penta.obj", 1f ), 70f * scaleMult );
		_meshPool.addMesh( "POLY_HOLE_SQUARE", MeshUtil.meshFromOBJ( P.p, FileUtil.getHaxademicDataPath() + "models/poly-hole-square.obj", 1f ), 70f * scaleMult );
		_meshPool.addMesh( "POLY_HOLE_TRI", MeshUtil.meshFromOBJ( P.p, FileUtil.getHaxademicDataPath() + "models/poly-hole-tri.obj", 1f ), 70f * scaleMult );

		// copy models into deformed model pool
		for( int i=0; i < _meshPool.size(); i++ ) {
			String id = _meshPool.getIds().get( i );
			_meshPoolDeformed.addMesh( id, _meshPool.getMesh( id ).copy(), 1f );
		}

		selectNewModel();
	}
	
	public void newLineMode() {
		_isWireframe = MathUtil.randBoolean( P.p );
	}
	
	public void newRotation() {
		// rotate
		float circleSegment = (float) ( Math.PI * 2f );
		_rotationTarget.x = MathUtil.randRangeDecimal( -circleSegment, circleSegment );
		_rotationTarget.y = MathUtil.randRangeDecimal( -circleSegment, circleSegment );
		_rotationTarget.z = MathUtil.randRangeDecimal( -circleSegment, circleSegment );

		_rotSpeed.x = MathUtil.randRangeDecimal( 0.001f, 0.01f );
		_rotSpeed.y = MathUtil.randRangeDecimal( 0.001f, 0.01f );
		_rotSpeed.z = MathUtil.randRangeDecimal( 0.001f, 0.01f );
	}
	
	public void updateRotation() {
		_rotation.easeToPoint( _rotationTarget, 5 );
		_texture.rotateX( _rotation.x );
		_texture.rotateY( _rotation.y );
		_texture.rotateZ( _rotation.z );

		_rotationTarget.x += _rotSpeed.x;
		_rotationTarget.y += _rotSpeed.y;
		_rotationTarget.z += _rotSpeed.z;
	}

	public void newMode() {
		selectNewModel();
	}
	public void selectNewModel() {
		_meshIndex++;
		if( _meshIndex >= _meshPool.size() ) _meshIndex = 0;
		_curMesh = _meshPool.getMesh( _meshPool.getIds().get( _meshIndex ) );
		_curMeshDeformed = _meshPoolDeformed.getMesh( _meshPoolDeformed.getIds().get( _meshIndex ) );
	}

	public void updateDraw() {
		_texture.clear();
		
		DrawUtil.resetGlobalProps( _texture );
		DrawUtil.setCenterScreen( _texture );
		_texture.pushMatrix();

		_texture.translate( 0, 0, -300 );

		_rotation.x += _rotSpeed.x;
		_rotation.y += _rotSpeed.y;
		_rotation.z += _rotSpeed.z;
		_texture.rotateX( _rotation.x );
		_texture.rotateY( _rotation.y );
		_texture.rotateZ( _rotation.z );

		if( _isWireframe ) {
			_texture.noFill(); 
			_texture.strokeWeight( 3 );
		} else {
			_texture.noStroke(); 
		}

		// deform and draw mesh
		if( _curMesh != null && _curMeshDeformed != null ) {
			MeshUtil.deformMeshWithAudio( _curMesh, _curMeshDeformed, P.p._audioInput, 1f );
			DrawMesh.drawMeshWithAudio( _texture, _curMeshDeformed, P.p._audioInput, _isWireframe, _color, _color, 0.1f );
		}
		
		_texture.popMatrix();		
	}
	
}
