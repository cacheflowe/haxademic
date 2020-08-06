package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.polygons.Triangle3d;
import com.haxademic.core.media.DemoAssets;

import processing.core.PShape;
import processing.core.PVector;

public class Demo_PShapeUtil_rayIntersectsTriangle 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected Triangle3d[] faces;

	protected void firstFrame() {
		// load shape, scale & center
		shape = DemoAssets.objSkullRealistic().getTessellation();
//		shape = PShapeUtil.createBox(100, 100, 100, 0xff00ff00).getTessellation();
		PShapeUtil.scaleShapeToHeight(shape, p.height * 0.45f);
		PShapeUtil.centerShape(shape);
		faces = PShapeUtil.getTesselatedFaces(shape);
	}
	
	protected void drawApp() {
		// set context
		p.background(0);
		PG.setBetterLights(p.g);
		PG.setCenterScreen(p.g);
		PG.setDrawCorner(p.g);
		PG.basicCameraFromMouse(p.g);
		
		// draw shape
		shape.enableStyle();
		shape.disableStyle();
//		p.fill(255,0,0, 50);
		p.noFill();
		p.stroke(0,255,0);
		p.strokeWeight(0.5f);
		p.shape(shape, 0, 0);
		
		// iterate & animate demo points
		float offset = p.frameCount/5f % 100;
		for (float x = -1000 + offset; x < 1000; x+=100) {
			float y = 200f * P.sin(x/50f + p.frameCount/30f);
			float z = 200f * P.cos(x/70f + p.frameCount/20f);
			// check point against all triangles
			int numCollisions = 0;
			for (int i = 0; i < faces.length; i++) {
				boolean collided = PShapeUtil.rayIntersectsTriangle(new PVector(x,y,z), new PVector(0,0,1f), faces[i].v1, faces[i].v2, faces[i].v3);
				if(collided) {
					numCollisions++;
					p.stroke(255,0,0);
					p.strokeWeight(4f);
					p.beginShape();
					p.vertex(faces[i].v1.x, faces[i].v1.y, faces[i].v1.z);
					p.vertex(faces[i].v2.x, faces[i].v2.y, faces[i].v2.z);
					p.vertex(faces[i].v3.x, faces[i].v3.y, faces[i].v3.z);
					p.endShape(P.CLOSE);
				}
			}
			
			// draw ray success
			p.strokeWeight(1f);
			p.push();
			p.translate(x, y, z);
			if(numCollisions % 2 == 1) {
				// inside mesh! an odd number of collisions means it's inside :) 
				p.stroke(255, 0, 0);
				p.fill(255, 0, 0);
			} else {
				// outside mesh!
				p.fill(255);
				p.stroke(255);
			}
			// ray
			p.line(0, 0, 0, 0, 0, 10000);
			// box
			p.noStroke();
			p.box(20);
			p.pop();
		}
	}
		
}
