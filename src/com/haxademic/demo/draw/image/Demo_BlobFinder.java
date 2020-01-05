package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.image.BlobFinder;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;
import com.haxademic.core.math.MathUtil;

import blobDetection.Blob;
import blobDetection.EdgeVertex;
import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_BlobFinder 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected BlobFinder blobFinder;
	
	protected int webcamW = 640;
	protected int webcamH = 480;
	protected PGraphics webcamBuffer;
	protected PGraphics webcamBufferLerped;

	protected void config() {
		Config.setProperty(AppSettings.WIDTH, webcamW);
		Config.setProperty(AppSettings.HEIGHT, webcamH);
	}

	protected void firstFrame() {

		
		// setup webcam
		WebCam.instance().setDelegate(this);
		webcamBuffer = p.createGraphics(webcamW, webcamH, PRenderers.P2D);
		webcamBufferLerped = p.createGraphics(webcamW, webcamH, PRenderers.P2D);
		
		// init blob detection
		blobFinder = new BlobFinder(webcamBufferLerped, 0.2f);
	}
	
	@Override
	public void newFrame(PImage frame) {
		ImageUtil.cropFillCopyImage(frame, webcamBuffer, true);
		
		float shaderBlurAmount = 1f;
		BlurHFilter.instance(P.p).setBlurByPercent(shaderBlurAmount, webcamBuffer.width);
		BlurVFilter.instance(P.p).setBlurByPercent(shaderBlurAmount, webcamBuffer.height);
		BlurHFilter.instance(P.p).applyTo(webcamBuffer);
		BlurVFilter.instance(P.p).applyTo(webcamBuffer);
		
		BlendTowardsTexture.instance(p).setSourceTexture(webcamBuffer);
		BlendTowardsTexture.instance(p).setBlendLerp(0.25f);
		BlendTowardsTexture.instance(p).applyTo(webcamBufferLerped);
	}
	
	protected void drawApp() {
		background(255);
//		p.image(webcamBufferLerped, 0, 0);
		p.image(blobFinder.blobOutputBuffer(), 0, 0);
		
		webcamBufferLerped.loadPixels();
		
		float iterations = 4;
		for (int x = 0; x < iterations; x++) {
			
			// update blob detection
			blobFinder.blobDetection().setPosDiscrimination(Mouse.yNorm > 0.5f);	// true if looking for bright areas
//			blobFinder.blobDetection().setThreshold(Mouse.xNorm); // will detect bright areas whose luminosity > threshold
			blobFinder.blobDetection().setThreshold((float) x / iterations); // will detect bright areas whose luminosity > threshold
			blobFinder.update();
			
			// draw blobs
			// draw to offscreen buffer so we can do ffedback & blending
			p.stroke(255);
			p.strokeWeight(1.5f);
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
//						p.beginShape();
						for (int m = 0; m < numBlobSegments; m++) {
	//						int safeIndex = (m * segmentsToSkip) % numBlobSegments;
							eA = blob.getEdgeVertexA(m);
							eB = blob.getEdgeVertexB(m);
							float segmentX = eA.x * blobScaleW;
							float segmentY = eA.y * blobScaleH;
							float segment2X = eB.x * blobScaleW;
							float segment2Y = eB.y * blobScaleH;
							
							p.stroke(ImageUtil.getPixelColor(webcamBufferLerped, (int) segmentX, (int) segmentY));
//							p.strokeWeight(1.5f);
//							p.noFill();

							totalX += segmentX;
							totalY += segmentY;
							// draw vertex
	//						p.ellipse(eA.x * blobScaleW, eA.y * blobScaleH, 5, 5);
							p.line(segmentX, segmentY, segment2X, segment2Y);
//							p.vertex(segmentX, segmentY);
						}
//						p.endShape(P.CLOSE);
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
						p.stroke(0, 255, 0);
						p.ellipse(centerX, centerY, 5, 5);
						p.stroke(255, 0, 0);
						p.ellipse(centerX, centerY, avgRadius * 2f, avgRadius * 2f);
					}
				}
			}
		}
	}
}
