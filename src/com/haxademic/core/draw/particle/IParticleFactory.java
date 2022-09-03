package com.haxademic.core.draw.particle;

public interface IParticleFactory {
	public Particle initNewParticle();
	public Particle randomize(Particle particle);
}
