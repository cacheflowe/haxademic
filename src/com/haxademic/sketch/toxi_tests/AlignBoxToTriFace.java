package com.haxademic.sketch.toxi_tests;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.vendor.Toxiclibs;

import toxi.geom.AABB;
import toxi.geom.Triangle3D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.TriangleMesh;
import toxi.processing.ToxiclibsSupport;

public class AlignBoxToTriFace 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	Triangle3D tri;
	TriangleMesh mesh;
	ToxiclibsSupport toxi;

	public void setupFirstFrame() {

		toxi = Toxiclibs.instance(p).toxi;
		randomize();
	}

	public void drawApp() {
		background(0);
		lights();
		translate(width/2, height/2, 0);
		p.rotateX(mouseY*0.01f);
		p.rotateY(mouseX*0.01f);
		// draw world space axes
		toxi.origin(300);
		// get triangle center and visualize normal vector
		Vec3D c=tri.computeCentroid();
		stroke(255, 0, 255);
		toxi.line(c, c.add(tri.computeNormal().scale(300)));
		noStroke();
		// draw triangle & mesh
		fill(255, 255, 0);
		toxi.triangle(tri);
		fill(0, 255, 255);
		toxi.mesh(mesh);
	}

	void randomize() {
		// create random triangle
		tri=new Triangle3D(
				Vec3D.randomVector().scale(100), 
				Vec3D.randomVector().scale(100), 
				Vec3D.randomVector().scale(100)
				);
		// create box mesh around origin
		mesh = (TriangleMesh)new AABB(50).toMesh();
		// get triangle normal
		Vec3D n=tri.computeNormal();
		// rotate mesh such that the +Z axis is aligned with the triangle normal
		mesh.pointTowards(n);
		// move box in the normal direction 100 units relative from the triangle center
		mesh.translate(tri.computeCentroid().add(n.scale(100)));
	}

	public void keyPressed() {
		if (key=='r') randomize();
	}

}
