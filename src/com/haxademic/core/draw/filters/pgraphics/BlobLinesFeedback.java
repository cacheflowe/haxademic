package com.haxademic.core.draw.filters.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.cv.BufferMotionDetectionMap;
import com.haxademic.core.draw.filters.pgraphics.shared.BaseVideoFilter;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.EdgeColorDarkenFilter;
import com.haxademic.core.draw.image.ImageUtil;

import blobDetection.Blob;
import blobDetection.BlobDetection;
import blobDetection.EdgeVertex;
import processing.core.PGraphics;
import processing.core.PImage;

public class BlobLinesFeedback
extends BaseVideoFilter {
	
	protected BufferMotionDetectionMap motionDetectionMap;
	protected BlobDetection blobDetection;
	protected PGraphics blobSourceBuffer;
	protected PGraphics blobOutputBuffer;

	public BlobLinesFeedback(int width, int height) {
		super(width, height);
	}

	protected void initBlobDetection() {
		// create offscreen buffer for blob processing
		int blurImgW = motionDetectionMap.bwBuffer().width;
		int blurImgH = motionDetectionMap.bwBuffer().height;
		blobSourceBuffer = PG.newPG(blurImgW, blurImgH);
		
		// set up blur shader for blob pre-processing
		float shaderBlurAmount = 1f;
		BlurHFilter.instance().setBlurByPercent(shaderBlurAmount, blurImgW);
		BlurVFilter.instance().setBlurByPercent(shaderBlurAmount, blurImgH);

		// init blob detection object
		blobDetection = new BlobDetection(blurImgW, blurImgH);
		blobDetection.setPosDiscrimination(true);	// true if looking for bright areas
		blobDetection.setThreshold(0.5f); // will detect bright areas whose luminosity > threshold
	}

	public void newFrame(PImage frame) {
		// store (and crop fill) frame into `sourceBuffer`
		super.newFrame(frame);
		
		// lazy init and update motion detection buffers/calcs
		if(motionDetectionMap == null) {
			motionDetectionMap = new BufferMotionDetectionMap(sourceBuffer, 0.1f);
			blobOutputBuffer = PG.newPG(width, height);
			initBlobDetection();
			DebugView.setTexture("sourceBuffer", sourceBuffer);
		}

		// run motion detection
		motionDetectionMap.setBlendLerp(0.25f);
		motionDetectionMap.setDiffThresh(0.03f);
		motionDetectionMap.setFalloffBW(0.75f);
		motionDetectionMap.setThresholdCutoff(0.5f);
		motionDetectionMap.setBlur(1f);
		motionDetectionMap.updateSource(sourceBuffer);
	}
	
	protected void updateBlobDetection() {
		// copy frame difference buffer
		ImageUtil.copyImage(motionDetectionMap.bwBuffer(), blobSourceBuffer);
		
		// darken the edges to help with blob cleanliness
		EdgeColorDarkenFilter.instance().setSpreadX(0.05f);
		EdgeColorDarkenFilter.instance().setSpreadY(0.05f);
		EdgeColorDarkenFilter.instance().applyTo(blobSourceBuffer);
		
		// blur for blob computation smoothness
		BlurHFilter.instance().applyTo(blobSourceBuffer);
		BlurVFilter.instance().applyTo(blobSourceBuffer);
		
		// load pixels and pass to blob detection object
		blobSourceBuffer.loadPixels();
		blobDetection.computeBlobs(blobSourceBuffer.pixels);
	}
	
	public void update() {
		if(motionDetectionMap == null) return;

		// run blob detection and draw blobs to buffer
		updateBlobDetection();
		drawBlobs();
		
		// show detection buffer
		destBuffer.beginDraw();
		
		ImageUtil.cropFillCopyImage(blobSourceBuffer, destBuffer, false);
		
		// draw webcam to screen
		PG.setPImageAlpha(destBuffer, 0.6f);
		ImageUtil.cropFillCopyImage(sourceBuffer, destBuffer, false);
		PG.resetPImageAlpha(destBuffer);

		// draw bloob buffer to screen
		destBuffer.blendMode(PBlendModes.ADD);
		destBuffer.image(blobOutputBuffer, 0, 0);
		destBuffer.blendMode(PBlendModes.BLEND);
		
		destBuffer.endDraw();
	}
	
	protected void drawBlobs() {
		// draw to offscreen buffer so we can do ffedback & blending
		blobOutputBuffer.beginDraw();
		PG.feedback(blobOutputBuffer, 2f);
		PG.fadeToBlack(blobOutputBuffer, 6);
		blobOutputBuffer.stroke(127, 127, 0);
		blobOutputBuffer.fill(255, 0);
		blobOutputBuffer.strokeWeight(2);

		// draw edges. scale up to screen size
		Blob b;
		EdgeVertex eA, eB;
		float blobScaleW = blobOutputBuffer.width;
		float blobScaleH = blobOutputBuffer.height;
		
		// loop through blobs
		int numBlobs = blobDetection.getBlobNb();
		for (int i=0 ; i < numBlobs; i++) {
			b = blobDetection.getBlob(i);
			if ( b != null ) {
				// loop through blob segments & draw vertices
				int numBlobSegments = b.getEdgeNb();
				blobOutputBuffer.beginShape();
				for (int m = 0; m < numBlobSegments; m++) {
					eA = b.getEdgeVertexA(m);
					eB = b.getEdgeVertexB(m);
					if (eA !=null && eB !=null) {
						blobOutputBuffer.vertex( eA.x * blobScaleW, eA.y * blobScaleH );
						blobOutputBuffer.vertex( eB.x * blobScaleW, eB.y * blobScaleH );
					}
				}
				// connect last vertex to first
				eA = b.getEdgeVertexA(0);
				eB = b.getEdgeVertexB(0);
				blobOutputBuffer.vertex( eA.x * blobScaleW, eA.y * blobScaleH );
				blobOutputBuffer.vertex( eB.x * blobScaleW, eB.y * blobScaleH );

				// complete shape
				blobOutputBuffer.endShape();
			}
		}
		blobOutputBuffer.endDraw();
	}

}
