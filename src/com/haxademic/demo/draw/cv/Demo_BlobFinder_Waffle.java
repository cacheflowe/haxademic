package com.haxademic.demo.draw.cv;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.cv.BlobFinder;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.render.FrameLoop;

import blobDetection.Blob;
import blobDetection.EdgeVertex;
import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_BlobFinder_Waffle 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected BlobFinder blobFinder;
	protected PImage img;
	protected PGraphics buffer;
	protected PGraphics bufferThresh;
	protected PGraphics bufferOut;

	protected void config() {
		Config.setProperty(AppSettings.SHOW_UI, true);
	}

	protected void firstFrame() {
		img = P.getImage("images/_sketch/waffle.jpg");
		buffer = ImageUtil.imageToGraphics(img);
			
		// init blob detection
		blobFinder = new BlobFinder(buffer, 0.2f);
	}
	
	public void newFrame() {
		ImageUtil.cropFillCopyImage(img, buffer, true);
		buffer.beginDraw();
		buffer.background(255);
		buffer.image(img, FrameLoop.osc(0.03f, 0, 100), 0);
		buffer.endDraw();
		
		float shaderBlurAmount = 1f;
		BlurHFilter.instance().setBlurByPercent(shaderBlurAmount, buffer.width);
		BlurVFilter.instance().setBlurByPercent(shaderBlurAmount, buffer.height);
		BlurHFilter.instance().applyTo(buffer);
		BlurVFilter.instance().applyTo(buffer);
		BlurHFilter.instance().applyTo(buffer);
		BlurHFilter.instance().applyTo(buffer);
		BlurVFilter.instance().applyTo(buffer);
		BlurHFilter.instance().applyTo(buffer);
		BlurVFilter.instance().applyTo(buffer);
		
		BrightnessFilter.instance().setBrightness(0.8f);
		BrightnessFilter.instance().applyTo(buffer);

		ContrastFilter.instance().setContrast(2.7f);
		ContrastFilter.instance().applyTo(buffer);

		ThresholdFilter.instance().setCutoff(Mouse.yNorm);
		// ThresholdFilter.instance().applyTo(buffer);
	}
	
	protected void drawApp() {
		newFrame();
			
		background(255);

		p.image(blobFinder.blobOutputBuffer(), 0, 0);
		
		buffer.loadPixels();
					
		// update blob detection
		blobFinder.blobDetection().setPosDiscrimination(true);	// true if looking for bright areas
		blobFinder.blobDetection().setThreshold(Mouse.xNorm); // will detect bright areas whose luminosity > threshold
		blobFinder.update();
		
		// draw image
		p.image(img, FrameLoop.osc(0.03f, 0, 100), 0);
		p.image(buffer, 0, img.height);
		
		// draw blobs
		// draw to offscreen buffer so we can do ffedback & blending
		p.stroke(255);
		p.strokeWeight(1.5f);
		p.noFill();

		// draw edges. scale up to screen size
		Blob blob;
		EdgeVertex eA, eB;
		float blobScaleW = buffer.width;
		float blobScaleH = buffer.height;
		
		// loop through blobs
		int numBlobs = blobFinder.numBlobs();
		for (int i=0 ; i < numBlobs; i++) {
			blob = blobFinder.getBlob(i);
			if ( blob != null ) {
				// loop through blob segments & draw vertices
				int numBlobSegments = blob.getEdgeNb();
			
				if(numBlobSegments > 10 && numBlobSegments < 300) {
					
					int numSegmentsToProcess = 20;
					int segmentsToSkip = P.floor((float) numBlobSegments / (float) numSegmentsToProcess);
					if(segmentsToSkip < 1) segmentsToSkip = 1;
					
					// get average position of blob segments for center of mass
					float totalX = 0;
					float totalY = 0;
						p.beginShape();
					for (int m = 0; m < numBlobSegments; m++) {
//						int safeIndex = (m * segmentsToSkip) % numBlobSegments;
						eA = blob.getEdgeVertexA(m);
						eB = blob.getEdgeVertexB(m);
						float segmentX = eA.x * blobScaleW;
						float segmentY = eA.y * blobScaleH;
						float segment2X = eB.x * blobScaleW;
						float segment2Y = eB.y * blobScaleH;
						
						p.stroke(ImageUtil.getPixelColor(buffer, (int) segmentX, (int) segmentY));
						p.stroke(0, 0, 0);
						p.strokeWeight(5f);
						p.fill(ColorsHax.colorFromGroupAt(5, P.round(p.frameCount/5) + i));

						totalX += segmentX;
						totalY += segmentY;
						// draw vertex
//						p.ellipse(eA.x * blobScaleW, eA.y * blobScaleH, 5, 5);
						// p.line(segmentX, segmentY, segment2X, segment2Y);
							// p.vertex(segmentX, segmentY);
					}
					p.endShape(P.CLOSE);
					float centerX = totalX / (float) numBlobSegments;
					float centerY = totalY / (float) numBlobSegments;
					
					// calculate average vertex distance from averaged center point
					float totalRadius = 0;
					for (int m = 0; m < numBlobSegments; m += segmentsToSkip) {
						int safeIndex = m % numBlobSegments;
						eA = blob.getEdgeVertexA(safeIndex);
						totalRadius += MathUtil.getDistance(centerX, centerY, eA.x * blobScaleW, eA.y * blobScaleH);
					}
					float avgRadius = totalRadius / (float) numSegmentsToProcess;
					
					// store center of mass and average radius
					// p.stroke(0, 255, 0);
					p.ellipse(centerX, centerY, 35, 35);
//					p.stroke(255, 0, 0);
//					p.ellipse(centerX, centerY, avgRadius * 2f, avgRadius * 2f);
				}
			}
		}
	}
}
