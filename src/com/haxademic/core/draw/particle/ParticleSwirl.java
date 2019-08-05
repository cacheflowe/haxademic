package com.haxademic.core.draw.particle;

import com.haxademic.core.app.P;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;
import processing.core.PImage;

public class ParticleSwirl
extends Particle {
	
	protected float radius;
	protected float radians;
		
	public ParticleSwirl() {}
	
	// launch
	
	public Particle launch(PGraphics pg, float x, float y, PImage img) {
		super.launch(pg, x, y, img);
		
		// repurpose x/y as radius/rads
		radius = x;
		radians = y;
		
		// chill out the reporposed values
		gravity.div(10f);
		speed.x = speed.x / 30f;
		speed.y = speed.y;
		
		return this;
	}
	
	// animate
	
	public void update(PGraphics pg) {
		if(available(pg)) return;
		
		// update position
		radians += speed.x;
		radius += speed.y;
		speed.add(gravity);
		
		pos.set(
				pg.width/2 + P.cos(radians) * radius, 
				pg.height/2 + P.sin(radians) * radius, 
				pos.z + speed.z	// rotation
				);
		
		// update size
		sizeProgress.update();
		float curSize = size * Penner.easeOutExpo(sizeProgress.value(), 0, 1, 1);
		if(sizeProgress.value() == 1) sizeProgress.setTarget(0);
		
		// draw image
		pg.pushMatrix();
		pg.translate(pos.x, pos.y);
		pg.rotate(pos.z);
		pg.tint(color);
		pg.image(image, 0, 0, curSize, curSize);
		pg.tint(255);
		pg.popMatrix();
	}
	
}

