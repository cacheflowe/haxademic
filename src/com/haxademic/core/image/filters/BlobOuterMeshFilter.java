package com.haxademic.core.image.filters;

import processing.core.PGraphics;
import processing.core.PImage;
import toxi.geom.Vec3D;
import blobDetection.Blob;
import blobDetection.BlobDetection;
import blobDetection.EdgeVertex;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.image.ImageUtil;
import com.haxademic.core.math.MathUtil;

public class BlobOuterMeshFilter {
	
	protected PAppletHax p;
	protected int _width;
	protected int _height;
	protected PGraphics _pg;
	protected PImage _image;
	BlobDetection theBlobDetection;
	PImage blobBufferImg;

	
	public BlobOuterMeshFilter( int width, int height ) {
		p = (PAppletHax) P.p;
		_width = width;
		_height = height;
		initBlobDetection();
	}
	
	public PImage pg() {
		return _pg;
	}
	
	protected void initBlobDetection() {
		_pg = p.createGraphics( _width, _height, P.P3D );
		_pg.smooth(OpenGLUtil.SMOOTH_HIGH);
		_image = p.createImage( _width, _height, P.ARGB );
		
		// BlobDetection
		// img which will be sent to detection (a smaller copy of the image frame);
		blobBufferImg = new PImage( (int)(_width * 0.15f), (int)(_height * 0.15f) ); 
		theBlobDetection = new BlobDetection( blobBufferImg.width, blobBufferImg.height );
		theBlobDetection.setPosDiscrimination(false);	// true if looking for bright areas
		theBlobDetection.setThreshold(0.32f); // will detect bright areas whose luminosity > 0.2f;
	}
	
	public PImage updateWithPImage( PImage source ) {
		runBlobDetection( source );
		drawEdges( source );
		return _image;
	}
	
	// IMAGE PROCESSING METHODS ===================================================================================
	protected void runBlobDetection( PImage source ) {
		blobBufferImg.copy(source, 0, 0, source.width, source.height, 0, 0, blobBufferImg.width, blobBufferImg.height);
		FastBlurFilter.blur(blobBufferImg, 2);
		theBlobDetection.computeBlobs(blobBufferImg.pixels);
	}
	
	// test 2 - mesh from outer
	protected void drawEdges(PImage source)
	{
		_pg.beginDraw();
		ImageUtil.clearPGraphics( _pg );
		_pg.noStroke();
		_pg.fill(0,0);
		_pg.image( source, 0, 0 );
		
		Blob b;
		EdgeVertex eA,eB;
		for (int n=0 ; n<theBlobDetection.getBlobNb() ; n++) {
			b=theBlobDetection.getBlob(n);
			if (b!=null) {
				for (int m=0;m<b.getEdgeNb();m++) {
					eA = b.getEdgeVertexA(m);
					eB = b.getEdgeVertexB(m);
										
					if (eA !=null && eB !=null) {
						
						float angle = -MathUtil.getAngleToTarget(eA.x, eA.y, b.x, b.y);
						float angleB = -MathUtil.getAngleToTarget(eB.x, eB.y, b.x, b.y);
						float distance = MathUtil.getDistance(b.x, b.y, eA.x, eA.y) * 1f;
						float distanceB = MathUtil.getDistance(b.x, b.y, eB.x, eB.y) * 1f;

						float outerX = eA.x + P.sin( MathUtil.degreesToRadians(angle) )*distance;
						float outerY = eA.y + P.cos( MathUtil.degreesToRadians(angle) )*distance;
						float outerXB = eB.x + P.sin( MathUtil.degreesToRadians(angleB) )*distanceB;
						float outerYB = eB.y + P.cos( MathUtil.degreesToRadians(angleB) )*distanceB;

						// draw inner lines
//						_pg.beginDraw();
//						_pg.stroke(0,127);
//						_pg.line(eA.x*_width, eA.y*_height, eB.x*_width, eB.y*_height);
//						_pg.noStroke();
//						_pg.endDraw();

						
						int color = ImageUtil.getPixelColor( source, P.round(eA.x*source.width-1), P.round(eA.y*source.height-1) );
						_pg.fill( color, 127 );
						// float bright = p.brightness(color);

						draw4PointsTriangles(
							new Vec3D(eA.x*_width, eA.y*_height, 0),
							new Vec3D(outerX*_width, outerY*_height, 0),
							new Vec3D(outerXB*_width, outerYB*_height, 0),
							new Vec3D(eB.x*_width, eB.y*_height, 0),
							_pg
						);
						
					}
				}
			}
		}
		_pg.endDraw();
		
		// draw shadow
//		p.pushMatrix();
//		p.translate(0,0,-3);
////		p.image(source,0,0);
////		p.image(_pg,-50,-50,_width+100,_height+100);
//		p.popMatrix();
		
		_image.copy( _pg, 0, 0, _width, _height, 0, 0, _width, _height );
	}

	public void draw4PointsTriangles( Vec3D point1, Vec3D point2, Vec3D point3, Vec3D point4, PGraphics shadow ) {
		// draw a line - currently disabled from noStroke()
//		p.toxi.line( new Line3D( point1, point2 ) );
//		p.toxi.line( new Line3D( point2, point3 ) );
//		p.toxi.line( new Line3D( point3, point4 ) );
//		p.toxi.line( new Line3D( point4, point1 ) );
		
		// draw 2 triangles from quad
//		p.toxi.triangle( new Triangle3D( point1, point2, point3 ) );
//		p.toxi.triangle( new Triangle3D( point1, point3, point4 ) );
		
		// draw native processing style
//		_pg.beginDraw();
		
		_pg.beginShape(P.TRIANGLES);
		_pg.vertex( point1.x, point1.y, point1.z );
		_pg.vertex( point2.x, point2.y, point2.z );
		_pg.vertex( point3.x, point3.y, point3.z );
		_pg.endShape();
		
		_pg.beginShape(P.TRIANGLES);
		_pg.vertex( point1.x, point1.y, point1.z );
		_pg.vertex( point3.x, point3.y, point3.z );
		_pg.vertex( point4.x, point4.y, point4.z );
		_pg.endShape();
		
//		_pg.endDraw();

		
		
//		if(1 == 2) {
//		// add to shadow graphics
//	//		shadow.tint( 255 );
//	//		shadow.fill(127,255);
//			shadow.fill(0,255);
//			shadow.noStroke();
//	
//	//		shadow.hint( P.DISABLE_DEPTH_TEST );
//			shadow.beginDraw();
//	
//			shadow.beginShape(P.TRIANGLES);
//			shadow.vertex( point1.x, point1.y, point1.z );
//			shadow.vertex( point2.x, point2.y, point2.z );
//			shadow.vertex( point3.x, point3.y, point3.z );
//			shadow.endShape();
//			
//			shadow.beginShape(P.TRIANGLES);
//			shadow.vertex( point1.x, point1.y, point1.z );
//			shadow.vertex( point3.x, point3.y, point3.z );
//			shadow.vertex( point4.x, point4.y, point4.z );
//			shadow.endShape();
//			
//			shadow.endDraw();
//	//		shadow.hint( P.ENABLE_DEPTH_TEST );
//		}
	}


}
