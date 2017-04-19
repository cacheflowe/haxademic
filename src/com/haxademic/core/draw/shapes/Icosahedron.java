package com.haxademic.core.draw.shapes;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

import com.haxademic.core.app.P;


//ported to Processing 2.0b8 by Amnon Owed (10/05/2013)
//from code by Gabor Papp (13/03/2010): http://git.savannah.gnu.org/cgit/fluxus.git/tree/libfluxus/src/GraphicsUtils.cpp
//based on explanation by Paul Bourke (01/12/1993): http://paulbourke.net/geometry/platonic
//using vertex/face list by Craig Reynolds: http://paulbourke.net/geometry/platonic/icosahedron.vf

public class Icosahedron {
	ArrayList <PVector> positions = new ArrayList <PVector> ();
	ArrayList <PVector> normals = new ArrayList <PVector> ();
	ArrayList <PVector> texCoords = new ArrayList <PVector> ();

	public Icosahedron(int level) {
		float sqrt5 = P.sqrt(5);
		float phi = (1f + sqrt5) * 0.5f;
		float ratio = P.sqrt(10f + (2f * sqrt5)) / (4f * phi);
		float a = (1f / ratio) * 0.5f;
		float b = (1f / ratio) / (2f * phi);

		PVector[] vertices = {
				new PVector( 0,  b, -a), 
				new PVector( b,  a,  0), 
				new PVector(-b,  a,  0), 
				new PVector( 0,  b,  a), 
				new PVector( 0, -b,  a), 
				new PVector(-a,  0,  b), 
				new PVector( 0, -b, -a), 
				new PVector( a,  0, -b), 
				new PVector( a,  0,  b), 
				new PVector(-a,  0, -b), 
				new PVector( b, -a,  0), 
				new PVector(-b, -a,  0)
		};

		int[] indices = { 
				0,1,2,    3,2,1,
				3,4,5,    3,8,4,
				0,6,7,    0,9,6,
				4,10,11,  6,11,10,
				2,5,9,    11,9,5,
				1,7,8,    10,8,7,
				3,5,2,    3,1,8,
				0,2,9,    0,7,1,
				6,9,11,   6,10,7,
				4,11,5,   4,8,10
		};

		for (int i=0; i<indices.length; i += 3) {
			makeIcosphereFace(vertices[indices[i]],  vertices[indices[i+1]],  vertices[indices[i+2]],  level);
		}
	}

	void makeIcosphereFace(PVector a, PVector b, PVector c, int level) {

		if (level <= 1) {

			// cartesian to spherical coordinates
			PVector ta = new PVector(P.atan2(a.z, a.x) / P.TWO_PI + 0.5f, P.acos(a.y) / P.PI);
			PVector tb = new PVector(P.atan2(b.z, b.x) / P.TWO_PI + 0.5f, P.acos(b.y) / P.PI);
			PVector tc = new PVector(P.atan2(c.z, c.x) / P.TWO_PI + 0.5f, P.acos(c.y) / P.PI);

			// texture wrapping coordinate limits
			float mint = 0.25f;
			float maxt = 1 - mint;

			// fix north and south pole textures
			if ((a.x == 0) && ((a.y == 1) || (a.y == -1))) {
				ta.x = (tb.x + tc.x) / 2;
				if (((tc.x < mint) && (tb.x > maxt)) || ((tb.x < mint) && (tc.x > maxt))) { ta.x += 0.5; }
			} else if ((b.x == 0) && ((b.y == 1) || (b.y == -1))) {
				tb.x = (ta.x + tc.x) / 2;
				if (((tc.x < mint) && (ta.x > maxt)) || ((ta.x < mint) && (tc.x > maxt))) { tb.x += 0.5; }
			} else if ((c.x == 0) && ((c.y == 1) || (c.y == -1))) {
				tc.x = (ta.x + tb.x) / 2;
				if (((ta.x < mint) && (tb.x > maxt)) || ((tb.x < mint) && (ta.x > maxt))) { tc.x += 0.5; }
			}

			// fix texture wrapping
			if ((ta.x < mint) && (tc.x > maxt)) {
				if (tb.x < mint) { tc.x -= 1; } else { ta.x += 1; }
			} else if ((ta.x < mint) && (tb.x > maxt)) {
				if (tc.x < mint) { tb.x -= 1; } else { ta.x += 1; }
			} else if ((tc.x < mint) && (tb.x > maxt)) {
				if (ta.x < mint) { tb.x -= 1; } else { tc.x += 1; }
			} else if ((ta.x > maxt) && (tc.x < mint)) {
				if (tb.x < mint) { ta.x -= 1; } else { tc.x += 1; }
			} else if ((ta.x > maxt) && (tb.x < mint)) {
				if (tc.x < mint) { ta.x -= 1; } else { tb.x += 1; }
			} else if ((tc.x > maxt) && (tb.x < mint)) {
				if (ta.x < mint) { tc.x -= 1; } else { tb.x += 1; }
			}

			addVertex(a, a, ta);
			addVertex(c, c, tc);
			addVertex(b, b, tb);

		} else { // level > 1

			PVector ab = midpointOnSphere(a, b);
			PVector bc = midpointOnSphere(b, c);
			PVector ca = midpointOnSphere(c, a);

			level--;
			makeIcosphereFace(a, ab, ca, level);
			makeIcosphereFace(ab, b, bc, level);
			makeIcosphereFace(ca, bc, c, level);
			makeIcosphereFace(ab, bc, ca, level);
		}
	}

	void addVertex(PVector p, PVector n, PVector t) {
		positions.add(p);
		normals.add(n);
		t.set(1.0f-t.x, 1.0f-t.y, t.z);
		texCoords.add(t);
	}

	PVector midpointOnSphere(PVector a, PVector b) {
		PVector midpoint = PVector.add(a, b);
		midpoint.mult(0.5f);
		midpoint.normalize();
		return midpoint;
	}

	public static PShape createIcosahedron(PApplet p, int level, PImage img) {
		// the icosahedron is created with positions, normals and texture coordinates in the above class
		Icosahedron ico = new Icosahedron(level);
		p.textureMode(P.NORMAL); // set textureMode to normalized (range 0 to 1);
		PShape mesh = p.createShape(); // create the initial PShape

		mesh.beginShape(P.TRIANGLES); // define the PShape type: TRIANGLES
		mesh.strokeWeight(0);
		mesh.noStroke();
		if(img != null) mesh.texture(img);

		// put all the vertices, uv texture coordinates and normals into the PShape
		for (int i=0; i<ico.positions.size(); i++) {
			PVector pos = ico.positions.get(i);
			PVector t = ico.texCoords.get(i);
			PVector n = ico.normals.get(i);
			mesh.normal(n.x, n.y, n.z);
			mesh.vertex(pos.x, pos.y, pos.z, t.x, t.y);
		}

		mesh.endShape();

		return mesh; // our work is done here, return DA MESH! ;-)
	}
	
	public static PShape createIcosahedronGrouped(PApplet p, int level, PImage img, int fillColor, int strokeColor, float strokeWeight) {
		// the icosahedron is created with positions, normals and texture coordinates in the above class
		Icosahedron ico = new Icosahedron(level);
		p.textureMode(P.NORMAL); // set textureMode to normalized (range 0 to 1);
		PShape mesh = p.createShape(P.GROUP); // create the initial PShape
		
		
		// put all the vertices, uv texture coordinates and normals into the PShape
		PShape triangle = null;
		for (int i=0; i<ico.positions.size(); i++) {
			if(i % 3 == 0) {
				triangle = p.createShape();
				triangle.beginShape(P.TRIANGLE); // define the PShape type: TRIANGLES
//				triangle.strokeWeight(strokeWeight);
//				triangle.stroke(strokeColor);
//				triangle.fill(fillColor);
				if(img != null) mesh.texture(img);
			}
			PVector pos = ico.positions.get(i);
			PVector t = ico.texCoords.get(i);
			PVector n = ico.normals.get(i);
			triangle.normal(n.x, n.y, n.z);
			triangle.vertex(pos.x, pos.y, pos.z, t.x, t.y);
			
			if(i % 3 == 2) {
				triangle.endShape();
				mesh.addChild(triangle);
			}
		}
		
		return mesh; // our work is done here, return DA MESH! ;-)
	}
}


