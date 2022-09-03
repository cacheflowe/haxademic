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

public class ParticleSystem {

	// INFO
	// - By default, particles will launch with a random image texture
	//   - Extend/override Particle & `launch()` to draw custom particles
	//   - This also requires overriding the Particle System. There are multiple demos
	
	// TODO
	// - Fix 3d particles demo - how to extend for 3d and pass in PShape?
	// - Test existing ATT particles - make sure they're still good
	// - Load into Numbers experience & continue building there
	//   - Add dynamic mask for number/text
	//   - Add floor with Bryce's pattern
	//   - Spotlight/shadow
	// - Later:
	//   - Billboard shader?
	//   - Cached geometry? move particles with PShape.translate() ? Or vertex shader attributes?
	//   - Fix ParticleSystemCustom & ParticleSystemSwirl - especially the case of not using a UI, but also rotation speed is bad
	
	// particles & source textures
	protected ArrayList<Particle> particles = new ArrayList<Particle>();
	protected IParticleFactory particleFactory;
	protected int activeParticles = 0;

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
	protected String ACCELERATION = "ACCELERATION";
	protected String LIFESPAN_MIN = "LIFESPAN_MIN";
	protected String LIFESPAN_MAX = "LIFESPAN_MAX";
	protected String LIFESPAN_SUSTAIN_MIN = "LIFESPAN_SUSTAIN_MIN";
	protected String LIFESPAN_SUSTAIN_MAX = "LIFESPAN_SUSTAIN_MAX";
	protected String SIZE_MIN = "SIZE_MIN";
	protected String SIZE_MAX = "SIZE_MAX";
	protected String SPEED_MIN = "SPEED_MIN";
	protected String SPEED_MAX = "SPEED_MAX";
	protected String GRAVITY_MIN = "GRAVITY_MIN";
	protected String GRAVITY_MAX = "GRAVITY_MAX";
	protected String ROTATION_MIN = "ROTATION_MIN";
	protected String ROTATION_MAX = "ROTATION_MAX";
	protected String ROTATION_SPEED_MIN = "ROTATION_SPEED_MIN";
	protected String ROTATION_SPEED_MAX = "ROTATION_SPEED_MAX";

	public ParticleSystem() {
		particleFactory = new ParticleFactory();
	}

	public ParticleSystem(IParticleFactory particleFactory) {
		this.particleFactory = particleFactory;
	}
	
	public void enableUI(String prefix, boolean saves) {
		usingUI = true;
		MAX_ATTEMPTS = prefix + MAX_ATTEMPTS;
		MAX_LAUNCHES = prefix + MAX_LAUNCHES;
		POOL_MAX_SIZE = prefix + POOL_MAX_SIZE;
		ACCELERATION = prefix + ACCELERATION;
		LIFESPAN_MIN = prefix + LIFESPAN_MIN;
		LIFESPAN_MAX = prefix + LIFESPAN_MAX;
		LIFESPAN_SUSTAIN_MIN = prefix + LIFESPAN_SUSTAIN_MIN;
		LIFESPAN_SUSTAIN_MAX = prefix + LIFESPAN_SUSTAIN_MAX;
		SIZE_MIN = prefix + SIZE_MIN;
		SIZE_MAX = prefix + SIZE_MAX;
		SPEED_MIN = prefix + SPEED_MIN;
		SPEED_MAX = prefix + SPEED_MAX;
		GRAVITY_MIN = prefix + GRAVITY_MIN;
		GRAVITY_MAX = prefix + GRAVITY_MAX;
		ROTATION_MIN = prefix + ROTATION_MIN;
		ROTATION_MAX = prefix + ROTATION_MAX;
		ROTATION_SPEED_MIN = prefix + ROTATION_SPEED_MIN;
		ROTATION_SPEED_MAX = prefix + ROTATION_SPEED_MAX;
		
		UI.addSlider(MAX_ATTEMPTS, 2000, 10, 5000, 5, saves);
		UI.addSlider(MAX_LAUNCHES, 10, 1, 100, 1, saves);
		UI.addSlider(POOL_MAX_SIZE, 10000, 10, 20000, 1, saves);
		UI.addSlider(LIFESPAN_MIN, 10, 10, 50, 1, saves);
		UI.addSlider(LIFESPAN_MAX, 50, 10, 200, 1, saves);
		UI.addSlider(LIFESPAN_SUSTAIN_MIN, 0, 0, 200, 1, saves);
		UI.addSlider(LIFESPAN_SUSTAIN_MAX, 0, 0, 500, 1, saves);
		UI.addSlider(SIZE_MIN, 10, 1, 40, 0.1f, saves);
		UI.addSlider(SIZE_MAX, 40, 10, 200, 0.1f, saves);
		UI.addSliderVector(ACCELERATION, 1, 0.8f, 1.2f, 0.001f, false);
		UI.addSliderVector(SPEED_MIN, -1, -5, 5, 0.01f, false);
		UI.addSliderVector(SPEED_MAX,  1, -5, 5, 0.01f, false);
		UI.addSliderVector(GRAVITY_MIN, -0.01f, -0.5f, 0.5f, 0.001f, false);
		UI.addSliderVector(GRAVITY_MAX,  0.01f, -0.5f, 0.5f, 0.001f, false);
		UI.addSliderVector(ROTATION_MIN, 0, -P.PI, P.PI, 0.01f, false);
		UI.addSliderVector(ROTATION_MAX, 0, -P.PI, P.PI, 0.01f, false);
		UI.addSliderVector(ROTATION_SPEED_MIN, 0, -0.1f, 0.1f, 0.001f, false);
		UI.addSliderVector(ROTATION_SPEED_MAX, 0, -0.1f, 0.1f, 0.001f, false);
	}
	
	public int poolSize() {
		return particles.size();
	}
	
	public int poolActiveSize() {
		return activeParticles;
	}
	
	public void drawParticles(PGraphics pg) {
		drawParticles(pg, PBlendModes.ADD);
	}
	
	public void drawParticles(PGraphics pg, int blendMode) {
		activeParticles = 0;
		PG.setDrawCenter(pg);
		pg.blendMode(blendMode);
		for (int i = 0; i < particles.size(); i++) {
			particles.get(i).update(pg);
			if(particles.get(i).available() == false) activeParticles++;
		}
		pg.blendMode(PBlendModes.BLEND);
		PG.setDrawCorner(pg);
	}

	public void launchParticlesFromMap(PGraphics pg) {
		launchParticlesFromMap(pg, 1f);
	}
	
	public void launchParticlesFromMap(PGraphics pg, float destScale) {
		int numLaunched = 0;
		int maxAttempts = (usingUI) ? UI.valueInt(MAX_ATTEMPTS) : MAX_MAP_ATTEMPTS_PER_FRAME;
		int maxLaunches = (usingUI) ? UI.valueInt(MAX_LAUNCHES) : MAX_LAUNCHES_PER_FRAME;
		for (int i = 0; i < maxAttempts; i++) {
			int checkX = MathUtil.randRange(0, pg.width);
			int checkY = MathUtil.randRange(0, pg.height);
			int pixelColor = ImageUtil.getPixelColor(pg, checkX, checkY);
			float redColor = (float) ColorUtil.redFromColorInt(pixelColor) / 255f;
			if(redColor > 0.5f && numLaunched < maxLaunches) {
				launchParticle(checkX * destScale, checkY * destScale, 0);
				numLaunched++;
			}
		}
	}

	public Particle launchParticle(float x, float y, float z) {
		// look for an available shape
		for (int i = 0; i < particles.size(); i++) {
			if(particles.get(i).available()) {
				launch(particles.get(i), x, y, z);
				return particles.get(i);
			}
		}
		// didn't find one
		int maxPoolSize = (usingUI) ? UI.valueInt(POOL_MAX_SIZE) : PARTICLE_POOL_MAX_SIZE;
		if(particles.size() < maxPoolSize) {
			Particle particle = particleFactory.initNewParticle();
			launch(particle, x, y, z);
			particles.add(particle);
			return particle;
		}
		return null;
	}
	
	protected void launch(Particle particle, float x, float y, float z) {
		if(usingUI) {
			particle
				.setSpeedRange(UI.valueX(SPEED_MIN), UI.valueX(SPEED_MAX), UI.valueY(SPEED_MIN), UI.valueY(SPEED_MAX), UI.valueZ(SPEED_MIN), UI.valueZ(SPEED_MAX))
				.setAcceleration(UI.valueX(ACCELERATION), UI.valueY(ACCELERATION), UI.valueZ(ACCELERATION))
				.setGravityRange(UI.valueX(GRAVITY_MIN), UI.valueX(GRAVITY_MAX), UI.valueY(GRAVITY_MIN), UI.valueY(GRAVITY_MAX), UI.valueZ(GRAVITY_MIN), UI.valueZ(GRAVITY_MAX))
				.setRotationRange(UI.valueX(ROTATION_MIN), UI.valueX(ROTATION_MAX), UI.valueY(ROTATION_MIN), UI.valueY(ROTATION_MAX), UI.valueZ(ROTATION_MIN), UI.valueZ(ROTATION_MAX))
				.setRotationSpeedRange(UI.valueX(ROTATION_SPEED_MIN), UI.valueX(ROTATION_SPEED_MAX), UI.valueY(ROTATION_SPEED_MIN), UI.valueY(ROTATION_SPEED_MAX), UI.valueZ(ROTATION_SPEED_MIN), UI.valueZ(ROTATION_SPEED_MAX))
				.setLifespanRange(UI.value(LIFESPAN_MIN), UI.value(LIFESPAN_MAX))
				.setLifespanSustainRange(UI.value(LIFESPAN_SUSTAIN_MIN), UI.value(LIFESPAN_SUSTAIN_MAX))
				.setSizeRange(UI.value(SIZE_MIN), UI.value(SIZE_MAX))
				.setColor(P.p.color(P.p.random(200, 255), P.p.random(200, 255), P.p.random(200, 255)))
				.launch(x, y, z);
		} else {
			particle
				.setSpeedRange(-1f, 1f, -1f, 0.5f, -1f, 1f)
				.setAcceleration(1)
				.setGravityRange(0, 0, 0f, -0.05f, 0, 0)
				.setRotationRange(0, 0, 0, 0, -0.9f, 0.9f)
				.setRotationSpeed(0, 0, 0)
				.setLifespanRange(10, 50)
				.setLifespanSustain(0)
				.setSizeRange(10, 40)
				.setColor(P.p.color(P.p.random(200, 255), P.p.random(200, 255), P.p.random(200, 255)))
				.launch(x, y, z);
		}
	}

}
