package com.haxademic.demo.draw.shadow;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurHMapFilter;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.BlurVMapFilter;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.filters.pshader.ToneMappingFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_BackgroundGradientAndShadow2
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics shadowMap;
	protected PGraphics depthMap;
	protected PShape obj;
	protected PShader depthShader;
	protected int FRAMES = 600;
	
	protected void config() {
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.WIDTH, 1024);
		Config.setProperty(AppSettings.HEIGHT, 1024);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
	}
	
	protected void firstFrame() {
		shadowMap = ImageUtil.imageToGraphics(p.g);
		depthMap = ImageUtil.imageToGraphics(p.g);
		DebugView.setTexture("shadowMap", shadowMap);
		DebugView.setTexture("depthMap", depthMap);
		
		depthShader = new PShader(this, 
			FileUtil.getPath("haxademic/shaders/vertex/depth-vert.glsl"), 
			FileUtil.getPath("haxademic/shaders/vertex/depth-frag.glsl")
		);
	}
	
	protected void drawApp() {
		background(0);
		PG.setDrawCenter(p);
		drawBgGradient();
		drawShadowBuffer();
		drawDepthBuffer();
		drawShadowToStage();
		drawShapeToStage();
		postProcessStage();
	}
	
	protected void drawBgGradient() {
		p.pushMatrix();
		p.translate(p.width/2, p.height/2, -p.width);
		p.rotate(P.HALF_PI);
		p.scale(2.5f);
		Gradients.linear(p, p.width, p.height, p.color(5), p.color(190, 70, 120));
		p.popMatrix();

		BlurProcessingFilter.instance(p).setBlurSize(10);
		BlurProcessingFilter.instance(p).setSigma(10);
		BlurProcessingFilter.instance(p).applyTo(p.g);
	}

	protected void drawShadowBuffer() {
		shadowMap.beginDraw();
		shadowMap.clear();
		shadowMap.perspective(); // shadowMap.ortho();
		PG.setCenterScreen(shadowMap);
		PG.setDrawCenter(shadowMap);
		shadowMap.rotateX(-P.HALF_PI);
		shadowMap.fill(0);
		shadowMap.stroke(0);
		drawShape(shadowMap);
		shadowMap.endDraw();
		applyBlur(shadowMap);
	}
	
	protected void drawDepthBuffer() {
		depthMap.beginDraw();
		depthMap.clear();
		depthShader.set("far", 1500f);
		depthShader.set("near", 100f);
		depthShader.set("farColor", 0f, 0f, 0f, 1f);
		depthShader.set("nearColor", 1f, 1f, 1f, 1f);
		depthMap.shader(depthShader);
		depthMap.perspective(); // depthMap.ortho();
		PG.setCenterScreen(depthMap);
		PG.setDrawCenter(depthMap);
		depthMap.rotateX(-P.HALF_PI);
		depthMap.fill(0);
		depthMap.stroke(0);
		drawShape(depthMap);
		depthMap.endDraw();
		
		applyBlur(depthMap);
		applyBlur(depthMap);
		applyBlur(depthMap);
		applyBlur(depthMap);
		applyBlur(depthMap);
		applyBlur(depthMap);
		
		BlurHMapFilter.instance(p).setMap(depthMap);
		BlurHMapFilter.instance(p).setAmpMin(0.0f);
		BlurHMapFilter.instance(p).setAmpMax(3.25f);
		BlurVMapFilter.instance(p).setMap(depthMap);
		BlurVMapFilter.instance(p).setAmpMin(0.0f);
		BlurVMapFilter.instance(p).setAmpMax(3.25f);
		
		BlurHMapFilter.instance(p).applyTo(shadowMap);
		BlurVMapFilter.instance(p).applyTo(shadowMap);
		BlurHMapFilter.instance(p).applyTo(shadowMap);
		BlurVMapFilter.instance(p).applyTo(shadowMap);
		BlurHMapFilter.instance(p).applyTo(shadowMap);
		BlurVMapFilter.instance(p).applyTo(shadowMap);
		BlurHMapFilter.instance(p).applyTo(shadowMap);
		BlurVMapFilter.instance(p).applyTo(shadowMap);
		BlurHMapFilter.instance(p).applyTo(shadowMap);
		BlurVMapFilter.instance(p).applyTo(shadowMap);
	}
	
	protected void drawShadowToStage() {
		p.pushMatrix();
		p.translate(p.width/2, p.height/2 + p.height*0.25f);
		p.rotateX(P.HALF_PI);
		p.scale(1.15f);
		PG.setPImageAlpha(p, 0.4f);
		p.image(shadowMap, 0, 0);
		p.popMatrix();
	}
	
	protected void drawShapeToStage() {
		p.pushMatrix();
		p.fill(0);
		p.stroke(255);
		PG.setCenterScreen(p);
//		p.lights();
		PG.setBetterLights(p);
		p.shininess(9);
		drawShape(p.g);
		p.popMatrix();
	}
	
	protected void drawShape(PGraphics pg) {
		// draw a bunch of cubes
		pg.push();
//		pg.rotateY(FrameLoop.progressRads());
		float numShapes = 32;
		float segmentRads = P.TWO_PI / numShapes;
		float radius = pg.width * 0.25f;
		for (int i = 0; i < numShapes; i++) {
			float x = P.cos(i * segmentRads + FrameLoop.progressRads()) * radius;
			float z = P.sin(i * segmentRads + FrameLoop.progressRads()) * radius;
			float y = -10 + 90 * P.sin(i*segmentRads * 2 + FrameLoop.progressRads());
			
			pg.push();
			pg.noStroke();
			pg.translate(x, y, z);
			pg.rotateY(i*segmentRads + FrameLoop.progressRads());
			pg.rotateX(i*segmentRads + FrameLoop.progressRads());
			
//			pg.box(20, 10, 10);
			pg.sphere(pg.height * 0.008f);
			pg.pop();
		}
		pg.pop();
		
		// draw a cylinder
		float cylH = pg.height * 0.225f;
		pg.push();
		pg.translate(0, -40 + cylH/2f + 10*P.sin(FrameLoop.progressRads()*4), 0);
		pg.noStroke();
		pg.rotateY(-FrameLoop.progressRads()/2);
		Shapes.drawCylinder(pg, 6, pg.width * 0.1f, pg.width * 0.f, cylH, true);
		pg.pop();
		
		pg.push();
		pg.translate(0, -40 - cylH/2 + 10*P.sin(FrameLoop.progressRads()*4), 0);
		pg.noStroke();
		pg.rotateY(-FrameLoop.progressRads()/2);
		Shapes.drawCylinder(pg, 6, pg.width * 0.f, pg.width * 0.1f, cylH, true);
		pg.pop();

		// draw a dashed circle
		pg.push();
		pg.translate(0, -35, 0);
		pg.strokeWeight(3);

		pg.push();
		pg.rotateX(P.HALF_PI);
		pg.rotateZ(-FrameLoop.progressRads());
		Shapes.drawDashedCircle(pg, 0, 0, pg.width * 0.15f, 30, 0, true);
		pg.pop();

		pg.push();
		pg.translate(0, -cylH, 0);
		pg.rotateX(P.HALF_PI);
		pg.rotateZ(FrameLoop.progressRads()*2);
		Shapes.drawDashedCircle(pg, 0, 0, pg.width * 0.055f, 30, 0, true);
		pg.pop();
		pg.push();
		pg.translate(0, cylH, 0);
		pg.rotateX(P.HALF_PI);
		pg.rotateZ(FrameLoop.progressRads()*2);
		Shapes.drawDashedCircle(pg, 0, 0, pg.width * 0.055f, 30, 0, true);
		pg.pop();

		pg.pop();
	}
	
	protected void applyBlur(PGraphics pg) {
//		BlurHFilter.instance(p).setBlurByPercent(3f + 2f * P.sin(P.PI + FrameLoop.progressRads()), pg.width);
		BlurHFilter.instance(p).setBlurByPercent(2f, pg.width);
		BlurHFilter.instance(p).applyTo(pg);
//		BlurVFilter.instance(p).setBlurByPercent(3f + 2f * P.sin(P.PI + FrameLoop.progressRads()), pg.height);
		BlurVFilter.instance(p).setBlurByPercent(2f, pg.height);
		BlurVFilter.instance(p).applyTo(pg);
	}
	
	protected void postProcessStage() {
		ToneMappingFilter.instance(P.p).setMode(1);
		ToneMappingFilter.instance(P.p).setGamma(1.75f);
		ToneMappingFilter.instance(P.p).setCrossfade(1f);
		ToneMappingFilter.instance(P.p).applyTo(p.g);
		
		// add some saturation back in
		SaturationFilter.instance(p).setSaturation(1.1f);
		SaturationFilter.instance(p).applyTo(p.g);
		ContrastFilter.instance(p).setContrast(1.1f);
		ContrastFilter.instance(p).applyTo(p.g);
		
		int bloomBlendMode = 2; // P.round(p.frameCount / 200f) % 3;
		BloomFilter.instance(p).setStrength(1);
		BloomFilter.instance(p).setBlurIterations(1);
		BloomFilter.instance(p).setBlendMode(bloomBlendMode);
//		BloomFilter.instance(p).applyTo(p.g);

		
		VignetteFilter.instance(p).setDarkness(0.5f);
		VignetteFilter.instance(p).applyTo(p.g);
//		VignetteAltFilter.instance(p).applyTo(p.g);
	}

}
