package com.haxademic.app.haxmapper.textures;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.shaders.BlendTowardsTexture;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shaders.textures.TextureShader;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PShape;

public class TexturePixelatedAudio 
extends BaseTexture {
	
	protected float cols;
	protected float colW;
	protected float rows;
	protected float rowH;
	protected int mapDivide = 30;
	protected PGraphics noiseMap;
	protected PGraphics noiseMapZoomed;
	protected PGraphics noiseMapFine;
	protected EasingFloat noiseZoom = new EasingFloat(1, 0.05f);
	protected EasingFloat noiseRot = new EasingFloat(0, 0.05f);
	protected EasingFloat noiseOffsetX = new EasingFloat(0, 0.05f);
	protected EasingFloat noiseOffsetY = new EasingFloat(0, 0.05f);
	protected Cell[] cells;
	protected TextureShader textureShader;
	protected ImageGradient gradient;
	
	protected boolean shows3d = false;
	protected PShape logoSvg; 
	protected PShape logo3d;
	protected EasingFloat colorLogoProgress = new EasingFloat(0, 0.1f);
	protected EasingFloat logoScale = new EasingFloat(1, 0.1f);
	protected EasingFloat logoRotY = new EasingFloat(0, 0.1f);
	protected EasingFloat logoRotX = new EasingFloat(0, 0.1f);
	protected EasingFloat logoRotZ = new EasingFloat(0, 0.1f);

	public TexturePixelatedAudio( int width, int height ) {
		super();
		buildGraphics( width, height );
		
		// grid size
		cols = 16 * 5f;
		rows = 9 * 5f;
		colW = _texture.width / cols;
		rowH = _texture.height / rows;
		
		noiseMap = P.p.createGraphics( width/mapDivide, height/mapDivide, PConstants.P2D );
		noiseMapZoomed = P.p.createGraphics( width/mapDivide, height/mapDivide, PConstants.P2D );
		noiseMapFine = P.p.createGraphics( width/mapDivide, height/mapDivide, PConstants.P2D );
		textureShader = new TextureShader(TextureShader.noise_simplex_2d_iq, 0.0005f);
		
		gradient = new ImageGradient(ImageGradient.BLACK_HOLE());
		gradient = new ImageGradient(ImageGradient.PASTELS());
//		gradient = new ImageGradient(P.p.loadImage(FileUtil.getFile("images/textures/palette-sendgrid.png")));
		
		// build grid
		createCells();
		
		// create logo
		if(shows3d) buildLogo();
	}
	
	protected void buildLogo() {
		logoSvg = P.p.loadShape( FileUtil.getFile("svg/sendgrid.svg")).getTessellation();
		PShapeUtil.repairMissingSVGVertex(logoSvg);
		
		PShapeUtil.centerShape(logoSvg);
		PShapeUtil.scaleShapeToHeight(logoSvg, _texture.height * 0.15f);
		
		
		// create extrusion
		// add UV coordinates to OBJ based on model extents
		logo3d = PShapeUtil.createExtrudedShape( logoSvg, 70 );
		PShapeUtil.addTextureUVToShape(logo3d, _texture);
//		PShapeUtil.addTextureUVToShape(logo3d, null);
	}

	protected void createCells() {
		cells = new Cell[P.round(cols * rows)];
		
		int index = 0;
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				float x = i * colW;
				float y = j * rowH;
				cells[index] = new Cell(x, y);
				index++;
			}
		}
	}
	
	public void newLineMode() {

	}
	
	public void preDraw() {
		// update noise params
		if(P.p.audioData.isBeat()) {
			noiseRot.setTarget(noiseRot.target() + MathUtil.randRangeDecimal(-0.6f, 0.6f));
			noiseOffsetX.setTarget(noiseOffsetX.target() + MathUtil.randRangeDecimal(-0.6f, 0.6f));
			noiseOffsetY.setTarget(noiseOffsetY.target() + MathUtil.randRangeDecimal(-0.6f, 0.6f));
			noiseZoom.setTarget(MathUtil.randRangeDecimal(0.5f, 1.1f));
		}
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
		textureShader.shader().set("zoom", 100f);
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
		
		// prep for pixel-reading
		noiseMap.loadPixels();
	}

	public void updateDraw() {
		_texture.clear();
		_texture.noStroke();
		DrawUtil.setDrawCorner(_texture);
		if(P.p.audioData.isBeat()) for (int i = 0; i < cells.length; i++) cells[i].beat();
		for (int i = 0; i < cells.length; i++) cells[i].update();
		
		// draw logo
		if(shows3d) {
			DrawUtil.setCenterScreen(_texture);
			_texture.translate(0, 0, 100);
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
	//		DrawUtil.setBetterLights(_texture);
			_texture.lights();
			_texture.fill(gradient.getColorAtProgress(colorLogoProgress.value()));
			_texture.rotateX(logoRotX.value());
			_texture.rotateY(logoRotY.value());
			_texture.rotateZ(logoRotZ.value());
			PShapeUtil.drawTriangles(_texture, logo3d, null, logoScale.value());
			_texture.noLights();
			_texture.popMatrix();
		}
	}
	
	public class Cell {
		
		public float x;
		public float y;
		public int mapX;
		public int mapY;
		public EasingFloat colorMapProgress = new EasingFloat(0, 0.01f);
		public EasingFloat audioAddIndex = new EasingFloat(0, 0.01f);
		
		public Cell(float x, float y) {
			this.x = x;
			this.y = y;
			mapX = P.floor(x/mapDivide);
			mapY = P.floor(y/mapDivide);
		}
		
		public void beat() {
			colorMapProgress.setTarget(MathUtil.randRangeDecimal(0, 1));
			audioAddIndex.setTarget(MathUtil.randRangeDecimal(0, 1));
		}
		
		public void update() {
			// update audio & color location
			colorMapProgress.update(true);
			audioAddIndex.update(true);
			
			int color = ImageUtil.getPixelColor(noiseMap, P.floor(x/mapDivide), P.floor(y/mapDivide));
			float noiseMapBrightness = ColorUtil.redFromColorInt(color) / 255f;//(ColorUtil.redFromColorInt(color) + ColorUtil.redFromColorInt(colorZoomed)) / 2f / 255f;
			if(noiseMapBrightness > 0.5f) {
				_texture.fill(255); 
				_texture.rect(x, y, colW, rowH);
			} else {
				// slide along the color map
				noiseMapBrightness = P.map(noiseMapBrightness, 0, 0.5f, 0, 1);
				_texture.fill(gradient.getColorAtProgress(colorMapProgress.value()));
				_texture.rect(x, y, colW, rowH);
				// _texture.ellipse(x, y, colW, rowH);
				// audio eq overlay
				_texture.fill(255, 50 * P.p.audioFreq(P.round(audioAddIndex.value() * 512)));
				_texture.rect(x, y, colW, rowH);
			}
		}
		
	}
}
