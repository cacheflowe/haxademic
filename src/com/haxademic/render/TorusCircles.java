package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.FXAAFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.InvertFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PFont;

public class TorusCircles
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int animStyle = 0;
	protected int FRAMES = 60 * 8;
	
	protected EasingFloat cameraProgress = new EasingFloat(0, 0.1f);
	protected LinearFloat cameraProgressEase = new LinearFloat(0, 0.01f);

	protected void config() {
		Config.setAppSize(1024, 1024);
		Config.setPgSize(1024*2, 1024*2);
		if(animStyle == 0) FRAMES = 900;
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + (FRAMES * 2));
	}

	protected void drawApp() {
		p.background(0);
		
		// prep pg context
		pg.beginDraw();
		pg.background(0);
		pg.ortho();

		// set to center
		pg.push();
		PG.setDrawCenter(pg);
		PG.setCenterScreen(pg);
		pg.blendMode(PBlendModes.ADD);

		// automate camera
		// move camera
		float cameraXRot = 0;
		if(animStyle == 0) {
			cameraProgress.setCompleteThreshold(0.000001f);
			cameraProgress.setEaseFactor(0.075f);
			float progressOffset = (FrameLoop.progress() + P.PI * 1.22f + P.TWO_PI) % 1;
			if(progressOffset > 0.75f) cameraProgress.setTarget(P.TWO_PI * 0.2f);
			else if(progressOffset > 0.50f) cameraProgress.setTarget(P.HALF_PI * 0.995f);
			else if(progressOffset > 0.25f) cameraProgress.setTarget(0.475f);
			else if(progressOffset > 0.0f) cameraProgress.setTarget(0f);
//			if(FrameLoop.loopCurFrame() == 1) cameraProgress.setCurrent(0f);
			cameraProgress.update(true);
			cameraXRot = cameraProgress.value();
		} else {
			float progressOffset = (FrameLoop.progress() + P.PI * 1.275f + P.TWO_PI) % 1;
			if(progressOffset > 0.5f) cameraProgressEase.setTarget(1f);
			else if(progressOffset > 0.f) cameraProgressEase.setTarget(0f);
			cameraProgressEase.update();
			cameraXRot = -0.475f * Penner.easeInOutQuart(cameraProgressEase.value(), 0, 1, 1);			
		}
		pg.rotateX(cameraXRot);
//		PG.basicCameraFromMouse(pg);
		
		// draw torus
		float torii = 36f;
		float segmentRads = P.TWO_PI / torii;
		float size20p = pg.height * 0.2f;
		for (int i = 0; i < torii; i++) {
			float curRads = segmentRads * i;
			curRads += FrameLoop.progressRads() / (torii / 6f);	// 6 places moved from circle start to finish 
			float yOffset = size20p * P.sin(curRads);
			float radiusOffset = size20p * 0.95f * P.sin(P.HALF_PI + curRads);
			
			// draw circles
			pg.push();
			pg.translate(0, yOffset);
			pg.rotateX(P.HALF_PI);
			if(animStyle == 0) {
				Shapes.drawDisc3D(pg, 
						size20p + radiusOffset, 
						size20p * 0.975f + radiusOffset, 
						size20p * 0.03f, 
						100, 
						p.color(255), 
						p.color(255));
			} else {
				Shapes.drawDisc3D(pg, 
						size20p + radiusOffset, 
						size20p * 0.985f + radiusOffset, 
						size20p * 0.02f, 
						100, 
						p.color(255), 
						p.color(255));
			}
			pg.pop();
		}
		pg.pop();

		pg.endDraw();
		
		
		// post process
		if(animStyle == 0) {
			BlurHFilter.instance(p).setBlurByPercent(0.5f, pg.width);
			BlurHFilter.instance(p).applyTo(pg);
			BlurVFilter.instance(p).setBlurByPercent(0.5f, pg.height);
			BlurVFilter.instance(p).applyTo(pg);
		}
		
		BloomFilter.instance(p).setStrength(2f);
		BloomFilter.instance(p).setBlurIterations(4);
		BloomFilter.instance(p).setBlendMode(BloomFilter.BLEND_SCREEN);
		BloomFilter.instance(p).applyTo(pg);
		
		VignetteFilter.instance(p).setDarkness(0.5f);
//		VignetteFilter.instance(p).applyTo(pg);
//		VignetteFilter.instance(p).applyTo(pg);
		
		GrainFilter.instance(p).setTime(p.frameCount * 0.01f);
		GrainFilter.instance(p).setCrossfade(0.13f);
		if(animStyle == 1) GrainFilter.instance(p).applyTo(pg);
		
		// draw to screen
		p.image(pg, 0, 0, p.width, p.height);

		if(animStyle == 0) {
			InvertFilter.instance(p).applyTo(pg);
			p.background(20);
			p.fill(100);
			p.rect(p.width * 0.13f, p.height * (0.13f - 0.05f), p.width * 0.74f, p.width * 0.74f);
			p.image(pg, p.width * 0.15f, p.height * (0.15f - 0.05f), p.width * 0.7f, p.width * 0.7f);
//			FXAAFilter.instance(p).applyTo(p.g);
			
			// draw text
			String fontFile = DemoAssets.fontRalewayPath;
			float fontSize = p.height * 0.035f;
			float heightHalf = fontSize/2; 
			PFont font = FontCacher.getFont(fontFile, fontSize);
			FontCacher.setFontOnContext(p.g, font, p.color(240), 1f, PTextAlign.CENTER, PTextAlign.CENTER);
			p.g.text("\"No, You're a Torus\"".toUpperCase(), 0, p.height - fontSize * 4.35f, p.width, fontSize * 3f);
		}
	}


}
