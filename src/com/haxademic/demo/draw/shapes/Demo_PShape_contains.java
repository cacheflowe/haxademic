package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.polygons.Triangle3d;
import com.haxademic.core.media.DemoAssets;

import processing.core.PShape;
import processing.core.PVector;

public class Demo_PShape_contains 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected Triangle3d[] faces;

	protected void firstFrame() {
		// load shape, scale & center
		shape = DemoAssets.objSkullRealistic().getTessellation();
//		shape = PShapeUtil.createBox(100, 100, 100, 0xff00ff00).getTessellation();
		PShapeUtil.scaleShapeToHeight(shape, p.height * 0.75f);
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
		
		// set up demo points
		float offset = p.frameCount/5f % 100;
		for (float x = -1000 + offset; x < 1000; x+=100) {
			float y = 100f * P.sin(x/50f + p.frameCount/10f);
			// check point against all triangles
			int numCollisions = 0;
			for (int i = 0; i < faces.length; i++) {
				boolean collided = rayIntersectsTriangle(new PVector(x,y,0), new PVector(0,0,1f), faces[i].v1, faces[i].v2, faces[i].v3);
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
			p.translate(x, y, 0);
			if(numCollisions % 2 == 1) {
				// inside mesh!
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
	
	// https://en.wikipedia.org/wiki/M%C3%B6ller%E2%80%93Trumbore_intersection_algorithm
    private static final float EPSILON = 0.0000001f;
    public static boolean rayIntersectsTriangle(PVector rayOrigin, 
                                                PVector rayVector,
                                                PVector vertex0,
                                                PVector vertex1,
                                                PVector vertex2) {
        PVector edge1 = new PVector();
        PVector edge2 = new PVector();
        PVector h = new PVector();
        PVector s = new PVector();
        PVector q = new PVector();
        float a, f, u, v;
        edge1 = PVector.sub(vertex1, vertex0);
        edge2 = PVector.sub(vertex2, vertex0);
        h = rayVector.cross(edge2);
        a = edge1.dot(h);
        if (a > -EPSILON && a < EPSILON) {
            return false;    // This ray is parallel to this triangle.
        }
        f = 1.0f / a;
        s = PVector.sub(rayOrigin, vertex0);
        u = f * (s.dot(h));
        if (u < 0.0 || u > 1.0) {
            return false;
        }
        q = s.cross(edge1);
        v = f * rayVector.dot(q);
        if (v < 0.0 || u + v > 1.0) {
            return false;
        }
        // At this stage we can compute t to find out where the intersection point is on the line.
        float t = f * edge2.dot(q);
        return (t > EPSILON); // ray intersection
    }
	
}
