package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.CubicLensDistortionFilter;
import com.haxademic.core.draw.filters.pshader.FeedbackRadialFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.RepeatFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;
import processing.core.PShape;

public class TunnelTorus
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	int FRAMES = 60 * 30;

	protected PShape shape;
	protected float torusRadius;
	protected PGraphics texture;
	
	protected void config() {
		Config.setAppSize(1024, 1024);
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
	}
	
	protected void firstFrame() {
		// build texture 
		texture = PG.newPG(2048, 1024);
		PG.setTextureRepeat(texture, true);
		DebugView.setTexture("texture", texture);
		
		// create torus w/quads
		torusRadius = p.width * 2;
//		shape = Shapes.createTorus(torusRadius, torusRadius/20, 360, 200, texture);
		shape = Shapes.createTorus(torusRadius, torusRadius/20, 180, 24, null);
		// get triangulaed version if desired
		shape = shape.getTessellation();
//		shape.setTexture(texture);
		
		// rotate up front for ease of later fancy rotation
		PShapeUtil.meshRotateOnAxis(shape, P.HALF_PI, P.X);
	}

	protected void drawApp() {
		background(0);
		
		// update texture
		texture.beginDraw();
		texture.background(0);
		texture.noStroke();
//		for (float i = -64 + (FrameLoop.progress() * 64f * 20)%64; i < texture.width + 32; i+=16) {
		for (float i = 0; i < texture.width; i+=16) {
			texture.fill(255);
			texture.rect(i, 0, 8, texture.height);
		}
		texture.endDraw();
		RepeatFilter.instance(p).setOffset(FrameLoop.progress() * -0.1f, 0);
//		RepeatFilter.instance(p).applyTo(texture);
		FeedbackRadialFilter.instance(p).setMultY(0);
		FeedbackRadialFilter.instance(p).setAmp(0.05f);
//		FeedbackRadialFilter.instance(p).applyTo(texture);
//		MirrorQuadFilter.instance(p).applyTo(texture);
//		ImageUtil.copyImage(DemoAssets.textureNebula(), texture);
		
		PG.setCenterScreen(p.g);
		p.g.translate(0, 0, Mouse.xNorm * -3000);
//		PG.basicCameraFromMouse(p.g);
//		PG.setBetterLights(p.g);
//		p.lights();
//		p.ambient(255);
//		p.ambientLight(75, 75, 75);
		
		// basic rotation
//		p.translate(torusRadius, 0, torusRadius/2);
//		p.rotateX(P.HALF_PI);
//		p.rotateZ(FrameLoop.count(-0.01f));
		
		// fancier rotating rotation
		float curRads = -1f * FrameLoop.progressRads();
		p.translate(torusRadius * P.cos(curRads), torusRadius * P.sin(curRads), torusRadius/2);
//		p.rotateX(-curRads);
		p.rotateZ(curRads);
		p.rotateY(-curRads);

		// draw torus
		shape.disableStyle();
		p.fill(0);
//		p.noFill();
		p.stroke(0, 255, 0);
		p.strokeWeight(2);
//		p.noStroke();
	
		// deform mesh
		MeshDeformAndTextureFilter.instance(p).setDisplacementMap(DemoAssets.textureNebula());
		MeshDeformAndTextureFilter.instance(p).setDisplaceAmp(0.01f);
		MeshDeformAndTextureFilter.instance(p).setSheetMode(false);
//		MeshDeformAndTextureFilter.instance(p).applyTo(p);

		// draw mesh
//		p.shape(shape);
		PShapeUtil.drawTriangles(p.g, shape, null, 1);
		p.resetShader();
		
		// postprocessing
		VignetteFilter.instance(p).setDarkness(0.75f);
		VignetteFilter.instance(p).setSpread(0.2f);
		VignetteFilter.instance(p).applyTo(p.g);

		GrainFilter.instance(p).setTime(p.frameCount * 0.02f);
		GrainFilter.instance(p).setCrossfade(0.02f);
//		GrainFilter.instance(p).applyTo(p.g);
		
		CubicLensDistortionFilter.instance(p).setAmplitude(-2f);
//		CubicLensDistortionFilter.instance(p).applyTo(p.g);
	}

}
