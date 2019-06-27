package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PShape;

public class TextureAudioSheetDeform 
extends BaseTexture {
	
	protected PShape gridShape; 
	protected PShape[] blocks; 
	protected EasingFloat logoRotY = new EasingFloat(0, 0.1f);
	protected EasingFloat logoRotX = new EasingFloat(0, 0.1f);
	protected EasingFloat logoRotZ = new EasingFloat(0, 0.1f);
	
	// textures to apply
	protected BaseTexture audioTexture;
	
	public TextureAudioSheetDeform( int width, int height ) {
		super(width, height);
		

		// build textures
//		audioTexture = new TextureEQGrid(200, 200);
//		audioTexture = new TextureEQBandDistribute(200, 200);
		audioTexture = new TextureEQConcentricCircles(400, 400);
	}

	public void newLineMode() {
		audioTexture.newLineMode();
	}
	
	public void newRotation() {
		super.newRotation();
		float rotAmp = 0.7f;
		logoRotY.setTarget(MathUtil.randRangeDecimal(-rotAmp, rotAmp));
		logoRotX.setTarget(MathUtil.randRangeDecimal(-rotAmp, rotAmp));
		logoRotZ.setTarget(MathUtil.randRangeDecimal(-rotAmp, rotAmp));
	}
	
	public void preDraw() {
		audioTexture.update();
		
		// lazy create & normalize shape - we need to wait until the audio texture has updated once and been lazy created itself 
		if(gridShape == null) {
			gridShape = Shapes.createSheet(40, audioTexture.texture());
			PShapeUtil.centerShape(gridShape);
			PShapeUtil.scaleShapeToHeight(gridShape, height * 1f);
		}
		
		BlurProcessingFilter.instance(P.p).setBlurSize(6);
		BlurProcessingFilter.instance(P.p).setSigma(2f);
		BlurProcessingFilter.instance(P.p).applyTo(audioTexture.texture());
		
		ContrastFilter.instance(P.p).setContrast(2f);
		ContrastFilter.instance(P.p).applyTo(audioTexture.texture());
		
//		ColorizeFilter.instance(P.p).setTargetR(_colorEase.rNorm());
//		ColorizeFilter.instance(P.p).setTargetG(_colorEase.gNorm());
//		ColorizeFilter.instance(P.p).setTargetB(_colorEase.bNorm());
//		ColorizeFilter.instance(P.p).applyTo(audioTexture.texture());
	}

	public void updateDraw() {
		// reset context
		_texture.background(0);
		_texture.noStroke();
		PG.setDrawCorner(_texture);
		PG.setCenterScreen(_texture);
		
		// update colors & pump scale on beat
		if(P.p.audioData.isBeat()) newRotation();
		_texture.fill(_colorEase.colorInt());
		
		// update lerping values
		logoRotY.update(true);
		logoRotX.update(true);
		logoRotZ.update(true);
		
		// set rotation
		_texture.pushMatrix();
		_texture.rotateX(logoRotX.value());
		_texture.rotateY(logoRotY.value());
		_texture.rotateZ(logoRotZ.value());

		// deform mesh
		MeshDeformAndTextureFilter.instance(P.p).setDisplacementMap(audioTexture.texture());
		MeshDeformAndTextureFilter.instance(P.p).setDisplaceAmp(100f);
		MeshDeformAndTextureFilter.instance(P.p).setSheetMode(true);
		MeshDeformAndTextureFilter.instance(P.p).applyTo(_texture);
		// set texture using PShape method
		gridShape.setTexture(audioTexture.texture());

		// draw shape
		_texture.shape(gridShape);
		
		// pop context
		_texture.resetShader();
		_texture.noLights();
		_texture.popMatrix();
	}
	
}
