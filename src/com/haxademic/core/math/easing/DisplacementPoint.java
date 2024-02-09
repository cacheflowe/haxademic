package com.haxademic.core.math.easing;

import com.haxademic.core.app.P;

public class DisplacementPoint {

	// ported from actionscript to javascript to java :)
	// https://codepen.io/cacheflowe/pen/domZpQ

	// position tracking
	protected float baseX;
	protected float baseY;
	protected float curX;
	protected float curY;
	protected float targetX;
	protected float targetY;
	protected float speedX = 0;
	protected float speedY = 0;

	// displacement amp & bounciness 
	float displaceAmp = 40;
	float friction = 0.77f;
	float acceleration = .1f;
	float influenceByDistance = 1f;

	public DisplacementPoint(float x, float y) {
		this(x, y, 50, 0.77f, 0.1f);
	}
	
	public DisplacementPoint(float x, float y, float displaceAmp, float friction, float acceleration) {
		// base location
		baseX = x;
		baseY = y;

		// current location
		curX = x;
		curY = y;
		targetX = x;
		targetY = y;
		
		// springiness
		this.displaceAmp = displaceAmp;
		this.friction = friction;
		this.acceleration = acceleration;
		this.influenceByDistance = 1;
	}
	
	public void baseX(float x) { baseX = x; }
	public void baseY(float y) { baseY = y; }
	public float x() { return curX; }
	public float y() { return curY; }
	public float targetX() { return targetX; }
	public float targetY() { return targetY; }
	public float displaceAmp() { return displaceAmp; }
	public void displaceAmp(float displaceAmp) { this.displaceAmp = displaceAmp; }
	public float friction() { return friction; }
	public void friction(float friction) { this.friction = friction; }
	public float acceleration() { return acceleration; }
	public void acceleration(float acceleration) { this.acceleration = acceleration; }
	public float influenceByDistance() { return influenceByDistance; }
	public void influenceByDistance(float influenceByDistance) { this.influenceByDistance = influenceByDistance; }

	public float displacedCurX() { return P.abs(baseX - curX); }
	public float displacedCurY() { return P.abs(baseY - curY); }
	public float displacedCurTotal() { return displacedCurX() + displacedCurY(); }

	public void update(float influenceX, float influenceY) {
		// calculate displacement based on distance from point base
		float xdiff = baseX - influenceX;
		float ydiff = baseY - influenceY;
		float dist = P.sqrt(xdiff * xdiff + ydiff * ydiff);
		float distInfluence = (influenceByDistance > 0) ? P.map(influenceByDistance, 0, 1, 1, dist / displaceAmp) : 1f;	// lerp between distance influence (1) or clamped extreme displacement (0)
		
		// update target based on influence point
		if (dist < displaceAmp && dist > 0) {
			targetX = baseX-(xdiff-displaceAmp*(xdiff/dist)) * (distInfluence);
			targetY = baseY-(ydiff-displaceAmp*(ydiff/dist)) * (distInfluence);
		} else {
			targetX = baseX;
			targetY = baseY;
		}
		
		// elastically move based on current target position vs. current position
		speedX = ((targetX-curX)*acceleration+speedX)*friction;
		speedY = ((targetY-curY)*acceleration+speedY)*friction;
		curX += speedX;
		curY += speedY;
	}
}
