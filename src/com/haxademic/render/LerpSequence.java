package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.math.easing.EasingFloat;

public class LerpSequence 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int FRAMES = 400;
	protected float rectSize = 400f;
	protected float numObjects = 16f;
	protected float easeFactor = 0.15f;
	protected float easeMult = 1.5f;
	protected EasingFloat size1 = new EasingFloat(0, easeFactor);
	protected EasingFloat size2 = new EasingFloat(0, easeFactor * easeMult);
	protected EasingFloat rot1 = new EasingFloat(0, easeFactor);
	protected EasingFloat rot2 = new EasingFloat(0, easeFactor * easeMult);
	protected EasingFloat rounded1 = new EasingFloat(0, easeFactor);
	protected EasingFloat rounded2 = new EasingFloat(0, easeFactor * easeMult);

	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 800);
		Config.setProperty(AppSettings.HEIGHT, 800);
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
	}
	
	protected void firstFrame() {
		
	}

	protected void drawApp() {
		// set context
		pg.beginDraw();
		pg.blendMode(PBlendModes.BLEND);
		pg.background(0);
		pg.noStroke();
		pg.fill(255);
		PG.setCenterScreen(pg);
		PG.setDrawCenter(pg);
		pg.blendMode(PBlendModes.EXCLUSION);
		
		// switch positions every x frames
		if(p.frameCount % FRAMES == 1) {
			size1.setTarget(1.5f);
			size2.setTarget(0f);
			rot1.setTarget(0);
			rot2.setTarget(0);
			rounded1.setTarget(12);
			rounded2.setTarget(3);
		}
		if(p.frameCount % FRAMES == 101) {
			size1.setTarget(0.05f);
			size2.setTarget(1.25f);
			rot1.setTarget(0);
			rot2.setTarget(P.TWO_PI / numObjects * 2f);
			rounded1.setTarget(0);
			rounded2.setTarget(0);
		}
		if(p.frameCount % FRAMES == 201) {
			float newRot = P.HALF_PI * (numObjects - 1);
			float startRot = -newRot / 2f + P.HALF_PI / 2f;
			size1.setTarget(2f);
			size2.setTarget(0.25f);
			rot1.setTarget(startRot);
			rot2.setTarget(startRot + newRot);
			rounded1.setTarget(0);
			rounded2.setTarget(2);
		}
		if(p.frameCount % FRAMES == 301) {
			float newRot = P.QUARTER_PI * (numObjects - 1);
			float startRot = -newRot / 2f + P.QUARTER_PI / 2f;
			size1.setTarget(0.05f);
			size2.setTarget(1.35f);
			rot1.setTarget(startRot);
			rot2.setTarget(startRot + newRot);
			rounded1.setTarget(6);
			rounded2.setTarget(6);
		}
		
		// lerp values
		size1.update(true);
		size2.update(true);
		rot1.update(true);
		rot2.update(true);
		rounded1.update(true);
		rounded2.update(true);
		
		// rotate, scale and draw
		for (int i = 0; i < numObjects; i++) {
			float size = P.map(i, 0, numObjects - 1, size1.value(), size2.value());
			float rot = P.map(i, 0, numObjects - 1, rot1.value(), rot2.value());
			float rounded = P.map(i, 0, numObjects - 1, rounded1.value(), rounded2.value());

			pg.pushMatrix();
			pg.rotate(rot);
			pg.rect(0, 0, size * rectSize, size * rectSize, rounded);
			pg.popMatrix();
		}
		
		// context end
		pg.endDraw();
		
		// post process
		GrainFilter.instance(p).setTime(p.frameCount * 0.01f);
		GrainFilter.instance(p).setCrossfade(0.12f);
		GrainFilter.instance(p).applyTo(pg);
		
		BloomFilter.instance(p).setStrength(1f);
		BloomFilter.instance(p).setBlurIterations(5);
		BloomFilter.instance(p).setBlendMode(BloomFilter.BLEND_SCREEN);
		BloomFilter.instance(p).applyTo(pg);
		
		VignetteFilter.instance(p).setDarkness(0.4f);
		VignetteFilter.instance(p).applyTo(pg);

		// draw to screen
		p.image(pg, 0, 0);
	}
		
}