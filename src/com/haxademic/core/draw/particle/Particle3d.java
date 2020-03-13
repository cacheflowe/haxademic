package com.haxademic.core.draw.particle;

import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;

public class Particle3d {
	
	protected PVector pos = new PVector(0, 0, 0);
	protected PVector speed = new PVector(0, 0, 0);
	protected PVector speedMin = new PVector(0, 0);
	protected PVector speedMax = new PVector(0, 0);

	protected float acceleration = 1;

	protected PVector gravity = new PVector(0, 0, 0);
	protected PVector gravityMin = new PVector(0, 0);
	protected PVector gravityMax = new PVector(0, 0);
	
	protected float size = 10f;
	protected float sizeMin = 10f;
	protected float sizeMax = 10f;
	
	protected PVector rotation = new PVector(0, 0, 0);
	protected PVector rotationSpeed = new PVector(0, 0, 0);
	
	protected float lifespan = 60;
	protected float lifespanMin = 60;
	protected float lifespanMax = 60;
	protected LinearFloat sizeProgress = new LinearFloat(0, 1f/lifespan);
	
	protected int color = 0xffffffff;
	protected boolean isSphere = false;
	protected PShape customShape;
	
	public Particle3d() {}
	
	// Random range setters
	
	public Particle3d setSize(float sizeMin, float sizeMax) {
		this.sizeMin = sizeMin;
		this.sizeMax = sizeMax;
		return this;
	}
	
	public Particle3d setLifespan(float lifespanMin, float lifespanMax) {
		this.lifespanMin = lifespanMin; 
		this.lifespanMax = lifespanMax; 
		return this;
	}
	
	public Particle3d setRotation(float rotX, float rotY, float rotZ, float rotSpeedX, float rotSpeedY, float rotSpeedZ) {
		this.rotation.set(rotX, rotY, rotZ);
		this.rotationSpeed.set(rotSpeedX, rotSpeedY, rotSpeedZ);
		return this;
	}
	
	public Particle3d setSpeed(float speedMinX, float speedMaxX, float speedMinY, float speedMaxY, float speedMinZ, float speedMaxZ) {
		this.speedMin.set(speedMinX, speedMinY, speedMinZ);
		this.speedMax.set(speedMaxX, speedMaxY, speedMaxZ);
		return this;
	}
	
	public Particle3d setGravity(float gravityMinX, float gravityMaxX, float gravityMinY, float gravityMaxY, float gravityMinZ, float gravityMaxZ) {
		this.gravityMin.set(gravityMinX, gravityMinY, gravityMinZ);
		this.gravityMax.set(gravityMaxX, gravityMaxY, gravityMaxZ);
		return this;
	}
	
	public Particle3d setAcceleration(float acceleration) {
		this.acceleration = acceleration;
		return this;
	}
	
	public Particle3d setColor(int color) {
		this.color = color;
		return this;
	}
	
	public Particle3d setShape(boolean sphere) {
		isSphere = sphere;
		customShape = null;
		return this;
	}
	
	public Particle3d setShape(PShape shape) {
		customShape = shape;
		if(customShape == null) isSphere = false;
		return this;
	}
	
	// Launch!
	
	public Particle3d launch(float x, float y, float z) {
		// random params
		sizeProgress.setInc(1f/MathUtil.randRange(lifespanMin, lifespanMax));
		sizeProgress.setCurrent(0);
		sizeProgress.setTarget(1);
		size = MathUtil.randRangeDecimal(sizeMin, sizeMax);
		
		// set motion properties
		pos.set(x, y, z);
		speed.set(
				MathUtil.randRangeDecimal(speedMin.x, speedMax.x), 
				MathUtil.randRangeDecimal(speedMin.y, speedMax.y), 
				MathUtil.randRangeDecimal(speedMin.z, speedMax.z));
		gravity.set(
				MathUtil.randRangeDecimal(gravityMin.x, gravityMax.x), 
				MathUtil.randRangeDecimal(gravityMin.y, gravityMax.y),
				MathUtil.randRangeDecimal(gravityMin.z, gravityMax.z));
		
		return this;
	}
	
	// animate
	
	public void update(PGraphics pg) {
		if(available()) return;
		
		// update position
		if(gravity.mag() > 0) speed.add(gravity);
		if(acceleration != 1) speed.mult(acceleration);
		pos.add(speed);
		rotation.add(rotationSpeed);
		
		// update size
		sizeProgress.update();
		float curSize = size * Penner.easeOutSine(sizeProgress.value());
		if(sizeProgress.value() == 1) sizeProgress.setTarget(0);
		
		// draw image
		pg.pushMatrix();
		pg.translate(pos.x, pos.y, pos.z);
		pg.rotateX(rotation.x);
		pg.rotateY(rotation.y);
		pg.rotateZ(rotation.z);
		pg.fill(color);
		if(customShape != null) {
			pg.pushMatrix();
			pg.scale(Penner.easeOutExpo(sizeProgress.value()) * curSize / customShape.getHeight());
			pg.shape(customShape);
			pg.popMatrix();
		} else {
			if(isSphere) {
				pg.sphere(curSize/2f);
			} else {
				pg.box(curSize, curSize, curSize);
			}
		}
		pg.fill(255);
		pg.popMatrix();
	}
	
	public boolean available() {
		return (sizeProgress.value() == 0 && sizeProgress.target() == 0);
	}
}

