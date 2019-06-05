package com.haxademic.core.draw.toxi;

import java.util.Iterator;

import com.haxademic.core.app.P;
import com.haxademic.core.media.audio.deprecated.AudioInputWrapper;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.color.TColor;
import toxi.geom.Matrix4x4;
import toxi.geom.Triangle3D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.WETriangleMesh;
import toxi.processing.ToxiclibsSupport;

public class DrawToxiMesh {
	public static Matrix4x4 normalMap = new Matrix4x4().translateSelf(128,128,128).scaleSelf(127);

	public static void drawMeshWithAudio( PApplet p, WETriangleMesh mesh, AudioInputWrapper audioInput, boolean isWireframe, TColor fillColor, TColor strokeColor, float baseAlpha ) {
		drawMeshWithAudio(p.g, mesh, audioInput, isWireframe, fillColor, strokeColor, baseAlpha);
	}
	public static void drawMeshWithAudio( PGraphics p, WETriangleMesh mesh, AudioInputWrapper audioInput, boolean isWireframe, TColor fillColor, TColor strokeColor, float baseAlpha ) {
		drawMeshWithAudio(p, mesh, audioInput, isWireframe, fillColor.toARGB(), strokeColor.toARGB(), baseAlpha);
	}
	public static void drawMeshWithAudio( PGraphics p, WETriangleMesh mesh, AudioInputWrapper audioInput, boolean isWireframe, int fillColor, int strokeColor, float baseAlpha ) {
		p.beginShape(PConstants.TRIANGLES);
		int faceIndex = 0;
		int color = fillColor;
		int colorStroke = strokeColor;
		float alpha;
		Face f;

		int numVertices = mesh.getNumVertices();
		int eqStep = Math.round( 512f / (float) numVertices );

		for (Iterator<Face> i = mesh.faces.iterator(); i.hasNext();) {
			// set colors
			alpha = baseAlpha + audioInput.getFFT().spectrum[(faceIndex*eqStep)%512];
			if( isWireframe ) {
				p.noFill();
				p.stroke( colorStroke, ( baseAlpha + alpha ) * 255 );
			} else {
				p.noStroke();
				p.fill( color, ( baseAlpha + alpha ) * 255 );
			}
			
			f = i.next();
			normalMap.applyTo(f.a.normal);
			p.normal(f.a.normal.x, f.a.normal.y, f.a.normal.z);
			p.vertex(f.a.x, f.a.y, f.a.z);
			normalMap.applyTo(f.b.normal);
			p.normal(f.b.normal.x, f.b.normal.y, f.b.normal.z);
			p.vertex(f.b.x, f.b.y, f.b.z);
			normalMap.applyTo(f.c.normal);
			p.normal(f.c.normal.x, f.c.normal.y, f.c.normal.z);
			p.vertex(f.c.x, f.c.y, f.c.z);
			
			faceIndex ++;
		}
		p.endShape();
	}
	
	public static void drawMeshWithAudioDeformed( PApplet p, ToxiclibsSupport toxi, WETriangleMesh mesh, AudioInputWrapper audioInput, boolean isWireframe, TColor fillColor, TColor strokeColor, float baseAlpha ) {
		p.beginShape(PConstants.TRIANGLES);
		int numVertices = mesh.getNumVertices();
		int eqStep = Math.round( 512f / (float) numVertices );
		int color = fillColor.toARGB();
		int colorStroke = strokeColor.toARGB();
		Face f;
		
		Triangle3D tri;
		for( int i = 0; i < mesh.faces.size(); i++ ) {
			float eq = baseAlpha + audioInput.getFFT().spectrum[(i*eqStep)%512];
			if( isWireframe ) {
				p.noFill();
				p.stroke( colorStroke, ( baseAlpha + eq ) * 255 );
			} else {
				p.noStroke();
				p.fill( color, ( baseAlpha + eq ) * 255 );
			}

			f = mesh.faces.get( i );
			tri = new Triangle3D( 
					new Vec3D( f.a.x * eq, f.a.y * eq, f.a.z * eq ), 
					new Vec3D( f.b.x * eq, f.b.y * eq, f.b.z * eq ), 
					new Vec3D( f.c.x * eq, f.c.y * eq, f.c.z * eq )
				);
			toxi.triangle( tri );
		}		

		p.endShape();
	}
	
	public static void drawPointsWithAudio( PApplet p, WETriangleMesh mesh, AudioInputWrapper audioData, float spectrumFaceRatio, float pointSize, TColor fillColor, TColor strokeColor, float baseAlpha ) {
		p.rectMode( P.CENTER );
		int faceIndex = 0;
		int color = fillColor.toARGB();
		Face f;
		float alpha;
		baseAlpha = baseAlpha * 255;
		p.noStroke();
		for (Iterator<Face> i = mesh.faces.iterator(); i.hasNext();) {
			if( faceIndex % 2 == 0 ) {
				// set colors
				alpha = audioData.getFFT().spectrum[(int)(faceIndex/spectrumFaceRatio) % 512] * 1.3f;
				p.fill( color, baseAlpha + alpha * 255 );
				
				p.pushMatrix();
				f = (Face) i.next();
				Vec3D center = f.getCentroid();
				
				p.translate( center.x, center.y, center.z );
				p.rotateX( f.normal.x );
				p.rotateY( f.normal.y );
				p.rotateZ( f.normal.z );
				p.rect( 0, 0, pointSize + pointSize * alpha, pointSize + pointSize * alpha );
				p.popMatrix();
				
			}
			faceIndex ++;
		}
	}

//	public static void drawObjModelFaces( PApplet p, ToxiclibsSupport toxi, OBJModel model ) {
//		// loop through and set vertices
//		Triangle3D tri;
//		
//		
//		// loop through model's vertices
//		for( int i = 0; i < model.getFaceCount(); i++ ) {
//			// get vertex
//			PVector[] facePoints = model.getFaceVertices( i );
//		
//			tri = new Triangle3D( 
//					new Vec3D( facePoints[0].x, facePoints[0].y, facePoints[0].z ), 
//					new Vec3D( facePoints[1].x, facePoints[1].y, facePoints[1].z ), 
//					new Vec3D( facePoints[2].x, facePoints[2].y, facePoints[2].z )
//				);
//			toxi.triangle( tri );
//		}		
//	}
	
	public static void drawToxiFaces( PApplet p, ToxiclibsSupport toxi, WETriangleMesh mesh ) {
		// loop through and set vertices
		Triangle3D tri;
		Face face;
		
		
		// loop through model's vertices
		for( int i = 0; i < mesh.faces.size(); i++ ) {
			face = mesh.faces.get( i );
			tri = new Triangle3D( 
					new Vec3D( face.a.x, face.a.y, face.a.z ), 
					new Vec3D( face.b.x, face.b.y, face.b.z ), 
					new Vec3D( face.c.x, face.c.y, face.c.z )
				);
			toxi.triangle( tri );
		}		
	}
	
	public static void drawToxiMeshFacesNative( PApplet p, WETriangleMesh mesh ) {
		Face f;
		for( int i = 0; i < mesh.faces.size(); i++ ) {
			p.beginShape(P.TRIANGLES);
			f = mesh.faces.get( i );
			p.vertex(f.a.x, f.a.y, f.a.z);
			p.vertex(f.b.x, f.b.y, f.b.z);
			p.vertex(f.c.x, f.c.y, f.c.z);
			p.endShape();
		}		
	}

	public static void drawToxiMeshFacesNative2d( PApplet p, WETriangleMesh mesh ) {
		Face f;
		for( int i = 0; i < mesh.faces.size(); i++ ) {
			p.beginShape(P.TRIANGLES);
			f = mesh.faces.get( i );
			p.vertex(f.a.x, f.a.y);
			p.vertex(f.b.x, f.b.y);
			p.vertex(f.c.x, f.c.y);
			p.endShape();
		}		
	}

}
