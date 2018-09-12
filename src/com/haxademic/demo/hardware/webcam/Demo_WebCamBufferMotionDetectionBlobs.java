package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PBlendModes;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.EdgeColorDarkenFilter;
import com.haxademic.core.draw.image.BufferMotionDetectionMap;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.webcam.IWebCamCallback;

import blobDetection.Blob;
import blobDetection.BlobDetection;
import blobDetection.EdgeVertex;
import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_WebCamBufferMotionDetectionBlobs 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics webcamBuffer;
	protected BufferMotionDetectionMap motionDetectionMap;
	protected BlobDetection blobDetection;
	protected PGraphics blobSourceBuffer;
	protected PGraphics blobOutputBuffer;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 800 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 600 );
		p.appConfig.setProperty(AppSettings.WEBCAM_INDEX, 5 );
	}
		
	public void setupFirstFrame () {
		p.webCamWrapper.setDelegate(this);
	}
	
	protected void initBlobDetection() {
		// create offscreen buffer for blob processing
		int blurImgW = motionDetectionMap.bwBuffer().width;
		int blurImgH = motionDetectionMap.bwBuffer().height;
		blobSourceBuffer = P.p.createGraphics(blurImgW, blurImgH, P.P2D);
		
		// set up blur shader for blob pre-processing
		float shaderBlurAmount = 1f;
		BlurHFilter.instance(P.p).setBlurByPercent(shaderBlurAmount, blurImgW);
		BlurVFilter.instance(P.p).setBlurByPercent(shaderBlurAmount, blurImgH);

		// init blob detection object
		blobDetection = new BlobDetection(blurImgW, blurImgH);
		blobDetection.setPosDiscrimination(true);	// true if looking for bright areas
		blobDetection.setThreshold(0.5f); // will detect bright areas whose luminosity > threshold
	}

	protected void updateBlobDetection() {
		// copy frame difference buffer
		ImageUtil.copyImage(motionDetectionMap.bwBuffer(), blobSourceBuffer);
		
		// darken the edges to help with blob cleanliness
		EdgeColorDarkenFilter.instance(p).setSpreadX(0.05f);
		EdgeColorDarkenFilter.instance(p).setSpreadY(0.05f);
		EdgeColorDarkenFilter.instance(p).applyTo(blobSourceBuffer);
		
		// blur for blob computation smoothness
		BlurHFilter.instance(P.p).applyTo(blobSourceBuffer);
		BlurVFilter.instance(P.p).applyTo(blobSourceBuffer);
		
		// load pixels and pass to blob detection object
		blobSourceBuffer.loadPixels();
		blobDetection.computeBlobs(blobSourceBuffer.pixels);
	}

	public void drawApp() {
		// set up context
		p.background(0);
		
		if(motionDetectionMap != null) {
			// show detection buffer
			ImageUtil.cropFillCopyImage(blobSourceBuffer, p.g, false);
			
			// draw webcam to screen
			DrawUtil.setPImageAlpha(p, 0.6f);
			ImageUtil.cropFillCopyImage(webcamBuffer, p.g, false);
			DrawUtil.resetPImageAlpha(p);
			
			// run blob detection
			updateBlobDetection();
			
			// draw blobs to buffer
			drawBlobs();
			
			// draw bloob buffer to screen
			p.blendMode(PBlendModes.ADD);
			p.image(blobOutputBuffer, 0, 0);
			p.blendMode(PBlendModes.BLEND);
		}
	}
	
	protected void drawBlobs() {
		// draw to offscreen buffer so we can do ffedback & blending
		blobOutputBuffer.beginDraw();
		DrawUtil.feedback(blobOutputBuffer, 2f);
		DrawUtil.fadeToBlack(blobOutputBuffer, 10);
		blobOutputBuffer.stroke(127, 127, 0);
		blobOutputBuffer.fill(255, 0);
		blobOutputBuffer.strokeWeight(4);

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
	
	@Override
	public void newFrame(PImage frame) {
		// lazy init and update motion detection buffers/calcs
		if(motionDetectionMap == null) {
			webcamBuffer = p.createGraphics(640, 480, PRenderers.P2D);
			blobOutputBuffer = p.createGraphics(p.width, p.height, PRenderers.P3D);
			motionDetectionMap = new BufferMotionDetectionMap(webcamBuffer, 0.1f);
			initBlobDetection();
		}
		
		// copy webcam and create motion detection at size of cropped webcam (and downscaling)
		ImageUtil.cropFillCopyImage(frame, webcamBuffer, true);
		ImageUtil.flipH(webcamBuffer);
		
		// set motion detection object props
		motionDetectionMap.setBlendLerp(0.25f);
		motionDetectionMap.setDiffThresh(0.03f);
		motionDetectionMap.setFalloffBW(0.75f);
		motionDetectionMap.setThresholdCutoff(0.5f);
		motionDetectionMap.setBlur(1f);
		motionDetectionMap.updateSource(webcamBuffer);
		
		// set textures for debug view
		p.debugView.setTexture(frame);
		p.debugView.setTexture(motionDetectionMap.backplate());
		p.debugView.setTexture(motionDetectionMap.differenceBuffer());
		p.debugView.setTexture(motionDetectionMap.bwBuffer());
		p.debugView.setTexture(blobSourceBuffer);
	}

}
