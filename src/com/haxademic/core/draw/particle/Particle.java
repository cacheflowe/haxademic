package com.haxademic.core.draw.particle;

import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

public class Particle {
	
	protected PVector pos = new PVector();
	protected PVector acceleration = new PVector(1f, 1f, 1f);
	protected PVector speed = new PVector();
	protected PVector speedMin = new PVector();
	protected PVector speedMax = new PVector();

	protected PVector gravity = new PVector();
	protected PVector gravityMin = new PVector();
	protected PVector gravityMax = new PVector();
	
	protected float size = 10f;
	protected float sizeMin = 10f;
	protected float sizeMax = 10f;
	
	protected PVector rotation = new PVector();
	protected PVector rotationMin = new PVector();
	protected PVector rotationMax = new PVector();
	protected PVector rotationSpeed = new PVector();
	protected PVector rotationSpeedMin = new PVector();
	protected PVector rotationSpeedMax = new PVector();
	
	protected float lifespan = 60;
	protected float lifespanMin = 60;
	protected float lifespanMax = 60;
	protected LinearFloat lifespanProgress = new LinearFloat(0, 1f/lifespan);
	protected int lifespanSustain = 0;
	protected float lifespanSustainMin = 0;
	protected float lifespanSustainMax = 0;
	
	protected int color = 0xffffffff;
	protected PImage image;
	protected PShape shape;
	
	public Particle() {}
	
	public Particle(PImage image) {
		this.image = image;
	}
	
	public Particle(PShape shape) {
		this.shape = shape;
	}
	
	///////////////////////////////
	// Random range setters
	// We can set the property to be static, or follow by setting a range when launching
	///////////////////////////////
	
	public Particle setSize(float size) {
		this.size = size;
		setSizeRange(size, size);
		return this;
	}
	
	public Particle setSizeRange(float sizeMin, float sizeMax) {
		this.sizeMin = sizeMin;
		this.sizeMax = sizeMax;
		return this;
	}
	
	public Particle setLifespan(float lifespan) {
		this.lifespan = lifespan;
		setLifespanRange(lifespan, lifespan);
		return this;
	}
	
	public Particle setLifespanRange(float lifespanMin, float lifespanMax) {
		this.lifespanMin = lifespanMin; 
		this.lifespanMax = lifespanMax; 
		return this;
	}
	
	public Particle setLifespanSustain(int lifespanSustain) {
		this.lifespanSustain = lifespanSustain;
		setLifespanSustainRange(lifespanSustain, lifespanSustain);
		return this;
	}
	
	public Particle setLifespanSustainRange(float lifespanSustainMin, float lifespanSustainMax) {
		this.lifespanSustainMin = lifespanSustainMin; 
		this.lifespanSustainMax = lifespanSustainMax; 
		return this;
	}
	
	public Particle setRotation(float rotX, float rotY, float rotZ, float rotSpeedX, float rotSpeedY, float rotSpeedZ) {
		this.rotation.set(rotX, rotY, rotZ);
		setRotationRange(rotX, rotY, rotZ, rotX, rotY, rotZ);
		return this;
	}
	
	public Particle setRotationRange(float rotationMinX, float rotationMaxX, float rotationMinY, float rotationMaxY, float rotationMinZ, float rotationMaxZ) {
		this.rotationMin.set(rotationMinX, rotationMinY, rotationMinZ);
		this.rotationMax.set(rotationMaxX, rotationMaxY, rotationMaxZ);
		return this;
	}
	
	public Particle setRotationSpeed(float rotSpeedX, float rotSpeedY, float rotSpeedZ) {
		this.rotationSpeed.set(rotSpeedX, rotSpeedY, rotSpeedZ);
		setRotationSpeedRange(rotSpeedX, rotSpeedY, rotSpeedZ, rotSpeedX, rotSpeedY, rotSpeedZ);
		return this;
	}
	
	public Particle setRotationSpeedRange(float rotationSpeedMinX, float rotationSpeedMaxX, float rotationSpeedMinY, float rotationSpeedMaxY, float rotationSpeedMinZ, float rotationSpeedMaxZ) {
		this.rotationSpeedMin.set(rotationSpeedMinX, rotationSpeedMinY, rotationSpeedMinZ);
		this.rotationSpeedMax.set(rotationSpeedMaxX, rotationSpeedMaxY, rotationSpeedMaxZ);
		return this;
	}
	
	public Particle setSpeed(float speedX, float speedY, float speedZ) {
		this.speed.set(speedX, speedY, speedZ);
		setSpeedRange(speedX, speedY, speedZ, speedX, speedY, speedZ);
		return this;
	}
	
	public Particle setSpeedRange(float speedMinX, float speedMaxX, float speedMinY, float speedMaxY, float speedMinZ, float speedMaxZ) {
		this.speedMin.set(speedMinX, speedMinY, speedMinZ);
		this.speedMax.set(speedMaxX, speedMaxY, speedMaxZ);
		return this;
	}
	
	public Particle setGravity(float gravityX, float gravityY, float gravityZ) {
		this.gravity.set(gravityX, gravityY, gravityZ);
		setGravityRange(gravityX, gravityY, gravityZ, gravityX, gravityY, gravityZ);
		return this;
	}
	
	public Particle setGravityRange(float gravityMinX, float gravityMaxX, float gravityMinY, float gravityMaxY, float gravityMinZ, float gravityMaxZ) {
		this.gravityMin.set(gravityMinX, gravityMinY, gravityMinZ);
		this.gravityMax.set(gravityMaxX, gravityMaxY, gravityMaxZ);
		return this;
	}
	
	public Particle setAcceleration(float accel) {
		this.acceleration.set(accel, accel, accel);
		return this;
	}
	
	public Particle setAcceleration(float accelX, float accelY, float accelZ) {
		this.acceleration.set(accelX, accelY, accelZ);
		return this;
	}
	
	public Particle setColor(int color) {
		this.color = color;
		if(shape != null) {
			PShapeUtil.setBasicShapeStyles(shape, color, 0, 0);
		}
		return this;
	}
	
	public Particle setImage(PImage image) {
		this.image = image;
		return this;
	}
	
	public Particle setShape(PShape shape) {
		this.shape = shape;
		return this;
	}
	
	///////////////////////////////
	// getters
	///////////////////////////////
	
	public float size() { return size; }
	public int color() { return color; }
	public PImage image() { return image; }
	public PShape shape() { return shape; }
	public boolean available() {
		boolean finished = (lifespanProgress.value() == 0 && lifespanProgress.target() == 0);
		return finished;
	}
	
	///////////////////////////////
	// Launch!
	///////////////////////////////
	
	public Particle launch(float x, float y, float z) {
		// random params
		calcLifespan();
		size = MathUtil.randRangeDecimal(sizeMin, sizeMax);
		
		// set motion properties
		pos.set(x, y, z);
		setRandomVector(speed, speedMin, speedMax);
		setRandomVector(gravity, gravityMin, gravityMax);
		setRandomVector(rotation, rotationMin, rotationMax);
		setRandomVector(rotationSpeed, rotationSpeedMin, rotationSpeedMax);
		return this;
	}
	
	protected void calcLifespan() {
		// calculate frames for LinearFloat
		float lifespanInc = 1f / MathUtil.randRange(lifespanMin, lifespanMax);
		lifespanInc /= 2; // since we go up, then down w/LinearFloat
		lifespanProgress.setInc(lifespanInc);
		lifespanProgress.setCurrent(0);
		lifespanProgress.setTarget(1);
		// add a potential delay to stay at 1 before descending to 0
		lifespanSustain = MathUtil.randRange(lifespanSustainMin, lifespanSustainMax);
	}
	
	protected void setRandomVector(PVector property, PVector rangeMin, PVector rangeMax) {
		property.set(
			MathUtil.randRangeDecimal(rangeMin.x, rangeMax.x), 
			MathUtil.randRangeDecimal(rangeMin.y, rangeMax.y),
			MathUtil.randRangeDecimal(rangeMin.z, rangeMax.z)
		);
	}
	
	///////////////////////////////
	// animate
	///////////////////////////////
	
	protected void updatePosition() {
		// update position
		if(gravity.mag() > 0) speed.add(gravity);
		speed.set(
			speed.x * acceleration.x, 
			speed.y * acceleration.y, 
			speed.z * acceleration.z
		);

		pos.add(speed);
		rotation.add(rotationSpeed);
	}
	
	protected void updateLifespan() {
		lifespanProgress.update();
		// if(lifespanProgress.value() == 1) lifespanProgress.setTarget(0);
		if(lifespanProgress.value() == 1 && lifespanProgress.target() == 1) {
			lifespanProgress.setTarget(0);
			lifespanProgress.setDelay(lifespanSustain);
		}
	}
	
	protected void setContext(PGraphics pg) {
		pg.translate(pos.x, pos.y, pos.z);
		pg.rotateX(rotation.x);
		pg.rotateY(rotation.y);
		pg.rotateZ(rotation.z);
	}
	
	public void updateAndDraw(PGraphics pg) {
		update();
		draw(pg);
	}
	
	// allow for separation of update vs draw, so we could draw to multiple buffers
	public void update() {
		if(available()) return;
		updatePosition();
		updateLifespan();
	}
	
	public void draw(PGraphics pg) {
		if(available()) return;
		pg.push();
		setContext(pg);
		drawParticle(pg);
		pg.pop();
	}

	public void kill() {
	    lifespanProgress
	        .setDelay(0)
	        .setCurrent(0)
	        .setTarget(0);
	}
	
	///////////////////////////////
	// draw
	///////////////////////////////
	
	// basic textured particle
	// can be overridden for other types of shapes & drawing
	protected void drawParticle(PGraphics pg) {
		if(image != null) {
			float curSize = size * Penner.easeOutSine(lifespanProgress.value());
			pg.tint(color);
			pg.image(image, 0, 0, curSize, curSize);
		}
		if(shape != null) {
			float curSize = size * Penner.easeOutSine(lifespanProgress.value());
			pg.push();
			pg.scale(curSize);
			pg.noStroke();
			pg.shape(shape, 0, 0);
			pg.pop();
		}
	}
	
}

