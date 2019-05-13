package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PShape;

public class TextureAudioBlocksDeform 
extends BaseTexture {
	
	protected PShape gridShape; 
	protected PShape[] blocks; 
	protected EasingFloat logoRotY = new EasingFloat(0, 0.1f);
	protected EasingFloat logoRotX = new EasingFloat(0, 0.1f);
	protected EasingFloat logoRotZ = new EasingFloat(0, 0.1f);
	
	// textures to apply
	protected BaseTexture audioTexture;
	
	public TextureAudioBlocksDeform( int width, int height ) {
		super();
		buildGraphics( width, height );

		// build textures
//		audioTexture = new TextureEQGrid(200, 200);
//		audioTexture = new TextureEQBandDistribute(200, 200);
		audioTexture = new TextureEQConcentricCircles(200, 200);
		audioTexture = new TexturePixelatedAudio(200, 200);
		
		_texture.noSmooth();
		
		// create shape
		buildGrid();
	}
	
	protected void buildGrid() {
		// build the parent group
		gridShape = P.p.createShape(P.GROUP);

		// build the blocks
		int cols = 20;
		int rows = 10;
		float blockSize = 20f;
		float blockSpacing = blockSize * 2f;
		float halfW = cols/2 * blockSpacing;
		float halfH = rows/2 * blockSpacing;
		blocks = new PShape[cols * rows];
		int buildIndex = 0;
		for (int x = 0; x < cols; x++) {
			for (int y = 0; y < rows; y++) {
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
			}
		}
		
		// normalize group shape
		PShapeUtil.centerShape(gridShape);
		PShapeUtil.scaleShapeToHeight(gridShape, _texture.height * 5f);
	}

	public void newLineMode() {
		audioTexture.newLineMode();
	}
	
	public void newRotation() {
		super.newRotation();
		// update camera
		float rotAmp = 0.3f;
		logoRotY.setTarget(MathUtil.randRangeDecimal(-rotAmp, rotAmp));
		logoRotX.setTarget(MathUtil.randRangeDecimal(-rotAmp, rotAmp));
		logoRotZ.setTarget(MathUtil.randRangeDecimal(-rotAmp, rotAmp));
		// pass along timing event
		audioTexture.newRotation();
	}
	
	public void preDraw() {
		audioTexture.update();
		
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
//		_texture.background(0);
		feedback(2f, 0f);
		_texture.noStroke();
		DrawUtil.setDrawCorner(_texture);
		DrawUtil.setCenterScreen(_texture);
//		DrawUtil.setDrawFlat2d(_texture, true);
		
		// update colors & pump scale on beat
		if(P.p.audioData.isBeat()) newRotation();
		_texture.fill(_colorEase.colorInt());
		
		// update lerping values
		logoRotY.update(true);
		logoRotX.update(true);
		logoRotZ.update(true);
		
		// set context
		_texture.pushMatrix();
		_texture.rotateX(logoRotX.value());
		_texture.rotateY(logoRotY.value());
		_texture.rotateZ(logoRotZ.value());

		// deform mesh
		MeshDeformAndTextureFilter.instance(P.p).setDisplacementMap(audioTexture.texture());
		MeshDeformAndTextureFilter.instance(P.p).setDisplaceAmp(200.5f);
		MeshDeformAndTextureFilter.instance(P.p).setSheetMode(true);
		MeshDeformAndTextureFilter.instance(P.p).setDisplaceAmp(1.5f);
		MeshDeformAndTextureFilter.instance(P.p).setSheetMode(false);
		MeshDeformAndTextureFilter.instance(P.p).applyTo(_texture);
		// set texture using PShape method
//		gridShape.setTexture(DemoAssets.textureNebula());

		// draw shape
		_texture.noLights();
		_texture.shape(gridShape);
		
		for (int i = 0; i < blocks.length; i++) {
			_texture.shape(blocks[i]);
		}
		
		// pop context
		_texture.resetShader();
		_texture.popMatrix();
	}
	
}
