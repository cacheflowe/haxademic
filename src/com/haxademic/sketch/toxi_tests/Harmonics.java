package com.haxademic.sketch.toxi_tests;

import java.util.Iterator;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.AABB;
import toxi.geom.Matrix4x4;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.SphericalHarmonics;
import toxi.geom.mesh.SurfaceMeshBuilder;
import toxi.geom.mesh.TriangleMesh;
import toxi.geom.mesh.Vertex;

@SuppressWarnings("serial")
public class Harmonics
extends PApplet {
	TriangleMesh mesh = new TriangleMesh();

	boolean isWireFrame;
	boolean showNormals;
	boolean doSave;

	Matrix4x4 normalMap = new Matrix4x4().translateSelf(128,128,128).scaleSelf(127);
	float[] m=new float[8];

	public void setup() {
	  size(1024,576, OPENGL);
	  randomizeMesh();
	}

	public void draw() {
		// inrcrementally update harmonics
		if(frameCount%1 == 0) {
		  for(int i=0; i<8; i++) {
			  if(i==0||i==6)m[i]=sin(i+frameCount/100f)*4.5f + 4.5f;
//			  else m[i]=7;
//			    println(m[i]);
			  }
		  SurfaceMeshBuilder b = new SurfaceMeshBuilder(new SphericalHarmonics(m));
		  mesh = (TriangleMesh)b.createMesh(null,80, 60);
//		  mesh.scale(2);
		}
		
	  background(0);
	  translate(width / 2, height / 2, 0);
	  rotateX(mouseY * 0.01f);
	  rotateY(mouseX * 0.01f);
	  lights();
	  shininess(16);
//	  directionalLight(255,255,255,0,-1,1);
//	  specular(255);
	  drawAxes(400);
	  if (isWireFrame) {
	    noFill();
	    stroke(255);
	  } 
	  else {
	    fill(255);
	    noStroke();
	  }
	  drawMesh(g, mesh, !isWireFrame, showNormals);
	  if (doSave) {
	    saveFrame("sh-"+(System.currentTimeMillis()/1000)+".png");
	    doSave=false;
	  }
	}

	public void drawAxes(float l) {
	  stroke(255, 0, 0);
	  line(0, 0, 0, l, 0, 0);
	  stroke(0, 255, 0);
	  line(0, 0, 0, 0, l, 0);
	  stroke(0, 0, 255);
	  line(0, 0, 0, 0, 0, l);
	}

	public void drawMesh(PGraphics gfx, TriangleMesh mesh, boolean vertexNormals, boolean showNormals) {
	  gfx.beginShape(PConstants.TRIANGLES);
	  AABB bounds=mesh.getBoundingBox();
	  Vec3D min=bounds.getMin();
	  Vec3D max=bounds.getMax();
//	  if (vertexNormals) {
//	    for (Iterator i=mesh.faces.iterator(); i.hasNext();) {
//	      Face f=(Face)i.next();
//	      Vec3D n = normalMap.applyTo(f.a.normal);
//	      gfx.fill(n.x, n.y, n.z);
//	      gfx.normal(f.a.normal.x, f.a.normal.y, f.a.normal.z);
//	      gfx.vertex(f.a.x, f.a.y, f.a.z);
//	      n = normalMap.applyTo(f.b.normal);
//	      gfx.fill(n.x, n.y, n.z);
//	      gfx.normal(f.b.normal.x, f.b.normal.y, f.b.normal.z);
//	      gfx.vertex(f.b.x, f.b.y, f.b.z);
//	      n = normalMap.applyTo(f.c.normal);
//	      gfx.fill(n.x, n.y, n.z);
//	      gfx.normal(f.c.normal.x, f.c.normal.y, f.c.normal.z);
//	      gfx.vertex(f.c.x, f.c.y, f.c.z);
//	    }
//	  } 
	  if (vertexNormals) {
		  float grey = 0;
		    for (Iterator i=mesh.faces.iterator(); i.hasNext();) {
		    	grey = grey%255;
		      Face f=(Face)i.next();
		      Vec3D n = normalMap.applyTo(f.a.normal);
		      gfx.fill(grey);
		      gfx.normal(f.a.normal.x, f.a.normal.y, f.a.normal.z);
		      gfx.vertex(f.a.x, f.a.y, f.a.z);
		      n = normalMap.applyTo(f.b.normal);
		      gfx.fill(grey);
		      gfx.normal(f.b.normal.x, f.b.normal.y, f.b.normal.z);
		      gfx.vertex(f.b.x, f.b.y, f.b.z);
		      n = normalMap.applyTo(f.c.normal);
		      gfx.fill(grey);
		      gfx.normal(f.c.normal.x, f.c.normal.y, f.c.normal.z);
		      gfx.vertex(f.c.x, f.c.y, f.c.z);
		      
		      grey += 0.01;
		    }
		  } 
	  else {
	    for (Iterator i=mesh.faces.iterator(); i.hasNext();) {
	      Face f=(Face)i.next();
	      gfx.normal(f.normal.x, f.normal.y, f.normal.z);
	      gfx.vertex(f.a.x, f.a.y, f.a.z);
	      gfx.vertex(f.b.x, f.b.y, f.b.z);
	      gfx.vertex(f.c.x, f.c.y, f.c.z);
	    }
	  }
	  gfx.endShape();
	  if (showNormals) {
	    if (vertexNormals) {
	      for (Iterator i=mesh.vertices.values().iterator(); i.hasNext();) {
	        Vertex v=(Vertex)i.next();
	        Vec3D w = v.add(v.normal.scale(10));
	        Vec3D n = v.normal.scale(127);
	        gfx.stroke(n.x + 128, n.y + 128, n.z + 128);
	        gfx.line(v.x, v.y, v.z, w.x, w.y, w.z);
	      }
	    } 
	    else {
	      for (Iterator i=mesh.faces.iterator(); i.hasNext();) {
	        Face f=(Face)i.next();
	        Vec3D c = f.a.add(f.b).addSelf(f.c).scaleSelf(1f / 3);
	        Vec3D d = c.add(f.normal.scale(20));
	        Vec3D n = f.normal.scale(127);
	        gfx.stroke(n.x + 128, n.y + 128, n.z + 128);
	        gfx.line(c.x, c.y, c.z, d.x, d.y, d.z);
	      }
	    }
	  }
	}

	public void keyPressed() {
	  if (key == 'r') {
	    randomizeMesh();
	  }
	  if (key == 'w') {
	    isWireFrame = !isWireFrame;
	  }
	  if (key == 'n') {
	    showNormals = !showNormals;
	  }
	  if (key == 's') {
//	    mesh.saveAsSTL(sketchPath("superellipsoid-"+(System.currentTimeMillis()/1000)+".stl"));
	  }
	  if (key == ' ') {
	    doSave=true;
	  }
	}

	public void randomizeMesh() {
	  for(int i=0; i<8; i++) {
	    m[i]=(int)random(9);
	  }
	  SurfaceMeshBuilder b = new SurfaceMeshBuilder(new SphericalHarmonics(m));
	  mesh = (TriangleMesh)b.createMesh(null,1, 1);
	}

}
