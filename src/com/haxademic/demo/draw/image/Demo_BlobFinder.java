package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.BlobFinder;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.webcam.IWebCamCallback;
import com.haxademic.core.math.MathUtil;

import blobDetection.Blob;
import blobDetection.EdgeVertex;
import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_BlobFinder 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected BlobFinder blobFinder;
	
	protected int webcamW = 640;
	protected int webcamH = 480;
	protected PGraphics webcamBuffer;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, webcamW);
		p.appConfig.setProperty(AppSettings.HEIGHT, webcamH);
		p.appConfig.setProperty(AppSettings.WEBCAM_INDEX, 5);
	}

	public void setupFirstFrame() {
		super.setup();
		
		// setup webcam
		p.webCamWrapper.setDelegate(this);
		webcamBuffer = p.createGraphics(webcamW, webcamH, PRenderers.P2D);
		
		// init blob detection
		blobFinder = new BlobFinder(webcamBuffer, 0.1f);
	}
	
	@Override
	public void newFrame(PImage frame) {
		ImageUtil.cropFillCopyImage(frame, webcamBuffer, true);
	}
	
	public void drawApp() {
		background(255);
		p.image(webcamBuffer, 0, 0);
		
		float iterations = 15;
		for (int x = 0; x < iterations; x++) {
			
			// update blob detection
			blobFinder.blobDetection().setPosDiscrimination(p.mousePercentY() > 0.5f);	// true if looking for bright areas
//			blobFinder.blobDetection().setThreshold(p.mousePercentX()); // will detect bright areas whose luminosity > threshold
			blobFinder.blobDetection().setThreshold((float) x / iterations); // will detect bright areas whose luminosity > threshold
			blobFinder.update();
			
			// draw blobs
			// draw to offscreen buffer so we can do ffedback & blending
			p.stroke(255);
			p.strokeWeight(1);
			p.noFill();
	
			// draw edges. scale up to screen size
			Blob blob;
			EdgeVertex eA, eB;
			float blobScaleW = webcamW;
			float blobScaleH = webcamH;
			
			// loop through blobs
			int numBlobs = blobFinder.blobDetection().getBlobNb();
			for (int i=0 ; i < numBlobs; i++) {
				blob = blobFinder.blobDetection().getBlob(i);
				if ( blob != null ) {
					// loop through blob segments & draw vertices
					int numBlobSegments = blob.getEdgeNb();
				
					if(numBlobSegments > 30) {
						
						int numSegmentsToProcess = 20;
						int segmentsToSkip = P.floor((float) numBlobSegments / (float) numSegmentsToProcess);
						if(segmentsToSkip < 1) segmentsToSkip = 1;
						
						// get average position of blob segments for center of mass
						float totalX = 0;
						float totalY = 0;
						for (int m = 0; m < numBlobSegments; m++) {
	//						int safeIndex = (m * segmentsToSkip) % numBlobSegments;
							eA = blob.getEdgeVertexA(m);
							eB = blob.getEdgeVertexB(m);
							totalX += eA.x * blobScaleW;
							totalY += eA.y * blobScaleH;
							// draw vertex
	//						p.ellipse(eA.x * blobScaleW, eA.y * blobScaleH, 5, 5);
							p.line(eA.x * blobScaleW, eA.y * blobScaleH, eB.x * blobScaleW, eB.y * blobScaleH);
						}
						float centerX = totalX / (float) numSegmentsToProcess;
						float centerY = totalY / (float) numSegmentsToProcess;
						
						// calculate average vertex distance from averaged center point
						float totalRadius = 0;
						for (int m = 0; m < numBlobSegments; m += segmentsToSkip) {
							int safeIndex = m % numBlobSegments;
							eA = blob.getEdgeVertexA(safeIndex);
							totalRadius += MathUtil.getDistance(centerX, centerY, eA.x * blobScaleW, eA.y * blobScaleH);
						}
						float avgRadius = totalRadius / (float) numSegmentsToProcess;
						
						// store center of mass and average radius
	//					p.stroke(0, 255, 0);
	//					p.ellipse(centerX, centerY, 5, 5);
	//					p.stroke(255, 0, 0);
	//					p.ellipse(centerX, centerY, avgRadius * 2f, avgRadius * 2f);
					}
				}
			}
		}
	}
}
