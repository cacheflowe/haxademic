package com.haxademic.core.draw.textures.pgraphics;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.ChromaColorFilter;
import com.haxademic.core.draw.filters.pshader.VignetteAltFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat3d;

import processing.core.PGraphics;
import processing.core.PShape;

public class TextureSphereAudioTextures_HaxVisualOnly
extends BaseTexture {

	PGraphics sphereTexture;
	
	protected EasingFloat3d _rotation = new EasingFloat3d( 0, 0, 0, 7f );
	protected final float _ninteyDeg = P.PI / 6f;
	
	protected BaseTexture[] audioTextures;
	protected int texturePoolIndex = 0;
	protected boolean audioMode = false;
	
	protected PShape shape;
	protected PShape shapeTessellated;
	protected PShape shapeIcos;
	
	public TextureSphereAudioTextures_HaxVisualOnly( int width, int height ) {
		super(width, height);
		
		sphereTexture = PG.newPG(1024, 512);
		sphereTexture.noSmooth();
//		buildAudioTextures();
//		pickRandomTexture();
	}
	
	protected void createNewSphere() {
		P.p.sphereDetail(40);
		shape = P.p.createShape(P.SPHERE, height/2.25f);
		shapeTessellated = shape.getTessellation();
		
		float extent = PShapeUtil.getMaxExtent(shape);
		
//		shape.setTexture(sphereTexture);
//		shapeTessellated.setTexture(sphereTexture);
		
		shapeIcos = Icosahedron.createIcosahedron(_texture, 7, sphereTexture);
		PShapeUtil.scaleShapeToExtent(shapeIcos, extent);
		
//		objSolid = new PShapeSolid(shapeIcos);

//		PShapeUtil.addUVsToPShape(shape, extent);
//		PShapeUtil.addUVsToPShape(shapeTessellated, extent);
	}
	
	protected void pickRandomTexture() {
		texturePoolIndex = 2; // (only the audio layer for now) // MathUtil.randRange(0, _curTexturePool.size() - 2);
	}
	
	public void setCurTexturePool(ArrayList<BaseTexture> curTexturePool) {
		super.setCurTexturePool(curTexturePool);
		pickRandomTexture();
	}
	
	public void newRotation() {
		_rotation.setTargetX( _ninteyDeg * MathUtil.randRangeDecimal( -1, 1 ) );
		_rotation.setTargetY( _ninteyDeg * MathUtil.randRangeDecimal( -1, 1 ) );
		_rotation.setTargetZ( _ninteyDeg * MathUtil.randRangeDecimal( -1, 1 ) );
	}
	
	public void newMode() {
		pickRandomTexture();
	}
	
	public void preDraw() {
		sphereTexture.beginDraw();
		PG.fadeToBlack(sphereTexture, 10);
		sphereTexture.background(0);
		sphereTexture.endDraw();

		// draw texture to sphere buffer
		ImageUtil.cropFillCopyImage(_curTexturePool.get(texturePoolIndex).texture(), sphereTexture, true);
		
		// effects
		ChromaColorFilter.instance(P.p).presetBlackKnockout().applyTo(sphereTexture);
//		LeaveWhiteFilter.instance(P.p).setMix(0.99f);
//		LeaveWhiteFilter.instance(P.p).applyTo(sphereTexture);
		VignetteAltFilter.instance(P.p).setSpread(0.95f);
		VignetteAltFilter.instance(P.p).setDarkness(3.f);
		VignetteAltFilter.instance(P.p).applyTo(sphereTexture);
//		BlurBasicFilter.instance(P.p).applyTo(sphereTexture);
		BlurProcessingFilter.instance(P.p).setBlurSize(6);
		BlurProcessingFilter.instance(P.p).setSigma(6);
		BlurProcessingFilter.instance(P.p).applyTo(sphereTexture);
		BlurProcessingFilter.instance(P.p).applyTo(sphereTexture);
	}
	
	public void updateDraw() {
		// lazy init shape after we for sure have a texture
		if(shape == null) createNewSphere();

		_texture.clear();

		// test show audio texture
		//_texture.image(sphereTexture, 0, 0);

		float frames = 600;
		float percentComplete = ((float)(P.p.frameCount%frames)/frames);

		// icosahedron
		// set position to center
		_texture.pushMatrix();
		_texture.translate(width/2f, height/2f);
		
		// shadow
		Gradients.radial(_texture, height * 2.5f, height * 2.5f, P.p.color(0,150), P.p.color(1, 0), 50);
		Gradients.radial(_texture, height * 1.5f, height * 1.5f, P.p.color(0,100), P.p.color(1, 0), 50);
		
		_rotation.update();
		_texture.rotateY( -P.HALF_PI + _rotation.x() );
		_texture.rotateX( _rotation.y() );
		_texture.rotateZ( _rotation.z() );
//		objSolid.deformWithAudioByNormals();
//		_texture.shape(objSolid.shape());
//		PShapeUtil.drawTrianglesWithTexture(_texture, objSolid.shape(), sphereTexture, 3f); // img

		float scaleOsc = P.p.frameCount * 0.001f;
		
		// deform mesh
		MeshDeformAndTextureFilter.instance(P.p).setDisplacementMap(sphereTexture);
		MeshDeformAndTextureFilter.instance(P.p).setDisplaceAmp(0.65f + 0.3f * P.sin(scaleOsc));
		MeshDeformAndTextureFilter.instance(P.p).setSheetMode(false);
		MeshDeformAndTextureFilter.instance(P.p).applyTo(_texture);
//		// set texture using PShape method
//		shape.setTexture(textureFlipped);

//		_texture.scale(0.65f + 0.45f * P.sin(P.PI + scaleOsc));
		_texture.scale(0.45f + 0.2f * P.sin(P.PI + scaleOsc));
		_texture.shape(shapeIcos);
		_texture.resetShader();
//		_texture.shape(shape);
//		_texture.shape(shapeTessellated);
		_texture.popMatrix();

	}
}
