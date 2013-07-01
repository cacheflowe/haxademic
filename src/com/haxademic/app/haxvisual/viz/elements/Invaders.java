package com.haxademic.app.haxvisual.viz.elements;

import processing.core.PApplet;
import processing.core.PConstants;
import saito.objloader.OBJModel;
import toxi.color.TColor;
import toxi.geom.mesh.WETriangleMesh;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.mesh.MeshUtil;
import com.haxademic.core.draw.shapes.Meshes;
import com.haxademic.core.draw.util.DrawMesh;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat3d;

public class Invaders
extends ElementBase 
implements IVizElement {
	
	protected WETriangleMesh _invaderMesh_01, _invaderMesh_01_alt, _invaderMesh_02, _invaderMesh_02_alt, _invaderMesh_03, _invaderMesh_03_alt, _logoMesh;
	protected int _numLines;
	protected TColor _baseColor, _logo_color, _invader_01_color, _invader_02_color, _invader_03_color;
	protected boolean _isWireFrame, _invader_01_wireframe, _invader_02_wireframe, _invader_03_wireframe = false;
	protected float _cols = 32;
	protected float _rows = 16;

	protected EasingFloat3d _rotation = new EasingFloat3d( 0f, 0f, 0f, 10f );
	protected float TOTAL_LINE_WIDTH = 5900;
	protected float SCROLL_SPEED = 20;

	public Invaders( PApplet p, ToxiclibsSupport toxi, AudioInputWrapper audioData ) {
		super( p, toxi, audioData );
		init();
	}

	public void init() {
		_invaderMesh_01 = Meshes.invader1( 1, 70 );
		_invaderMesh_01_alt = Meshes.invader1( 2, 70 );
		
		_invaderMesh_02 = Meshes.invader2( 1, 70 );
		_invaderMesh_02_alt = Meshes.invader2( 2, 70 );

		_invaderMesh_03 = Meshes.invader3( 1, 70 );
		_invaderMesh_03_alt = Meshes.invader3( 2, 70 );
		
		
		OBJModel model = new OBJModel( p, "./models/submish-horiz-rotated.obj" );
		model.disableMaterial();
		model.disableTexture();
		_logoMesh = MeshUtil.ConvertObjModelToToxiMesh( p, model );
		_logoMesh.scale( 400f );
	}
	
	public void updateColorSet( ColorGroup colors ) {
		_logo_color = colors.getRandomColor().copy();
		_invader_01_color = colors.getRandomColor().copy();
		_invader_02_color = colors.getRandomColor().copy();
		_invader_03_color = colors.getRandomColor().copy();
		_baseColor = colors.getRandomColor().copy();
	}

	public void update() {
		DrawUtil.resetGlobalProps( p );
		DrawUtil.setCenter( p );
		p.pushMatrix();
		
		p.translate( 0, 0, -2000f );
		
		p.rectMode(PConstants.CENTER);
		p.noStroke();
		
		// ease rotation of the view
		_rotation.update();
		p.rotateX( _rotation.x() );
		p.rotateY( _rotation.y() );
		p.rotateZ( _rotation.z() );
		
		// scroll left
		p.translate( -TOTAL_LINE_WIDTH + (-p.frameCount*SCROLL_SPEED) % TOTAL_LINE_WIDTH, 0, 0 );

		// draw 2 object lines
		drawObjectLine();
		p.translate( TOTAL_LINE_WIDTH, 0, 0 );
		drawObjectLine();
		p.translate( TOTAL_LINE_WIDTH, 0, 0 );
		drawObjectLine();
		
		p.popMatrix();
	}
	
	protected void drawObjectLine() {
		p.pushMatrix();

		// draw logo
		DrawMesh.drawMeshWithAudio( p, _logoMesh, _audioData, false, _logo_color, _logo_color, 0.25f );
		p.translate(1900f, 0, 0);

		// draw invaders
		WETriangleMesh mesh2 = ( p.round( p.frameCount / 30f ) % 2 == 0 ) ? _invaderMesh_02 : _invaderMesh_02_alt;
		DrawMesh.drawMeshWithAudio( p, mesh2, _audioData, _invader_02_wireframe, _invader_02_color, _invader_02_color, 0.25f );
		p.translate(1000f, 0, 0);
		
		WETriangleMesh mesh3 = ( p.round( p.frameCount / 30f ) % 2 == 0 ) ? _invaderMesh_03 : _invaderMesh_03_alt;
		DrawMesh.drawMeshWithAudio( p, mesh3, _audioData, _invader_03_wireframe, _invader_03_color, _invader_03_color, 0.25f );
		p.translate(1000f, 0, 0);
		
		WETriangleMesh mesh1 = ( p.round( p.frameCount / 30f ) % 2 == 0 ) ? _invaderMesh_01 : _invaderMesh_01_alt;
		DrawMesh.drawMeshWithAudio( p, mesh1, _audioData, _invader_01_wireframe, _invader_01_color, _invader_01_color, 0.25f );

		p.popMatrix();
	}

	public void reset() {
		updateLineMode();
		updateCamera();
	}

	public void updateLineMode() {
		_isWireFrame = ( MathUtil.randBoolean( p ) ) ? false : true;
		_invader_01_wireframe = ( MathUtil.randBoolean( p ) ) ? false : true;
		_invader_02_wireframe = ( MathUtil.randBoolean( p ) ) ? false : true;
		_invader_03_wireframe = ( MathUtil.randBoolean( p ) ) ? false : true;
	}
	
	public void updateCamera() {
		float circleSegment = (float) ( Math.PI * 2f ) / 16f;
		_rotation.setTargetX( p.random( -circleSegment * 2, circleSegment * 2 ) );
		_rotation.setTargetY( p.random( -circleSegment, circleSegment ) );
		_rotation.setTargetZ( p.random( -circleSegment, circleSegment ) );
	}
	
	public void dispose() {
		_audioData = null;
	}
	
}
