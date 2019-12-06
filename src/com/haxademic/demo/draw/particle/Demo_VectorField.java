package com.haxademic.demo.draw.particle;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.VignetteAltFilter;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.media.audio.analysis.AudioIn;

import processing.core.PVector;

public class Demo_VectorField 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArrayList<PVector> _vectorField;
	protected ArrayList<FieldParticle> _particles;
	protected int frames = 130;
	float FIELD_SPACING = 30f;
	float NUM_PARTICLES = 2000f;
	float ATTENTION_RADIUS = 100;
	int DRAWS_PER_FRAME = 1;
	int OVERDRAW_FADE = 16;
	boolean DEBUG_VECTORS = false;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false);
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, frames);

	}
	
	public void setupFirstFrame() {
		AudioIn.instance();
		
		_vectorField = new ArrayList<PVector>();
		
		for( int x = 0; x <= p.width; x += FIELD_SPACING ) {
			for( int y = 0; y <= p.height; y += FIELD_SPACING ) {
				_vectorField.add( new PVector(x, y, 0) );
			}
		}
		
		_particles = new ArrayList<FieldParticle>();
		for( int i = 0; i < NUM_PARTICLES; i++ ) {
			_particles.add( new FieldParticle() );
		}
	}

	public void drawApp() {
		if( p.frameCount == 1 ) p.background(0);

//		OpenGLUtil.setBlending(p.g, true);
//		OpenGLUtil.setBlendMode(p.g, OpenGLUtil.Blend.ADDITIVE);
		
//		feedback(4, 0.2f);
		
		// fade out background
		PG.setDrawCorner(p);
		p.noStroke();
		p.fill(0, OVERDRAW_FADE);
		p.rect(0,0,p.width, p.height);
		
		// draw field
		PG.setDrawCenter(p);
		p.fill(0);
		for (PVector vector : _vectorField) {
			float noise = p.noise(
					vector.x/15f + p.noise(p.frameCount/50f), 
					vector.y/10f + p.noise(p.frameCount/50f) 
					);
			float targetRotation = noise * 6f * P.TWO_PI;
			vector.set(vector.x, vector.y, P.lerp(vector.z, targetRotation, 0.2f));
			
			// draw attractors
			p.pushMatrix();
			p.translate(vector.x, vector.y);
			p.rotate( vector.z );	// use z for rotation!
			// p.rect(0, 0, 1, 10);
			p.popMatrix();
		}
		
		updateVectors();

		for (int j = 0; j < DRAWS_PER_FRAME; j++) {
			// draw particles
			p.strokeWeight(2f);
			PG.setDrawCenter(p);
			for( int i = 0; i < _particles.size(); i++ ) {
//				p.fill((i % 150 + 55 / 10), i % 155 + 100, i % 100 + 100); // blue/green
				p.stroke(180 + (i % 75), 200 + (i % 55), 210 + (i % 45));
				_particles.get(i).update( _vectorField, i );
			}
		}
		
//		postProcessForRendering();
	}
	
	public void feedback(float amp, float darkness) {
		PG.setDrawCorner(p);
		p.g.copy(
			p.g, 
			0, 
			0, 
			p.width, 
			p.height, 
			P.round(-amp/2f), 
			P.round(-amp/2f), 
			P.round(p.width + amp), 
			P.round(p.height + amp)
		);
		p.fill(0, darkness * 255f);
		p.noStroke();
		p.rect(0, 0, p.width, p.height);
	}

	
	protected void updateVectors() {
		// override this if we want
	}
	
	protected void postProcessForRendering() {
		// overlay
		int transitionIn = 50;
		int transition = 40;
		PG.setDrawCorner(p);
		if(p.frameCount <= transitionIn) {
			VignetteAltFilter.instance(p).setDarkness(P.map(p.frameCount, 1f, transitionIn, -7f, -1.75f));
			VignetteAltFilter.instance(p).setSpread(P.map(p.frameCount, 1f, transitionIn, -3f, -1.25f));
			VignetteAltFilter.instance(p).applyTo(p);
			p.fill(255, P.map(p.frameCount, 1f, transition, 255f, 0));
			p.rect(0,0,p.width, p.height);
		} else if(p.frameCount >= frames - transition) {
			VignetteAltFilter.instance(p).setDarkness(P.map(p.frameCount, frames - transition, frames, -1.75f, -7f));
			VignetteAltFilter.instance(p).setSpread(P.map(p.frameCount, frames - transition, frames, -1.25f, -3f));
			VignetteAltFilter.instance(p).applyTo(p);
			p.fill(255, P.map(p.frameCount, frames - transition, frames, 0, 255f));
			p.rect(0,0,p.width, p.height);
		} else {
			VignetteAltFilter.instance(p).setDarkness(-1.75f);
			VignetteAltFilter.instance(p).setSpread(-1.25f);
			VignetteAltFilter.instance(p).applyTo(p);
		}
	}
	
	public class FieldParticle {
		
		public PVector lastPosition;
		public PVector position;
		public EasingFloat radians;
		public float speed;
		
		public FieldParticle() {
			speed = p.random(2,6);
			radians = new EasingFloat(0, p.random(6,20) );
			position = new PVector( p.random(0, p.width), p.random(0, p.height) );
			lastPosition = new PVector();
			lastPosition.set(position);
		}
		
		public void update( ArrayList<PVector> vectorField, int index ) {
			// adjust to surrounding vectors
			int closeVectors = 0;
			float averageDirection = 0;
			for (PVector vector : _vectorField) {
				if( vector.dist( position ) < ATTENTION_RADIUS ) {
					averageDirection += vector.z;
					closeVectors++;
				}
			}
			if( closeVectors > 0 ) {
				if( p.frameCount == 1 ) {
					radians.setCurrent( -averageDirection / closeVectors );
				} else {
					radians.setTarget( -averageDirection / closeVectors );
				}
			}

			radians.update();
			
			// update position
			lastPosition.set(position);
			float curSpeed = speed * AudioIn.audioFreq(index);
			position.set( position.x + P.sin(radians.value()) * curSpeed * P.map(p.mouseX, 0, p.width, 0, 2f), position.y + P.cos(radians.value()) * curSpeed * P.map(p.mouseX, 0, p.width, 0, 2f) );
			if( position.x < 0 ) position.set( p.width, position.y );
			if( position.x > p.width ) position.set( 0, position.y );
			if( position.y < 0 ) position.set( position.x, p.height );
			if( position.y > p.height ) position.set( position.x, 0 );
			
			// draw
			if(position.dist(lastPosition) < curSpeed * 2f) {
				p.line(position.x, position.y, lastPosition.x, lastPosition.y);
			}
		}
	}
}