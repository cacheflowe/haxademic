package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;

import processing.core.PGraphics;
import processing.core.PShape;

public class Demo_MeshDeformAndTextureFilter_SimpleSheetGravityHole 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float cols = 30;
	protected float rows = 30;

	protected PShape shape;
	protected PGraphics texture;
	protected PGraphics displaceTexture;

	protected void setupFirstFrame() {
		int sheetW = P.round(cols * 20);
		int sheetH = P.round(cols * 20);
		
		// displace texture
		displaceTexture = p.createGraphics(sheetW, sheetH, PRenderers.P3D);
		
		// create wireframe texture
		texture = p.createGraphics(sheetW, sheetH, PRenderers.P3D);
		
		// build sheet mesh
		shape = Shapes.createSheet((int) cols, (int) rows, texture);
		
		// debug view
		p.debugView.setValue("shape.getVertexCount();", shape.getVertexCount());
		p.debugView.setTexture("texture", texture);
		p.debugView.setTexture("displaceTexture", displaceTexture);
	}

	public void drawApp() {
		// draw grid
		PG.drawGrid(texture, p.color(0), p.color(255), cols, rows, 4);
		
		// update displace texture
		displaceTexture.beginDraw();
		displaceTexture.background(0);
		displaceTexture.noStroke();
		displaceTexture.blendMode(PBlendModes.ADD);
		PG.setDrawCenter(displaceTexture);
		PG.setCenterScreen(displaceTexture);
//		float scaleImg = MathUtil.scaleToTarget(DemoAssets.particle().height, displaceTexture.height * 1.4f);
		float iter = 6f;
		for (float i = 0; i < iter; i++) {
			// radial gradient
			Gradients.radial(displaceTexture, displaceTexture.width * 1/iter * i, displaceTexture.height * 1/iter * i, p.color(50), p.color(0), 100);
			BlurVFilter.instance(p).setBlurByPercent(1, displaceTexture.width);
			BlurVFilter.instance(p).applyTo(displaceTexture);
			BlurHFilter.instance(p).setBlurByPercent(1, displaceTexture.width);
			BlurHFilter.instance(p).applyTo(displaceTexture);
		}
		iter = 60f;
		for (float i = 0; i < iter; i++) {
			// ellipse
			displaceTexture.fill(1);
			displaceTexture.ellipse(0, 0, displaceTexture.width * 1/iter * i, displaceTexture.height * 1/iter * i);
		}			

		displaceTexture.blendMode(PBlendModes.BLEND);
		displaceTexture.endDraw();
		
		// context & camera
		background(0);
		PG.setCenterScreen(p.g);
		PG.basicCameraFromMouse(p.g);

		// deform mesh
		MeshDeformAndTextureFilter.instance(p).setDisplacementMap(displaceTexture);
		MeshDeformAndTextureFilter.instance(p).setDisplaceAmp(300f + 300f * P.sin(p.frameCount * 0.05f));
		MeshDeformAndTextureFilter.instance(p).setSheetMode(true);
		MeshDeformAndTextureFilter.instance(p).applyTo(p);

		// draw mesh
		p.shape(shape);
		p.resetShader();
	}
		
}