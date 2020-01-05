package com.haxademic.sketch.visualgorithms;


import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.math.MathUtil;

import blobDetection.Blob;
import blobDetection.BlobDetection;
import blobDetection.EdgeVertex;
import processing.core.PGraphics;
import processing.core.PImage;
import toxi.geom.Triangle3D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.TriangleMesh;

public class WebCamBlobDetect 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Triangle3D tri;
	protected TriangleMesh mesh;
	protected PImage image;
	protected PImage curFrame;
	protected PGraphics pg;

	BlobDetection theBlobDetection;
	PGraphics img;
	boolean newFrame=false;
	

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 720 );
		Config.setProperty( AppSettings.FULLSCREEN, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
	}

	protected void firstFrame() {

		//		image = ImageUtil.getScaledImage( WebCamWrapper.getImage(), 64, 48 );
		pg = p.createGraphics(p.width,p.height,P.P3D);
		
		// BlobDetection
		// img which will be sent to detection (a smaller copy of the cam frame);
		img = p.createGraphics(p.width / 10, p.height / 10, P.P3D);
		theBlobDetection = new BlobDetection(img.width, img.height);
		theBlobDetection.setPosDiscrimination(true);	// true if looking for dark objects
		theBlobDetection.setThreshold(0.35f); // will detect bright areas whose luminosity > 0.2f;
	}

	protected void drawApp() {
		background(0);

		curFrame = WebCam.instance().image();
		p.image(curFrame, 0, 0, p.width, p.height);

		// copy source to blob buffer, and blur it
		img.copy(curFrame, 0, 0, curFrame.width, curFrame.height, 0, 0, img.width, img.height);
		BlurHFilter.instance(p).setBlurByPercent(0.8f, img.width);
		BlurHFilter.instance(p).applyTo(img);
		BlurVFilter.instance(p).setBlurByPercent(0.8f, img.height);
		BlurVFilter.instance(p).applyTo(img);

		img.loadPixels();
		// theBlobDetection.setThreshold(P.map(p.mouseX, 0, p.width, 0, 1));
		theBlobDetection.computeBlobs(img.pixels);
		
		drawEdges2(true,true);
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
		curFrame.loadPixels();
		
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
							fill(color, 255);
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
	}

	public void draw4PointsTriangles( Vec3D point1, Vec3D point2, Vec3D point3, Vec3D point4, PGraphics shadow ) {	
		p.beginShape(P.TRIANGLES);
		p.vertex( point1.x, point1.y, point1.z );
		p.vertex( point2.x, point2.y, point2.z );
		p.vertex( point3.x, point3.y, point3.z );
		p.endShape();
		
		p.beginShape(P.TRIANGLES);
		p.vertex( point1.x, point1.y, point1.z );
		p.vertex( point3.x, point3.y, point3.z );
		p.vertex( point4.x, point4.y, point4.z );
		p.endShape();
	}

}
