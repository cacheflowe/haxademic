package com.haxademic.sketch.toxi_tests;

import java.util.ArrayList;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.vendor.Toxiclibs;

import toxi.color.TColor;
import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.geom.ZAxisCylinder;
import toxi.geom.mesh.TriangleMesh;

public class PointMeshToVec 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	public ArrayList<MovingBox> boxes;
	public AABB focus = new AABB(10);
	
	public void firstFrame() {

		
		boxes = new ArrayList<MovingBox>();
		for( int i=0; i < 10; i++ ) {
			boxes.add( new MovingBox() );
		}
		
		
//		box2.pointTowards(new Vec3D(0,500,500));
	}
	
	protected void config() {
		Config.setProperty( AppSettings.SUNFLOW, "false" );
		Config.setProperty( AppSettings.RENDERING_MOVIE, "false" );
	}
		
	public void drawApp() {
		PG.resetGlobalProps( p );
		PG.setCenterScreen(p);

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

			Toxiclibs.instance(p).toxi.mesh( mesh.copy().pointTowards(focus.sub(position), Vec3D.Z_AXIS).translate(position) );
	
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
			Toxiclibs.instance(p).toxi.mesh( meshhh.copy().pointTowards(focus.sub(position), Vec3D.Z_AXIS).translate(position) );
	
			p.fill(255, 0, 0);
			p.stroke(255, 0, 0);
			Toxiclibs.instance(p).toxi.mesh( focus.toMesh() );
			Toxiclibs.instance(p).toxi.line(position, focus);
		}
	}
	
}
