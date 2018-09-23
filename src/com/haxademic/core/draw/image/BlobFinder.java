package com.haxademic.core.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.filters.pshader.EdgeColorDarkenFilter;

import blobDetection.BlobDetection;
import processing.core.PGraphics;

public class BlobFinder {
	
	protected BlobDetection blobDetection;
	protected PGraphics sourceBuffer;
	protected PGraphics blobSourceBuffer;
	protected PGraphics blobOutputBuffer;

	public BlobFinder(PGraphics sourceBuffer, float scale) {
		// store source buffer
		this.sourceBuffer = sourceBuffer;
		
		// create scaled-down offscreen buffer for blob processing
		int blurImgW = P.round(sourceBuffer.width * scale);
		int blurImgH = P.round(sourceBuffer.height * scale);
		blobSourceBuffer = P.p.createGraphics(blurImgW, blurImgH, P.P2D);
		blobOutputBuffer = P.p.createGraphics(blurImgW, blurImgH, P.P2D);

		
//		// set up blur shader for blob pre-processing
//		float shaderBlurAmount = 1f;
//		BlurHFilter.instance(P.p).setBlurByPercent(shaderBlurAmount, blurImgW);
//		BlurVFilter.instance(P.p).setBlurByPercent(shaderBlurAmount, blurImgH);

		// init blob detection object
		blobDetection = new BlobDetection(blurImgW, blurImgH);
		blobDetection.setPosDiscrimination(true);	// true if looking for bright areas
		blobDetection.setThreshold(0.5f); // will detect bright areas whose luminosity > threshold
	}
	
	public BlobDetection blobDetection() {
		return blobDetection;
	}
	
	public void update() {
		// copy frame difference buffer
		ImageUtil.copyImage(sourceBuffer, blobSourceBuffer);
		
		// darken the edges to help with blob cleanliness
		EdgeColorDarkenFilter.instance(P.p).setSpreadX(0.05f);
		EdgeColorDarkenFilter.instance(P.p).setSpreadY(0.05f);
		EdgeColorDarkenFilter.instance(P.p).applyTo(blobSourceBuffer);
		
		// blur for blob computation smoothness
//		BlurHFilter.instance(P.p).applyTo(blobSourceBuffer);
//		BlurVFilter.instance(P.p).applyTo(blobSourceBuffer);
		
		// load pixels and pass to blob detection object
		blobSourceBuffer.loadPixels();
		blobDetection.computeBlobs(blobSourceBuffer.pixels);
	}
}
