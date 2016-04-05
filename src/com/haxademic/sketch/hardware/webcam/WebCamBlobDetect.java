package com.haxademic.sketch.hardware.webcam;


import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.hardware.webcam.WebCamWrapper;
import com.haxademic.core.image.ImageUtil;
import com.haxademic.core.image.filters.FastBlurFilter;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.vendor.Toxiclibs;

import blobDetection.Blob;
import blobDetection.BlobDetection;
import blobDetection.EdgeVertex;
import processing.core.PGraphics;
import processing.core.PImage;
import toxi.geom.Line3D;
import toxi.geom.Triangle3D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.TriangleMesh;
import toxi.processing.ToxiclibsSupport;

public class WebCamBlobDetect 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Triangle3D tri;
	protected TriangleMesh mesh;
	protected PImage image;
	protected PImage curFrame;
	protected boolean isWebCam;
	protected PGraphics pg;

	BlobDetection theBlobDetection;
	PImage img;
	boolean newFrame=false;
	
	ToxiclibsSupport toxi;

	public void setup() {
		super.setup();
		//		image = ImageUtil.getScaledImage( WebCamWrapper.getImage(), 64, 48 );
		toxi = Toxiclibs.instance(p).toxi;
		pg = p.createGraphics(p.width,p.height,P.P3D);
		
		// BlobDetection
		// img which will be sent to detection (a smaller copy of the cam frame);
		img = new PImage(80,60); 
		theBlobDetection = new BlobDetection(img.width, img.height);
		theBlobDetection.setPosDiscrimination(false);	// true if looking for dark objects
		theBlobDetection.setThreshold(0.3f); // will detect bright areas whose luminosity > 0.2f;

		WebCamWrapper.initWebCam( this, 640, 480 );
	}

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
	}

	public void drawApp() {
		background(0);
		lights();
		p.noStroke();

		curFrame = WebCamWrapper.getImage();
		if(curFrame != null) {
			DrawUtil.setColorForPImage(this);
			DrawUtil.setPImageAlpha(this, 0.5f);
			p.pushMatrix();
			p.translate(0,0,-4);
			p.image(curFrame,0,0,width,height);
			p.popMatrix();
	
			img.copy(curFrame, 0, 0, curFrame.width, curFrame.height, 0, 0, img.width, img.height);
			FastBlurFilter.blur(img, 2);
			theBlobDetection.setPosDiscrimination(true);
			theBlobDetection.computeBlobs(img.pixels);
			drawEdges2(false,true);
			
			DrawUtil.resetPImageAlpha(this);
		}
	}

	// ==================================================
	// drawBlobsAndEdges()
	// ==================================================
	void drawBlobsAndEdges(boolean drawBlobs, boolean drawEdges)
	{
		noFill();
		Blob b;
		EdgeVertex eA,eB;
		for (int n=0 ; n<theBlobDetection.getBlobNb() ; n++)
		{
			b=theBlobDetection.getBlob(n);
			if (b!=null)
			{
				// Edges
				if (drawEdges)
				{
					strokeWeight(3);
					stroke(0,255,0);
					for (int m=0;m<b.getEdgeNb();m++)
					{
						eA = b.getEdgeVertexA(m);
						eB = b.getEdgeVertexB(m);
						if (eA !=null && eB !=null)
							line(
									eA.x*width, eA.y*height, 
									eB.x*width, eB.y*height
									);
					}
				}

				// Blobs
				if (drawBlobs)
				{
					strokeWeight(1);
					stroke(255,0,0);
					rect(
							b.xMin*width,b.yMin*height,
							b.w*width,b.h*height
							);
				}

			}

		}
	}

	// test 1 - lines from outer
	void drawEdges(boolean drawBlobs, boolean drawEdges)
	{
		noFill();
		Blob b;
		EdgeVertex eA,eB;
		for (int n=0 ; n<theBlobDetection.getBlobNb() ; n++)
		{
			b=theBlobDetection.getBlob(n);
			if (b!=null)
			{
				// Edges
				if (drawEdges)
				{
					strokeWeight(3);
					stroke(0,255,0);
					for (int m=0;m<b.getEdgeNb();m++)
					{
						eA = b.getEdgeVertexA(m);
						eB = b.getEdgeVertexB(m);
						
						if (eA !=null && eB !=null) {
							// inner line
							stroke(255, 255, 255, 40);	
							line(
									eA.x*width, eA.y*height, 
									b.x*width, b.y*height
									);
							
							// outer line
							float angle = -MathUtil.getAngleToTarget(eA.x, eA.y, b.x, b.y);
							float distance = MathUtil.getDistance(b.x, b.y, eA.x, eA.y);
							int color = ImageUtil.getPixelColor( curFrame, P.round(eA.x*curFrame.width-1), P.round(eA.y*curFrame.height-1) );
							stroke(color);
							float outerX = eA.x + P.sin( MathUtil.degreesToRadians(angle) )*distance;
							float outerY = eA.y + P.cos( MathUtil.degreesToRadians(angle) )*distance;
							
							
							line(
									eA.x*width, eA.y*height, 
									outerX*width, outerY*height
									);
						}

												
//						if (eA !=null && eB !=null)
//							line(
//									eA.x*width, eA.y*height, 
//									eB.x*width, eB.y*height
//									);
					}
				}
			}

		}
	}

	
	// test 2 - mesh from outer
	void drawEdges2(boolean drawBlobs, boolean drawEdges)
	{
		pg.background(0,0);
		
		noFill();
		Blob b;
		EdgeVertex eA,eB;
		for (int n=0 ; n<theBlobDetection.getBlobNb() ; n++)
		{
			b=theBlobDetection.getBlob(n);
			if (b!=null)
			{
//				p.println(b.id);
				if (drawEdges)
				{
					strokeWeight(1);
					stroke(0,255,0);
					for (int m=0;m<b.getEdgeNb();m++)
					{
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
							
							int color = ImageUtil.getPixelColor( curFrame, P.round(eA.x*curFrame.width-1), P.round(eA.y*curFrame.height-1) );
							fill(color, 200);
							// float bright = brightness(color);
							noStroke();
							
							draw4PointsTriangles(
								new Vec3D(eA.x*width, eA.y*height, 0),
								new Vec3D(outerX*width, outerY*height, 0),
								new Vec3D(outerXB*width, outerYB*height, 0),
								new Vec3D(eB.x*width, eB.y*height, 0),
								pg
							);
							
						}
					}
				}
			}

		}
		
		// draw shadow
		p.pushMatrix();
		p.translate(0,0,-2);
		
		DrawUtil.setPImageAlpha(this, 0.5f);
		p.image(pg,-50,-50,width+100,height+100);
		
		p.popMatrix();
	}

	public void draw4PointsTriangles( Vec3D point1, Vec3D point2, Vec3D point3, Vec3D point4, PGraphics shadow ) {
		// draw a line - currently disabled from noStroke()
		toxi.line( new Line3D( point1, point2 ) );
		toxi.line( new Line3D( point2, point3 ) );
		toxi.line( new Line3D( point3, point4 ) );
		toxi.line( new Line3D( point4, point1 ) );
		
		// draw 2 triangles from quad
		toxi.triangle( new Triangle3D( point1, point2, point3 ) );
		toxi.triangle( new Triangle3D( point1, point3, point4 ) );
		
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
