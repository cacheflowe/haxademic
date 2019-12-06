package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.InvertFilter;
import com.haxademic.core.draw.filters.pshader.ReflectFilter;
import com.haxademic.core.draw.filters.pshader.VignetteAltFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.pshader.LinesDeformAndTextureFilter;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.media.audio.analysis.AudioIn;

import processing.core.PGraphics;
import processing.core.PShape;

public class TextureEQLinesTerrain 
extends BaseTexture {

	protected PGraphics eqHistory; 
	protected PGraphics eqHistoryCopy; 

	protected PShape shape; 
	protected float shapeExtent;

	public TextureEQLinesTerrain( int width, int height ) {
		super(width, height);
		
		// build scrolling audio map history
		eqHistory = PG.newPG(256, 256, false, false);
		eqHistory.noSmooth();
		OpenGLUtil.setTextureQualityLow(eqHistory);
		eqHistoryCopy = PG.newPG(256, 256);
		eqHistoryCopy.noSmooth();
		OpenGLUtil.setTextureQualityLow(eqHistoryCopy);

		// build sheet mesh
		shape = P.p.createShape(P.GROUP);
		int rows = eqHistory.height;
		int cols = eqHistory.width;
		for (int y = 0; y < rows; y++) {
			PShape line = P.p.createShape();
			line.beginShape();
			line.stroke(255);
			line.strokeWeight(1);
			line.noFill();
			for (int x = 0; x < cols; x++) {
				line.vertex(x * 20f, y * 20f, 0);
			}
			line.endShape();
			shape.addChild(line);
		}

		// normalize & texture mesh
		PShapeUtil.centerShape(shape);
		PShapeUtil.scaleShapeToHeight(shape, height * 1f);
		PShapeUtil.addTextureUVToShape(shape, eqHistory);
		shapeExtent = PShapeUtil.getMaxExtent(shape);
		shape.disableStyle();
		shape.setTexture(eqHistory);
	}
	
	public void newLineMode() {

	}

	public void preDraw() {
		// build audio map w/scrolling history
		eqHistory.beginDraw();
		eqHistory.noStroke();
		// scroll up
		eqHistory.copy(0, 0, eqHistory.width, eqHistory.height, 0, 1, eqHistory.width, eqHistory.height);
		// draw bottom line of current eq
		for (int i = 0; i < eqHistory.width; i++) {
			eqHistory.fill(255f * AudioIn.audioFreq(i));
			eqHistory.rect(i, 0, 1, 1);
		}
		eqHistory.endDraw();

		// make gradient-faded copy
		eqHistoryCopy.beginDraw();
		ImageUtil.copyImage(eqHistory, eqHistoryCopy);
		eqHistoryCopy.beginShape();
		eqHistoryCopy.fill(0, 0);
		eqHistoryCopy.vertex(0, 0);
		eqHistoryCopy.vertex(eqHistoryCopy.width, 0);
		eqHistoryCopy.fill(0);
		eqHistoryCopy.vertex(eqHistoryCopy.width, eqHistoryCopy.height);
		eqHistoryCopy.vertex(0, eqHistoryCopy.height);
		eqHistoryCopy.endShape(P.CLOSE);
		eqHistoryCopy.endDraw();
		
		ReflectFilter.instance(P.p).applyTo(eqHistoryCopy);
	}
	
	public void updateDraw() {
		// background & debug
		_texture.background(255);
//		ImageUtil.drawImageCropFill(eqHistoryCopy, _texture, false);
		
		// rotate
		PG.setCenterScreen(_texture);
//		PG.basicCameraFromMouse(_texture);
		_texture.rotateX(1.2f);

		// draw shader-displaced mesh
		LinesDeformAndTextureFilter.instance(P.p).setDisplacementMap(eqHistoryCopy);
		LinesDeformAndTextureFilter.instance(P.p).setColorMap(eqHistoryCopy);
		LinesDeformAndTextureFilter.instance(P.p).setWeight(10f);
		LinesDeformAndTextureFilter.instance(P.p).setModelMaxExtent(shapeExtent * 2f);
		LinesDeformAndTextureFilter.instance(P.p).setColorThicknessMode(true);
		LinesDeformAndTextureFilter.instance(P.p).setSheetMode(true);
		LinesDeformAndTextureFilter.instance(P.p).setDisplaceAmp(200);
		LinesDeformAndTextureFilter.instance(P.p).applyTo(_texture);

		_texture.stroke(255);
		_texture.shape(shape);
		_texture.resetShader();

		// post effects
		VignetteAltFilter.instance(P.p).setDarkness(-4f);
		VignetteAltFilter.instance(P.p).setSpread(1f);
		VignetteAltFilter.instance(P.p).applyTo(_texture);
		BlurProcessingFilter.instance(P.p).setSigma(6);
		BlurProcessingFilter.instance(P.p).setBlurSize(1);
		BlurProcessingFilter.instance(P.p).applyTo(_texture);
		InvertFilter.instance(P.p).applyTo(_texture);
	}
}
