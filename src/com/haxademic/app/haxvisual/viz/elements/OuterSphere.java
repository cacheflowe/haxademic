package com.haxademic.app.haxvisual.viz.elements;

import processing.core.PApplet;
import saito.objloader.OBJModel;
import toxi.color.TColor;
import toxi.geom.Sphere;
import toxi.geom.Vec3D;
import toxi.geom.mesh.WETriangleMesh;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.data.Point3D;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.toxi.DrawMesh;
import com.haxademic.core.draw.toxi.MeshUtilToxi;
import com.haxademic.core.math.MathUtil;

public class OuterSphere
extends ElementBase 
implements IVizElement {
	
	protected float _radius = 10000;
	protected float _targetRadius = 10000;
	protected boolean _isWireframe = true;
	protected boolean _isSphere = true;
	protected int _meshResolution;
	protected Point3D _rotSpeed = new Point3D( 0, 0, 0 );
	protected Point3D _rotation = new Point3D( 0, 0, 0 );
	protected Point3D _rotationTarget = new Point3D( 0, 0, 0 );
	protected TColor _baseColor;
	protected TColor _strokeColor;
	protected WETriangleMesh _sphereMesh;
	protected WETriangleMesh _objMesh;
	protected boolean _makeNewMesh;
	

	public OuterSphere( PApplet p, ToxiclibsSupport toxi, AudioInputWrapper audioData ) {
		super( p, toxi, audioData );
		init();
	}

	public void init() {
		// set some defaults
		_meshResolution = 30;
		buildModel();
		reset();
	}
	
	protected void buildModel() {
		OBJModel model = new OBJModel( p, "./models/the-discovery-multiplied-seied.obj" );
		model.disableMaterial();
		model.disableTexture();
		_objMesh = MeshUtilToxi.ConvertObjModelToToxiMesh( p, model );
		_objMesh.scale( _radius * 4 );
	}

	public void updateColorSet( ColorGroup colors ) {
		_baseColor = colors.getRandomColor().copy().lighten( 15 );
		_strokeColor = colors.getRandomColor().copy().lighten( 30 );
	}
	
	public void update() {
		DrawUtil.resetGlobalProps( p );
		DrawUtil.setCenter( p );
		p.pushMatrix();

		_rotation.x += _rotSpeed.x;
		_rotation.y += _rotSpeed.y;
		_rotation.z += _rotSpeed.z;
		p.rotateX( _rotation.x );
		p.rotateY( _rotation.y );
		p.rotateZ( _rotation.z );
		
		_radius = MathUtil.easeTo(_radius, _targetRadius, 5);
		
		if( _isWireframe ) {
			p.noFill(); 
		} else {
			p.noStroke(); 
		}
		
		if( _makeNewMesh == true ) {
			Sphere newSphere = new Sphere( new Vec3D(0,0,0), _radius );
			_sphereMesh = new WETriangleMesh();
			_sphereMesh.addMesh( newSphere.toMesh( _meshResolution ) );
			_makeNewMesh = false;
		}
	
		// draw outer spheres
		if( _isSphere ) {
			DrawMesh.drawMeshWithAudio( p, _sphereMesh, _audioData, _isWireframe, _baseColor, _strokeColor, 0 );
		} else {
			DrawMesh.drawMeshWithAudio( p, _objMesh, _audioData, _isWireframe, _baseColor, _strokeColor, 0 );
		}
		
		p.popMatrix();
	}

	public void reset() {
		updateLineMode();
		updateCamera();
	}
	
	public void updateTiming() {
		_targetRadius = MathUtil.randRange(1000, 1600);
	}
	
	public void updateLineMode() {
		_isWireframe = ( MathUtil.randBoolean( p ) == true ) ? false : true;
		_isSphere = ( MathUtil.randBoolean( p ) == true ) ? false : true;
		_meshResolution = (int) p.random( 10, 40 );
		
		// new sphere mesh flag - don't do it here since it's asynchronous apparently
		_makeNewMesh = true;
	}
	
	public void updateCamera() {
		float newRotSpeed = p.random( 0.001f, 0.01f );
		int wichAxis = (int) p.random( 0, 2 );
		_rotSpeed.x = ( wichAxis == 0 ) ? newRotSpeed : 0;
		_rotSpeed.y = ( wichAxis == 1 ) ? newRotSpeed : 0;
		_rotSpeed.z = ( wichAxis == 2 ) ? newRotSpeed : 0;
	}


	public void dispose() {
		_audioData = null;
	}

}
