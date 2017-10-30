package com.haxademic.core.draw.toxi;

import java.util.ArrayList;

import toxi.geom.AABB;
import toxi.geom.mesh.WETriangleMesh;


public class Meshes {
	
	public static ArrayList<AABB> invader1Boxes( int state, float scale ) {
		ArrayList<AABB> boxes = new ArrayList<AABB>();

		// add blocks by row
		createBoxAtCoordinate( -1, -4,  0, scale, boxes );
		createBoxAtCoordinate(  1, -4,  0, scale, boxes );

		createBoxAtCoordinate( -2, -3,  0, scale, boxes );
		createBoxAtCoordinate(  1, -3,  0, scale, boxes );
		createBoxAtCoordinate( -1, -3,  0, scale, boxes );
		createBoxAtCoordinate(  2, -3,  0, scale, boxes );

		createBoxAtCoordinate( -3, -2,  0, scale, boxes );
		createBoxAtCoordinate( -2, -2,  0, scale, boxes );
		createBoxAtCoordinate( -1, -2,  0, scale, boxes );
		createBoxAtCoordinate(  1, -2,  0, scale, boxes );
		createBoxAtCoordinate(  2, -2,  0, scale, boxes );
		createBoxAtCoordinate(  3, -2,  0, scale, boxes );

		createBoxAtCoordinate( -4, -1,  0, scale, boxes );
		createBoxAtCoordinate( -3, -1,  0, scale, boxes );
		createBoxAtCoordinate( -1, -1,  0, scale, boxes );
		createBoxAtCoordinate(  1, -1,  0, scale, boxes );
		createBoxAtCoordinate(  3, -1,  0, scale, boxes );
		createBoxAtCoordinate(  4, -1,  0, scale, boxes );

		createBoxAtCoordinate( -4,  1,  0, scale, boxes );
		createBoxAtCoordinate( -3,  1,  0, scale, boxes );
		createBoxAtCoordinate( -2,  1,  0, scale, boxes );
		createBoxAtCoordinate( -1,  1,  0, scale, boxes );
		createBoxAtCoordinate(  1,  1,  0, scale, boxes );
		createBoxAtCoordinate(  2,  1,  0, scale, boxes );
		createBoxAtCoordinate(  3,  1,  0, scale, boxes );
		createBoxAtCoordinate(  4,  1,  0, scale, boxes );
		
		if( state == 1 ) {
			createBoxAtCoordinate( -2,  2,  0, scale, boxes );
			createBoxAtCoordinate(  2,  2,  0, scale, boxes );

			createBoxAtCoordinate( -3,  3,  0, scale, boxes );
			createBoxAtCoordinate( -1,  3,  0, scale, boxes );
			createBoxAtCoordinate(  1,  3,  0, scale, boxes );
			createBoxAtCoordinate(  3,  3,  0, scale, boxes );

			createBoxAtCoordinate( -4,  4,  0, scale, boxes );
			createBoxAtCoordinate( -2,  4,  0, scale, boxes );
			createBoxAtCoordinate(  2,  4,  0, scale, boxes );
			createBoxAtCoordinate(  4,  4,  0, scale, boxes );
		} else {
			createBoxAtCoordinate( -3,  2,  0, scale, boxes );
			createBoxAtCoordinate( -1,  2,  0, scale, boxes );
			createBoxAtCoordinate(  1,  2,  0, scale, boxes );
			createBoxAtCoordinate(  3,  2,  0, scale, boxes );

			createBoxAtCoordinate( -4,  3,  0, scale, boxes );
			createBoxAtCoordinate(  4,  3,  0, scale, boxes );

			createBoxAtCoordinate( -3,  4,  0, scale, boxes );
			createBoxAtCoordinate(  3,  4,  0, scale, boxes );
		}
		
		return boxes;
	}
	
	public static WETriangleMesh invader1( int state, float scale ) {
		// setup / objects
		WETriangleMesh mesh = new WETriangleMesh();
		
		// TODO: store this box array in a stativ var and check for it before creating again
		ArrayList<AABB> boxes = Meshes.invader1Boxes( state, scale );
		
		for( int i=0; i < boxes.size(); i++ ) {
			addBoxToMesh( boxes.get( i ), mesh );
		}

		return mesh;
	} 
	
	public static ArrayList<AABB> invader2Boxes( int state, float scale ) {
		ArrayList<AABB> boxes = new ArrayList<AABB>();

		createBoxAtCoordinate( -3, -4,  0, scale, boxes );
		createBoxAtCoordinate(  3, -4,  0, scale, boxes );

		createBoxAtCoordinate( -2, -3,  0, scale, boxes );
		createBoxAtCoordinate(  2, -3,  0, scale, boxes );
		
		createBoxAtCoordinate( -3, -2,  0, scale, boxes );
		createBoxAtCoordinate( -2, -2,  0, scale, boxes );
		createBoxAtCoordinate( -1, -2,  0, scale, boxes );
		createBoxAtCoordinate(  1, -2,  0, scale, boxes );
		createBoxAtCoordinate(  2, -2,  0, scale, boxes );
		createBoxAtCoordinate(  3, -2,  0, scale, boxes );

		createBoxAtCoordinate( -4, -1,  0, scale, boxes );
		createBoxAtCoordinate( -3, -1,  0, scale, boxes );
		createBoxAtCoordinate( -1, -1,  0, scale, boxes );
		createBoxAtCoordinate(  1, -1,  0, scale, boxes );
		createBoxAtCoordinate(  3, -1,  0, scale, boxes );
		createBoxAtCoordinate(  4, -1,  0, scale, boxes );

		createBoxAtCoordinate( -5,  1,  0, scale, boxes );
		createBoxAtCoordinate( -4,  1,  0, scale, boxes );
		createBoxAtCoordinate( -3,  1,  0, scale, boxes );
		createBoxAtCoordinate( -2,  1,  0, scale, boxes );
		createBoxAtCoordinate( -1,  1,  0, scale, boxes );
		createBoxAtCoordinate(  1,  1,  0, scale, boxes );
		createBoxAtCoordinate(  2,  1,  0, scale, boxes );
		createBoxAtCoordinate(  3,  1,  0, scale, boxes );
		createBoxAtCoordinate(  4,  1,  0, scale, boxes );
		createBoxAtCoordinate(  5,  1,  0, scale, boxes );

		createBoxAtCoordinate( -3,  2,  0, scale, boxes );
		createBoxAtCoordinate( -2,  2,  0, scale, boxes );
		createBoxAtCoordinate( -1,  2,  0, scale, boxes );
		createBoxAtCoordinate(  1,  2,  0, scale, boxes );
		createBoxAtCoordinate(  2,  2,  0, scale, boxes );
		createBoxAtCoordinate(  3,  2,  0, scale, boxes );
	
		createBoxAtCoordinate( -3,  3,  0, scale, boxes );
		createBoxAtCoordinate(  3,  3,  0, scale, boxes );
		
		if( state == 1 ) {			
			createBoxAtCoordinate( -5, -3,  0, scale, boxes );
			createBoxAtCoordinate(  5, -3,  0, scale, boxes );
			
			createBoxAtCoordinate( -5, -2,  0, scale, boxes );
			createBoxAtCoordinate(  5, -2,  0, scale, boxes );
			
			createBoxAtCoordinate( -5, -1,  0, scale, boxes );
			createBoxAtCoordinate(  5, -1,  0, scale, boxes );
			
			createBoxAtCoordinate( -4,  2,  0, scale, boxes );
			createBoxAtCoordinate(  4,  2,  0, scale, boxes );
			
			createBoxAtCoordinate( -4,  4,  0, scale, boxes );
			createBoxAtCoordinate(  4,  4,  0, scale, boxes );
		} else {
			createBoxAtCoordinate( -5,  2,  0, scale, boxes );
			createBoxAtCoordinate(  5,  2,  0, scale, boxes );
			
			createBoxAtCoordinate( -5,  3,  0, scale, boxes );
			createBoxAtCoordinate(  5,  3,  0, scale, boxes );
			
			createBoxAtCoordinate( -2,  4,  0, scale, boxes );
			createBoxAtCoordinate(  2,  4,  0, scale, boxes );
		}
		
		return boxes;
	}	
	
	public static WETriangleMesh invader2( int state, float scale ) {
		// setup / objects
		WETriangleMesh mesh = new WETriangleMesh();
		
		// TODO: store this box array in a stativ var and check for it before creating again
		ArrayList<AABB> boxes = Meshes.invader2Boxes( state, scale );
		
		for( int i=0; i < boxes.size(); i++ ) {
			addBoxToMesh( boxes.get( i ), mesh );
		}

		return mesh;
	} 
	
	
	public static ArrayList<AABB> invader3Boxes( int state, float scale ) {
		ArrayList<AABB> boxes = new ArrayList<AABB>();
		
		// add blocks by row
		createBoxAtCoordinate( -2, -4,  0, scale, boxes );
		createBoxAtCoordinate( -1, -4,  0, scale, boxes );
		createBoxAtCoordinate(  1, -4,  0, scale, boxes );
		createBoxAtCoordinate(  2, -4,  0, scale, boxes );

		createBoxAtCoordinate( -5, -3,  0, scale, boxes );
		createBoxAtCoordinate( -4, -3,  0, scale, boxes );
		createBoxAtCoordinate( -3, -3,  0, scale, boxes );
		createBoxAtCoordinate( -2, -3,  0, scale, boxes );
		createBoxAtCoordinate( -1, -3,  0, scale, boxes );
		createBoxAtCoordinate(  1, -3,  0, scale, boxes );
		createBoxAtCoordinate(  2, -3,  0, scale, boxes );
		createBoxAtCoordinate(  3, -3,  0, scale, boxes );
		createBoxAtCoordinate(  4, -3,  0, scale, boxes );
		createBoxAtCoordinate(  5, -3,  0, scale, boxes );

		createBoxAtCoordinate( -6, -2,  0, scale, boxes );
		createBoxAtCoordinate( -5, -2,  0, scale, boxes );
		createBoxAtCoordinate( -4, -2,  0, scale, boxes );
		createBoxAtCoordinate( -3, -2,  0, scale, boxes );
		createBoxAtCoordinate( -2, -2,  0, scale, boxes );
		createBoxAtCoordinate( -1, -2,  0, scale, boxes );
		createBoxAtCoordinate(  1, -2,  0, scale, boxes );
		createBoxAtCoordinate(  2, -2,  0, scale, boxes );
		createBoxAtCoordinate(  3, -2,  0, scale, boxes );
		createBoxAtCoordinate(  4, -2,  0, scale, boxes );
		createBoxAtCoordinate(  5, -2,  0, scale, boxes );
		createBoxAtCoordinate(  6, -2,  0, scale, boxes );

		createBoxAtCoordinate( -6, -1,  0, scale, boxes );
		createBoxAtCoordinate( -5, -1,  0, scale, boxes );
		createBoxAtCoordinate( -4, -1,  0, scale, boxes );
		createBoxAtCoordinate( -1, -1,  0, scale, boxes );
		createBoxAtCoordinate(  1, -1,  0, scale, boxes );
		createBoxAtCoordinate(  4, -1,  0, scale, boxes );
		createBoxAtCoordinate(  5, -1,  0, scale, boxes );
		createBoxAtCoordinate(  6, -1,  0, scale, boxes );

		createBoxAtCoordinate( -6,  1,  0, scale, boxes );
		createBoxAtCoordinate( -5,  1,  0, scale, boxes );
		createBoxAtCoordinate( -4,  1,  0, scale, boxes );
		createBoxAtCoordinate( -3,  1,  0, scale, boxes );
		createBoxAtCoordinate( -2,  1,  0, scale, boxes );
		createBoxAtCoordinate( -1,  1,  0, scale, boxes );
		createBoxAtCoordinate(  1,  1,  0, scale, boxes );
		createBoxAtCoordinate(  2,  1,  0, scale, boxes );
		createBoxAtCoordinate(  3,  1,  0, scale, boxes );
		createBoxAtCoordinate(  4,  1,  0, scale, boxes );
		createBoxAtCoordinate(  5,  1,  0, scale, boxes );
		createBoxAtCoordinate(  6,  1,  0, scale, boxes );
		
		if( state == 1 ) {
			createBoxAtCoordinate( -3,  2,  0, scale, boxes );
			createBoxAtCoordinate( -2,  2,  0, scale, boxes );
			createBoxAtCoordinate(  2,  2,  0, scale, boxes );
			createBoxAtCoordinate(  3,  2,  0, scale, boxes );

			createBoxAtCoordinate( -4,  3,  0, scale, boxes );
			createBoxAtCoordinate( -3,  3,  0, scale, boxes );
			createBoxAtCoordinate( -1,  3,  0, scale, boxes );
			createBoxAtCoordinate(  1,  3,  0, scale, boxes );
			createBoxAtCoordinate(  3,  3,  0, scale, boxes );
			createBoxAtCoordinate(  4,  3,  0, scale, boxes );

			createBoxAtCoordinate( -6,  4,  0, scale, boxes );
			createBoxAtCoordinate( -5,  4,  0, scale, boxes );
			createBoxAtCoordinate(  5,  4,  0, scale, boxes );
			createBoxAtCoordinate(  6,  4,  0, scale, boxes );
		} else {
			createBoxAtCoordinate( -4,  2,  0, scale, boxes );
			createBoxAtCoordinate( -3,  2,  0, scale, boxes );
			createBoxAtCoordinate( -2,  2,  0, scale, boxes );
			createBoxAtCoordinate(  2,  2,  0, scale, boxes );
			createBoxAtCoordinate(  3,  2,  0, scale, boxes );
			createBoxAtCoordinate(  4,  2,  0, scale, boxes );

			createBoxAtCoordinate( -5,  3,  0, scale, boxes );
			createBoxAtCoordinate( -4,  3,  0, scale, boxes );
			createBoxAtCoordinate( -1,  3,  0, scale, boxes );
			createBoxAtCoordinate(  1,  3,  0, scale, boxes );
			createBoxAtCoordinate(  4,  3,  0, scale, boxes );
			createBoxAtCoordinate(  5,  3,  0, scale, boxes );

			createBoxAtCoordinate( -4,  4,  0, scale, boxes );
			createBoxAtCoordinate( -3,  4,  0, scale, boxes );
			createBoxAtCoordinate(  3,  4,  0, scale, boxes );
			createBoxAtCoordinate(  4,  4,  0, scale, boxes );
		}

		return boxes;
	} 

	public static WETriangleMesh invader3( int state, float scale ) {
		// setup / objects
		WETriangleMesh mesh = new WETriangleMesh();
		
		// TODO: store this box array in a stativ var and check for it before creating again
		ArrayList<AABB> boxes = Meshes.invader3Boxes( state, scale );
		
		for( int i=0; i < boxes.size(); i++ ) {
			addBoxToMesh( boxes.get( i ), mesh );
		}

		return mesh;
	} 
	

	public static void createBoxAtCoordinate( float x, float y, float z, float size, ArrayList<AABB> boxArray ) {
		float boxRadius = size * 0.5f;
		AABB box = new AABB( boxRadius );
		x *= size;
		y *= size;
		z *= size;
		x = ( x < 0 ) ? x + boxRadius : x - boxRadius;
		y = ( y < 0 ) ? y + boxRadius : y - boxRadius;
		z = ( z < 0 ) ? z + boxRadius : z - boxRadius;
		box.set( x, y, z ); 
		boxArray.add( box );
	}
	
	public static void addBoxToMesh( AABB box, WETriangleMesh mesh ) {
		mesh.addMesh( box.toMesh() );
	}
	
	public static void addBoxAtCoordinateToMesh( float x, float y, float z, AABB box, WETriangleMesh mesh ) {
		x = ( x < 0 ) ? x + 0.5f : x - 0.5f;
		y = ( y < 0 ) ? y + 0.5f : y - 0.5f;
		z = ( z < 0 ) ? z + 0.5f : z - 0.5f;
		box.set( x, y, z ); 
		mesh.addMesh( box.toMesh() );
		
	}
}
