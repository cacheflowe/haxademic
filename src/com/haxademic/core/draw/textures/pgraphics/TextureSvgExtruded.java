package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.DisplacementMapFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.media.DemoAssets;

import processing.core.PShape;

public class TextureSvgExtruded 
extends BaseTexture {
	
	protected ImageGradient gradient;
	protected PShape logoSvg; 
	protected PShape logo3d;
	protected EasingFloat colorLogoProgress = new EasingFloat(0, 0.1f);
	protected EasingFloat logoScale = new EasingFloat(1, 0.1f);
	protected EasingFloat logoRotY = new EasingFloat(0, 0.1f);
	protected EasingFloat logoRotX = new EasingFloat(0, 0.1f);
	protected EasingFloat logoRotZ = new EasingFloat(0, 0.1f);
	
	// textures to apply
	protected TextureShader noiseTexture;
	protected BaseTexture audioTexture;
	
	enum DrawMode {
		Color,
		AudioTriangles,
		Displacement2d,
		Displacement3d,
	}

	public TextureSvgExtruded( int width, int height ) {
		super(width, height);
		
		
		noiseTexture = new TextureShader(TextureShader.noise_simplex_2d_iq, 0.0005f);
		audioTexture = new TextureEQGrid(200, 200);

		gradient = new ImageGradient(ImageGradient.BLACK_HOLE());
//		gradient = new ImageGradient(ImageGradient.PASTELS());
//		gradient = new ImageGradient(P.p.loadImage(FileUtil.getFile("images/textures/palette-sendgrid.png")));
		
		// create logo
		buildLogo();
	}
	
	protected void buildLogo() {
		logoSvg = DemoAssets.shapeX().getTessellation();
		PShapeUtil.repairMissingSVGVertex(logoSvg);
		
		PShapeUtil.centerShape(logoSvg);
		PShapeUtil.scaleShapeToHeight(logoSvg, height * 0.15f);
		
		// create extrusion
		// add UV coordinates to OBJ based on model extents
		logo3d = PShapeUtil.createExtrudedShape( logoSvg, 70 );
		PShapeUtil.addTextureUVToShape(logo3d, audioTexture.texture());
	}

	public void newLineMode() {
//		drawMode = ;
	}
	
	public void preDraw() {
		// update audio texture
		audioTexture.update();

		// if replacing audio with noise, just use the audio buffer to draw noise into
		audioTexture.texture().filter(noiseTexture.shader());

//		P.p.debugView.setTexture(audioTexture.texture());
	}

	public void updateDraw() {
		_texture.clear();
		_texture.noStroke();
		PG.setDrawCorner(_texture);
		
		// draw logo
		PG.setCenterScreen(_texture);
		if(P.p.audioData.isBeat()) {
			colorLogoProgress.setTarget(MathUtil.randRangeDecimal(0, 1));
			logoScale.setCurrent(1.2f);
			logoScale.setTarget(1f);
			logoRotY.setTarget(MathUtil.randRangeDecimal(-0.3f, 0.3f));
			logoRotX.setTarget(MathUtil.randRangeDecimal(-0.3f, 0.3f));
			logoRotZ.setTarget(MathUtil.randRangeDecimal(-0.3f, 0.3f));
		}
		colorLogoProgress.update(true);
		logoScale.update(true);
		logoRotY.update(true);
		logoRotX.update(true);
		logoRotZ.update(true);
		
		_texture.pushMatrix();
		PG.setBasicLights(_texture);
//		PG.setBetterLights(_texture);
//		_texture.lights();
		_texture.fill(gradient.getColorAtProgress(colorLogoProgress.value()));
		_texture.rotateX(logoRotX.value());
		_texture.rotateY(logoRotY.value());
		_texture.rotateZ(logoRotZ.value());
//		_texture.shape(logo3d);
//		PShapeUtil.drawTriangles(_texture, logo3d, audioTexture.texture(), logoScale.value());
		PShapeUtil.drawTrianglesAudio(_texture, logo3d, logoScale.value(), _colorEase.colorInt());
		_texture.noLights();
		_texture.popMatrix();
		// post-process
		DisplacementMapFilter.instance(P.p).setMap(audioTexture.texture());
		DisplacementMapFilter.instance(P.p).setAmp(2f);
		DisplacementMapFilter.instance(P.p).setMode(3);
		DisplacementMapFilter.instance(P.p).applyTo(_texture);
	}
	
}
