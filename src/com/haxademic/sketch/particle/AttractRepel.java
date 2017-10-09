package com.haxademic.sketch.particle;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.math.MathUtil;

import processing.core.PVector;

public class AttractRepel
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	 
	
	// code ported from https://codepen.io/ashrewdmint/pen/PwJqwy
	
	protected Particle[] particles;
	int maxCircles = 100; // maximum amount of circles on the screen

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 500 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 500 );
		p.appConfig.setProperty( AppSettings.RETINA, false );
	}

	public void setup() {
		super.setup();	
		particles = new Particle[maxCircles];
		for (int i = 0; i < particles.length; i++) {
			particles[i] = new Particle();
		}
	}

	public void drawApp() {
		background(0);
		
		// replace a particle every once in a while
		if(p.frameCount % 20 == 0) particles[MathUtil.randRange(0, maxCircles - 1)] = new Particle();

		// Apply gravity
		for (int i = 0; i < particles.length; i++) {
			
			Particle c = particles[i];
			PVector cv = c.position.copy();
			PVector vel = c.velocity.copy();
			
			for (int j = 0; j < particles.length; j++) {
				
				if(i != j) {	// don't compare self
					
					Particle c2 = particles[j];
					PVector c2v = c2.position.copy();
					PVector los = c2v.copy().sub(cv).normalize(); // "Line of Sight" from c to c2
					float distSq = P.pow(cv.dist(c2v), 2);
					float g = 100f;
					float attract = (1f/distSq)*g;
					float force = P.min(attract, 1f);
					
					vel.add(los.mult(force));
				}
			}
			
			c.velocity.set(vel);
		}
		
		// Apply repulsion
		for (int i = 0; i < particles.length; i++) {
			
			Particle c = particles[i];
			PVector cv = c.position.copy();
			PVector vel = c.velocity.copy();
			float horizon = c.radius + 100f;
			float horzSq  = P.pow(horizon, 2);
			float cushion = horizon * 1.5f;
			float cushSq = P.pow(cushion, 2);
			
			PVector fut = cv.copy().add(vel); // Future position
			
			for (int j = 0; j < particles.length; j++) {
				
				if(i != j) {	// don't compare self
					
					Particle c2 = particles[j];
					PVector c2v = c2.position.copy();
					PVector los = c2v.copy().sub(fut);
					float c2RadSq = P.pow(c2.radius, 2);
					float futDistanceSq = P.pow(fut.dist(c2v), 2) - c2RadSq;

					if (futDistanceSq < cushSq) {
						// TODO some math to figure out if I can do this without sqrt()
						// var ratio = (futDistanceSq-horzSq)/(cushSq-horzSq);
						float intersect = P.sqrt(cushSq - futDistanceSq);
						PVector push = los.normalize().mult(intersect * 0.1f);
						vel.sub(push);
					}
				}
			}
			
			c.velocity.set(vel);
		}

		// Move circles
		PVector middle = new PVector(p.width/2, p.height/2); 
		for (int i = 0; i < particles.length; i++) {
			Particle c = particles[i];
			
			PVector vel = c.velocity.copy();
			vel.sub(c.position.copy().sub(middle)).normalize();
			vel.mult(4.1f); // 0.3
			vel.mult(1f - 0.3f); // friction
			c.velocity.set(vel);
			
			c.position.add(c.velocity);
		}

		
		// Draw circles
		DrawUtil.setDrawCenter(p);
		for (int i = 0; i < particles.length; i++) {
			Particle c = particles[i];
			p.ellipse(c.position.x, c.position.y, c.radius/2, c.radius/2);
		}
		
	}

	public void keyPressed() {
		if(key == ' ') { }
	}
	
	public class Particle {
		
		public PVector position;
		public PVector velocity;
		public float radius;
		
		public Particle() {
			radius = MathUtil.randRange(20, 50);
			position = new PVector(p.random(p.width), p.random(p.height));
			velocity = new PVector();
		}
		
	}
}
