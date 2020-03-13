package com.haxademic.core.draw.particle;

import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class Particle2d {
	
	protected PVector pos = new PVector(0, 0, 0);
	protected PVector speed = new PVector(0, 0, 0);
	protected PVector speedMin = new PVector(0, 0);
	protected PVector speedMax = new PVector(0, 0);

	protected PVector gravity = new PVector(0, 0, 0);
	protected PVector gravityMin = new PVector(0, 0);
	protected PVector gravityMax = new PVector(0, 0);
	
	protected float size = 10f;
	protected float sizeMin = 10f;
	protected float sizeMax = 10f;
	
	protected float rotationMin = 0f;
	protected float rotationMax = 0f;
	
	protected float lifespan = 60;
	protected float lifespanMin = 60;
	protected float lifespanMax = 60;
	protected LinearFloat sizeProgress = new LinearFloat(0, 1f/lifespan);
	
	protected int color = 0xffffffff;
	protected PImage image;
	
	public Particle2d() {}
	
	// Random range setters
	
	public Particle2d setSize(float sizeMin, float sizeMax) {
		this.sizeMin = sizeMin;
		this.sizeMax = sizeMax;
		return this;
	}
	
	public Particle2d setLifespan(float lifespanMin, float lifespanMax) {
		this.lifespanMin = lifespanMin; 
		this.lifespanMax = lifespanMax; 
		return this;
	}
	
	public Particle2d setRotation(float rotationMin, float rotationMax) {
		this.rotationMin = rotationMin;
		this.rotationMax = rotationMax;
		return this;
	}
	
	public Particle2d setSpeed(float speedMinX, float speedMaxX, float speedMinY, float speedMaxY) {
		this.speedMin.set(speedMinX, speedMinY);
		this.speedMax.set(speedMaxX, speedMaxY);
		return this;
	}
	
	public Particle2d setGravity(float gravityMinX, float gravityMaxX, float gravityMinY, float gravityMaxY) {
		this.gravityMin.set(gravityMinX, gravityMinY);
		this.gravityMax.set(gravityMaxX, gravityMaxY);
		return this;
	}
	
	public Particle2d setColor(int color) {
		this.color = color;
		return this;
	}
	
	// Launch!
	
	public Particle2d launch(PGraphics pg, float x, float y, PImage img) {
		// get random particle texture
		image = img;
		
		// random params
		sizeProgress.setInc(1f/MathUtil.randRange(lifespanMin, lifespanMax));
		sizeProgress.setCurrent(0);
		sizeProgress.setTarget(1);
		size = MathUtil.randRangeDecimal(sizeMin, sizeMax);
		
		// set motion properties
		pos.set(x, y, 0);
		speed.set(
				MathUtil.randRangeDecimal(speedMin.x, speedMax.x), 
				MathUtil.randRangeDecimal(speedMin.y, speedMax.y), 
				MathUtil.randRangeDecimal(rotationMin, rotationMax));
		gravity.set(
				MathUtil.randRangeDecimal(gravityMin.x, gravityMax.x), 
				MathUtil.randRangeDecimal(gravityMin.y, gravityMax.y));
		
		return this;
	}
	
	// animate
	
	public void update(PGraphics pg) {
		if(available(pg)) return;
		
		// update position
		speed.add(gravity);
		pos.add(speed);
		
		// update size
		sizeProgress.update();
		float curSize = size * Penner.easeOutExpo(sizeProgress.value());
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
	
	public boolean available(PGraphics pg) {
		boolean finished = (sizeProgress.value() == 0 && sizeProgress.target() == 0);
		return finished; //  || pos.y < -100 || pos.x < -100 || pos.y > pg.height + 100 || pos.x > pg.height + 100;
	}
}

