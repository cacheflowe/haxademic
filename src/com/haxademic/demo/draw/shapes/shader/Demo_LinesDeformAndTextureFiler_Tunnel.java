package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.pshader.LinesDeformAndTextureFilter;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.file.DemoAssets;

import processing.core.PGraphics;
import processing.core.PShape;

public class Demo_LinesDeformAndTextureFiler_Tunnel 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PGraphics noiseBuffer;
	protected TextureShader noiseTexture;
	protected float shapeExtent = 100;

	protected void overridePropsFile() {
		int FRAMES = 358;
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE, false);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
	}
	
	protected void setupFirstFrame() {
		// load texture
		noiseBuffer = p.createGraphics(p.width, p.height, PRenderers.P2D);
		noiseTexture = new TextureShader(TextureShader.noise_simplex_2d_iq, 0.0005f);
		p.debugView.setTexture(noiseBuffer);
		
		// build sheet mesh
		shape = p.createShape(P.GROUP);
		int rows = 200;
		int circleSegments = 200;
		float radius = 200;
		float segmentRads = P.TWO_PI / (float) circleSegments;
		for (int y = 0; y < rows; y++) {
			PShape line = P.p.createShape();
			line.beginShape();
			line.stroke(255);
			line.strokeWeight(1);
			line.noFill();
			for (int i = 0; i <= circleSegments; i++) {
				line.vertex(radius * P.sin(segmentRads * i), y * 10f, radius * P.cos(segmentRads * i));
			}
			line.endShape();
			shape.addChild(line);
		}
		PShapeUtil.centerShape(shape);
		PShapeUtil.scaleShapeToHeight(shape, p.height * 2f);
		PShapeUtil.addTextureUVSpherical(shape, noiseBuffer);
		shapeExtent = PShapeUtil.getMaxExtent(shape);
		shape.disableStyle();

		shape.setTexture(noiseBuffer);
		p.debugView.setValue("shape.getVertexCount();", PShapeUtil.vertexCount(shape));
	}

	public void drawApp() {
		background(0);
		
		// update displacement texture
		noiseTexture.shader().set("zoom", 2.5f + 1.5f * P.sin(p.loop.progressRads()));
		noiseTexture.shader().set("rotation", p.loop.progressRads());
		noiseBuffer.filter(noiseTexture.shader());
		// blur texture for smooothness
		BlurProcessingFilter.instance(p).setBlurSize(5);
		BlurProcessingFilter.instance(p).applyTo(noiseBuffer);
		
		// set context & camera
		DrawUtil.setCenterScreen(p);
		DrawUtil.basicCameraFromMouse(p.g);
		p.rotateX(P.sin(loop.progressRads()) * 0.2f);
//		CameraUtil.setCameraDistance(p.g, 100, 10000);
		
		// set shader & draw mesh
		LinesDeformAndTextureFilter.instance(p).setDisplacementMap(noiseBuffer);
		LinesDeformAndTextureFilter.instance(p).setColorMap(DemoAssets.textureNebula());
		LinesDeformAndTextureFilter.instance(p).setWeight(p.mousePercentX() * 20f);
		LinesDeformAndTextureFilter.instance(p).setModelMaxExtent(shapeExtent * 2f);
		LinesDeformAndTextureFilter.instance(p).setColorThicknessMode((p.mousePercentY() > 0.5f));
		if(p.mousePercentX() > 0.5f) {
			LinesDeformAndTextureFilter.instance(p).setSheetMode(true);
			LinesDeformAndTextureFilter.instance(p).setDisplaceAmp(p.mousePercentY() * pg.height * 0.7f);
		} else {
			LinesDeformAndTextureFilter.instance(p).setSheetMode(false);
			LinesDeformAndTextureFilter.instance(p).setDisplaceAmp(p.mousePercentY() * pg.height * 0.01f);
		}
		//		p.shader(displacementShader, P.LINES);
		LinesDeformAndTextureFilter.instance(p).applyTo(p);
		p.stroke(255);
		p.shape(shape);
		p.resetShader();
	}
		
}