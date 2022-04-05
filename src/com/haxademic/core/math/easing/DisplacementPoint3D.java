package com.haxademic.core.math.easing;

import com.haxademic.core.app.P;

import processing.core.PVector;

public class DisplacementPoint3D {

	// ported from actionscript to javascript to java :)
	// https://codepen.io/cacheflowe/pen/domZpQ

	// position tracking
	protected PVector basePos;
	protected PVector curPos;
	protected PVector targetPos;
	protected PVector speedX;

	// displacement amp & bounciness 
	float displaceAmp = 40;
	float friction = 0.77f;
	float acceleration = .1f;
	float influenceByDistance = 1f;

	public DisplacementPoint3D(float x, float y, float z) {
		this(x, y, z, 50, 0.77f, 0.1f);
	}
	
	public DisplacementPoint3D(float x, float y, float z, float displaceAmp, float friction, float acceleration) {
		// base location
		basePos = new PVector(x, y, z);
		curPos = new PVector(x, y, z);
		targetPos = new PVector(x, y, z);
		speedX = new PVector(0, 0, 0);
		
		// springiness
		this.displaceAmp = displaceAmp;
		this.friction = friction;
		this.acceleration = acceleration;
		this.influenceByDistance = 1;
	}
	
	public void basePos(float x, float y, float z) { basePos.set(x, y, z); }
	public PVector pos() { return curPos; }
	public PVector targetX() { return targetPos; }
	public float displaceAmp() { return displaceAmp; }
	public void displaceAmp(float displaceAmp) { this.displaceAmp = displaceAmp; }
	public float friction() { return friction; }
	public void friction(float friction) { this.friction = friction; }
	public float acceleration() { return acceleration; }
	public void acceleration(float acceleration) { this.acceleration = acceleration; }
	public float influenceByDistance() { return influenceByDistance; }
	public void influenceByDistance(float influenceByDistance) { this.influenceByDistance = influenceByDistance; }

	public float displacedCurX() { return P.abs(basePos.x - curPos.x); }
	public float displacedCurY() { return P.abs(basePos.y - curPos.y); }
	public float displacedCurZ() { return P.abs(basePos.z - curPos.z); }
	public float displacedCurTotal() { return displacedCurX() + displacedCurY() + displacedCurZ(); }

	protected PVector utilVec = new PVector();
	public void update(float influenceX, float influenceY, float influenceZ) {
		// calculate displacement based on mouse distance from point base
		float xdiff = basePos.x - influenceX;
		float ydiff = basePos.y - influenceY;
		float zdiff = basePos.z - influenceZ;
		utilVec.set(influenceX, influenceY, influenceZ);
		float dist = PVector.dist(basePos, utilVec);
		float distInfluence = (influenceByDistance > 0) ? P.map(influenceByDistance, 0, 1, 1, dist / displaceAmp) : 1f;	// lerp between distance influence (1) or clamped extreme displacement (0)
		
		// update target based on influence point
		if (dist < displaceAmp && dist > 0) {
			targetPos.set( 
				basePos.x-(xdiff-displaceAmp*(xdiff/dist)) * (distInfluence),
				basePos.y-(ydiff-displaceAmp*(ydiff/dist)) * (distInfluence),
				basePos.z-(zdiff-displaceAmp*(zdiff/dist)) * (distInfluence)
			);
		} else {
			targetPos.set(basePos);
		}
		
		// elastically move based on current target position vs. current position
		speedX.set(
			((targetPos.x-curPos.x)*acceleration+speedX.x)*friction,
			((targetPos.y-curPos.y)*acceleration+speedX.y)*friction,
			((targetPos.z-curPos.z)*acceleration+speedX.z)*friction
		);
		curPos.add(speedX);
	}
}
