package com.haxademic.app.haxmapper.overlays;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PVector;

public class MeshParticles {

	protected ArrayList<PVector> _meshVertices;
	protected ArrayList<VectorFlyer2d> _particles;
	protected PGraphics pg;
	protected EasingColor _colorEase;

	protected int NUM_PARTICLES = 100;
	protected int _curNumParticles = NUM_PARTICLES;
	protected int _mode = 4;
	protected enum ParticleMode {
		FLOCKING_PARTICLES
	}
	protected int _numModes = ParticleMode.values().length;

	public MeshParticles( PGraphics pg ) {
		this.pg = pg;
		_meshVertices = new ArrayList<PVector>();
		_colorEase = new EasingColor( "#ffffff", 5 );
	}

	public PGraphics texture() {
		return pg;
	}

	public void addVertex( float x, float y ) {
		boolean hasVertexAlready = false;
		for( int i=0; i < _meshVertices.size(); i++ ) {
			if( _meshVertices.get(i).x == x && _meshVertices.get(i).y == y ) {
				hasVertexAlready = true;
			}
		}
		if( hasVertexAlready == false ) {
			_meshVertices.add( new PVector( x, y ) );
		}
	}

	public void update() {
		// lazy-init particles
		if(_particles == null) {
			NUM_PARTICLES = _meshVertices.size() * 2;
			_particles = new ArrayList<VectorFlyer2d>();
			for(int i=0; i < NUM_PARTICLES; i++) {
				_particles.add(new VectorFlyer2d(new PVector(P.p.random(pg.width), P.p.random(pg.height))));
				PVector closestAttractor = getClosestVertexToParticle( _particles.get(i) );
				_particles.get(i).update(closestAttractor.x, closestAttractor.y, 1);
			}
		}
		
		_colorEase.update();
		DrawUtil.setDrawCenter( pg );

//		float spectrumInterval = (int) ( 256 / _meshVertices.size() );	// 256 keeps it in the bottom half of the spectrum since the high ends is so overrun
		
		// oscillate number of particles
		int halfNumParticles = NUM_PARTICLES / 2;
		_curNumParticles = halfNumParticles + P.round(halfNumParticles * P.sin(P.p.frameCount/1000f));
		
		for(int i=0; i < _curNumParticles; i++) {
			float amp = P.p.audioFreq( i % 32 ) / 20f;
			PVector closestAttractor = getOneOfTheClosestVertexToParticle( _particles.get(i) );
			_particles.get(i).update(closestAttractor.x, closestAttractor.y, amp);
		}

		
		for( int i=0; i < _meshVertices.size(); i++ ) {
			// _meshVertices.get(i).update( pg, _mode, _colorEase.colorInt(), P.p.audioIn.getEqAvgBand( 15 ), P.p.audioIn.getEqBand( 20 + P.floor(i*spectrumInterval) ) );
		}
	}
	
	public PVector getClosestVertexToParticle(VectorFlyer2d particle) {
		float leastDistance = Integer.MAX_VALUE;
		PVector closest = _meshVertices.get(0);
		for(int i=0; i < _meshVertices.size(); i++) {
			float checkDist = _meshVertices.get(i).dist(particle.position);
			if( checkDist < leastDistance ) {
				leastDistance = checkDist;
				closest = _meshVertices.get(i);
			}
		}
		return closest;
	}

	public PVector getOneOfTheClosestVertexToParticle(VectorFlyer2d particle) {
		float leastDistance = Integer.MAX_VALUE;
		int closestIndex = MathUtil.randRange(0, _meshVertices.size() - 1);
		int closestIndex2nd = MathUtil.randRange(0, _meshVertices.size() - 1);
		for(int i=0; i < _meshVertices.size(); i++) {
			float checkDist = _meshVertices.get(i).dist(particle.position);
			if( checkDist < leastDistance && particle.closestIndex != i ) {
				closestIndex2nd = closestIndex;
				closestIndex = i;
				leastDistance = checkDist;
			}
		}
		int randClosestIndex =  (MathUtil.randBoolean(P.p) == true) ? closestIndex : closestIndex2nd;
		particle.closestIndex = randClosestIndex;
		return _meshVertices.get(randClosestIndex);
	}

	
	public void updateLineMode() {
		_mode += MathUtil.randRange(1, 2);
		if( _mode >= _numModes ) _mode = 0;
	}

	public void resetLineMode( int index ) {
		_mode = index;
	}

	public void setColor( int color ) {
		_colorEase.setTargetInt( color );
	}

	
	
	public class VectorFlyer2d {
		public PVector position = new PVector();
		public PVector attractor;
  	  	protected float radians = MathUtil.randRangeDecimal( 0, P.TWO_PI );
  	  	protected float speed = MathUtil.randRangeDecimal( 0.5f, 0.9f );
  	  	protected float turnRadius = MathUtil.randRangeDecimal(0.4f,0.6f);// MathUtil.randRangeDecimal( .01f, 0.2f );
  	  	protected int color;
		
  	  	public int closestIndex = -1;
  	  	
		public VectorFlyer2d( PVector newPosition ) {
			position.set( newPosition );
			color = P.p.color(255);// P.p.color(P.p.random(100), 100+P.p.random(100), 155+P.p.random(100), speed * 30);
		}
		
		public PVector position() {
			return position;
		}

		public void update(float attractorX, float attractorY, float amp) {
			amp *= 0.5;
			// if we get close enough to an attractor, change to the next one
			// P.println(MathUtil.getDistance(attractor.x, attractor.y, position.x, position.y), speed);

			if(attractor == null || MathUtil.getDistance(attractor.x, attractor.y, position.x, position.y) < speed) {
				if( attractor == null ) attractor = new PVector();
				attractor.set(attractorX, attractorY);
			}
			
			float radiansToAttractor = MathUtil.getRadiansToTargetWrong( position.x, position.y, attractor.x, attractor.y );
			radians += turnRadius * MathUtil.getRadiansDirectionToTarget(radians, radiansToAttractor);
			if(radians < 0) radians += P.TWO_PI;
			if(radians > P.TWO_PI) radians -= P.TWO_PI;
			position.set(position.x + P.sin(radians) * (speed + amp*speed), position.y + P.cos(radians) * (speed + amp*speed)); //  + amp*speed
			draw(amp);
		}
		
		protected void draw(float amp) {
			pg.fill(color);
			pg.noStroke();
			pg.pushMatrix();
			pg.translate(position.x, position.y);
			pg.rotate(-radians);
			pg.rect(0, 0, 2, speed);
			pg.popMatrix();
		}
	}
	
	public class ParticleTrail extends VectorFlyer2d {
		public ParticleTrail( PVector newPosition ) {
			super( newPosition );
			
		}
	}

}