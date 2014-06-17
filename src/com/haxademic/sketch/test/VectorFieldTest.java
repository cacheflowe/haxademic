package com.haxademic.sketch.test;

import java.util.ArrayList;

import processing.core.PVector;
import processing.opengl.PShader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class VectorFieldTest 
extends PAppletHax  
{

	protected ArrayList<PVector> _vectorField;
	protected ArrayList<FieldParticle> _particles;
	protected PShader _fxaa;
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "1280" );
		_appConfig.setProperty( "height", "720" );
//		_appConfig.setProperty( "fps", "30" );
//		_appConfig.setProperty( "rendering", "true" );
	}

	public void setup() {
		super.setup();
		
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
//		p.smooth();
		_fxaa = p.loadShader( FileUtil.getHaxademicDataPath() + "shaders/filters/fxaa.glsl" );
		
		_vectorField = new ArrayList<PVector>();
		float spacing = 30f;
		for( int x = 0; x <= p.width; x += spacing ) {
			for( int y = 0; y <= p.height; y += spacing ) {
				_vectorField.add( new PVector(x, y, 0) );
			}
		}
		
		_particles = new ArrayList<FieldParticle>();
		for( int i = 0; i < 3000; i++ ) {
			_particles.add( new FieldParticle() );
		}
	}

	public void drawApp() {
		if( p.frameCount == 1 ) p.background(0);

		// fade out background
		DrawUtil.setDrawCorner(p);
		p.noStroke();
		p.fill(0, 20);
		p.rect(0,0,p.width, p.height);
		
		// draw field
		DrawUtil.setDrawCenter(p);
		p.fill(30);
		for (PVector vector : _vectorField) {
			float noise = p.noise(
					vector.x/11f + p.noise(p.frameCount/100f), 
					vector.y/20f + p.noise(p.frameCount/50f) 
			);
			vector.set(vector.x, vector.y, noise * 2f * P.TWO_PI);
//			p.pushMatrix();
//			p.translate(vector.x, vector.y);
//			p.rotate( vector.z );	// use z for rotation!
//		    p.rect(0, 0, 1, 5);
//		    p.popMatrix();
		}
		
		// draw particles
		DrawUtil.setDrawCenter(p);
		for( int i = 0; i < _particles.size(); i++ ) {
			p.fill(i % 55 + 100, i % 100 + 100, i % 150 + 55);
			_particles.get(i).update( _vectorField );
		}
		
		p.filter(_fxaa);
		p.filter(_fxaa);
		p.filter(_fxaa);
	}
	
	public class FieldParticle {
		
		public PVector position;
		public EasingFloat radians;
		public float speed;
		
		public FieldParticle() {
			speed = p.random(4,10);
			radians = new EasingFloat(0, p.random(6,20) );
			position = new PVector( p.random(0, p.width), p.random(0, p.height) );
		}
		
		public void update( ArrayList<PVector> vectorField ) {
			// adjust to surrounding vectors
			int closeVectors = 0;
			float averageDirection = 0;
			for (PVector vector : _vectorField) {
				if( vector.dist( position ) < 40 ) {
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
		    p.rect(0, 0, speed * 0.5f, speed * 1.8f);
		    p.popMatrix();
		}
	}
}