package com.haxademic.core.draw.shapes;

import java.util.ArrayList;

import processing.core.PApplet;
import toxi.geom.Vec3D;
import toxi.geom.mesh.WETriangleMesh;
import wblut.hemesh.core.HE_Face;
import wblut.hemesh.core.HE_Mesh;
import wblut.hemesh.creators.HEC_Box;
import wblut.hemesh.creators.HEMC_VoronoiCells;

public class Voronoi3D {
	
	public static ArrayList<WETriangleMesh> getShatteredBox( PApplet p, float scale ) {
		
		// from example: http://www.wblut.com/2010/10/20/hemesh-voronoi-example/
		ArrayList<WETriangleMesh> meshes = new ArrayList<WETriangleMesh>();

		// or a box
		HEC_Box box=new HEC_Box();
		box.setWidth( 100 );
		box.setHeight( 100 );
		box.setDepth( 100 );
		HE_Mesh container = new HE_Mesh(box);

		// generate fracture points
		int numpoints = 15;
		float[][] points = new float[numpoints][3];
		for(int i=0;i<numpoints;i++) {
			points[i][0] = p.random(-100,100);
			points[i][1] = p.random(-100,100);
			points[i][2] = p.random(-100,100);
		}

		// generate voronoi cells
		HEMC_VoronoiCells vcmc = new HEMC_VoronoiCells();
		vcmc.setPoints(points).setContainer(container).setOffset(5);
		HE_Mesh[] cells = vcmc.create();
		int numcells = cells.length;
		
		
		// convert to toxiclibs mesh
		HE_Mesh cell;
		HE_Face[] faces;
		for( int i=0; i < numcells; i++ ) {
			cell = cells[i];
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
					// simple subdivision of polygons to triangles
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
			toxiMesh.scale( 0.01f * scale );	// make up for the fact that we did the fracture on a 100 size box 
		}
		return meshes;
	}
}
