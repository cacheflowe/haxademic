package com.haxademic.core.draw.particle;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;

public class ParticleSystem {

	// particles & source textures
	protected ArrayList<Particle2d> particles = new ArrayList<Particle2d>();
	protected PImage[] particleImages;

	// config
	protected int MAX_MAP_ATTEMPTS_PER_FRAME = 2000;
	protected int MAX_LAUNCHES_PER_FRAME = 10;
	protected int PARTICLE_POOL_MAX_SIZE = 10000;
	protected boolean screenBlendMode = false;

	// ui
	protected boolean usingUI = false;
	protected String MAX_ATTEMPTS = "MAX_ATTEMPTS";
	protected String MAX_LAUNCHES = "MAX_LAUNCHES";
	protected String POOL_MAX_SIZE = "POOL_MAX_SIZE";
	protected String SPEED_X_MIN = "SPEED_X_MIN";
	protected String SPEED_X_MAX = "SPEED_X_MAX";
	protected String SPEED_Y_MIN = "SPEED_Y_MIN";
	protected String SPEED_Y_MAX = "SPEED_Y_MAX";
	protected String GRAVITY_X_MIN = "GRAVITY_X_MIN";
	protected String GRAVITY_X_MAX = "GRAVITY_X_MAX";
	protected String GRAVITY_Y_MIN = "GRAVITY_Y_MIN";
	protected String GRAVITY_Y_MAX = "GRAVITY_Y_MAX";
	protected String LIFESPAN_MIN = "LIFESPAN_MIN";
	protected String LIFESPAN_MAX = "LIFESPAN_MAX";
	protected String ROTATION_MIN = "ROTATION_MIN";
	protected String ROTATION_MAX = "ROTATION_MAX";
	protected String SIZE_MIN = "SIZE_MIN";
	protected String SIZE_MAX = "SIZE_MAX";

	public ParticleSystem(PImage[] particleImages) {
		this.particleImages = particleImages;
	}

	public void enableUI(String prefix, boolean saves) {
		usingUI = true;
		MAX_ATTEMPTS = prefix + MAX_ATTEMPTS;
		MAX_LAUNCHES = prefix + MAX_LAUNCHES;
		POOL_MAX_SIZE = prefix + POOL_MAX_SIZE;
		SPEED_X_MIN = prefix + SPEED_X_MIN;
		SPEED_X_MAX = prefix + SPEED_X_MAX;
		SPEED_Y_MIN = prefix + SPEED_Y_MIN;
		SPEED_Y_MAX = prefix + SPEED_Y_MAX;
		GRAVITY_X_MIN = prefix + GRAVITY_X_MIN;
		GRAVITY_X_MAX = prefix + GRAVITY_X_MAX;
		GRAVITY_Y_MIN = prefix + GRAVITY_Y_MIN;
		GRAVITY_Y_MAX = prefix + GRAVITY_Y_MAX;
		LIFESPAN_MIN = prefix + LIFESPAN_MIN;
		LIFESPAN_MAX = prefix + LIFESPAN_MAX;
		ROTATION_MIN = prefix + ROTATION_MIN;
		ROTATION_MAX = prefix + ROTATION_MAX;
		SIZE_MIN = prefix + SIZE_MIN;
		SIZE_MAX = prefix + SIZE_MAX;
		
		UI.addSlider(MAX_ATTEMPTS, 2000, 10, 5000, 5, saves);
		UI.addSlider(MAX_LAUNCHES, 10, 1, 100, 1, saves);
		UI.addSlider(POOL_MAX_SIZE, 10000, 10, 20000, 1, saves);
		UI.addSlider(SPEED_X_MIN, -1f, -5f, 0, 0.01f, saves);
		UI.addSlider(SPEED_X_MAX, 1f, 0, 5, 0.01f, saves);
		UI.addSlider(SPEED_Y_MIN, -1f, -5f, 0, 0.01f, saves);
		UI.addSlider(SPEED_Y_MAX, 1f, 0, 5, 0.01f, saves);
		UI.addSlider(GRAVITY_X_MIN, -0.01f, -0.5f, 0, 0.001f, saves);
		UI.addSlider(GRAVITY_X_MAX, 0.01f, 0, 0.5f, 0.001f, saves);
		UI.addSlider(GRAVITY_Y_MIN, -0.01f, -0.5f, 0, 0.001f, saves);
		UI.addSlider(GRAVITY_Y_MAX, 0.01f, 0, 0.5f, 0.001f, saves);
		UI.addSlider(LIFESPAN_MIN, 10, 10, 50, 1, saves);
		UI.addSlider(LIFESPAN_MAX, 50, 10, 200, 1, saves);
		UI.addSlider(ROTATION_MIN, -0.1f, -1, 0, 0.001f, saves);
		UI.addSlider(ROTATION_MAX, 0.1f, 0, 1f, 0.001f, saves);
		UI.addSlider(SIZE_MIN, 10, 1, 40, 0.1f, saves);
		UI.addSlider(SIZE_MAX, 40, 10, 200, 0.1f, saves);
	}

	public void drawParticles(PGraphics pg) {
		PG.setDrawCenter(pg);
		pg.blendMode(PBlendModes.ADD);
		for (int i = 0; i < particles.size(); i++) {
			particles.get(i).update(pg);
		}
		pg.blendMode(PBlendModes.BLEND);
		PG.setDrawCorner(pg);
	}

	public void launchParticles(PGraphics pg) {
		launchParticles(pg, 1f);
	}
	
	public void launchParticles(PGraphics pg, float destScale) {
		int numLaunched = 0;
		int maxAttempts = (usingUI) ? UI.valueInt(MAX_ATTEMPTS) : MAX_MAP_ATTEMPTS_PER_FRAME;
		int maxLaunches = (usingUI) ? UI.valueInt(MAX_LAUNCHES) : MAX_LAUNCHES_PER_FRAME;
		for (int i = 0; i < maxAttempts; i++) {
			int checkX = MathUtil.randRange(0, pg.width);
			int checkY = MathUtil.randRange(0, pg.height);
			int pixelColor = ImageUtil.getPixelColor(pg, checkX, checkY);
			float redColor = (float) ColorUtil.redFromColorInt(pixelColor) / 255f;
			if(redColor > 0.5f && numLaunched < maxLaunches) {
				launchParticle(pg, checkX * destScale, checkY * destScale);
				numLaunched++;
			}
		}
	}

	protected void launchParticle(PGraphics pg, float x, float y) {
		// look for an available shape
		for (int i = 0; i < particles.size(); i++) {
			if(particles.get(i).available(pg)) {
				launch(pg, particles.get(i), x, y);
				return;
			}
		}
		// didn't find one
		int maxPoolSize = (usingUI) ? UI.valueInt(POOL_MAX_SIZE) : PARTICLE_POOL_MAX_SIZE;
		if(particles.size() < maxPoolSize) {
			Particle2d newShape = initNewParticle();
			launch(pg, newShape, x, y);
			particles.add(newShape);
		}
	}
	
	protected Particle2d initNewParticle() {
		return new Particle2d();
	}

	protected void launch(PGraphics pg, Particle2d shape, float x, float y) {
		if(usingUI) {
			shape
				.setSpeed(UI.value(SPEED_X_MIN), UI.value(SPEED_X_MAX), UI.value(SPEED_Y_MIN), UI.value(SPEED_Y_MAX))
				.setGravity(UI.value(GRAVITY_X_MIN), UI.value(GRAVITY_X_MAX), UI.value(GRAVITY_Y_MIN), UI.value(GRAVITY_Y_MAX))
				.setLifespan(UI.value(LIFESPAN_MIN), UI.value(LIFESPAN_MAX))
				.setRotation(UI.value(ROTATION_MIN), UI.value(ROTATION_MAX))
				.setSize(UI.value(SIZE_MIN), UI.value(SIZE_MAX))
				.setColor(P.p.color(P.p.random(200, 255), P.p.random(200, 255), P.p.random(200, 255)))
				.launch(pg, x, y, randomImg());
		} else {
			shape
				.setSpeed(-1f, 1f, -1f, 0.5f)
				.setGravity(0, 0, 0f, -0.05f)
				.setLifespan(10, 50)
				.setRotation(-0.1f, 0.1f)
				.setSize(10, 40)
				.setColor(P.p.color(P.p.random(200, 255), P.p.random(200, 255), P.p.random(200, 255)))
				.launch(pg, x, y, randomImg());
		}
	}

	protected PImage randomImg() {
		return particleImages[MathUtil.randRange(0, particleImages.length - 1)];
	}

}
