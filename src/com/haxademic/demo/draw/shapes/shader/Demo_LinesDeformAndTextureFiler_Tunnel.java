package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.pshader.LinesDeformAndTextureFilter;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;
import processing.core.PShape;

public class Demo_LinesDeformAndTextureFiler_Tunnel 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PGraphics noiseBuffer;
	protected TextureShader noiseTexture;
	protected float shapeExtent = 100;

	protected void config() {
		int FRAMES = 358;
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
	}
	
	protected void firstFrame() {
		// load texture
		noiseBuffer = p.createGraphics(p.width, p.height, PRenderers.P2D);
		noiseTexture = new TextureShader(TextureShader.noise_simplex_2d_iq, 0.0005f);
		DebugView.setTexture("noiseBuffer", noiseBuffer);
		
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
		DebugView.setValue("shape.getVertexCount();", PShapeUtil.vertexCount(shape));
	}

	protected void drawApp() {
		background(0);
		
		// update displacement texture
		noiseTexture.shader().set("zoom", 2.5f + 1.5f * P.sin(FrameLoop.progressRads()));
		noiseTexture.shader().set("rotation", FrameLoop.progressRads());
		noiseBuffer.filter(noiseTexture.shader());
		// blur texture for smooothness
		BlurProcessingFilter.instance(p).setBlurSize(5);
		BlurProcessingFilter.instance(p).applyTo(noiseBuffer);
		
		// set context & camera
		PG.setCenterScreen(p);
		PG.basicCameraFromMouse(p.g);
		p.rotateX(P.sin(FrameLoop.progressRads()) * 0.2f);
//		CameraUtil.setCameraDistance(p.g, 100, 10000);
		
		// set shader & draw mesh
		LinesDeformAndTextureFilter.instance(p).setDisplacementMap(noiseBuffer);
		LinesDeformAndTextureFilter.instance(p).setColorMap(DemoAssets.textureNebula());
		LinesDeformAndTextureFilter.instance(p).setWeight(Mouse.xNorm * 20f);
		LinesDeformAndTextureFilter.instance(p).setModelMaxExtent(shapeExtent * 2f);
		LinesDeformAndTextureFilter.instance(p).setColorThicknessMode((Mouse.yNorm > 0.5f));
		if(Mouse.xNorm > 0.5f) {
			LinesDeformAndTextureFilter.instance(p).setSheetMode(true);
			LinesDeformAndTextureFilter.instance(p).setDisplaceAmp(Mouse.yNorm * pg.height * 0.7f);
		} else {
			LinesDeformAndTextureFilter.instance(p).setSheetMode(false);
			LinesDeformAndTextureFilter.instance(p).setDisplaceAmp(Mouse.yNorm * pg.height * 0.01f);
		}
		//		p.shader(displacementShader, P.LINES);
		LinesDeformAndTextureFilter.instance(p).applyTo(p);
		p.stroke(255);
		p.shape(shape);
		p.resetShader();
	}
		
}