package com.haxademic.sketch.particle;

import java.util.ArrayList;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.system.FileUtil;

import processing.core.PConstants;
import processing.core.PVector;
import processing.opengl.PShader;


public class ShirtDesignParticles
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArrayList<ParticleTrail> _particles;
	protected ArrayList<Attractor> _attractors;
	protected PShader _fxaa;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "700" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "1000" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
	}

	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		_fxaa = p.loadShader( FileUtil.getHaxademicDataPath() + "shaders/filters/fxaa.glsl" );
		reset();
	}
	
	protected void reset() {
		_attractors = new ArrayList<Attractor>();
		for(int i=0; i < 4; i++) _attractors.add(new Attractor());
		
		_particles = new ArrayList<ParticleTrail>();
//		for(int i=0; i < 5; i++) {
			_particles.add(
				new ParticleTrail(
					new PVector(
						p.random(p.width * 0.9f, p.width * 1.1f), 
						p.random(p.height * 0.9f, p.height * 1.1f)
					)
				)
			);
//		}
	}
	
	public void mousePressed() {
		reset();
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
  	  	protected float speed = MathUtil.randRangeDecimal( 4, 8 );
  	  	protected float turnRadius = MathUtil.randRangeDecimal( .04f, 0.09f );
  	  	protected int color;
  	  	
  	  	
		
		public VectorFlyer2d( PVector newPosition ) {
			position.set( newPosition );
			radians = MathUtil.getRadiansToTarget( position.x, position.y, 0, 0 );

			color = p.color(255);
		}
		
		public void setRadians(float newRadians) {
			radians = newRadians;
		}

		public PVector position() {
			return position;
		}

		public void update(float attractorX, float attractorY) {
			float radiansToAttractor = MathUtil.getRadiansToTarget( position.x, position.y, attractorX, attractorY );
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
		
		protected ArrayList<PVector> _positionsArr;
		protected boolean _active = true;
		protected int myFrameCount = 0;

		public ParticleTrail( PVector newPosition ) {
			super( newPosition );
			_positionsArr = new ArrayList<PVector>();
		}
		
		public void update(float attractorX, float attractorY) {
			p.noStroke();
			myFrameCount++;
			
			
			// spawn children every x frames
			if(_active && myFrameCount > 1 && myFrameCount % 30 == 0) {
				_particles.add(
					new ParticleTrail(
						new PVector(position.x, position.y)
					)
				);
				_particles.get(_particles.size()-1).setRadians(radians + p.random(-0.02f, 0.02f));
			}
			
			// update position
			super.update(attractorX, attractorY);
			
			// add points every x frames
//			if(p.frameCount % 3 == 0) {
				if(_active && position.x > p.width * 0.1f && position.y > p.height * 0.1f) {
					_positionsArr.add(new PVector(position.x, position.y));
				} else {
					_active = false;
				}
//			}
			
			// draw trail
			p.stroke(255);
			p.noFill();
			p.strokeCap(P.ROUND);
			p.strokeWeight(3);
			p.beginShape();
			for (int i = 0; i < _positionsArr.size(); i++) {
				if( i == 0 ) {
					p.vertex(_positionsArr.get(i).x, _positionsArr.get(i).y);
				} else {	//  if(i % 2 == 0) 
					p.quadraticVertex(
							_positionsArr.get(i-1).x, 
							_positionsArr.get(i-1).y, 
							_positionsArr.get(i).x, 
							_positionsArr.get(i).y
					);
				}
			}
			p.endShape();
		}
	}

	public class Attractor {
		public PVector position = new PVector();
		protected boolean autoControl = true;
		protected float autoControlFactor = MathUtil.randRange(1, 8)/100f;
		protected float radians = 0;
		protected float radiansOsc = MathUtil.randRangeDecimal(0.03f, 0.2f);
		protected float speed = 7;
		protected int myFrameCount = 0;
		
		public Attractor() {
			position.set(p.width * 0.9f, p.height * 0.9f);
			radians = MathUtil.getRadiansToTarget( position.x, position.y, 1000, 0 );
		}
		
		public PVector position() {
			return position;
		}

		public void update() {
			myFrameCount++;
			if( autoControl == false ) {
				position.set( p.mouseX, p.mouseY );
			} else {
				// move twards top corner
				position.set(position.x + P.sin(radians) * speed, position.y + P.cos(radians) * speed);
				radians += P.sin(myFrameCount*0.2f) * radiansOsc;
			}
			p.fill( 255 );
			p.noStroke();
			p.ellipse(position.x, position.y, 10, 10);
		}
	}

}