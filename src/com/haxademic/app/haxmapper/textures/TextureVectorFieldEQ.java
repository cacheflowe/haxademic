package com.haxademic.app.haxmapper.textures;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PVector;

public class TextureVectorFieldEQ
extends BaseTexture {

	protected ArrayList<PVector> _vectorField;
	protected ArrayList<FieldParticle> _particles;
	float FIELD_SPACING = 30f;
	float NUM_PARTICLES = 700f;
	float ATTENTION_RADIUS = 40;
	int DRAWS_PER_FRAME = 1;
	int OVERDRAW_FADE = 20;
	boolean DEBUG_VECTORS = false;


	public TextureVectorFieldEQ( int width, int height ) {
		super();
		buildGraphics( width, height );
		initParticles();
	}
	
	protected void initParticles() {
		_vectorField = new ArrayList<PVector>();
		for( int x = 0; x <= _texture.width; x += FIELD_SPACING ) {
			for( int y = 0; y <= _texture.height; y += FIELD_SPACING ) {
				_vectorField.add( new PVector(x, y, 0) );
			}
		}
		
		_particles = new ArrayList<FieldParticle>();
		for( int i = 0; i < NUM_PARTICLES; i++ ) {
			_particles.add( new FieldParticle() );
		}
	}
	
	public void updateDraw() {
		// fade out background
		DrawUtil.setDrawCorner(_texture);
		_texture.noStroke();
		_texture.fill(0, OVERDRAW_FADE);
		_texture.rect(0,0,_texture.width, _texture.height);
		
		// update & draw field
		DrawUtil.setDrawCenter(_texture);
		_texture.fill(255);
		for (PVector vector : _vectorField) {
			float noise = P.p.noise(
					vector.x/11f + P.p.noise(P.p.frameCount/50f), 
					vector.y/20f + P.p.noise(P.p.frameCount/50f) 
					);
			float targetRotation = noise * 4f * P.TWO_PI;
			vector.set(vector.x, vector.y, P.lerp(vector.z, targetRotation, 0.2f));

			if(DEBUG_VECTORS == true) {
				// draw attractors
				_texture.pushMatrix();
				_texture.translate(vector.x, vector.y);
				_texture.rotate( vector.z );	// use z for rotation!
				_texture.rect(0, 0, 2, 10);
				_texture.popMatrix();
			}
		}
		
		for (int j = 0; j < DRAWS_PER_FRAME; j++) {
			// draw particles
			_texture.strokeWeight(2f);
			DrawUtil.setDrawCenter(_texture);
			for( int i = 0; i < _particles.size(); i++ ) {
				_texture.stroke(180 + (i % 75), 200 + (i % 55), 210 + (i % 45));
				_particles.get(i).update( _vectorField, i );
			}
		}
	}
	
	public void updateTiming() {
		super.updateTiming();
	}
	
	public void updateTimingSection() {
		super.updateTimingSection();
	}
	
	public void newLineMode() {

	}
	
	public void newRotation() {
	}

	public class FieldParticle {
		
		public PVector lastPosition;
		public PVector position;
		public EasingFloat radians;
		public float speed;
		
		public FieldParticle() {
			speed = P.p.random(2,6);
			radians = new EasingFloat(0, P.p.random(6,20) );
			position = new PVector( P.p.random(0, _texture.width), P.p.random(0, P.p.height) );
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
				if( P.p.frameCount == 1 ) {
					radians.setCurrent( -averageDirection / closeVectors );
				} else {
					radians.setTarget( -averageDirection / closeVectors );
				}
			}

			radians.update();
			
			// update position
			lastPosition.set(position);
			float curSpeed = speed * (0.15f + P.p._audioInput.getFFT().spectrum[index % 512]);
			position.set( position.x + P.sin(radians.value()) * curSpeed, position.y + P.cos(radians.value()) * curSpeed );
			if( position.x < 0 ) position.set( _texture.width, position.y );
			if( position.x > _texture.width ) position.set( 0, position.y );
			if( position.y < 0 ) position.set( position.x, _texture.height );
			if( position.y > _texture.height ) position.set( position.x, 0 );
			
			// draw
			if(position.dist(lastPosition) < curSpeed * 2f) {
				_texture.line(position.x, position.y, lastPosition.x, lastPosition.y);
			}
		}
	}
}


