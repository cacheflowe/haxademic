package com.haxademic.app.haxvisual.viz.elements;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.toxi.DrawMesh;
import com.haxademic.core.draw.toxi.MeshPool;
import com.haxademic.core.draw.toxi.MeshUtilToxi;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

import processing.core.PVector;
import toxi.color.TColor;
import toxi.geom.mesh.WETriangleMesh;
import toxi.processing.ToxiclibsSupport;

public class MeshDeform 
extends ElementBase 
implements IVizElement {

	protected MeshPool _meshPool, _meshPoolDeformed;
	protected WETriangleMesh _curMesh, _curMeshDeformed;
	protected int _meshIndex = -1;
	protected boolean isWireFrame;
	protected float _colorGradientDivider = 1;

	protected TColor _baseColor = null;
	protected TColor _strokeColor = null;
	protected boolean _isWireframe = true;

	protected PVector _rotSpeed = new PVector( 0, 0, 0 );
	protected PVector _rotation = new PVector( 0, 0, 0 );
	protected PVector _rotationTarget = new PVector( 0, 0, 0 );

	public MeshDeform( PAppletHax p, ToxiclibsSupport toxi, AudioInputWrapper audioData ) {
		super( p, toxi, audioData );
		init();
	}

	public void init() {
		float scaleMult = 1.0f;
		_meshPool = new MeshPool( p );
		_meshPoolDeformed = new MeshPool( p );
//		_meshPool.addMesh( "MUSIC_NOTE", MeshUtil.meshFromImg( p, FileUtil.getHaxademicDataPath() + "images/music.gif", 1f ), 40f * scaleMult );
		
//		_meshPool.addMesh( "DIAMOND", MeshUtil.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/diamond.obj", 1f ), 1.2f * scaleMult );
//		_meshPool.addMesh( "INVADER", MeshUtil.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/invader.obj", 1f ), 45 * scaleMult );
//		_meshPool.addMesh( "LEGO_MAN", MeshUtil.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/lego-man.obj", 1f ), 30 * scaleMult );
		
		_meshPool.addMesh( "DISCOVERY", MeshUtilToxi.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/the-discovery-multiplied-seied.obj", 1f ), 900 * scaleMult );
		_meshPool.addMesh( "TOPSECRET", MeshUtilToxi.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/topsecret-seied.obj", 1f ), 400 * scaleMult );
		_meshPool.addMesh( "SKULL", MeshUtilToxi.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/skull.obj", 1f ), 50 * scaleMult );
//		_meshPool.addMesh( "MODE_SET", MeshUtil.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/mode-set.obj", 1f ), 250 * scaleMult );
		
		_meshPool.addMesh( "POLY_HOLE_PENT", MeshUtilToxi.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/poly-hole-penta.obj", 1f ), 70f * scaleMult );
		_meshPool.addMesh( "POLY_HOLE_SQUARE", MeshUtilToxi.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/poly-hole-square.obj", 1f ), 70f * scaleMult );
		_meshPool.addMesh( "POLY_HOLE_TRI", MeshUtilToxi.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/poly-hole-tri.obj", 1f ), 70f * scaleMult );

		
		
		
//		_meshPool.addMesh( "MAYAN_PYRAMID", MeshUtil.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/pyramid-mayan.obj", 1f ), 2 * scaleMult );
		
//		_meshPool.addMesh( "CACHEFLOWE", MeshUtil.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/cacheflowe-3d.obj", 1f ), 150 * scaleMult );
				
//		_meshPool.addMesh( "CDW_LOGO", MeshUtil.meshFromSVG( p, FileUtil.getHaxademicDataPath() + "svg/create-denver-logo.svg", -1, 2, 0.8f ), 1 );
//		_meshPool.addMesh( "DIAMOND_2D", MeshUtil.getExtrudedMesh( MeshUtil.meshFromSVG( p, FileUtil.getHaxademicDataPath() + "svg/diamond.svg", -1, 3, 0.5f ), 20 ), 1 );

//		WETriangleMesh sphere = new WETriangleMesh();
//		sphere.addMesh( (new Sphere(200)).toMesh( 15 ) );
//		_meshPool.addMesh( "SPHERE", sphere, 1f * scaleMult );
		
		// copy models into deformed model pool
		for( int i=0; i < _meshPool.size(); i++ ) {
			String id = _meshPool.getIds().get( i );
			_meshPoolDeformed.addMesh( id, _meshPool.getMesh( id ).copy(), 1f );
		}

		selectNewModel();
	}

	public void setDrawProps() {

	}

	public void updateColorSet( ColorGroup colors ) {
		_baseColor = colors.getRandomColor().copy().lighten( 20 );
		_strokeColor = _baseColor.copy().lighten( 20 );
	}

	public void update() {
		DrawUtil.resetGlobalProps( p );
		DrawUtil.setCenter( p );
		p.pushMatrix();

		p.translate( 0, 0, -600 );

		_rotation.x += _rotSpeed.x;
		_rotation.y += _rotSpeed.y;
		_rotation.z += _rotSpeed.z;
		p.rotateX( _rotation.x );
		p.rotateY( _rotation.y );
		p.rotateZ( _rotation.z );

		if( _isWireframe ) {
			p.noFill(); 
			p.strokeWeight( 3 );
		} else {
			p.noStroke(); 
		}

		// deform and draw mesh
		if( _curMesh != null && _curMeshDeformed != null ) {
			MeshUtilToxi.deformMeshWithAudio( _curMesh, _curMeshDeformed, _audioData, 1f );
			DrawMesh.drawMeshWithAudio( p, _curMeshDeformed, _audioData, _isWireframe, _baseColor, _strokeColor, 0.1f );
		}
		
		p.popMatrix();
	}

	protected void updateRotation() {
		_rotation.lerp( _rotationTarget, 0.2f );
		p.rotateX( _rotation.x );
		p.rotateY( _rotation.y );
		p.rotateZ( _rotation.z );

		_rotationTarget.x += _rotSpeed.x;
		_rotationTarget.y += _rotSpeed.y;
		_rotationTarget.z += _rotSpeed.z;
	}

	public void reset() {
		updateCamera();
		updateLineMode();
		selectNewModel();
	}

	public void selectNewModel() {
		_meshIndex++;
		if( _meshIndex >= _meshPool.size() ) _meshIndex = 0;
		_curMesh = _meshPool.getMesh( _meshPool.getIds().get( _meshIndex ) );
		_curMeshDeformed = _meshPoolDeformed.getMesh( _meshPoolDeformed.getIds().get( _meshIndex ) );
	}

	public void updateLineMode() {
		_isWireframe = MathUtil.randBoolean( p );
	}

	public void updateCamera() {
		// rotate
		float circleSegment = (float) ( Math.PI * 2f );
		_rotationTarget.x = p.random( -circleSegment, circleSegment );
		_rotationTarget.y = p.random( -circleSegment, circleSegment );
		_rotationTarget.z = p.random( -circleSegment, circleSegment );

		_rotSpeed.x = p.random( 0.001f, 0.01f );
		_rotSpeed.y = p.random( 0.001f, 0.01f );
		_rotSpeed.z = p.random( 0.001f, 0.01f );
	}

	public void dispose() {
		_audioData = null;
	}
}
