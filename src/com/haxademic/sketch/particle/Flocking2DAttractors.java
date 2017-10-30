package com.haxademic.sketch.particle;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

import processing.core.PConstants;
import processing.core.PVector;
import processing.opengl.PShader;


public class Flocking2DAttractors
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArrayList<VectorFlyer2d> _particles;
	protected ArrayList<Attractor> _attractors;
	protected PShader _fxaa;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "1280" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "720" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
	}

	public void setup() {
		super.setup();
		_fxaa = p.loadShader( FileUtil.getHaxademicDataPath() + "shaders/filters/fxaa.glsl" );
	
		_particles = new ArrayList<VectorFlyer2d>();
		for(int i=0; i < 30000; i++) _particles.add(new VectorFlyer2d(new PVector(p.random(p.width), p.random(p.height))));
		_attractors = new ArrayList<Attractor>();
		for(int i=0; i < 5; i++) _attractors.add(new Attractor(new PVector(p.width/2, p.height/2)));
	}

	public void drawApp() {
		p.background(0);
//		DrawUtil.setDrawCorner(p);
//		p.fill(0, 20);
//		p.rect(0, 0, p.width, p.height);
		DrawUtil.setDrawCenter(p);
		
		p.blendMode(PConstants.ADD);
		for(int i=0; i < _particles.size(); i++) {
			Attractor closestAttractor = getClosestAttractorToParticle(_particles.get(i));
			_particles.get(i).update(closestAttractor.position.x, closestAttractor.position.y);
		}
		for(int i=0; i < _attractors.size(); i++) _attractors.get(i).update();

		// post-process effects
//		p.filter(_fxaa);
//		p.filter(_fxaa);
//		p.filter(_fxaa);
//		p.filter(_fxaa);
	}
	
	public Attractor getClosestAttractorToParticle(VectorFlyer2d particle) {
		float leastDistance = Integer.MAX_VALUE;
		Attractor closest = _attractors.get(0);
		for(int i=0; i < _attractors.size(); i++) {
			float checkDist = _attractors.get(i).position.dist(particle.position);
			if( checkDist < leastDistance ) {
				leastDistance = checkDist;
				closest = _attractors.get(i);
			}
		}
		return closest;
	}
	
	
	
	
	public class VectorFlyer2d {
		public PVector position = new PVector();
  	  	protected float radians = MathUtil.randRangeDecimal( 0, P.TWO_PI );
  	  	protected float speed = MathUtil.randRangeDecimal( 2, 18 );
  	  	protected float turnRadius = MathUtil.randRangeDecimal( .01f, 0.2f );
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
			p.fill(color);
			p.noStroke();
			p.pushMatrix();
			p.translate(position.x, position.y);
			p.rotate(-radians);
			p.rect(0, 0, 2, speed * 1.2f);
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