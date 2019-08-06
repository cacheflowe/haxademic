package com.haxademic.core.draw.particle;

import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class ParticleSystemSwirl
extends ParticleSystem {

	public ParticleSystemSwirl(PImage[] particleImages) {
		super(particleImages);
	}
	
	public void enableUI() {
		usingUI = true;
		P.p.ui.addSlider(MAX_ATTEMPTS, 2000, 10, 5000, 5, false);
		P.p.ui.addSlider(MAX_LAUNCHES, 8, 1, 100, 1, false);
		P.p.ui.addSlider(POOL_MAX_SIZE, 10000, 10, 20000, 1, false);
		P.p.ui.addSlider(SPEED_X_MIN, 0, -5f, 0, 0.01f, false);
		P.p.ui.addSlider(SPEED_X_MAX, 0.66f, 0, 5, 0.01f, false);
		P.p.ui.addSlider(SPEED_Y_MIN, 0f, -5f, 0, 0.01f, false);
		P.p.ui.addSlider(SPEED_Y_MAX, 0.44f, 0, 5, 0.01f, false);
		P.p.ui.addSlider(GRAVITY_X_MIN, 0, -0.5f, 0, 0.001f, false);
		P.p.ui.addSlider(GRAVITY_X_MAX, 0.002f, 0, 0.5f, 0.001f, false);
		P.p.ui.addSlider(GRAVITY_Y_MIN, -0.33f, -0.5f, 0, 0.001f, false);
		P.p.ui.addSlider(GRAVITY_Y_MAX, 0.12f, 0, 0.5f, 0.001f, false);
		P.p.ui.addSlider(LIFESPAN_MIN, 10, 10, 50, 1, false);
		P.p.ui.addSlider(LIFESPAN_MAX, 50, 10, 200, 1, false);
		P.p.ui.addSlider(ROTATION_MIN, 0, -1, 0, 0.001f, false);
		P.p.ui.addSlider(ROTATION_MAX, 0.281f, 0, 1f, 0.001f, false);
		P.p.ui.addSlider(SIZE_MIN, 10, 1, 40, 0.1f, false);
		P.p.ui.addSlider(SIZE_MAX, 90, 10, 200, 0.1f, false);
	}

	public void launchParticles(PGraphics pg) {
		int numLaunched = 0;
		int maxAttempts = (usingUI) ? P.p.ui.valueInt(MAX_ATTEMPTS) : MAX_MAP_ATTEMPTS_PER_FRAME;
		int maxLaunches = (usingUI) ? P.p.ui.valueInt(MAX_LAUNCHES) : MAX_LAUNCHES_PER_FRAME;
		for (int i = 0; i < maxAttempts; i++) {
			float radius = MathUtil.randRangeDecimal(pg.height * 0.15f, pg.height * 0.35f);
			float rads = MathUtil.randRangeDecimal(0, P.TWO_PI);
			if(numLaunched < maxLaunches) {
				launchParticle(pg, radius, rads);
				numLaunched++;
			}
		}
	}
	
	protected Particle initNewParticle() {
		// override with custom particle subclass
		return new ParticleSwirl();
	}

}
