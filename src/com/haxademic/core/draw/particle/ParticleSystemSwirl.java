package com.haxademic.core.draw.particle;

import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;

public class ParticleSystemSwirl
extends ParticleSystem {

	public ParticleSystemSwirl(PImage[] particleImages) {
		super(particleImages);
	}
	
	public void enableUI(String prefix, boolean saves) {
		usingUI = true;
		UI.addSlider(prefix + MAX_ATTEMPTS, 2000, 10, 5000, 5, saves);
		UI.addSlider(prefix + MAX_LAUNCHES, 8, 1, 100, 1, saves);
		UI.addSlider(prefix + POOL_MAX_SIZE, 10000, 10, 20000, 1, saves);
		UI.addSlider(prefix + SPEED_X_MIN, 0, -5f, 5, 0.01f, saves);
		UI.addSlider(prefix + SPEED_X_MAX, 0.66f, -5, 5, 0.01f, saves);
		UI.addSlider(prefix + SPEED_Y_MIN, 0f, -5f, 5, 0.01f, saves);
		UI.addSlider(prefix + SPEED_Y_MAX, 0.44f, -5, 5, 0.01f, saves);
		UI.addSlider(prefix + GRAVITY_X_MIN, 0, -0.5f, 0, 0.001f, saves);
		UI.addSlider(prefix + GRAVITY_X_MAX, 0.002f, 0, 0.5f, 0.001f, saves);
		UI.addSlider(prefix + GRAVITY_Y_MIN, -0.33f, -0.5f, 0, 0.001f, saves);
		UI.addSlider(prefix + GRAVITY_Y_MAX, 0.12f, 0, 0.5f, 0.001f, saves);
		UI.addSlider(prefix + LIFESPAN_MIN, 10, 10, 50, 1, saves);
		UI.addSlider(prefix + LIFESPAN_MAX, 50, 10, 200, 1, saves);
		UI.addSlider(prefix + ROTATION_MIN, 0, -1, 0, 0.001f, saves);
		UI.addSlider(prefix + ROTATION_MAX, 0.281f, 0, 1f, 0.001f, saves);
		UI.addSlider(prefix + SIZE_MIN, 10, 1, 40, 0.1f, saves);
		UI.addSlider(prefix + SIZE_MAX, 90, 10, 200, 0.1f, saves);
	}

	public void launchParticles(PGraphics pg) {
		int numLaunched = 0;
		int maxAttempts = (usingUI) ? UI.valueInt(MAX_ATTEMPTS) : MAX_MAP_ATTEMPTS_PER_FRAME;
		int maxLaunches = (usingUI) ? UI.valueInt(MAX_LAUNCHES) : MAX_LAUNCHES_PER_FRAME;
		for (int i = 0; i < maxAttempts; i++) {
			float radius = MathUtil.randRangeDecimal(pg.height * 0.15f, pg.height * 0.35f);
			float rads = MathUtil.randRangeDecimal(0, P.TWO_PI);
			if(numLaunched < maxLaunches) {
				launchParticle(pg, radius, rads);
				numLaunched++;
			}
		}
	}
	
	protected Particle2d initNewParticle() {
		// override with custom particle subclass
		return new ParticleSwirl();
	}

}
