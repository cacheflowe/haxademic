	package com.haxademic.sketch.toxi_tests;

import processing.core.PApplet;
import toxi.geom.mesh.SuperEllipsoid;
import toxi.geom.mesh.SurfaceFunction;
import toxi.geom.mesh.SurfaceMeshBuilder;
import toxi.geom.mesh.TriangleMesh;
import toxi.math.waves.AbstractWave;
import toxi.math.waves.SineWave;
import toxi.processing.ToxiclibsSupport;

public class SuperEllipse
extends PApplet{
	TriangleMesh mesh = new TriangleMesh();

	AbstractWave modX, modY;

	boolean isWireFrame;
	boolean showNormals;

	ToxiclibsSupport gfx;

	public void setup() {
	  size(1024,576, OPENGL);
	  modX = new SineWave(0, 0.01f, 2.5f, 2.5f);
	  modY = new SineWave(PI, 0.017f, 2.5f, 2.5f);
	  gfx=new ToxiclibsSupport(this);
	}

	public void draw() {
	  SurfaceFunction functor=new SuperEllipsoid(modX.update(), modY.update());
	  SurfaceMeshBuilder b = new SurfaceMeshBuilder(functor);
	  mesh = (TriangleMesh)b.createMesh(null,80, 80);
	  mesh.computeVertexNormals();
	  background(0);
	  lights();
	  translate(width / 2, height / 2, 0);
	  rotateX(mouseY * 0.01f);
	  rotateY(mouseX * 0.01f);
	  gfx.origin(300);
	  if (isWireFrame) {
	    noFill();
	    stroke(255);
	  } 
	  else {
	    fill(255);
	    noStroke();
	  }
	  mesh.scale(4);
//	  scale(2);
	  gfx.mesh(mesh, !isWireFrame, showNormals ? 10 : 0);
	}


	public void keyPressed() {
	  if (key == 'w') {
	    isWireFrame = !isWireFrame;
	  }
	  if (key == 'n') {
	    showNormals = !showNormals;
	  }
	  if (key == 's') {
	    mesh.saveAsSTL(sketchPath("superellipsoid-"+(System.currentTimeMillis()/1000)+".stl"));
	  }
	}
}
