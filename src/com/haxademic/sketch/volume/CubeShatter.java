package com.haxademic.sketch.volume;

import java.util.ArrayList;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;

import toxi.geom.Vec3D;
import toxi.geom.mesh.WETriangleMesh;
import toxi.processing.ToxiclibsSupport;
import wblut.geom.WB_Plane;
import wblut.hemesh.HEC_Box;
import wblut.hemesh.HEC_Geodesic;
import wblut.hemesh.HEMC_VoronoiCells;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_MeshCollection;
import wblut.processing.WB_Render;

public class CubeShatter
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }


	// from example: http://www.wblut.com/2010/10/20/hemesh-voronoi-example/
	float[][] points;
	int numpoints;
	HE_Mesh container;
	HE_MeshCollection cells;
	int numcells;
	WB_Plane P1,P2;
	WB_Render render;
	
	ArrayList<WETriangleMesh> meshes;
	ToxiclibsSupport toxi;

	public void setup () {
		super.setup();

		p.shininess(1000); 
		p.lights();
		
		toxi = new ToxiclibsSupport( p );

		createSphere();
	}

	protected void createSphere() {
		render=new WB_Render(this);

		//create a sphere
		HEC_Geodesic sphere=new HEC_Geodesic();
		sphere.setRadius(300);//.setLevel(2); 
		// or a box
		HEC_Box box=new HEC_Box();
		box.setWidth( 300 );
		box.setHeight( 300 );
		box.setDepth( 300 );
		
		container = new HE_Mesh(box);

		//slice off most of both hemispheres
		//		  P1=new WB_Plane(new WB_Point3d(0f,0f,-10f), new WB_Vector3d(0f,0f,1f));
		//		  P2=new WB_Plane(new WB_Point3d(0f,0f,10f), new WB_Vector3d(0f,0f,-1f));
		//		  HEM_Slice s=new HEM_Slice().setPlane(P1);
		//		  container.modify(s);
		//		  s=new HEM_Slice().setPlane(P2);
		//		  container.modify(s);

		//generate points
		numpoints=15;
		points=new float[numpoints][3];
		for(int i=0;i<numpoints;i++) {
			points[i][0]=random(-250,250);
			points[i][1]=random(-250,250);
			points[i][2]=random(-250,250);
		}

		//generate voronoi cells
		HEMC_VoronoiCells vcmc=new HEMC_VoronoiCells();
		vcmc.setPoints(points).setContainer(container).setOffset(5);
		cells=vcmc.create();
		numcells=cells.size();
		
		
		// convert to toxiclibs
		HE_Mesh cell;
		HE_Face[] faces;
		meshes = new ArrayList<WETriangleMesh>();
		for(int i=0;i<numcells;i++) {			
			cell = cells.getMesh(i);
			faces = cell.getFacesAsArray();
			
			WETriangleMesh toxiMesh = new WETriangleMesh(); 
			meshes.add( toxiMesh );
			
			for(int j=0;j<faces.length;j++) {
				int numVertices = faces[j].getFaceVertices().size();
				if( numVertices == 3 ) {
					// straight triangle conversion 
					toxiMesh.addFace( 
						new Vec3D( faces[j].getFaceVertices().get( 0 ).xf(), faces[j].getFaceVertices().get( 0 ).yf(), faces[j].getFaceVertices().get( 0 ).zf() ), 
						new Vec3D( faces[j].getFaceVertices().get( 1 ).xf(), faces[j].getFaceVertices().get( 1 ).yf(), faces[j].getFaceVertices().get( 1 ).zf() ), 
						new Vec3D( faces[j].getFaceVertices().get( 2 ).xf(), faces[j].getFaceVertices().get( 2 ).yf(), faces[j].getFaceVertices().get( 2 ).zf() )
					);
				} else if( numVertices > 3 ) {
					// subdivide polygons to triangles
					int trisLeft = 1 + numVertices - 3;
					int firstVertex = 1;
					while( trisLeft > 0 ) {
						toxiMesh.addFace(
								new Vec3D( faces[j].getFaceVertices().get( 0 ).xf(), faces[j].getFaceVertices().get( 0 ).yf(), faces[j].getFaceVertices().get( 0 ).zf() ), 
								new Vec3D( faces[j].getFaceVertices().get( firstVertex ).xf(), faces[j].getFaceVertices().get( firstVertex ).yf(), faces[j].getFaceVertices().get( firstVertex ).zf() ), 
								new Vec3D( faces[j].getFaceVertices().get( firstVertex + 1 ).xf(), faces[j].getFaceVertices().get( firstVertex + 1 ).yf(), faces[j].getFaceVertices().get( firstVertex + 1 ).zf() )
						);
						trisLeft--;
						firstVertex++;
					}
				} 
			}
		}
	}

	public void drawApp() {
		DrawUtil.setCenterScreen( p );
		p.translate(0,0,-500);
		p.background( 0, 0, 0 );

		rotateX(1f/height*mouseY*TWO_PI-PI);
		rotateY(1f/width*mouseX*TWO_PI-PI);
		
		// draw toxiclibs mesh
		fill(200);
		for(int j=0;j<meshes.size();j++) {
			toxi.mesh( meshes.get( j ) );
		}

		// draw hemesh & expand pieces
//		for(int i=0;i<numcells;i++) {
//			WB_Point3d pos = cells[i].getCenter();
//			cells[i].moveTo( new WB_Point3d( pos.x * 1.002f, pos.y * 1.002f, pos.z * 1.002f ) );
//		}
//
//		drawFaces();
//		drawEdges();
	}

	void drawEdges(){
		smooth();
		stroke(0);
		strokeWeight(2);
		for(int i=0;i<numcells;i++) {
			render.drawEdges( cells.getMesh(i) );
		} 
	}

	void drawFaces(){
		noSmooth();
		noStroke();
		for(int i=0;i<numcells;i++) {
			//fill(100+i,i,i);
			fill(200);
			render.drawFaces( cells.getMesh(i) );
		}   
	}
}
