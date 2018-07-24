package com.haxademic.sketch.test;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.GodRays;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;
import processing.core.PShape;

public class ButterflyTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape icosa;

	protected PImage butterflyCenter;
	protected PImage butterflyLeft;
	protected PImage butterflyRight;

	protected void overridePropsFile() {
		int FRAMES = 360;
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE, false);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 3);
	}

	public void setupFirstFrame() {
		// load sphere
		int detail = 4;
		icosa = Icosahedron.createIcosahedron(p.g, detail, p.loadImage(FileUtil.getFile("haxademic/images/spherical/outdoors.jpg")));
		PShapeUtil.centerShape(icosa);
		PShapeUtil.scaleShapeToHeight(icosa, p.height * 5f);
		
		// load butterfly
		butterflyCenter = p.loadImage(FileUtil.getFile("images/_sketch/butterfly-test-center.png"));
		butterflyLeft = p.loadImage(FileUtil.getFile("images/_sketch/butterfly-test-left.png"));
		butterflyRight = p.loadImage(FileUtil.getFile("images/_sketch/butterfly-test-right.png"));
	}
	


	public void drawApp() {
		p.background(0);
		DrawUtil.setCenterScreen(p);

		float sceneRot = 0.6f * P.sin(p.loop.progressRads());
		
		//////////////////////
		// sphere ////////////
		//////////////////////
		p.pushMatrix();
		DrawUtil.setDrawCorner(p);
		p.rotateY(-sceneRot);
		p.shape(icosa);
		p.popMatrix();
		
		BrightnessFilter.instance(p).setBrightness(1.1f);
		BrightnessFilter.instance(p).applyTo(p);
		BlurHFilter.instance(p).setBlurByPercent(0.5f, p.width);
		BlurHFilter.instance(p).applyTo(p);
		BlurVFilter.instance(p).setBlurByPercent(0.5f, p.height);
		BlurVFilter.instance(p).applyTo(p);
		
		//////////////////////
		// set camera rotation
		//////////////////////
		p.rotateX(0.8f + 0.3f * P.sin(p.loop.progressRads()));
		p.rotateZ(sceneRot);

		//////////////////////
		// butterfly props ///
		//////////////////////
		
		float wingOsc = p.loop.progressRads() * 12f;
		float wingRot = 0.7f + 0.6f * P.sin(wingOsc); // p.mousePercentX();
		// wingRot = p._audioInput.getFFT().spectrum[4] * 10f;
		p.translate(0, 0, P.sin(P.HALF_PI + wingOsc) * p.height * 0.015f);	// bob up/down
		
		//////////////////////
		// body //////////////
		//////////////////////
		
		DrawUtil.setDrawCenter(p);
		p.scale(0.25f);
		p.image(butterflyCenter, 0, 0);

		//////////////////////
		// wings /////////////
		//////////////////////
				
		p.pushMatrix();
		// p.translate(0, 0, 5);
		p.rotateY(wingRot);
		p.image(butterflyLeft, 0, 0);
		p.popMatrix();
		
		p.pushMatrix();
		// p.translate(-2, 0, -1);
		p.rotateY(-wingRot);
		p.image(butterflyRight, 0, 0);
		p.popMatrix();
		
		//////////////////////
		// post //////////////
		//////////////////////

		GodRays.instance(p).setRotation(1.2f * P.sin(p.loop.progressRads()));
		GodRays.instance(p).setAmp(0.6f);
		GodRays.instance(p).setWeight(0.03f);
		GodRays.instance(p).applyTo(p);
		SaturationFilter.instance(p).setSaturation(0.8f);
		SaturationFilter.instance(p).applyTo(p);
		VignetteFilter.instance(p).setDarkness(0.5f);
		VignetteFilter.instance(p).applyTo(p);
//		HueFilter.instance(p).setHue(p.loop.progress() * 360f);
//		HueFilter.instance(p).applyTo(p);
	}

}

