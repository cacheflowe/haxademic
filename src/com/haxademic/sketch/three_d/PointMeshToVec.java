package com.haxademic.sketch.three_d;

import java.util.ArrayList;

import toxi.color.TColor;
import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.geom.ZAxisCylinder;
import toxi.geom.mesh.TriangleMesh;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;

public class PointMeshToVec 
extends PAppletHax {
	
	public ArrayList<MovingBox> boxes;
	public AABB focus = new AABB(10);
	
	public void setup() {
		super.setup();
		
		boxes = new ArrayList<MovingBox>();
		for( int i=0; i < 10; i++ ) {
			boxes.add( new MovingBox() );
		}
		
		
//		box2.pointTowards(new Vec3D(0,500,500));
	}
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "sunflow", "false" );
		_appConfig.setProperty( "rendering", "false" );
	}
		
	public void drawApp() {
		DrawUtil.resetGlobalProps( p );
		DrawUtil.setCenter( p );

		p.shininess(1000f); 
		p.lights();
		p.background(0);
		p.smooth();

		p.translate( 0, 0, -400 );
		
		TColor fill = new TColor( TColor.WHITE ).setAlpha( 1.0f );
		TColor stroke = TColor.newRGB( 200, 200, 200 ).setAlpha( 0.3f );
		p.fill( fill.toARGB() );
		p.stroke( stroke.toARGB() );
		
		p.rotateY( p.mouseX / 100f );
		p.rotateZ( p.mouseY / 100f );		
		
		// set focal point
		focus.set(0, mouseY-height/2, 0);
		
		for( int i=0; i < boxes.size(); i++ ) {
			boxes.get(i).update();
		}

		
//		Vec3D newVec = box2.interpolateTo(box1, 0.1f);
	}

	public class MovingBox {
		
		public Vec3D position = new Vec3D(0,0,0);
		public Vec3D vector = new Vec3D();
		public TriangleMesh mesh;
		public ZAxisCylinder cone;
		public AABB box;
		
		public MovingBox() {
			float size = p.random(80,400);
			cone = new ZAxisCylinder(new Vec3D(), size/25, size ); 
			mesh = (TriangleMesh)cone.toMesh();
			
			vector.x = p.random(-10,10);
			vector.y = p.random(-10,10);
			vector.z = p.random(-10,10);
			
		}
		
		public void update() {
			p.noStroke();
			p.fill(255);
			
			position.addSelf(vector);

			p.toxi.mesh( mesh.copy().pointTowards(focus.sub(position), Vec3D.Z_AXIS).translate(position) );
	
			// debug positioning
//			p.fill(0, 255, 0);
//			p.stroke(0, 255, 0);
//			p.toxi.mesh( focus.toMesh() );
//			p.toxi.line(position, focus);
		}
		
		public void updateBoxes() {
			p.noStroke();
			p.fill(255);
			
			position.addSelf(vector);
			box.set(position);

			TriangleMesh meshhh = (TriangleMesh)box.toMesh();
			p.toxi.mesh( meshhh.copy().pointTowards(focus.sub(position), Vec3D.Z_AXIS).translate(position) );
	
			p.fill(255, 0, 0);
			p.stroke(255, 0, 0);
			p.toxi.mesh( focus.toMesh() );
			p.toxi.line(position, focus);
		}
	}
	
}
