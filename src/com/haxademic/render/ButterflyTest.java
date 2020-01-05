package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.GodRays;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.render.FrameLoop;

import processing.core.PImage;
import processing.core.PShape;

public class ButterflyTest
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape icosa;

	protected PImage butterflyCenter;
	protected PImage butterflyLeft;
	protected PImage butterflyRight;

	protected void config() {
		int FRAMES = 360;
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 3);
	}

	protected void firstFrame() {
		// load sphere
		int detail = 4;
		icosa = Icosahedron.createIcosahedron(p.g, detail, p.loadImage(FileUtil.getPath("haxademic/images/spherical/outdoors.jpg")));
		PShapeUtil.centerShape(icosa);
		PShapeUtil.scaleShapeToHeight(icosa, p.height * 5f);
		
		// load butterfly
		butterflyCenter = p.loadImage(FileUtil.getPath("images/_sketch/butterfly-test-center.png"));
		butterflyLeft = p.loadImage(FileUtil.getPath("images/_sketch/butterfly-test-left.png"));
		butterflyRight = p.loadImage(FileUtil.getPath("images/_sketch/butterfly-test-right.png"));
	}
	


	protected void drawApp() {
		p.background(0);
		PG.setCenterScreen(p);

		float sceneRot = 0.6f * P.sin(FrameLoop.progressRads());
		
		//////////////////////
		// sphere ////////////
		//////////////////////
		p.pushMatrix();
		PG.setDrawCorner(p);
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
		p.rotateX(0.8f + 0.3f * P.sin(FrameLoop.progressRads()));
		p.rotateZ(sceneRot);

		//////////////////////
		// butterfly props ///
		//////////////////////
		
		float wingOsc = FrameLoop.progressRads() * 12f;
		float wingRot = 0.7f + 0.6f * P.sin(wingOsc); // Mouse.xNorm;
		// wingRot = p._audioInput.getFFT().spectrum[4] * 10f;
		p.translate(0, 0, P.sin(P.HALF_PI + wingOsc) * p.height * 0.015f);	// bob up/down
		
		//////////////////////
		// body //////////////
		//////////////////////
		
		PG.setDrawCenter(p);
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

		GodRays.instance(p).setRotation(1.2f * P.sin(FrameLoop.progressRads()));
		GodRays.instance(p).setAmp(0.6f);
		GodRays.instance(p).setWeight(0.03f);
		GodRays.instance(p).applyTo(p);
		SaturationFilter.instance(p).setSaturation(0.8f);
		SaturationFilter.instance(p).applyTo(p);
		VignetteFilter.instance(p).setDarkness(0.5f);
		VignetteFilter.instance(p).applyTo(p);
//		HueFilter.instance(p).setHue(AnimationLoop.progress() * 360f);
//		HueFilter.instance(p).applyTo(p);
	}

}

