package com.haxademic.core.draw.particle;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;
import toxi.color.TColor;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.TColorBlendBetween;
import com.haxademic.core.draw.color.TColorInit;
import com.haxademic.core.draw.context.OrientationUtil;

public class VectorFlyer {

	protected PVector positionLast = new PVector();
	protected PVector position = new PVector();
	protected PVector vector = new PVector();
	protected PVector target = new PVector();
	protected float accel = 1;
	protected float baseAccel = 1;
	protected float maxSpeed = 1;

	protected float distToDest;
	protected TColorBlendBetween color;

	protected TColor BLACK = TColor.WHITE.copy();
	protected TColor MODE_SET_BLUE = TColorInit.newRGBA( 0, 200, 234, 255 );

	
	public VectorFlyer( float accel, float speed ) {
		baseAccel = accel;
		maxSpeed = speed;
		
		color = new TColorBlendBetween( BLACK, MODE_SET_BLUE );
	}

	public VectorFlyer( float accel, float speed, PVector startPos ) {
		position.set( startPos );
		baseAccel = accel;
		maxSpeed = speed;
		
		color = new TColorBlendBetween( BLACK, MODE_SET_BLUE );
	}

	public void update( PApplet p ) {
		update( p, true );
	}
	
	public void update( PApplet p, boolean draws ) {
		// color - if closer than threshold, ease towards saturated color
		p.noStroke();
		if( distToDest < 200 ) {
			p.fill(color.argbWithPercent(1f - distToDest/200f));
			// if( target != null ) BoxBetween.draw( p, new PVector(position.x, position.y, position.z), new PVector(target.x, target.y, target.z), 10 );
		} else {
			p.fill(color.argbWithPercent(0));
		}
		
		// store last position for rotation towards heading
		positionLast.set(position);
		
		//		accel = baseAccel * ( 0.5f + 0.6f * (float)Math.sin(p.frameCount * 0.01f) ); // was an effect to get particles to slow on following attractors 

		// always accelerate towards destination using basic xyz comparison & cap speed
		vector.x += ( position.x < target.x ) ? accel : -accel;
		vector.x = P.constrain(vector.x, -maxSpeed, maxSpeed);
		vector.y += ( position.y < target.y ) ? accel : -accel;
		vector.y = P.constrain(vector.y, -maxSpeed, maxSpeed);
		vector.z += ( position.z < target.z ) ? accel : -accel;
		vector.z = P.constrain(vector.z, -maxSpeed, maxSpeed);
		position.add(vector);
		
					
		if( draws == true ) { 
			// point and position
			p.pushMatrix();
			p.translate(position.x, position.y, position.z);
			OrientationUtil.setRotationTowards( p.g, new PVector(position.x, position.y, position.z), new PVector(positionLast.x, positionLast.y, positionLast.z) );
			p.box(20, 50, 20);
			p.popMatrix();
		}
	}
	
	public PVector position() {
		return position;
	}
	
	public void setPosition( PVector vec ) {
		position.set( vec );
	}
	
	public void setVector( PVector vec ) {
		vector.set( vec );
	}
	
	public void setTarget( PVector vec ) {
		target.set( vec );
	}
	
	public PVector findClosestPoint( ArrayList<PVector> points ) {
		// loop through attractors and store the closest & our distance for coloring
		PVector closest = null;
		float minDist = 999999;
		float distToPoint;
		for(int i=0; i < points.size(); i++) {
			distToPoint = position.dist( points.get(i) ); 
			if( distToPoint < minDist ) {
				minDist = distToPoint;
				distToDest = minDist;
				closest = points.get(i);
			}
		}
		return closest;
	}
}
