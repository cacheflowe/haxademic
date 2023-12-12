package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.media.audio.analysis.AudioIn;

import processing.core.PShape;

public class TextureBlocksSheet 
extends BaseTexture {
	
	protected ImageGradient gradient;
	protected PShape gridShape; 
	protected PShape[] blocks; 
	protected EasingFloat logoScale = new EasingFloat(1, 0.1f);
	protected EasingFloat logoRotY = new EasingFloat(0, 0.1f);
	protected EasingFloat logoRotX = new EasingFloat(0, 0.1f);
	protected EasingFloat logoRotZ = new EasingFloat(0, 0.1f);
	
	// textures to apply
	protected TextureShader noiseTexture;
	protected BaseTexture audioTexture;
	
	public TextureBlocksSheet( int width, int height ) {
		super(width, height);
		

		// build textures
		noiseTexture = new TextureShader(TextureShader.noise_simplex_2d_iq, 0.0005f);
//		audioTexture = new TextureEQGrid(200, 200);
//		audioTexture = new TextureEQBandDistribute(200, 200);
		audioTexture = new TextureEQConcentricCircles(200, 100);
		
		// create shape
		gradient = new ImageGradient(ImageGradient.PASTELS());
	}
	
	protected void buildGrid() {
		// build the parent group
		gridShape = P.p.createShape(P.GROUP);

		// build the blocks
		int cols = 20;
		int rows = 10;
		float blockSize = 15f;
		float blockSpacing = blockSize * 2f;
		float halfW = cols/2 * blockSpacing;
		float halfH = rows/2 * blockSpacing;
		blocks = new PShape[cols * rows];
		int buildIndex = 0;
		for (int x = 0; x < cols; x++) {
			for (int y = 0; y < rows; y++) {
//				PShape box = P.p.createShape(P.BOX, 10);
//				box.setFill(gradient.getColorAtProgress(MathUtil.randRangeDecimal(0, 1)));
//				box.translate(x, y, -MathUtil.randRangeDecimal(0, 1));
//				gridShape.addChild(box);
				
				float u = P.map(x, 0, cols - 1, 0.005f, 0.995f);
				float v = P.map(y, 0, rows - 1, 0.005f, 0.995f);
				PShape boxx = Shapes.createBoxSingleUV(blockSize, u, v);
//				PShape boxx = Shapes.createRectSingleUV(blockSize, u, v);
				boxx.setStroke(false);
//				boxx.setFill(gradient.getColorAtProgress(MathUtil.randRangeDecimal(0, 1)));
				boxx.setTexture(audioTexture.texture());
				boxx.translate(x * blockSpacing - halfW, y * blockSpacing - halfH, 0);
				blocks[buildIndex] = boxx;
				buildIndex++;
				
//				gridShape.addChild(boxx);
			}
		}
		
//		gridShape = Shapes.createSheet(40, audioTexture.texture());
//		gridShape = gridShape.getTessellation();
//		gridShape.setTexture(audioTexture.texture());
		
		// normalize group shape
		PShapeUtil.centerShape(gridShape);
		PShapeUtil.scaleShapeToHeight(gridShape, height * 5f);
		
//		for (int i = 0; i < blocks.length; i++) {
//			PShapeUtil.scaleShapeToHeight(blocks[i], height * 0.2f);
//		}
		
		// debug
//		DebugView.setValue("grid cubes", cols * rows);
//		DebugView.setValue("grid vertices", PShapeUtil.vertexCount(gridShape));
//		DebugView.setValue("grid max extent", PShapeUtil.getMaxExtent(gridShape));
	}

	public void newLineMode() {
		audioTexture.newLineMode();
	}
	
	public void newRotation() {
		super.newRotation();
		logoScale.setCurrent(1.2f);
		logoScale.setTarget(1f);
		float rotAmp = 0.3f;
		logoRotY.setTarget(MathUtil.randRangeDecimal(-rotAmp, rotAmp));
		logoRotX.setTarget(MathUtil.randRangeDecimal(-rotAmp, rotAmp));
		logoRotZ.setTarget(MathUtil.randRangeDecimal(-rotAmp, rotAmp));
	}
	
	public void setColor(int color) {
		int newColor = gradient.getColorAtProgress(MathUtil.randRangeDecimal(0, 1));
		super.setColor(newColor);
		audioTexture.setColor(newColor);
	}
	
	public void drawPre() {
		audioTexture.update();
		if(gridShape == null) buildGrid();
		
//		noiseTexture.shader().set("offset", 0f, P.p.frameCount * 0.005f);
//		audioTexture.texture().filter(noiseTexture.shader());
		
		BlurProcessingFilter.instance().setBlurSize(6);
		BlurProcessingFilter.instance().setSigma(2f);
		BlurProcessingFilter.instance().applyTo(audioTexture.texture());
		
		ContrastFilter.instance().setContrast(2f);
		ContrastFilter.instance().applyTo(audioTexture.texture());
		
//		ColorizeFilter.instance().setTargetR(_colorEase.rNorm());
//		ColorizeFilter.instance().setTargetG(_colorEase.gNorm());
//		ColorizeFilter.instance().setTargetB(_colorEase.bNorm());
//		ColorizeFilter.instance().applyTo(audioTexture.texture());
//		DebugView.setTexture(audioTexture.texture());
	}

	public void draw() {
//		if(P.p.frameCount == 10) PG.setDrawFlat2d(_texture, true);

		// reset context
		pg.background(0);
		pg.noStroke();
//		PG.setBetterLights(_texture);
//		_texture.stroke(255);
		
		// update colors & pump scale on beat
		if(AudioIn.isBeat()) newRotation();
		pg.fill(_colorEase.colorInt());
		
		// update lerping values
		logoScale.update(true);
		logoRotY.update(true);
		logoRotX.update(true);
		logoRotZ.update(true);
		
		// set context
		pg.pushMatrix();
		PG.setDrawCorner(pg);
		PG.setCenterScreen(pg);
		pg.rotateX(logoRotX.value());
		pg.rotateY(logoRotY.value());
		pg.rotateZ(logoRotZ.value());

		// deform mesh
		MeshDeformAndTextureFilter.instance().setDisplacementMap(audioTexture.texture());
		MeshDeformAndTextureFilter.instance().setDisplaceAmp(1.5f);
		MeshDeformAndTextureFilter.instance().setSheetMode(false);
		MeshDeformAndTextureFilter.instance().setOnContext(pg);
		// set texture using PShape method
//		gridShape.setTexture(DemoAssets.textureNebula());

		// draw shape
		pg.noLights();
		pg.shape(gridShape);
		
		for (int i = 0; i < blocks.length; i++) {
			pg.shape(blocks[i]);
		}
		
		// pop context
		pg.resetShader();
		pg.popMatrix();

		// post-processing
//		RotateFilter.instance().setZoom(Mouse.yNorm * 3f + 0.5f);
//		RotateFilter.instance().setRotation(Mouse.xNorm * 2f * P.TWO_PI);
//		RotateFilter.instance().applyTo(_texture);
		
		// black to transparent
//		applyChromaBlackKnockout(_texture);
	}
	
}
