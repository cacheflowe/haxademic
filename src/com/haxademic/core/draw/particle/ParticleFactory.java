// package com.haxademic.core.draw.particle;

// import com.haxademic.core.math.MathUtil;
// import com.haxademic.core.media.DemoAssets;

// import processing.core.PImage;

// public class ParticleFactory
// implements IParticleFactory {
	
// 	// ParticleFactory creates a good default particle, 
// 	// but can be overridden to pass custom Particle subclasses 
// 	// to a ParticleSystem instance, or a custom subclass of 
// 	// ParticleSystem.
// 	//
// 	// ParticleFactory can load & hold assets needed to pass 
// 	// media to Particle objects upon instantiation
	
// 	protected PImage[] particleImages;
	
// 	public ParticleFactory() {
// 		this(new PImage[] { DemoAssets.particle() });
// 	}
	
// 	public ParticleFactory(PImage[] particleImages) {
// 		this.particleImages = particleImages;
// 	}
	
// 	public Particle initNewParticle() {
// 		return new Particle(randomImg());
// 	}
	
// 	public PImage randomImg() {
// 		return particleImages[MathUtil.randRange(0, particleImages.length - 1)];
// 	}
	
// 	public Particle randomize(Particle particle) {
// 		particle.setImage(randomImg());
// 		return particle;
// 	}

// }
