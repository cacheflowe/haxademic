package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.BoxBetween;
import com.haxademic.core.math.easing.ElasticFloat;

import processing.core.PVector;

public class Demo_BoxBetween
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PVector point1 = new PVector();
	protected PVector point2 = new PVector();
	protected ElasticFloat polySides = new ElasticFloat(3, 0.5f, 0.5f);

	public void setupFirstFrame() {
		p.background( 0 );
		p.smooth();
		p.noStroke();
	}

	public void drawApp() {
		// context
		p.background(0);
		p.perspective();
		DrawUtil.setDrawCenter( p );
		DrawUtil.setBetterLights( p );
		p.translate(p.width/2, p.height/2, -p.width);
		p.noStroke();
		p.rotateX(P.HALF_PI * p.mousePercentY());
		p.rotateY(p.frameCount * 0.01f);
		
		// spiral
		float y = p.height * 2;
		float yInc = p.height * 0.05f;
		float pSides = 3 + 19f * p.mousePercentX();
		polySides.setTarget(pSides);
		polySides.setAccel(0.1f);
		polySides.setFriction(0.3f);
		polySides.update();
		float segmentRads = P.TWO_PI / polySides.value();
		float radius = p.width * 0.6f;
		float thickness = p.width * 0.05f;
		
		int index = 0;
		for( float yy=y; yy > -p.height * 2; yy -= yInc ) {
			float x = P.cos(index * segmentRads) * radius;
			float z = P.sin(index * segmentRads) * radius;
			
			// color cycle
			p.fill(
					127f + 127f * P.sin(1 + index * 0.1f + p.frameCount * 0.05f),
					127f + 127f * P.sin(2 + index * 0.1f + p.frameCount * 0.05f),
					127f + 127f * P.sin(0 + index * 0.1f + p.frameCount * 0.05f)
					);
			
//			p.fill(127f + 127f * P.sin(0 + index * 0.1f + p.frameCount * 0.05f));
			
//			p.fill(255f);
			p.pushMatrix();
			p.translate(x, yy, z);
			p.sphere(thickness * 1.0f);
			p.popMatrix();
			
			if(index > 0) {
				point1.set(x, yy, z);
				BoxBetween.draw( p, point1, point2, thickness );
			}
			
			point2.set(x, yy, z);
			index++;
		}
		
	}

}
