package com.haxademic.sketch.particle;

import java.util.ArrayList;

import processing.core.PVector;
import toxi.color.TColor;
import toxi.geom.Sphere;
import toxi.geom.Vec3D;
import toxi.geom.mesh.WETriangleMesh;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.TColorInit;
import com.haxademic.core.draw.particle.VectorFlyer;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;

@SuppressWarnings("serial")
public class ModeSetLogoParticles
extends PAppletHax {
	
	WETriangleMesh _meshCrest;
	ArrayList<PVector> outerPoints;
	protected TColor MODE_SET_BLUE = TColorInit.newRGBA( 0, 200, 234, 255 );
	protected TColor MODE_SET_BLUE_TRANS = TColorInit.newRGBA( 0, 200, 234, 100 );
	protected TColor BLACK = TColor.BLACK.copy();

	public ArrayList<VectorFlyer> boxes;
	public ArrayList<Attractor> attractors;
	public ArrayList<PVector> attractorsPositions;

	
	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "800" );
		_appConfig.setProperty( "height", "800" );
		_appConfig.setProperty( "rendering", "false" );
	}
	
	public void setup() {
		super.setup();

		_meshCrest = new WETriangleMesh(); 
		manualBuildTriangleMesh();
		
		initFlyers();
		
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
	}
	
	protected void initFlyers() {
		attractors = new ArrayList<Attractor>();
		attractorsPositions = new ArrayList<PVector>();
		for( int i=0; i < outerPoints.size(); i++ ) {
			attractors.add( new Attractor( outerPoints.get(i) ) );
			attractorsPositions.add( attractors.get(i).position() );
		}

		boxes = new ArrayList<VectorFlyer>();
		for( int i=0; i < 150; i++ ) {
			boxes.add( new VectorFlyer( p.random(2.5f, 3.5f), p.random(18f, 35f) ) );
		}
	}

	
	public void drawApp() {
		DrawUtil.setBasicLights(p);
		DrawUtil.setCenterScreen(p);
		p.translate(0, 0, -1000);
		p.background(0);
		
		// set target of particle to closest attractor
		for( int i=0; i < boxes.size(); i++ ) {
			boxes.get(i).setTarget( boxes.get(i).findClosestPoint( attractorsPositions ) );
		}

		
//		p.rotateX(((float)p.mouseY - (float)p.height/2f) * -0.01f);
//		p.rotateY(((float)p.mouseX - (float)p.width/2f) * 0.01f);
		
		p.noFill();
		p.stroke( MODE_SET_BLUE.toARGB() );
//		p.noStroke();
//		p.fill( MODE_SET_BLUE.toARGB() );
		p.strokeWeight(3);
		p.strokeJoin(P.MITER); // BEVEL, ROUND
		p.toxi.mesh( _meshCrest );
		
		p.beginShape();
		for( int i=0; i < outerPoints.size(); i++ ) {
			p.vertex( outerPoints.get(i).x, outerPoints.get(i).y );
		}
		p.vertex( outerPoints.get(0).x, outerPoints.get(0).y );
		p.endShape();

		
//		DrawMesh.drawToxiMeshFacesNative( p, _meshCrest );
		
		p.stroke( MODE_SET_BLUE.toARGB() );

		for( int i=0; i < attractors.size(); i++ ) attractors.get(i).update();
		for( int i=0; i < boxes.size(); i++ ) boxes.get(i).update( p );

	}
	
	protected void manualBuildTriangleMesh() {
		
		outerPoints = new ArrayList<PVector>(); 
		
		// set up hard-coded outer logo _points in a 1000x1000 px space
		outerPoints.add( new PVector( 148 - 500, 346 - 500 ) );
		outerPoints.add( new PVector( 148 - 500, 662 - 500 ) );
		outerPoints.add( new PVector( 236 - 500, 790 - 500 ) );
		outerPoints.add( new PVector( 236 - 500, 644 - 500 ) );
		outerPoints.add( new PVector( 436 - 500, 932 - 500 ) );
		outerPoints.add( new PVector( 436 - 500, 726 - 500 ) );
		outerPoints.add( new PVector( 492 - 500, 658 - 500 ) );
		outerPoints.add( new PVector( 492 - 500, 974 - 500 ) );
		outerPoints.add( new PVector( 694 - 500, 782 - 500 ) );
		outerPoints.add( new PVector( 694 - 500, 622 - 500 ) );
		outerPoints.add( new PVector( 738 - 500, 582 - 500 ) );
		outerPoints.add( new PVector( 738 - 500, 668 - 500 ) );
		outerPoints.add( new PVector( 848 - 500, 568 - 500 ) );
		outerPoints.add( new PVector( 848 - 500, 22 - 500 ) );
		outerPoints.add( new PVector( 738 - 500, 128 - 500 ) );
		outerPoints.add( new PVector( 738 - 500, 284 - 500 ) );
		outerPoints.add( new PVector( 694 - 500, 326 - 500 ) );
		outerPoints.add( new PVector( 694 - 500, 238 - 500 ) );
		outerPoints.add( new PVector( 590 - 500, 336 - 500 ) );
		outerPoints.add( new PVector( 402 - 500, 64 - 500 ) );
		outerPoints.add( new PVector( 402 - 500, 336 - 500 ) );
		outerPoints.add( new PVector( 206 - 500, 52 - 500 ) );
		outerPoints.add( new PVector( 206 - 500, 282 - 500 ) );
		
		// connect sub-triangles to fill in the inside of the logo
//		addFaceToMesh( outerPoints.get(0), outerPoints.get(1), outerPoints.get(3) );
//		addFaceToMesh( outerPoints.get(1), outerPoints.get(2), outerPoints.get(3) );
//		addFaceToMesh( outerPoints.get(3), outerPoints.get(4), outerPoints.get(5) );
//		addFaceToMesh( outerPoints.get(3), outerPoints.get(5), outerPoints.get(6) );
//		addFaceToMesh( outerPoints.get(0), outerPoints.get(3), outerPoints.get(6) );
//		addFaceToMesh( outerPoints.get(6), outerPoints.get(7), outerPoints.get(8) );
//		addFaceToMesh( outerPoints.get(6), outerPoints.get(8), outerPoints.get(9) );
//		addFaceToMesh( outerPoints.get(10), outerPoints.get(11), outerPoints.get(12) );
//		addFaceToMesh( outerPoints.get(10), outerPoints.get(12), outerPoints.get(15) );
//		addFaceToMesh( outerPoints.get(12), outerPoints.get(15), outerPoints.get(13) );
//		addFaceToMesh( outerPoints.get(13), outerPoints.get(14), outerPoints.get(15) );
//		addFaceToMesh( outerPoints.get(10), outerPoints.get(15), outerPoints.get(16) );
//		addFaceToMesh( outerPoints.get(16), outerPoints.get(17), outerPoints.get(18) );
//		addFaceToMesh( outerPoints.get(9), outerPoints.get(10), outerPoints.get(16) );
//		addFaceToMesh( outerPoints.get(9), outerPoints.get(16), outerPoints.get(18) );
//		addFaceToMesh( outerPoints.get(6), outerPoints.get(9), outerPoints.get(18) );
//		addFaceToMesh( outerPoints.get(6), outerPoints.get(20), outerPoints.get(18) );
//		addFaceToMesh( outerPoints.get(18), outerPoints.get(19), outerPoints.get(20) );
//		addFaceToMesh( outerPoints.get(0), outerPoints.get(6), outerPoints.get(20) );
//		addFaceToMesh( outerPoints.get(20), outerPoints.get(21), outerPoints.get(22) );
//		addFaceToMesh( outerPoints.get(0), outerPoints.get(20), outerPoints.get(22) );
		
//		_meshCrest.scale(0.5f);
		
//		_meshCrest.subdivide( new DualSubdivision() );
	}
	
	protected void addFaceToMesh( PVector point1, PVector point2, PVector point3 ) {
		_meshCrest.addFace( 
				new Vec3D( point1.x, point1.y, 0 ), 
				new Vec3D( point2.x, point2.y, 0 ), 
				new Vec3D( point3.x, point3.y, 0 )
				);
	}
	
	
	
	public class Attractor {
		public Sphere box = new Sphere(30f);
		protected TColor fillColor = TColor.newHex("00ff00");
		protected PVector position = new PVector();
		
		public Attractor( PVector newPosition ) {
			position.set( newPosition );
			box.set(
					position.x,
					position.y,
					position.z
				);
		}
		
		public PVector position() {
			return position;
		}
		

		public void update() {
			// oscillate position

			// draw attractor
			p.fill( fillColor.toARGB() );
			p.noStroke();
//			p.toxi.mesh( box.toMesh(20) );
		}
	}
	
	
}