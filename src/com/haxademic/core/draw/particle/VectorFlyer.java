package com.haxademic.core.draw.particle;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.draw.context.OrientationUtil;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class VectorFlyer {

	protected PVector positionLast = new PVector();
	protected PVector position = new PVector();
	protected PVector vector = new PVector();
	protected PVector target = new PVector();
	protected float accel = 1;
	protected float baseAccel = 1;
	protected float maxSpeed = 1;

	protected float distToDest;
	protected EasingColor color;
	protected EasingColor color2;

	public VectorFlyer( float accel, float speed ) {
		baseAccel = this.accel = accel;
		maxSpeed = speed;
		
		color = new EasingColor(255, 255, 255);
		color2 = new EasingColor(0, 200, 234);
	}

	public VectorFlyer( float accel, float speed, PVector startPos ) {
		this(accel, speed);
		position.set( startPos );
	}

	public void update( PApplet p ) {
		update( p.g, true );
	}
	
	public void update( PGraphics pg, boolean draws ) {
		// color - if closer than threshold, ease towards saturated color
		pg.noStroke();
		if( distToDest < 200 ) {
			pg.fill(color.colorIntMixedWith(color2, 1f - distToDest/200f));
			// if( target != null ) BoxBetween.draw( p, new PVector(position.x, position.y, position.z), new PVector(target.x, target.y, target.z), 10 );
		} else {
			pg.fill(color.colorInt());
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
			pg.pushMatrix();
			pg.translate(position.x, position.y, position.z);
			OrientationUtil.setRotationTowards( pg, new PVector(position.x, position.y, position.z), new PVector(positionLast.x, positionLast.y, positionLast.z) );
			pg.box(10, 30, 10);
			pg.popMatrix();
			
			// line to target
			pg.stroke(255);
			pg.line(position.x, position.y, position.z, target.x, target.y, target.z);
		}
	}
	
	public void setAccel( float accel ) {
		this.baseAccel = this.accel = accel;
	}
	public void setSpeed( float speed ) {
		this.maxSpeed = speed;
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
