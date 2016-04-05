package com.haxademic.sketch.text;

import geomerative.RCommand;
import geomerative.RFont;
import geomerative.RG;
import geomerative.RGroup;
import geomerative.RMesh;
import geomerative.RPoint;
import processing.core.PApplet;
import toxi.geom.Vec3D;
import toxi.geom.mesh.WETriangleMesh;
import toxi.processing.ToxiclibsSupport;
//import toxi.volume.MeshVoxelizer;

public class TextGeomExtrude
extends PApplet{

	RFont font;
	ToxiclibsSupport toxi;
	WETriangleMesh weMesh;

	public void setup() {
		size(1200,800,OPENGL);
//		size(1200,800, "hipstersinc.P5Sunflow" );

		frameRate( 30 );
		smooth();

		RG.init(this);
		toxi = new ToxiclibsSupport( this );

		font = new RFont( "../data/fonts/bitlow.ttf", 200, RFont.CENTER);
		buildToxiMesh();
	}

	protected void buildToxiMesh() {
		RCommand.setSegmentLength(8);
		RCommand.setSegmentator(RCommand.UNIFORMLENGTH);

		RGroup grp = font.toGroup("HAI THERE");
		// RPoint[] pnts = grp.getPoints();

		RMesh rMesh = grp.toMesh();
		weMesh = new WETriangleMesh();

		for ( int i = 0; i < rMesh.strips.length; i++ ) {
			RPoint[] meshPoints = rMesh.strips[i].getPoints();

			for ( int ii = 0; ii < meshPoints.length - 2; ii++ ) {
				weMesh.addFace( 
						new Vec3D( meshPoints[ii].x, meshPoints[ii].y, 0 ), 
						new Vec3D( meshPoints[ii+1].x, meshPoints[ii+1].y, 0 ), 
						new Vec3D( meshPoints[ii+2].x, meshPoints[ii+2].y, 0 ) 
				);
			}
		}
	}

	protected void buildToxiMesh3D() {
		RCommand.setSegmentLength(8);
		RCommand.setSegmentator(RCommand.UNIFORMLENGTH);

		RGroup grp = font.toGroup("MODE SET");
		// RPoint[] pnts = grp.getPoints();

		RMesh rMesh = grp.toMesh();
		weMesh = new WETriangleMesh();

		float depth = 30;

		for ( int i = 0; i < rMesh.strips.length; i++ ) {
			RPoint[] meshPoints = rMesh.strips[i].getPoints();

			for ( int ii = 0; ii < meshPoints.length - 2; ii++ ) {
				// draw front/back faces
				weMesh.addFace( 
						new Vec3D( meshPoints[ii].x, meshPoints[ii].y, depth ), 
						new Vec3D( meshPoints[ii+1].x, meshPoints[ii+1].y, depth ), 
						new Vec3D( meshPoints[ii+2].x, meshPoints[ii+2].y, depth ) 
				);
				weMesh.addFace( 
						new Vec3D( meshPoints[ii].x, meshPoints[ii].y, -depth ), 
						new Vec3D( meshPoints[ii+1].x, meshPoints[ii+1].y, -depth ), 
						new Vec3D( meshPoints[ii+2].x, meshPoints[ii+2].y, -depth ) 
				);
				
				// draw walls - close the 2 triangles drawn above
				addTriQuadToMeshWith4Points( 
						weMesh, 
						new Vec3D( meshPoints[ii].x, meshPoints[ii].y, depth ),
						new Vec3D( meshPoints[ii].x, meshPoints[ii].y, -depth ),
						new Vec3D( meshPoints[ii+1].x, meshPoints[ii+1].y, -depth ),
						new Vec3D( meshPoints[ii+1].x, meshPoints[ii+1].y, depth )
				);

				addTriQuadToMeshWith4Points( 
						weMesh, 
						new Vec3D( meshPoints[ii+1].x, meshPoints[ii+1].y, depth ),
						new Vec3D( meshPoints[ii+1].x, meshPoints[ii+1].y, -depth ),
						new Vec3D( meshPoints[ii+2].x, meshPoints[ii+2].y, -depth ),
						new Vec3D( meshPoints[ii+2].x, meshPoints[ii+2].y, depth )
				);

				addTriQuadToMeshWith4Points( 
						weMesh, 
						new Vec3D( meshPoints[ii+2].x, meshPoints[ii+2].y, depth ),
						new Vec3D( meshPoints[ii+2].x, meshPoints[ii+2].y, -depth ),
						new Vec3D( meshPoints[ii].x, meshPoints[ii].y, -depth ),
						new Vec3D( meshPoints[ii].x, meshPoints[ii].y, depth )
				);

			}
		}
	}
	
	public void addTriQuadToMeshWith4Points( WETriangleMesh mesh, Vec3D pt1, Vec3D pt2, Vec3D pt3, Vec3D pt4 ) {
		// make sure 4 points go in order around the square
		weMesh.addFace( pt1, pt2, pt3 );
		weMesh.addFace( pt3, pt4, pt1 );
	}

	public void draw() {
		background(10);
		translate(width/2,height/2,-600);
		rotateX(-mouseY/100f);
		rotateY(mouseX/100f);
//		println(mouseX/100f+","+(-mouseY/100f));
//		rotateX(5.79f);
//		rotateY(-5.95f);
		
		lights();
		shininess( 1000 );
		
		//	    drawText2d();
		drawText3d();
	}

	public void drawText2d() {
		RGroup grp = font.toGroup("Hx");

		// die folgenden einstellungen beinflussen wieviele punkte die
		// polygone am ende bekommen werden.

		//RCommand.setSegmentStep(random(0,3));
		//RCommand.setSegmentator(RCommand.UNIFORMSTEP);

		RCommand.setSegmentLength(1);
		RCommand.setSegmentator(RCommand.UNIFORMLENGTH);

		//RCommand.setSegmentAngle(random(0,HALF_PI));
		//RCommand.setSegmentator(RCommand.ADAPTATIVE);

		RPoint[] pnts = grp.getPoints();

		for ( int i = 0; i < pnts.length; i++ ) {
			line( pnts[i-1].x, pnts[i-1].y, pnts[i].x, pnts[i].y );
		}

	}

	public void drawText3d() {
		fill(255,127,0,255);
		fill(255,249,0, 255);
		noStroke();
//		stroke(0);
		toxi.mesh( weMesh );
	}
}
