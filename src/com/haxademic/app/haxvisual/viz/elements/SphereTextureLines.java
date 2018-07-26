package com.haxademic.app.haxvisual.viz.elements;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IAudioTexture;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.app.haxvisual.viz.textures.ColumnAudioTexture;
import com.haxademic.app.haxvisual.viz.textures.EQGridTexture;
import com.haxademic.app.haxvisual.viz.textures.EQSquareTexture;
import com.haxademic.app.haxvisual.viz.textures.TintedImageTexture;
import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.toxi.MeshUtilToxi;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat3d;

import processing.core.PApplet;
import toxi.color.TColor;
import toxi.geom.Sphere;
import toxi.geom.mesh.WETriangleMesh;
import toxi.processing.ToxiclibsSupport;

public class SphereTextureLines 
extends ElementBase 
implements IVizElement {
	
	protected float _baseRadius;
	protected float _deformFactor;

	protected TColor _baseColor = null;
	Sphere _sphere;
	WETriangleMesh _sphereMesh, _deformMesh;
	protected final float _ninteyDeg = P.PI / 2f;
	protected EasingFloat3d _rotation = new EasingFloat3d( 0, 0, 0, 5f );
	
	protected IAudioTexture _texture;
	protected IAudioTexture _columns;
	protected IAudioTexture _eqGrid;
	protected IAudioTexture _eqSquare;
	protected IAudioTexture _imageTexture;

	public SphereTextureLines( PApplet p, ToxiclibsSupport toxi ) {
		super( p, toxi );
		init();
	}

	public void init() {
		setDrawProps( 200, 1f );
		_columns = new ColumnAudioTexture( 32 );
		_eqGrid = new EQGridTexture( 32, 32 );
		_eqSquare = new EQSquareTexture( 32, 32 );
		_imageTexture = new TintedImageTexture();
		pickRandomTexture();
	}
	
	public void setDrawProps( float baseRadius, float deformFactor ) {
		_baseRadius = baseRadius * 0.7f;
		_deformFactor = deformFactor * 1.4f;
		createNewSphere();
	}
	
	protected void createNewSphere() {
		_sphere = new Sphere( _baseRadius );
//		AABB box = new AABB( _baseRadius );
		_sphereMesh = new WETriangleMesh();
		_sphereMesh.addMesh( _sphere.toMesh( 30 ) );
		MeshUtilToxi.calcTextureCoordinates( _sphereMesh );
		_deformMesh = _sphereMesh.copy();
	}
	
	public void updateCamera() {
		// random 45 degree angles
		_rotation.setTargetX( _ninteyDeg * MathUtil.randRange( 0, 8 ) );
		_rotation.setTargetY( _ninteyDeg * MathUtil.randRange( 0, 8 ) );
		_rotation.setTargetZ( _ninteyDeg * MathUtil.randRange( 0, 8 ) );
	}
	
	public void updateColorSet( ColorGroup colors ) {
		_baseColor = colors.getRandomColor().copy();
//		_texture.updateColorSet( colors );
		_columns.updateColorSet( colors );
		_eqGrid.updateColorSet( colors );
		_eqSquare.updateColorSet( colors );
		_imageTexture.updateColorSet( colors );
	}

	public void updateLineMode() {
		pickRandomTexture();
	}
	
	protected void pickRandomTexture() {
		int rand = MathUtil.randRange( 0, 3 );
		if( rand == 0 ) {
			_texture = _columns;
		} else if( rand == 1 ) {
			_texture = _imageTexture;
		} else if( rand == 2 ) {
			_texture = _eqSquare;
		} else if( rand == 3 ) {
			_texture = _eqGrid;
		}
	}
	
	public void update() {
//		if( _texture == null ) return;
		p.pushMatrix();
		
		DrawUtil.setColorForPImage(p);
		p.noStroke();
//		_texture.updateTexture( _audioData );

		
		p.translate( 0, 0, -400 );
		_rotation.update();
		p.rotateY( _rotation.x() );
		p.rotateX( _rotation.y() );
		p.rotateZ( _rotation.z() );
		
//		MeshUtilToxi.deformMeshWithAudio( _sphereMesh, _deformMesh, _audioData, _deformFactor );
	
		// draw texture. if tinting happened, reset after drawing
		if( _texture.getTexture() != null ) MeshUtilToxi.drawToxiMesh( p, toxi, _deformMesh, _texture.getTexture() );
		DrawUtil.setColorForPImage(p);
		DrawUtil.resetPImageAlpha(p);
		
		p.popMatrix();
	}

	public void reset() {
		pickRandomTexture();
		updateCamera();
	}

	public void dispose() {
		super.dispose();
	}

}
