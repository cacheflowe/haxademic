package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.media.audio.analysis.AudioIn;

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
	
	public void drawPre() {
		audioTexture.update();
		
		// lazy create & normalize shape - we need to wait until the audio texture has updated once and been lazy created itself 
		if(gridShape == null) {
			gridShape = Shapes.createSheet(40, audioTexture.texture());
			PShapeUtil.centerShape(gridShape);
			PShapeUtil.scaleShapeToHeight(gridShape, height * 1f);
		}
		
		BlurProcessingFilter.instance().setBlurSize(6);
		BlurProcessingFilter.instance().setSigma(2f);
		BlurProcessingFilter.instance().applyTo(audioTexture.texture());
		
		ContrastFilter.instance().setContrast(2f);
		ContrastFilter.instance().applyTo(audioTexture.texture());
		
//		ColorizeFilter.instance().setTargetR(_colorEase.rNorm());
//		ColorizeFilter.instance().setTargetG(_colorEase.gNorm());
//		ColorizeFilter.instance().setTargetB(_colorEase.bNorm());
//		ColorizeFilter.instance().applyTo(audioTexture.texture());
	}

	public void draw() {
		// reset context
		pg.background(0);
		pg.noStroke();
		PG.setDrawCorner(pg);
		PG.setCenterScreen(pg);
		
		// update colors & pump scale on beat
		if(AudioIn.isBeat()) newRotation();
		pg.fill(_colorEase.colorInt());
		
		// update lerping values
		logoRotY.update(true);
		logoRotX.update(true);
		logoRotZ.update(true);
		
		// set rotation
		pg.pushMatrix();
		pg.rotateX(logoRotX.value());
		pg.rotateY(logoRotY.value());
		pg.rotateZ(logoRotZ.value());

		// deform mesh
		MeshDeformAndTextureFilter.instance().setDisplacementMap(audioTexture.texture());
		MeshDeformAndTextureFilter.instance().setDisplaceAmp(100f);
		MeshDeformAndTextureFilter.instance().setSheetMode(true);
		MeshDeformAndTextureFilter.instance().setOnContext(pg);
		// set texture using PShape method
		gridShape.setTexture(audioTexture.texture());

		// draw shape
		pg.shape(gridShape);
		
		// pop context
		pg.resetShader();
		pg.noLights();
		pg.popMatrix();
	}
	
}
