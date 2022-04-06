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
	protected PVector speed;
	protected PVector influencePos;

	// displacement amp & bounciness 
	protected float displaceRange = 40;
	protected float friction = 0.77f;
	protected float acceleration = .1f;
	protected float displaceAmp = 1;
	protected float influenceProximityRamp = 1;
	protected float resultDisplacedAmp = 0;

	public DisplacementPoint3D(float x, float y, float z) {
		this(x, y, z, 50, 0.77f, 0.1f);
	}
	
	public DisplacementPoint3D(float x, float y, float z, float displaceRange, float friction, float acceleration) {
		// base location
		basePos = new PVector(x, y, z);
		curPos = new PVector(x, y, z);
		targetPos = new PVector(x, y, z);
		speed = new PVector();
		influencePos = new PVector();
		
		// springiness
		this.displaceRange = displaceRange;
		this.friction = friction;
		this.acceleration = acceleration;
		this.influenceProximityRamp = 1;
	}
	
	public void basePos(float x, float y, float z) { basePos.set(x, y, z); }
	public PVector pos() { return curPos; }
	public PVector targetPos() { return targetPos; }
	public float displaceRange() { return displaceRange; }
	public void displaceRange(float displaceRange) { this.displaceRange = displaceRange; }
	public float friction() { return friction; }
	public void friction(float friction) { this.friction = friction; }
	public float acceleration() { return acceleration; }
	public void acceleration(float acceleration) { this.acceleration = acceleration; }
	public float displaceAmp() { return displaceAmp; }
	public void displaceAmp(float displaceAmp) { this.displaceAmp = displaceAmp; }
	public float influenceProximityRamp() { return influenceProximityRamp; }
	public void influenceProximityRamp(float influenceProximityRamp) { this.influenceProximityRamp = influenceProximityRamp; }
	public float resultDisplacedAmp() { return resultDisplacedAmp; }

	public void update(float influenceX, float influenceY, float influenceZ) {
		// calculate displacement based on mouse distance from point base
		influencePos.set(influenceX, influenceY, influenceZ);
		float xdiff = basePos.x - influenceX;
		float ydiff = basePos.y - influenceY;
		float zdiff = basePos.z - influenceZ;
		float distFromInfluence = PVector.dist(basePos, influencePos);
		
		// further from influence point can become less effective, if we use the `influenceProximityRamp`
		float distFromInfluenceNorm = distFromInfluence / displaceRange;
		distFromInfluenceNorm = P.constrain(distFromInfluenceNorm, 0, 1);
		float adjustedDisplaceAmp = 1f - (distFromInfluenceNorm * influenceProximityRamp);
		resultDisplacedAmp = 1f - distFromInfluenceNorm;	// for reporting back
		
		// update target based on influence point
		if (distFromInfluence < displaceRange) {
			targetPos.set( 
				basePos.x - (xdiff-displaceRange*(xdiff/distFromInfluence)) * (adjustedDisplaceAmp * displaceAmp),
				basePos.y - (ydiff-displaceRange*(ydiff/distFromInfluence)) * (adjustedDisplaceAmp * displaceAmp),
				basePos.z - (zdiff-displaceRange*(zdiff/distFromInfluence)) * (adjustedDisplaceAmp * displaceAmp)
			);
		} else {
			targetPos.set(basePos);
		}
		
		// elastically move based on current target position vs. current position
		speed.set(
			((targetPos.x-curPos.x)*acceleration+speed.x)*friction,
			((targetPos.y-curPos.y)*acceleration+speed.y)*friction,
			((targetPos.z-curPos.z)*acceleration+speed.z)*friction
		);
		curPos.add(speed);
	}
}
