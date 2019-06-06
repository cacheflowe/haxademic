package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeFromTexture;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PConstants;
import processing.core.PGraphics;

public class TexturePixelatedAudio 
extends BaseTexture {
	
	protected float cols;
	protected float colW;
	protected float rows;
	protected float rowH;
	protected float cellCover = 3f;
	protected PGraphics noiseMap;
	protected PGraphics noiseMapZoomed;
	protected PGraphics noiseMapFine;
	protected PGraphics noiseComposite;
	protected EasingFloat noiseZoom = new EasingFloat(1, 0.05f);
	protected EasingFloat noiseRot = new EasingFloat(0, 0.05f);
	protected EasingFloat noiseOffsetX = new EasingFloat(0, 0.05f);
	protected EasingFloat noiseOffsetY = new EasingFloat(0, 0.05f);
	protected AudioCell[] cells;
	protected TextureShader textureShader;
	protected ImageGradient gradient;

	
	public TexturePixelatedAudio( int width, int height ) {
		super();
		buildGraphics( width, height );
		
		// grid size
		cols = 100;
		rows = 60;
		int bufferW = (int) cols;
		int bufferH = (int) rows;
		
		// create texture steps
		noiseMap = P.p.createGraphics( bufferW, bufferH, PConstants.P2D );
		noiseMapZoomed = P.p.createGraphics( bufferW, bufferH, PConstants.P2D );
		noiseMapFine = P.p.createGraphics( bufferW, bufferH, PConstants.P2D );
		noiseComposite = P.p.createGraphics( bufferW, bufferH, PConstants.P2D );
		OpenGLUtil.setTextureQualityLow(noiseMap);
		OpenGLUtil.setTextureQualityLow(noiseMapZoomed);
		OpenGLUtil.setTextureQualityLow(noiseMapFine);
		OpenGLUtil.setTextureQualityLow(noiseComposite);
		
		// simplex noise shader
		textureShader = new TextureShader(TextureShader.noise_simplex_2d_iq, 0.0005f);
		
		// debug textures
//		P.p.debugView.setTexture(noiseMap);
//		P.p.debugView.setTexture(noiseMapZoomed);
//		P.p.debugView.setTexture(noiseMapFine);
//		P.p.debugView.setTexture(noiseComposite);
		
		// create gradients
		gradient = new ImageGradient(ImageGradient.BLACK_HOLE());
//		gradient = new ImageGradient(ImageGradient.PASTELS());
//		gradient = new ImageGradient(P.p.loadImage(FileUtil.getFile("images/_sketch/sendgrid/palette-sendgrid.png")));
		
		// build grid
		createCells();
	}
	
	protected void createCells() {
		cells = new AudioCell[P.round(cols * rows)];
		int index = 0;
		for (int i = 0; i < cols/cellCover; i ++) {
			for (int j = 0; j < rows/cellCover; j ++) {
				float x = i * cellCover;
				float y = j * cellCover;
				cells[index] = new AudioCell(x, y);
				index++;
			}
		}
	}
	
	public void newRotation() {
		noiseRot.setTarget(noiseRot.target() + MathUtil.randRangeDecimal(-0.6f, 0.6f));
		noiseOffsetX.setTarget(noiseOffsetX.target() + MathUtil.randRangeDecimal(-0.6f, 0.6f));
		noiseOffsetY.setTarget(noiseOffsetY.target() + MathUtil.randRangeDecimal(-0.6f, 0.6f));
		noiseZoom.setTarget(MathUtil.randRangeDecimal(0.5f, 1.1f));
	}
	
	public void preDraw() {
		// update noise params
		if(P.p.audioData.isBeat()) newRotation();
		noiseRot.update(true);
		noiseOffsetX.update(true);
		noiseOffsetY.update(true);
		noiseZoom.update(true);
		
		// update noise map #1
		textureShader.updateTime();
		textureShader.shader().set("zoom", noiseZoom.value());
		textureShader.shader().set("rotation", noiseRot.value());
		textureShader.shader().set("offset", noiseOffsetX.value(), noiseOffsetY.value());
		noiseMap.filter(textureShader.shader());
//		P.p.debugView.setTexture(noiseMap);

		// update noise map #2
		textureShader.shader().set("zoom", 10f * noiseZoom.value());
		textureShader.shader().set("rotation", noiseRot.value());
		textureShader.shader().set("offset", noiseOffsetX.value(), noiseOffsetY.value());
		noiseMapZoomed.filter(textureShader.shader());
//		P.p.debugView.setTexture(noiseMapZoomed);

		// update noise map #3
		textureShader.shader().set("zoom", 200f);
		textureShader.shader().set("rotation", noiseRot.value() * 0.01f);
		textureShader.shader().set("offset", noiseOffsetX.value() * 0.01f, noiseOffsetY.value() * 0.01f);
		noiseMapFine.filter(textureShader.shader());
//		P.p.debugView.setTexture(noiseMapFine);
		
		// blend 2 maps together
		BlendTowardsTexture.instance(P.p).setSourceTexture(noiseMapZoomed);
		BlendTowardsTexture.instance(P.p).setBlendLerp(0.2f);
		BlendTowardsTexture.instance(P.p).applyTo(noiseMap);

		// blend another map together
		BlendTowardsTexture.instance(P.p).setSourceTexture(noiseMapFine);
		BlendTowardsTexture.instance(P.p).setBlendLerp(0.2f);
		BlendTowardsTexture.instance(P.p).applyTo(noiseMap);
		
		// Blur it
		BlurProcessingFilter.instance(P.p).setSigma(0.15f);
		BlurProcessingFilter.instance(P.p).setBlurSize(4);
		BlurProcessingFilter.instance(P.p).applyTo(noiseMap);
		
		// texture to current palette
		ColorizeFromTexture.instance(P.p).setTexture(gradient.texture());
		ColorizeFromTexture.instance(P.p).applyTo(noiseMap);
		
		// draw audio cells over scaled-up low-res texture
		noiseMap.beginDraw();
		noiseMap.noStroke();
		for (int i = 0; i < cells.length; i++) {
			if(cells[i] != null) {
				if(P.p.audioData.isBeat()) cells[i].beat();
				cells[i].update();
			}
		}
		noiseMap.endDraw();
		
		// blend composite
		BlendTowardsTexture.instance(P.p).setSourceTexture(noiseMap);
		BlendTowardsTexture.instance(P.p).setBlendLerp(0.5f);
		BlendTowardsTexture.instance(P.p).applyTo(noiseComposite);
	}

	public void updateDraw() {
		_texture.clear();
		_texture.noStroke();
		PG.setDrawCorner(_texture);
		// draw image
		ImageUtil.drawImageCropFill(noiseComposite, _texture, true);
	}
	
	public class AudioCell {
		
		public float x;
		public float y;
		public EasingFloat audioAddIndex = new EasingFloat(0, 0.1f);
		
		public AudioCell(float x, float y) {
			this.x = x;
			this.y = y;
		}
		
		public void beat() {
			audioAddIndex.setTarget(MathUtil.randRangeDecimal(0, 1));
		}
		
		public void update() {
			// update audio & color location
			audioAddIndex.update(true);
			
			// audio eq overlay, pulling from random EQ indexes video audioAddIndex
			noiseMap.fill(255, 180f * P.p.audioFreq(P.round(audioAddIndex.value() * 512)));
			noiseMap.rect(x, y, cellCover, cellCover);
		}
		
	}
}
