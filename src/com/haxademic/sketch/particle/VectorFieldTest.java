package com.haxademic.sketch.particle;

import java.util.ArrayList;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PVector;

public class VectorFieldTest 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArrayList<PVector> _vectorField;
	protected ArrayList<FieldParticle> _particles;
	
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
//		p.appConfig.setProperty( AppSettings.DISPLAY, 2 );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, false );
		p.appConfig.setProperty( AppSettings.RETINA, true );
		p.appConfig.setProperty( AppSettings.FORCE_FOREGROUND, true );
		p.appConfig.setProperty( AppSettings.SMOOTHING, OpenGLUtil.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.SHOW_STATS, false );
//		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "true" );
	}
	
	public void setup() {
		super.setup();
		
		_vectorField = new ArrayList<PVector>();
		float spacing = 100f;
		for( int x = 0; x <= p.width; x += spacing ) {
			for( int y = 0; y <= p.height; y += spacing ) {
				_vectorField.add( new PVector(x, y, 0) );
			}
		}
		
		_particles = new ArrayList<FieldParticle>();
		for( int i = 0; i < 6000; i++ ) {
			_particles.add( new FieldParticle() );
		}
	}

	public void drawApp() {
		if( p.frameCount == 1 ) p.background(0);

//		OpenGLUtil.setBlending(p.g, true);
//		OpenGLUtil.setBlendMode(p.g, OpenGLUtil.Blend.ADDITIVE);
		
		// fade out background
		DrawUtil.setDrawCorner(p);
		p.noStroke();
		p.fill(0, 20);
		p.rect(0,0,p.width, p.height);
		
		// draw field
		DrawUtil.setDrawCenter(p);
		p.fill(255);
		for (PVector vector : _vectorField) {
			float noise = p.noise(
					vector.x/11f + p.noise(p.frameCount/100f), 
					vector.y/20f + p.noise(p.frameCount/50f) 
			);
			vector.set(vector.x, vector.y, noise * 2f * P.TWO_PI);
			
			// draw attractors
			p.pushMatrix();
			p.translate(vector.x, vector.y);
			p.rotate( vector.z );	// use z for rotation!
		    p.rect(0, 0, 1, 10);
		    p.popMatrix();
		}
		
		// draw particles
		DrawUtil.setDrawCenter(p);
		for( int i = 0; i < _particles.size(); i++ ) {
			p.fill((i % 150 + 55 / 10), i % 155 + 100, i % 100 + 100);
			_particles.get(i).update( _vectorField );
		}
		
//		FXAAFilter.instance(p).applyTo(p.g);
	}
	
	public class FieldParticle {
		
		public PVector position;
		public EasingFloat radians;
		public float speed;
		
		public FieldParticle() {
			speed = p.random(10,20);
			radians = new EasingFloat(0, p.random(6,20) );
			position = new PVector( p.random(0, p.width), p.random(0, p.height) );
		}
		
		public void update( ArrayList<PVector> vectorField ) {
			// adjust to surrounding vectors
			int closeVectors = 0;
			float averageDirection = 0;
			for (PVector vector : _vectorField) {
				if( vector.dist( position ) < 100 ) {
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
			position.set( position.x + P.sin(radians.value()) * speed, position.y + P.cos(radians.value()) * speed );
			if( position.x < 0 ) position.set( p.width, position.y );
			if( position.x > p.width ) position.set( 0, position.y );
			if( position.y < 0 ) position.set( position.x, p.height );
			if( position.y > p.height ) position.set( position.x, 0 );
			
			// draw
			p.pushMatrix();
			p.translate(position.x, position.y);
			p.rotate( -radians.value() );
		    p.rect(0, 0, speed * 0.2f, speed * 1.8f);
		    p.popMatrix();
		}
	}
}