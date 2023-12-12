package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.draw.toxi.MeshUtilToxi;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat3d;
import com.haxademic.core.vendor.Toxiclibs;

import toxi.geom.Sphere;
import toxi.geom.mesh.WETriangleMesh;

public class TextureSphereAudioTextures_OLD
extends BaseTexture {

	protected float _baseRadius;
	protected float _deformFactor;

	Sphere _sphere;
	WETriangleMesh _sphereMesh, _deformMesh;
	protected final float _ninteyDeg = P.PI / 2f;
	protected EasingFloat3d _rotation = new EasingFloat3d( 0, 0, 0, 5f );
	
	protected BaseTexture _sphereTexture;
	protected BaseTexture _texWindowShade;
//	protected IAudioTexture _eqGrid;
//	protected IAudioTexture _eqSquare;
//	protected IAudioTexture _imageTexture;
	
	public TextureSphereAudioTextures_OLD( int width, int height ) {
		super(width, height);

		
		
		float baseRadius = height * 0.5f;
		float deformFactor = 0.2f;
		_baseRadius = baseRadius;
		_deformFactor = deformFactor;
		createNewSphere();

//		_columns = new ColumnAudioTexture( 32 );
//		_eqGrid = new EQGridTexture( 32, 32 );
//		_eqSquare = new EQSquareTexture( 32, 32 );
//		_imageTexture = new TintedImageTexture();
		_texWindowShade = new TextureBasicWindowShade( 32, 32 );
		pickRandomTexture();
	}
	
	protected void createNewSphere() {
		_sphere = new Sphere( _baseRadius );
//		AABB box = new AABB( _baseRadius );
		_sphereMesh = new WETriangleMesh();
		_sphereMesh.addMesh( _sphere.toMesh( 30 ) );
		MeshUtilToxi.calcTextureCoordinates( _sphereMesh );
		_deformMesh = _sphereMesh.copy();
	}
	
	protected void pickRandomTexture() {
		int rand = MathUtil.randRange( 0, 3 );
		//P.println("new audio texture: "+rand);
		_sphereTexture = _texWindowShade;
//		if( rand == 0 ) {
//			_sphereTexture = _columns;
//		} else if( rand == 1 ) {
//			_sphereTexture = _imageTexture;
//		} else if( rand == 2 ) {
//			_sphereTexture = _eqSquare;
//		} else if( rand == 3 ) {
//			_sphereTexture = _eqGrid;
//		}
	}
	
	public void newRotation() {
		// random 45 degree angles
		_rotation.setTargetX( _ninteyDeg * MathUtil.randRange( 0, 8 ) );
		_rotation.setTargetY( _ninteyDeg * MathUtil.randRange( 0, 8 ) );
		_rotation.setTargetZ( _ninteyDeg * MathUtil.randRange( 0, 8 ) );
	}
	
	public void newMode() {
		pickRandomTexture();
	}
	
	public void draw() {
		pg.clear();

		// make sure no PGraphics drawing at the same time - draw sphere texture first
		_sphereTexture.setColor( _color );
		_sphereTexture.update();
			
		// then draw sphere w/texture applied
		
		pg.pushMatrix();
		
//		PG.setColorForPImage(_texture);
		PG.setCenterScreen(pg);
		pg.noStroke();
		
		pg.translate( 0, 0, -height/2f );
		_rotation.update();
		pg.rotateY( _rotation.x() );
		pg.rotateX( _rotation.y() );
		pg.rotateZ( _rotation.z() );
		
		// Now broken since the PAppletHax audio data updates
//		MeshUtilToxi.deformMeshWithAudio( _sphereMesh, _deformMesh, P.p._audioInput, _deformFactor );
	
		// draw texture. if tinting happened, reset after drawing
		Toxiclibs.instance(P.p).toxi.setGraphics(pg);
		if( _sphereTexture.texture() != null ) MeshUtilToxi.drawToxiMesh( P.p, Toxiclibs.instance(P.p).toxi, _deformMesh, _sphereTexture.texture() );
		PG.setColorForPImage(pg);
		PG.resetPImageAlpha(pg);
		
		pg.popMatrix();
	}
}
