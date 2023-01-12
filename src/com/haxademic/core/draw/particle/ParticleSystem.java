package com.haxademic.core.draw.particle;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;

public class ParticleSystem {

	// INFO
	// - By default, particles will launch with a random image texture
	//   - You can extend/override ParticleSystem, Particle & ParticleLauncher to draw custom particles
	//   - There are multiple demos for this
	
	// TODO
	// - Multiple ParticleLaunchers within a ParticleSystem? This is problematic for recycling different types of particles...
	// - Should all of the randomized launch params be in ParticleLauncher, and not ParticleSystem... Probably!
	// - Billboard shader?
	// - Cached geometry? move particles with PShape.translate() ? Or vertex shader attributes?
	// - Look at making looping particle launches easy - WashYourHands demo has the code
	
	
	// particles & source textures
	protected ArrayList<Particle> particles = new ArrayList<Particle>();
	protected IParticleFactory particleFactory;
	protected int activeParticles = 0;

	// config
	protected int MAX_MAP_ATTEMPTS_PER_FRAME = 2000;
	protected int MAX_LAUNCHES_PER_FRAME = 10;
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
		UI.addSliderVector(ACCELERATION, 1, 0.8f, 1.2f, 0.001f, saves);
		UI.addSliderVector(SPEED_MIN, -1, -5, 5, 0.01f, saves);
		UI.addSliderVector(SPEED_MAX,  1, -5, 5, 0.01f, saves);
		UI.addSliderVector(GRAVITY_MIN, 0, -0.5f, 0.5f, 0.001f, saves);
		UI.addSliderVector(GRAVITY_MAX,  0, -0.5f, 0.5f, 0.001f, saves);
		UI.addSliderVector(ROTATION_MIN, 0, -P.PI, P.PI, 0.01f, saves);
		UI.addSliderVector(ROTATION_MAX, 0, -P.PI, P.PI, 0.01f, saves);
		UI.addSliderVector(ROTATION_SPEED_MIN, 0, -0.1f, 0.1f, 0.001f, saves);
		UI.addSliderVector(ROTATION_SPEED_MAX, 0, -0.1f, 0.1f, 0.001f, saves);
	}
	
	public int poolSize() {
		return particles.size();
	}
	
	public int poolActiveSize() {
		return activeParticles;
	}
	
	// particle factory
	
	public ParticleSystem setParticleFactory(IParticleFactory particleFactory) {
		this.particleFactory = particleFactory;
		return this;
	}
	
	/////////////////////////////////////////////////////////////
	// update & draw
	/////////////////////////////////////////////////////////////
	
	// update and draw independently so we can update once and draw to multiple buffers
	
	public void updateParticles() {
		activeParticles = 0;
		for (int i = 0; i < particles.size(); i++) {
			particles.get(i).update();
			if(particles.get(i).available() == false) activeParticles++;
		}
	}
	
	public void drawParticles(PGraphics pg) {
		drawParticles(pg, PBlendModes.BLEND);
	}
	
	public void drawParticles(PGraphics pg, int blendMode) {
		pg.blendMode(blendMode);
		for (int i = 0; i < particles.size(); i++) {
			particles.get(i).draw(pg);
		}
		pg.blendMode(PBlendModes.BLEND);
	}

	// both update and draw if we're only using a single buffer for output
	
	public void updateAndDrawParticles(PGraphics pg) {
		updateAndDrawParticles(pg, PBlendModes.BLEND);
	}
	
	public void updateAndDrawParticles(PGraphics pg, int blendMode) {
		activeParticles = 0;
		pg.blendMode(blendMode);
		for (int i = 0; i < particles.size(); i++) {
			particles.get(i).updateAndDraw(pg);
			if(particles.get(i).available() == false) activeParticles++;
		}
		pg.blendMode(PBlendModes.BLEND);
	}
	
	// special actions
	
	public void killAll() {
        for (int i = 0; i < particles.size(); i++) {
            particles.get(i).kill();
        }
	}
	
	/////////////////////////////////////////////////////////////
	// Init/recycle particles from the pool, and launch them! 
	/////////////////////////////////////////////////////////////

	public Particle launchParticle(float x, float y, float z) {
		// look for an available shape
		for (int i = 0; i < particles.size(); i++) {
			if(particles.get(i).available()) {
				Particle particle = particles.get(i);
				randomize(particle);
				particle.launch(x, y, z);
				return particle;
			}
		}
		// didn't find one
		Particle particle = particleFactory.initNewParticle();
		randomize(particle);
		particle.launch(x, y, z);
		particles.add(particle);
		return particle;
	}
	
	/////////////////////////////////////////////////////////////
	// Launch partciles from a map
	// TODO:
	// - Lazy-init UI controls *if* we use the map-launching functions?
	/////////////////////////////////////////////////////////////
	
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
	
	protected void randomize(Particle particle) {
		// randomize anything on the ParticleFactory side,
		// like distributing a collection of media to each particle
		// that was loaded in the factory
		particleFactory.randomize(particle);
		
		// then randomize any other params per launch
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
				.setColor(P.p.color(P.p.random(100, 255), P.p.random(100, 255), P.p.random(100, 255)));
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
				.setColor(P.p.color(P.p.random(100, 255), P.p.random(100, 255), P.p.random(100, 255)));
		}
	}

}
