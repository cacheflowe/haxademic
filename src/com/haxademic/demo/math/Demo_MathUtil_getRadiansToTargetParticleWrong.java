package com.haxademic.demo.math;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PConstants;
import processing.core.PVector;


public class Demo_MathUtil_getRadiansToTargetParticleWrong
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArrayList<VectorFlyer2d> particles;
	protected ArrayList<Attractor> attractors;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
	}

	public void setupFirstFrame() {
		particles = new ArrayList<VectorFlyer2d>();
		for(int i=0; i < 10000; i++) particles.add(new VectorFlyer2d(new PVector(p.random(p.width), p.random(p.height))));
		attractors = new ArrayList<Attractor>();
		for(int i=0; i < 5; i++) attractors.add(new Attractor(new PVector(p.width/2, p.height/2)));
	}

	public void drawApp() {
		p.background(127);
		PG.setDrawCenter(p);
		
		p.blendMode(PConstants.BLEND);
		for(int i=0; i < particles.size(); i++) {
			Attractor closestAttractor = getClosestAttractorToParticle(particles.get(i));
			particles.get(i).update(closestAttractor.position.x, closestAttractor.position.y);
		}
		for(int i=0; i < attractors.size(); i++) attractors.get(i).update();
	}
	
	public Attractor getClosestAttractorToParticle(VectorFlyer2d particle) {
		float leastDistance = Integer.MAX_VALUE;
		Attractor closest = attractors.get(0);
		for(int i=0; i < attractors.size(); i++) {
			float checkDist = attractors.get(i).position.dist(particle.position);
			if( checkDist < leastDistance ) {
				leastDistance = checkDist;
				closest = attractors.get(i);
			}
		}
		return closest;
	}
	
	
	
	
	public class VectorFlyer2d {
		public PVector position = new PVector();
  	  	protected float radians = MathUtil.randRangeDecimal( 0, P.TWO_PI );
  	  	protected float speed = MathUtil.randRangeDecimal( 2, 4 );
  	  	protected float turnRadius = MathUtil.randRangeDecimal( .01f, 0.03f );
  	  	protected int color;
		
		public VectorFlyer2d( PVector newPosition ) {
			position.set( newPosition );
			color = p.color(p.random(100), 100+p.random(100), 155+p.random(100), speed * 30);
		}
		
		public PVector position() {
			return position;
		}

		public void update(float attractorX, float attractorY) {
			float radiansToAttractor = MathUtil.getRadiansToTargetWrong( position.x, position.y, attractorX, attractorY );
			radians += turnRadius * MathUtil.getRadiansDirectionToTarget(radians, radiansToAttractor);
			if(radians < 0) radians += P.TWO_PI;
			if(radians > P.TWO_PI) radians -= P.TWO_PI;
			position.set(position.x + P.sin(radians) * speed, position.y + P.cos(radians) * speed);
			draw();
		}
		
		protected void draw() {
//			p.fill(color);
//			p.noStroke();
			p.pushMatrix();
			p.translate(position.x, position.y);
			p.rotate(-radians + P.HALF_PI);
			p.image(DemoAssets.arrow(), 0, 0, 10, 10);
//			p.rect(0, 0, 2, speed * 1.2f);
			p.popMatrix();
		}
	}
	
	public class ParticleTrail extends VectorFlyer2d {
		public ParticleTrail( PVector newPosition ) {
			super( newPosition );
			
		}
	}

	public class Attractor {
		public PVector position = new PVector();
		protected boolean autoControl = true;
		protected float autoControlFactor = MathUtil.randRange(1, 8)/100f;
		
		public Attractor( PVector newPosition ) {
			position.set( newPosition );
		}
		
		public PVector position() {
			return position;
		}

		public void update() {
			autoControl = false;
			if( autoControl == false ) {
				position.set( mouseX, mouseY );
			} else {
				float oscSpeed = P.sin(p.frameCount * autoControlFactor/10f) + 1;
				float radius = 250 * P.sin(p.frameCount * autoControlFactor);
				position.set( 
						p.width/2f + radius * P.sin(p.frameCount * oscSpeed * autoControlFactor), 
						p.height/2f + radius * P.cos(p.frameCount * oscSpeed * autoControlFactor)
						);
			}
			p.fill( 255 );
			p.noStroke();
			// p.ellipse(position.x, position.y, 10, 10);
		}
	}

}