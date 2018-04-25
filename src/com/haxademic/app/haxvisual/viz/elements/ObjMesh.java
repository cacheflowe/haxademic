package com.haxademic.app.haxvisual.viz.elements;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.toxi.DrawMesh;

import processing.core.PApplet;
import processing.core.PVector;
import toxi.color.TColor;
import toxi.geom.mesh.TriangleMesh;
import toxi.geom.mesh.WETriangleMesh;
import toxi.processing.ToxiclibsSupport;

public class ObjMesh 
extends ElementBase 
implements IVizElement {
	
	protected TriangleMesh mesh = new TriangleMesh();
	protected boolean isWireFrame;
	protected WETriangleMesh _objMesh;
	protected float _colorGradientDivider = 1;
	
	protected TColor _baseColor = null;
	protected TColor _strokeColor = null;
	protected boolean _isWireframe = true;
	protected boolean _isPoints = false;
	
	protected PVector _rotSpeed = new PVector( 0, 0, 0 );
	protected PVector _rotation = new PVector( 0, 0, 0 );
	protected PVector _rotationTarget = new PVector( 0, 0, 0 );

	public ObjMesh( PApplet p, ToxiclibsSupport toxi, AudioInputWrapper audioData ) {
		super( p, toxi, audioData );
		init();
	}

	public void init() {
		buildModel();
	}
	
	protected void buildModel() {
//		OBJModel model = new OBJModel( p, "./models/THEDISCOVERYMULTIPLIED.obj" );
//		OBJModel model = new OBJModel( p, "./models/car65.obj" );
//		OBJModel model = new OBJModel( p, "./models/lego-man.obj" );
//		model.disableMaterial();
//		model.disableTexture();
//		_objMesh = MeshUtilToxi.ConvertObjModelToToxiMesh( p, model );
//		_objMesh.scale( 2000 );
//		_objMesh.scale( 50 );
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
		
		// draw outer spheres
		if( _isPoints == true ) {
			DrawMesh.drawPointsWithAudio( p, _objMesh, _audioData, _colorGradientDivider, 15, _baseColor, _strokeColor, 0f );
		} else {
			DrawMesh.drawMeshWithAudio( p, _objMesh, _audioData, _isWireframe, _baseColor, _strokeColor, 0 );
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
	}
	
	public void updateLineMode() {
		int linesMode = p.round( p.random( 0, 1 ) );
		if( linesMode == 0 ) {
			_isWireframe = true;
			_isPoints = false;
		} else if( linesMode == 1 ) {
			_isWireframe = false;
			_isPoints = false;
		} else if( linesMode == 2 ) {
			_isWireframe = false;
			_isPoints = true;
		}
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
