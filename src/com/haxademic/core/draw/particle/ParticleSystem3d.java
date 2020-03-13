package com.haxademic.core.draw.particle;

import java.util.ArrayList;

import processing.core.PGraphics;

public class ParticleSystem3d {

	// particles & source textures
	protected ArrayList<Particle3d> particles = new ArrayList<Particle3d>();

	public ParticleSystem3d() {
	}
	
	/////////////////
	// Draw
	/////////////////

	public void drawParticles(PGraphics pg) {
		for (int i = 0; i < particles.size(); i++) {
			particles.get(i).update(pg);
		}
	}
	
	/////////////////
	// Pool
	/////////////////

	public Particle3d launch() {
		// look for an available shape
		for (int i = 0; i < particles.size(); i++) {
			if(particles.get(i).available()) {
				return particles.get(i);
			}
		}
		// didn't find one
		Particle3d newShape = initNewParticle();
		particles.add(newShape);
		return newShape;
	}
	
	protected Particle3d initNewParticle() {
		return new Particle3d();
	}
	
	public int poolSize() {
		return particles.size();
	}
	
}
