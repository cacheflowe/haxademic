package com.haxademic.core.draw.toxi;

import com.haxademic.core.app.P;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.image.ImageUtil;

import geomerative.RCommand;
import geomerative.RFont;
import geomerative.RG;
import geomerative.RGroup;
import geomerative.RMesh;
import geomerative.RPoint;
import geomerative.RSVG;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import saito.objloader.OBJModel;
import toxi.geom.AABB;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.WETriangleMesh;
import toxi.processing.ToxiclibsSupport;

public class MeshUtilToxi {
	
	public static WETriangleMesh meshFromOBJ( PApplet p, String file, float scale ) {
		// load and scale the .obj file. convert to mesh and pass back 
		OBJModel obj = new OBJModel( p, file, OBJModel.RELATIVE );
		WETriangleMesh mesh = MeshUtilToxi.ConvertObjModelToToxiMesh( p, obj );
		mesh.scale( scale );
		return mesh;
	}
	
	public static WETriangleMesh ConvertObjModelToToxiMesh( PApplet p, OBJModel model ) {
		WETriangleMesh mesh = new WETriangleMesh();
		
		for( int i = 0; i < model.getFaceCount(); i++ ) {
			PVector[] facePoints = model.getFaceVertices( i );
			mesh.addFace( 
					new Vec3D( facePoints[0].x, facePoints[0].y, facePoints[0].z ), 
					new Vec3D( facePoints[1].x, facePoints[1].y, facePoints[1].z ), 
					new Vec3D( facePoints[2].x, facePoints[2].y, facePoints[2].z )
			);
		}	
		return mesh;
	}

	public static WETriangleMesh meshFromImg( PApplet p, String file, float scale ) {
		WETriangleMesh mesh = new WETriangleMesh();
		PImage image = p.loadImage( file );
		AABB box = new AABB( 0.5f );
		for( int x=0; x < image.width; x++ ){
			for(int y=0; y < image.height; y++){
				if( ImageUtil.getPixelColor( image, x, y ) != -1 ) {
					box.set( x, y, 0 );
					mesh.addMesh( box.toMesh() );
				}
			}
		}
		mesh.translate( new Vec3D( -image.width/2, -image.height/2, 0 ) );
		mesh.scale( scale );
		return mesh;
	}
	

	public static WETriangleMesh meshFromSVG( PApplet p, String file, int segmentLength, int segmentStep, float scale ) {
		setRMeshResolution( p, segmentLength, segmentStep );
		
		// build from font path or a passed-in RFont
		RSVG rSvg = new RSVG();
		RGroup grp = rSvg.toGroup( file );
		grp.centerIn( p.g );  // this centers the shape in the screen
		
		//RPoint[] pnts = grp.getPoints();

		RMesh rMesh = grp.toMesh();
		WETriangleMesh mesh = rMeshToToxiMesh( rMesh );
		mesh.scale( scale );
		return mesh;
	}
	
	public static WETriangleMesh mesh2dFromTextFont( PApplet p, RFont font, String fontPath, int fontSize, String text, int segmentLength, int segmentStep, float scale ) {
		setRMeshResolution( p, segmentLength, segmentStep );
		
		// build from font path or a passed-in RFont
		if( font == null ) font = new RFont( fontPath, fontSize, RFont.CENTER);
		RGroup grp = font.toGroup( text );
		//RPoint[] pnts = grp.getPoints();

		RMesh rMesh = grp.toMesh();
		WETriangleMesh mesh = rMeshToToxiMesh( rMesh );
		mesh.scale( scale );
		return mesh;
	}
	
	/**
	 * Sets resolution for the Geomerative library to create 2D meshes from text or svg files.
	 * @param p					The applet
	 * @param segmentLength		Uses UNIFORMLENGTH - smaller numbers are higher resolution
	 * @param segmentStep		Uses UNIFORMSTEP - larger numbers are higher resolution
	 */
	public static void setRMeshResolution( PApplet p, int segmentLength, int segmentStep ) {
		if( RG.initialized() == false ) RG.init( p );
		
		if( segmentLength != -1 ) {
			RCommand.setSegmentator( RCommand.UNIFORMLENGTH );	
			RCommand.setSegmentLength( segmentLength );
		} else if( segmentStep != -1 ) {
			RCommand.setSegmentator( RCommand.UNIFORMSTEP );	
			RCommand.setSegmentStep( segmentStep );
		} else {
			RCommand.setSegmentator( RCommand.ADAPTATIVE );	
		}
	}
	
	public static WETriangleMesh rMeshToToxiMesh( RMesh rMesh ) {
		WETriangleMesh mesh = new WETriangleMesh();

		// copy faces to toxi mesh
		for ( int i = 0; i < rMesh.strips.length; i++ ) {
			RPoint[] meshPoints = rMesh.strips[i].getPoints();

			for ( int ii = 0; ii < meshPoints.length - 2; ii++ ) {
				mesh.addFace( 
						new Vec3D( meshPoints[ii].x, meshPoints[ii].y, 0 ), 
						new Vec3D( meshPoints[ii+1].x, meshPoints[ii+1].y, 0 ), 
						new Vec3D( meshPoints[ii+2].x, meshPoints[ii+2].y, 0 ) 
				);
			}
		}
		return mesh;
	}
	
	public static WETriangleMesh getExtrudedMesh( WETriangleMesh mesh, float depth ) {
		WETriangleMesh mesh3d = new WETriangleMesh();
		
		Face face;
		for ( int i = 0; i < mesh.faces.size(); i++ ) {
			face = mesh.faces.get( i );
			
			// draw front/back faces
			mesh3d.addFace( 
					new Vec3D( face.a.x, face.a.y, depth ), 
					new Vec3D( face.b.x, face.b.y, depth ), 
					new Vec3D( face.c.x, face.c.y, depth ) 
			);
			mesh3d.addFace( 
					new Vec3D( face.a.x, face.a.y, -depth ), 
					new Vec3D( face.b.x, face.b.y, -depth ), 
					new Vec3D( face.c.x, face.c.y, -depth ) 
			);
			
			// draw walls between the 2 faces - close the 2 triangles 
			addTriQuadToMeshWith4Points( 
					mesh3d, 
					new Vec3D( face.a.x, face.a.y, depth ),
					new Vec3D( face.a.x, face.a.y, -depth ),
					new Vec3D( face.b.x, face.b.y, -depth ),
					new Vec3D( face.b.x, face.b.y, depth )
			);

			addTriQuadToMeshWith4Points( 
					mesh3d, 
					new Vec3D( face.b.x, face.b.y, depth ),
					new Vec3D( face.b.x, face.b.y, -depth ),
					new Vec3D( face.c.x, face.c.y, -depth ),
					new Vec3D( face.c.x, face.c.y, depth )
			);

			addTriQuadToMeshWith4Points( 
					mesh3d, 
					new Vec3D( face.c.x, face.c.y, depth ),
					new Vec3D( face.c.x, face.c.y, -depth ),
					new Vec3D( face.a.x, face.a.y, -depth ),
					new Vec3D( face.a.x, face.a.y, depth )
			);

		}
		return mesh3d;
	}
	
	public static void addTriQuadToMeshWith4Points( WETriangleMesh mesh, Vec3D pt1, Vec3D pt2, Vec3D pt3, Vec3D pt4 ) {
		// make sure 4 points go in order around the square
		mesh.addFace( pt1, pt2, pt3 );
		mesh.addFace( pt3, pt4, pt1 );
	}
	
	public static void deformMeshWithAudio( WETriangleMesh mesh, WETriangleMesh meshDeform, AudioInputWrapper audioInput, float deformFactor ) {
		int numVertices = mesh.getNumVertices();
		int numDeformedVertices = mesh.getNumVertices();
		int eqStep = Math.round( 512f / (float) numVertices );
		if( numVertices == numDeformedVertices ) {
			for( int i = 0; i < numVertices - 1; i++ ) {
				float eq = 1 + audioInput.getFFT().spectrum[(i*eqStep)%512] * deformFactor;
				if( mesh.getVertexForID( i ) != null && meshDeform.getVertexForID( i ) != null ) {
					meshDeform.getVertexForID( i ).x = mesh.getVertexForID( i ).x * eq;
					meshDeform.getVertexForID( i ).y = mesh.getVertexForID( i ).y * eq;
					meshDeform.getVertexForID( i ).z = mesh.getVertexForID( i ).z * eq;
				}
			}
		}
	}
	
	public static void calcTextureCoordinates(WETriangleMesh mesh) {
		for( Face f : mesh.getFaces() ) {
//			f.computeNormal();
			f.uvA = calcUV(f.a);
			f.uvB = calcUV(f.b);
			f.uvC = calcUV(f.c);
		}
	}
	
	public static Vec2D calcUV(Vec3D pos) {
		Vec3D s = pos.copy().toSpherical();
		Vec2D uv = new Vec2D( s.y / P.TWO_PI, ( 1.0f - ( s.z / P.PI + 0.5f ) ) );
		// make sure longitude is always within 0.0 ... 1.0 interval
		if (uv.x < 0) uv.x += 1f;
		else if (uv.x > 1) uv.x -= 1f;
		if (uv.y < 0) uv.y += 1f;
		else if (uv.y > 1) uv.y -= 1f;
		uv.x = P.abs(uv.x);
		uv.y = P.abs(uv.y);
//		uv.x = P.constrain( uv.x, 0.000001f, 0.9999999f );
		return uv;
	}

	public static void drawToxiMesh( PApplet p, ToxiclibsSupport toxi, WETriangleMesh mesh, PImage image ) {
		p.textureMode(P.NORMAL);	// P.NORMAL ??
		toxi.texturedMesh( mesh.toWEMesh(), image, false );
	}
	
}
