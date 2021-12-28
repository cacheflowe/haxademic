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
		super.enableUI(prefix, saves);
		P.out("ENABLEUI YO", MAX_ATTEMPTS);
	}

	public void launchParticlesFromMap(PGraphics pg) {
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
