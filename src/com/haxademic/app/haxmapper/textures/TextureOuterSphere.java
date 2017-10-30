package com.haxademic.app.haxmapper.textures;

import com.haxademic.core.app.P;
import com.haxademic.core.data.Point3D;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.toxi.DrawMesh;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

import saito.objloader.OBJModel;
import toxi.color.TColor;
import toxi.geom.Sphere;
import toxi.geom.Vec3D;
import toxi.geom.mesh.WETriangleMesh;

public class TextureOuterSphere
extends BaseTexture {

	protected float _radius = 1000;
	protected float _targetRadius = 1000;
	protected boolean _isWireframe = true;
	protected boolean _isSphere = true;
	protected int _meshResolution;
	protected Point3D _rotSpeed = new Point3D( 0, 0, 0 );
	protected Point3D _rotation = new Point3D( 0, 0, 0 );
	protected Point3D _rotationTarget = new Point3D( 0, 0, 0 );
	protected TColor _baseColor = new TColor(TColor.WHITE);
	protected TColor _strokeColor = new TColor(TColor.WHITE);
	protected WETriangleMesh _sphereMesh;
//	protected WETriangleMesh _objMesh;
	protected boolean _makeNewMesh;

	public TextureOuterSphere( int width, int height ) {
		super();
		buildGraphics( width, height );
		
		_meshResolution = 30;
//		buildModel();
		reset();

	}
	
	protected void buildModel() {
		OBJModel model = new OBJModel( P.p, FileUtil.getHaxademicDataPath() + "models/the-discovery-multiplied-seied.obj" );
		model.disableMaterial();
		model.disableTexture();
//		_objMesh = MeshUtil.ConvertObjModelToToxiMesh( P.p, model );
//		_objMesh.scale( _radius );
	}

	
	public void reset() {
		newLineMode();
		updateCamera();
	}
	
	public void newLineMode() {
		_isWireframe = ( MathUtil.randBoolean( P.p ) == true ) ? false : true;
		_isSphere = ( MathUtil.randBoolean( P.p ) == true ) ? false : true;
		_meshResolution = (int) P.p.random( 10, 40 );
		
		// new sphere mesh flag - don't do it here since it's asynchronous apparently
		_makeNewMesh = true;
	}
	
	public void updateCamera() {
		float newRotSpeed = P.p.random( 0.001f, 0.01f );
		int wichAxis = (int) P.p.random( 0, 2 );
		_rotSpeed.x = ( wichAxis == 0 ) ? newRotSpeed : 0;
		_rotSpeed.y = ( wichAxis == 1 ) ? newRotSpeed : 0;
		_rotSpeed.z = ( wichAxis == 2 ) ? newRotSpeed : 0;
	}

	public void updateDraw() {
		_texture.clear();
		
//		_texture.background(0);
		DrawUtil.resetGlobalProps( _texture );
		DrawUtil.setCenterScreen( _texture );
		_texture.pushMatrix();

		_rotation.x += _rotSpeed.x;
		_rotation.y += _rotSpeed.y;
		_rotation.z += _rotSpeed.z;
		_texture.rotateX( _rotation.x );
		_texture.rotateY( _rotation.y );
		_texture.rotateZ( _rotation.z );
		
		_radius = MathUtil.easeTo(_radius, _targetRadius, 5);
		
		if( _isWireframe ) {
			_texture.noFill(); 
		} else {
			_texture.noStroke(); 
		}
		
		if( _makeNewMesh == true ) {
			Sphere newSphere = new Sphere( new Vec3D(0,0,0), _radius );
			_sphereMesh = new WETriangleMesh();
			_sphereMesh.addMesh( newSphere.toMesh( _meshResolution ) );
			_makeNewMesh = false;
		}
	
		// draw outer spheres
		_baseColor.setARGB(_color);
		_strokeColor.setARGB(_color);
		_texture.strokeWeight = 4;
//		if( _isSphere ) {
			DrawMesh.drawMeshWithAudio( _texture, _sphereMesh, P.p._audioInput, _isWireframe, _baseColor, _strokeColor, 0 );
//		} else {
//			DrawMesh.drawMeshWithAudio( _texture, _objMesh, P.p._audioInput, _isWireframe, _baseColor, _strokeColor, 0 );
//		}
		
		_texture.popMatrix();
	}
}
