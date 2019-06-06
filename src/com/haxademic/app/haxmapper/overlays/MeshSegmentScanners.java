package com.haxademic.app.haxmapper.overlays;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PVector;

public class MeshSegmentScanners {

	protected ArrayList<MeshLineSegment> _meshLineSegments;
	protected ArrayList<ScannerParticle> _particles;
	protected PGraphics pg;
	protected EasingColor _colorEase;

	protected int NUM_PARTICLES = 30;
	protected enum ScannerMode {
		FLOCKING_PARTICLES
	}
	protected int _numModes = ScannerMode.values().length;

	public MeshSegmentScanners( PGraphics pg, ArrayList<MeshLineSegment> meshLineSegments ) {
		this.pg = pg;
		_meshLineSegments = meshLineSegments;
		NUM_PARTICLES = _meshLineSegments.size() * 1;
		_colorEase = new EasingColor( "#ffffff", 5 );
	}

	public PGraphics texture() {
		return pg;
	}

	public void update() {
		// lazy-init particles
		if(_particles == null) {
			_particles = new ArrayList<ScannerParticle>();
			for(int i=0; i < NUM_PARTICLES; i++) {
				_particles.add(new ScannerParticle());
			}
		}

		_colorEase.update();
		PG.setDrawCenter( pg );

		//		float spectrumInterval = (int) ( 256 / _meshVertices.size() );	// 256 keeps it in the bottom half of the spectrum since the high ends is so overrun

		for(int i=0; i < NUM_PARTICLES; i++) {
			float amp = P.p.audioFreq( i % 32 ) / 20f;
			_particles.get(i).update(amp);
		}
	}

	public void setColor( int color ) {
		_colorEase.setTargetInt( color );
	}

	public MeshLineSegment randomSegmentPosition() {
		return _meshLineSegments.get(MathUtil.randRange(0, _meshLineSegments.size()-1));
	}
	
	public PVector nextSegmentPosition(PVector curTargetVec) {
//		return randomSegmentPosition().randomPoint(); // was used for testing
		PVector containsVec;
		int containsVecCount = 0;
		int indexToReturn = 0;
		// find segment with current point (make sure to exclude current segment)
		for (int i = 0; i < _meshLineSegments.size(); i++) {
			containsVec = _meshLineSegments.get(i).contains(curTargetVec);
			if( containsVec != null ) {
				containsVecCount++;
				if(MathUtil.randRange(0, 5) > 3) {
					indexToReturn = i;
				}
			}
		}
		if(containsVecCount > 0) {
			return _meshLineSegments.get(indexToReturn).otherPoint(curTargetVec);
		}
		return null;
	}

	public class ScannerParticle {
		public PVector position;
		public PVector lastPosition;
		public PVector targetPosition;
		protected MeshLineSegment _lastSegment;
		protected int color;

		public int closestIndex = -1;

		public ScannerParticle() {
			color = P.p.color(255);// P.p.color(P.p.random(100), 100+P.p.random(100), 155+P.p.random(100), speed * 30);
		}

		public void update(float amp) {
			// initialize initial positions
			if(position == null) {
				position = new PVector();
				position.set(randomSegmentPosition().randomPoint());

				lastPosition = new PVector();
				lastPosition.set(position);
				
				targetPosition = new PVector();
				targetPosition.set(position);
			} 

			// if we get close enough to the target, change to the next one
			PVector nextPoint = null;
			if(targetPosition.dist(position) < 2f) {
				nextPoint = nextSegmentPosition(targetPosition);
				if(nextPoint != null) {
					targetPosition.set(nextPoint);
				}
			}

			// move towards target
			if(lastPosition.dist(position) > 2) lastPosition.set(position);
			position.lerp(targetPosition, P.constrain(amp/2f, 0, 0.5f));
			draw(amp);
		}

		protected void draw(float amp) {
			pg.noFill();
			pg.stroke(255, 210);
			pg.strokeWeight(1.3f);
			pg.line(position.x, position.y, lastPosition.x, lastPosition.y);
		}
	}

}