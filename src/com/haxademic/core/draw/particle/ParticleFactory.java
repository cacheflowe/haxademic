package com.haxademic.core.draw.particle;

import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PImage;

public class ParticleFactory
implements IParticleFactory {
	
	protected PImage[] particleImages;
	
	public ParticleFactory() {
		this(new PImage[] { DemoAssets.particle() });
	}
	
	public ParticleFactory(PImage[] particleImages) {
		this.particleImages = particleImages;
	}
	
	public PImage randomImg() {
		return particleImages[MathUtil.randRange(0, particleImages.length - 1)];
	}
	
	public Particle randomize(Particle particle) {
		particle.setImage(randomImg());
		return particle;
	}
	
	public Particle initNewParticle() {
		return new Particle(randomImg());
	}

}
