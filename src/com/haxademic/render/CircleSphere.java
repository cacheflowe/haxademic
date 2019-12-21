package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;

public class CircleSphere 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int FRAMES = 300;
	protected int animStyle = 1;
	protected EasingFloat cameraProgress = new EasingFloat(0, 0.1f);
	protected LinearFloat cameraProgressEase = new LinearFloat(0, 0.02f);

	protected void config() {
		if(animStyle == 0) FRAMES = 900;
		Config.setProperty(AppSettings.WIDTH, 1000);
		Config.setProperty(AppSettings.HEIGHT, 1000);
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + (FRAMES * 2));
	}
	
	protected void firstFrame() {
		
	}

	public void drawApp() {
		// move camera
		float cameraXRot = 0;
		if(animStyle == 0) {
			cameraProgress.setCompleteThreshold(0.000001f);
			cameraProgress.setEaseFactor(0.075f);
			if(p.loop.progress() > 0.8f) cameraProgress.setTarget(0.96f);
			else if(p.loop.progress() > 0.6f) cameraProgress.setTarget(0.75f);
			else if(p.loop.progress() > 0.4f) cameraProgress.setTarget(0.5f);
			else if(p.loop.progress() > 0.2f) cameraProgress.setTarget(0.04f);
			else if(p.loop.progress() > 0.0f) cameraProgress.setTarget(0f);
			if(p.loop.loopCurFrame() == 1) cameraProgress.setCurrent(-0.04f);
			cameraProgress.update(true);
			cameraXRot = cameraProgress.value();
		} else {
			if(p.loop.progress() > 0.5f) cameraProgressEase.setTarget(1f);
			else if(p.loop.progress() > 0.f) cameraProgressEase.setTarget(0f);
			cameraProgressEase.update();
			cameraXRot = -0.04f * Penner.easeInOutQuart(cameraProgressEase.value(), 0, 1, 1);			
		}
		
		// set context
		pg.beginDraw();
		pg.blendMode(PBlendModes.BLEND);
		pg.background(ColorUtil.colorFromHex("050516"));
		pg.stroke(ColorUtil.colorFromHex("fd5e53"));
		pg.strokeWeight(4f);
		pg.noFill();
		pg.fill(0, 0);
		pg.ortho();
//		pg.perspective();
		PG.setCenterScreen(pg);
		PG.setDrawCenter(pg);
		PG.setBetterLights(pg);
		pg.rotateX(cameraXRot * P.TWO_PI);
//		PG.basicCameraFromMouse(pg);
		
		// config
		float numCircles = 20;
		float segmentRads = P.PI / numCircles;
		float circleDiam = p.height * 0.6f;
		float circleRadius = circleDiam/2f;
		float spacingProgess = 1f / numCircles;
		
		// rotate, scale and draw
		for (float i = -16f; i < numCircles + 16f; i++) {
			float layoutYProgress = i / numCircles;
			float yRads = segmentRads * i;
			
			float y = (-1f + 2f * layoutYProgress) + (spacingProgess * p.loop.progress() * 12f);
			float x = P.cos(P.asin(y));

			pg.pushMatrix();
			pg.translate(0, y * circleRadius, 0);
			pg.rotateX(P.HALF_PI);
			pg.ellipse(0, 0, x * circleDiam, x * circleDiam);
			pg.popMatrix();
		}
		
		// context end
		pg.endDraw();
		
		// post process
		BloomFilter.instance(p).setStrength(2f);
		BloomFilter.instance(p).setBlurIterations(10);
		BloomFilter.instance(p).setBlendMode(BloomFilter.BLEND_SCREEN);
		BloomFilter.instance(p).applyTo(pg);
		
		VignetteFilter.instance(p).setDarkness(0.5f);
		VignetteFilter.instance(p).applyTo(pg);
		VignetteFilter.instance(p).applyTo(pg);
		
		GrainFilter.instance(p).setTime(p.frameCount * 0.01f);
		GrainFilter.instance(p).setCrossfade(0.12f);
		GrainFilter.instance(p).applyTo(pg);
		
		// draw to screen
		p.image(pg, 0, 0);
	}
		
	public void keyPressed() {
		super.keyPressed();
//		if(p.key == ' ') 
	}

}