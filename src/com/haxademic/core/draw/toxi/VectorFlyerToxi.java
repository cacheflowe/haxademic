package com.haxademic.core.draw.toxi;

import toxi.color.TColor;
import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.geom.mesh.TriangleMesh;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.draw.color.TColorBlendBetween;
import com.haxademic.core.vendor.Toxiclibs;

public class VectorFlyerToxi {
	protected PAppletHax p;
	protected Vec3D positionLast = new Vec3D();
	protected Vec3D position = new Vec3D();
	protected Vec3D vector = new Vec3D();
	protected Vec3D target = new Vec3D();
	protected float accel = 1;
	protected float maxSpeed = 1;

	protected TriangleMesh mesh;
	protected float distToDest;
	protected TColorBlendBetween color;

	public VectorFlyerToxi( TColor colorLow, TColor colorHigh ) {
		DebugUtil.printErr("This VectorFlyer class should be deprecated");
		p = (PAppletHax) P.p;
		accel = p.random(0.5f, 8.0f);
		maxSpeed = p.random(5f, 45f);
		
		color = new TColorBlendBetween( colorLow, colorHigh );
		float size = p.random(10f,35f);
		
//		ZAxisCylinder cylinder = new ZAxisCylinder(new Vec3D(), size/8, size ); 
//		mesh = (TriangleMesh)cylinder.toMesh();
		mesh = (TriangleMesh)(new AABB(size)).toMesh();
		mesh.scale(new Vec3D(0.25f, 0.25f, 1));
//		mesh = MeshUtil.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/pointer_cursor_2_hollow.obj", 0.005f * size );
//		mesh.rotateX(P.PI/2f);
	}

	public void update() {
		// color - if closer than threshold, ease towards saturated color
		p.noStroke();
		p.fill(color.argbWithPercent(1f));
		
		// store last position for pointing towards heading
		positionLast.set(position);

		// always accelerate towards destination using basic xyz comparison & cap speed
		vector.x += ( position.x < target.x ) ? accel : -accel;
		vector.x = P.constrain(vector.x, -maxSpeed, maxSpeed);
		vector.y += ( position.y < target.y ) ? accel : -accel;
		vector.y = P.constrain(vector.y, -maxSpeed, maxSpeed);
		vector.z += ( position.z < target.z ) ? accel : -accel;
		vector.z = P.constrain(vector.z, -maxSpeed, maxSpeed);
		position.addSelf(vector);
					
		// point and position
		Toxiclibs.instance(p).toxi.mesh( mesh.copy().pointTowards(positionLast.sub(position), Vec3D.Z_AXIS).translate(position) );
	}
	
	public Vec3D position() {
		return position;
	}
	
	public void setTarget( Vec3D newTarget ) {
		target = newTarget;
	}
	
}
