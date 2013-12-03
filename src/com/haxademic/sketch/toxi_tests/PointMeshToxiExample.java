package com.haxademic.sketch.toxi_tests;

import java.util.ArrayList;
import java.util.List;

import toxi.geom.AABB;
import toxi.geom.Circle;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.TriangleMesh;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.core.app.PAppletHax;

public class PointMeshToxiExample 
extends PAppletHax {

	// container for mesh positions
	List<Vec3D> positions=new ArrayList<Vec3D>();

	ToxiclibsSupport toxi;

	public void setup() {
		super.setup();
		// compute mesh positions on circle in XZ plane
		Circle circle = new Circle(200);
		for(Vec2D p : circle.toPolygon2D(8).vertices) {
			positions.add(p.to3DXZ());
		}
	}

	public void drawApp() {
		background(51);
		lights();
		noStroke();
		translate(width/2,height/2,0);
		rotateX(-PI/6);
		// create manual focal point in XY plane
		Vec3D focus = new Vec3D(mouseX-width/2, mouseY-height/2, 0);
		// create mesh prototype to draw at all generated positions
		// the mesh is a simple box placed at the world origin
		TriangleMesh m = (TriangleMesh)new AABB(25).toMesh();
		// draw focus
		p.toxi.box(new AABB(focus, 5));
		for(Vec3D p : positions) {
			// align the positive z-axis of mesh to point at focus
			// mesh needs to be located at world origin for it to work correctly
			// only once rotated, move it to actual position
			this.p.toxi.mesh(m.copy().pointTowards(focus.sub(p), Vec3D.Z_AXIS).translate(p));
		}
		// draw connections from mesh centers to focal point
		stroke(0,255,255);
		for(Vec3D p : positions) {
			this.p.toxi.line(p, focus);
		}
	}


}
