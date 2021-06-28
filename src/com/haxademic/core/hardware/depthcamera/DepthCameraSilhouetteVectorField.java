package com.haxademic.core.hardware.depthcamera;

import java.util.ArrayList;
import java.util.Vector;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PGraphics;
import processing.core.PVector;

public class DepthCameraSilhouetteVectorField 
extends DepthCameraSilhouetteBasic {
	
	protected ArrayList<PVector> _vectorFieldBase;
	protected ArrayList<PVector> _vectorFieldOffset;
	protected boolean SHOW_ATTRACTORS = false;

	public DepthCameraSilhouetteVectorField(boolean depthKeying, boolean hasParticles) {
		super(depthKeying, hasParticles);
		
		buildVectorField();
	}

	protected void buildVectorField() {
		_vectorFieldBase = new ArrayList<PVector>();
		_vectorFieldOffset = new ArrayList<PVector>();
		float spacing = _canvas.width / 13f;
		for( int x = 0; x <= _canvas.width; x += spacing ) {
			for( int y = 0; y <= _canvas.height; y += spacing ) {
				float direction = MathUtil.getRadiansToTarget(_canvasW * 0.5f, _canvasH, x, y);
				_vectorFieldBase.add( new PVector(x, y, direction) );
				_vectorFieldOffset.add( new PVector(x, y, 0) );
			}
		}
	}
	
	protected void updateField() {
		// draw field
		PG.setDrawCenter(_canvas);
		_canvas.fill(255);
		for (int i = 0; i < _vectorFieldBase.size(); i++) {
			PVector vector = _vectorFieldBase.get(i);
			PVector vectorOffset = _vectorFieldOffset.get(i);
			float noise = P.p.noise(
					vector.x/31f + P.p.noise(P.p.frameCount/100f), 
					vector.y/20f + P.p.noise(P.p.frameCount/80f) 
			);
			vectorOffset.set(vector.x, vector.y, P.sin(noise * 1f) * 1f);
			
			if(SHOW_ATTRACTORS == true) {
				// draw attractors
				_canvas.pushMatrix();
				_canvas.translate(vector.x, vector.y);
				_canvas.rotate( vector.z + vectorOffset.z );	// use z for rotation!
			    _canvas.rect(0, 0, 3, 10);
			    _canvas.popMatrix();
			}
		}
	}

	protected Vector<VectorFieldParticle> _particles;
	protected Vector<VectorFieldParticle> _inactiveParticles;

	
	protected void updateParticles() {
		updateField();
		if(_particles == null) {
			_particles = new Vector<VectorFieldParticle>();
			_inactiveParticles = new Vector<VectorFieldParticle>();
		}

		// update particles
		_canvas.fill( 255 );
		_canvas.stroke(255);
		_canvas.strokeCap(P.ROUND);
		VectorFieldParticle particle;
		int particlesLen = _particles.size() - 1;
		for( int i = particlesLen; i > 0; i-- ) {
			particle = _particles.get(i);
			if( particle.active == true ) {
				particle.update();
			} else {
				_particles.remove(i);
				_inactiveParticles.add(particle);
			}
		}
		if(DEBUG_OUTPUT) P.println("_particles.size()",_particles.size());
		_canvas.noStroke();
	}
	
	
	protected void newParticle( float x, float y, float blobX, float blobY ) {
		if(_particles == null) return;
		VectorFieldParticle particle;
		if( _inactiveParticles.size() > 0 ) {
			particle = _inactiveParticles.remove( _inactiveParticles.size() - 1 );
		} else {
			particle = new VectorFieldParticle(_canvas);
		}
		float direction = MathUtil.getRadiansToTarget(blobX, blobY, x, y);
		particle.startAt( x, y, direction );
		_particles.add( particle );
	}

	
	public class VectorFieldParticle {
		
		PGraphics p;
		PVector _position = new PVector();
		PVector _positionPrev = new PVector();
		float _speed = P.p.random(10,20);
		EasingFloat _radians = new EasingFloat(0, P.p.random(10,20) );
		int _color = 0;
		float _size = 4;
		public boolean active = false;
		protected int _audioIndex;
		
		public VectorFieldParticle(PGraphics p) {
			this.p = p;
		}
		
		public void startAt( float x, float y, float direction ) {
			_position.set( x, y );
			_positionPrev.set( x, y );
			_radians.setCurrent(direction);
			_radians.setTarget(direction);
			_speed = P.p.random(6,12);
			active = true;
			_size = 4; //P.max(6,(P.abs(_speed.x) + P.abs(_speed.y))*0.3f);
			_audioIndex = MathUtil.randRange(0, 511);
		}
		
		public void update() {
			updateWithVectorField();
			
			_radians.update();
			_positionPrev.set(_position);
			_position.add( P.sin(_radians.value()) * _speed, P.cos(_radians.value()) * _speed, 0 );
//			_speed *= 0.98f;
			_size *= 0.99f;
			
			if( _size <= 0.1f ) {
				active = false;
			} else if( _position.y < -_speed ) {
				active = false;
			} else if( _position.y > p.height + _speed ) {
				active = false;
			} else if( _position.x < -_speed ) {
				active = false;
			} else if( _position.x > p.width + _speed ) {
				active = false;
			} else {
				p.strokeWeight(_size);
				p.line(_position.x, _position.y, _positionPrev.x, _positionPrev.y);
			}
		}
		
		protected void updateWithVectorField() {
			// adjust to surrounding vectors
			int closeVectors = 0;
			float averageDirection = 0;
			for (int i = 0; i < _vectorFieldBase.size(); i++) {
				PVector vector = _vectorFieldBase.get(i);
				PVector vectorOffset = _vectorFieldOffset.get(i);
				if( vector.dist( _position ) < _canvasW/10f ) {
					averageDirection += vector.z + vectorOffset.z;
					closeVectors++;
				}
			}
			if( closeVectors > 0 ) {
				_radians.setTarget( (-averageDirection / closeVectors) ); // _radians.target() + 4f * 
			}

		}
	}
}
