package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.PointsDeformAndTextureFilter;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.render.FrameLoop;

import processing.core.PShape;

public class TextureEQPointsDeformAndTexture 
extends BaseTexture {

	protected BaseTexture audioTexture;
	protected PShape svg;
	protected float svgExtent;

	public TextureEQPointsDeformAndTexture(int width, int height) {
		super(width, height);

		// build audio texture
		audioTexture = new TextureEQConcentricCircles(width / 2, height / 2);

		// build geometry
		svg = Shapes.createSheetPoints(height / 5, width, height);
		svg.disableStyle();
		PShapeUtil.centerShape(svg);
		PShapeUtil.scaleShapeToHeight(svg, height * 0.9f);
		PShapeUtil.addTextureUVSpherical(svg, audioTexture.texture());		// necessary for vertex shader `varying vertTexCoord`
		svgExtent = PShapeUtil.getMaxExtent(svg);
	}

	public void drawPre() {
		// update audio texture
		audioTexture.update();

		// blur texture for smooothness
		BlurProcessingFilter.instance().setBlurSize(5);
		BlurProcessingFilter.instance().applyTo(audioTexture.texture());
	}
	
	public void updateDraw() {
		// clear background
		_texture.background(0);

		// set context
		PG.setCenterScreen(_texture);
		// PG.basicCameraFromMouse(_texture);
		_texture.rotateX(P.cos(FrameLoop.count(0.01f)) * 0.3f);
		_texture.rotateY(P.sin(FrameLoop.count(0.01f)) * 0.3f);
		_texture.rotateZ(FrameLoop.count(0.003f));

		// draw geometry with shader
		PointsDeformAndTextureFilter.instance().setColorMap(audioTexture.texture());
		PointsDeformAndTextureFilter.instance().setDisplacementMap(audioTexture.texture());
		PointsDeformAndTextureFilter.instance().setMaxPointSize(height * 0.017f);
		PointsDeformAndTextureFilter.instance().setDisplaceAmp(height * 0.4f);			// multiplied by obj extent
		PointsDeformAndTextureFilter.instance().setModelMaxExtent(svgExtent * 2.01f);		// texture mapping UV
		PointsDeformAndTextureFilter.instance().setSheetMode(true);
		PointsDeformAndTextureFilter.instance().setColorPointSizeMode(true);
		
		// draw points mesh 
		_texture.stroke(255);	// make sure to reset stroke
		PointsDeformAndTextureFilter.instance().setOnContext(_texture);
		_texture.shape(svg);
		_texture.resetShader();
	}
}
