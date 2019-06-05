package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PShape;

public class Demo_BackgroundGradientAndShadow
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics shadowMap;
	protected PShape obj;
	protected int FRAMES = 240;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		p.appConfig.setProperty(AppSettings.WIDTH, 800);
		p.appConfig.setProperty(AppSettings.HEIGHT, 800);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE, false);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
	}
	
	public void setupFirstFrame() {
		shadowMap = ImageUtil.imageToGraphics(p.g);
		loadObj();
	}
	
	public void loadObj() {
		obj = DemoAssets.objSkullRealistic();
		obj = DemoAssets.objHumanoid();
		PShapeUtil.centerShape(obj);
		PShapeUtil.scaleShapeToMaxAbsY(obj, p.height * 0.25f);
		obj.disableStyle();
	}
	
	public void drawApp() {
		background(0);
		DrawUtil.setDrawCenter(p);
		drawBgGradient();
		drawShadowBuffer();
		drawShadowToStage();
		drawShapeToStage();
		postProcessStage();
	}
	
	protected void drawBgGradient() {
		p.pushMatrix();
		p.translate(p.width/2, p.height/2, -p.width);
		p.rotate(P.HALF_PI);
		p.scale(2.5f);
		Gradients.linear(p, p.width, p.height, p.color(255), p.color(70, 70, 120));
		p.popMatrix();
	}

	protected void drawShadowBuffer() {
		shadowMap.beginDraw();
		shadowMap.clear();
		DrawUtil.setCenterScreen(shadowMap);
		DrawUtil.setDrawCenter(shadowMap);
		shadowMap.rotateX(-P.HALF_PI);
		shadowMap.fill(0);
		drawShape(shadowMap);
		shadowMap.endDraw();
		applyBlur(shadowMap);
	}
	
	protected void drawShadowToStage() {
		p.pushMatrix();
		p.translate(p.width/2, p.height/2 + 200);
		p.rotateX(P.HALF_PI);
		p.scale(1.25f);
		DrawUtil.setPImageAlpha(p, 0.3f);
		p.image(shadowMap, 0, 0);
		p.popMatrix();
	}
	
	protected void drawShapeToStage() {
		p.pushMatrix();
		p.fill(255);
		DrawUtil.setCenterScreen(p);
		p.lights();
//		DrawUtil.setBasicLights(p);
		drawShape(p.g);
		p.popMatrix();
	}
	
	protected void drawShape(PGraphics pg) {
		pg.noStroke();
		pg.pushMatrix();
		
		pg.translate(0, 180 * P.sin(loop.progressRads()));
		pg.rotateY(loop.progressRads());
		
		pg.box(400, 20, 50);
		pg.box(50, 20, 400);
		// PShapeUtil.drawTriangles(pg, obj, null, 1);
		
		pg.popMatrix();
	}
	
	protected void applyBlur(PGraphics pg) {
		BlurHFilter.instance(p).setBlurByPercent(3f + 2f * P.sin(P.PI + loop.progressRads()), pg.width);
		BlurHFilter.instance(p).applyTo(pg);
		BlurVFilter.instance(p).setBlurByPercent(3f + 2f * P.sin(P.PI + loop.progressRads()), pg.height);
		BlurVFilter.instance(p).applyTo(pg);
	}
	
	protected void postProcessStage() {
		VignetteFilter.instance(p).setDarkness(0.5f);
		VignetteFilter.instance(p).applyTo(p.g);
//		VignetteAltFilter.instance(p).applyTo(p.g);
	}

}
