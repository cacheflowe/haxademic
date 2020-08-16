package com.haxademic.core.hardware.depthcamera;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;

import processing.core.PGraphics;

public class KinectDepthSilhouetteSmoothed {

	protected IDepthCamera kinectWrapper;
	protected int pixelSkip = 5;
	protected int pixelsActive = 0;
	protected float frameBlend = 0.25f;
	protected float smoothing = 0.25f;
	protected float thresholdCutoff = 0.4f;
	protected float thresholdPreBrightness = 1.25f;
	protected float postBlur = 0;
	public static int KINECT_NEAR = 500;
	public static int KINECT_FAR = 1800;

	protected PGraphics depthBuffer;
	protected PGraphics avgBuffer;
	protected PGraphics postBuffer;

	public KinectDepthSilhouetteSmoothed(IDepthCamera kinectWrapper, int pixelSkip) {
		this.kinectWrapper = kinectWrapper;
		this.pixelSkip = pixelSkip;
		
		depthBuffer = P.p.createGraphics(DepthCameraSize.WIDTH / pixelSkip, DepthCameraSize.HEIGHT / pixelSkip, PRenderers.P3D);
		avgBuffer = P.p.createGraphics(DepthCameraSize.WIDTH / pixelSkip, DepthCameraSize.HEIGHT / pixelSkip, PRenderers.P3D);
		postBuffer = P.p.createGraphics(DepthCameraSize.WIDTH / pixelSkip, DepthCameraSize.HEIGHT / pixelSkip, PRenderers.P3D);
	}
	
	public void setFrameBlend(float frameBlend) {
		this.frameBlend = frameBlend;
	}
	
	public void setSmoothing(float smoothing) {
		this.smoothing = smoothing;
	}
	
	public void setThresholdPreBrightness(float thresholdPreBrightness) {
		this.thresholdPreBrightness = thresholdPreBrightness;
	}
	
	public void setThresholdCutoff(float thresholdCutoff) {
		this.thresholdCutoff = thresholdCutoff;
	}
	
	public void setPostBlur(float postBlur) {
		this.postBlur = postBlur;
	}
	
	public int pixelsActive() {
		return pixelsActive;
	}
	
	public PGraphics depthBuffer() {
		return depthBuffer;
	}
	
	public PGraphics avgBuffer() {
		return avgBuffer;
	}
	
	public PGraphics image() {
		return postBuffer;
	}
	
	public void update() {
		// draw current depth to buffer
		depthBuffer.beginDraw();
		depthBuffer.noStroke();
		depthBuffer.background(0);
		depthBuffer.fill(255);
		float pixelDepth;
		pixelsActive = 0;
		for ( int x = 0; x < depthBuffer.width; x++ ) {
			for ( int y = 0; y < depthBuffer.height; y++ ) {
				pixelDepth = kinectWrapper.getDepthAt( x * pixelSkip, y * pixelSkip );
				if( pixelDepth != 0 && pixelDepth > KINECT_NEAR && pixelDepth < KINECT_FAR ) {
					depthBuffer.pushMatrix();
					depthBuffer.rect(x, y, 1, 1);
					depthBuffer.popMatrix();
					pixelsActive++;
				}
			}
		}
		depthBuffer.endDraw();
		
		// lerp texture
		BlendTowardsTexture.instance(P.p).setBlendLerp(frameBlend);
		BlendTowardsTexture.instance(P.p).setSourceTexture(depthBuffer);
		BlendTowardsTexture.instance(P.p).applyTo(avgBuffer);

		// blur averaged buffer pre-threshold
		BlurHFilter.instance(P.p).setBlurByPercent(smoothing, avgBuffer.width);
		BlurHFilter.instance(P.p).applyTo(avgBuffer);
		BlurVFilter.instance(P.p).setBlurByPercent(smoothing, avgBuffer.height);
		BlurVFilter.instance(P.p).applyTo(avgBuffer);
		
		// clean up post copy blobs
		ImageUtil.copyImage(avgBuffer, postBuffer);
		BrightnessFilter.instance(P.p).setBrightness(thresholdPreBrightness);
		BrightnessFilter.instance(P.p).applyTo(postBuffer);
		ThresholdFilter.instance(P.p).setCutoff(thresholdCutoff);
		ThresholdFilter.instance(P.p).applyTo(postBuffer);
		
		// do post blur if applicable
		if(postBlur > 0) {
			// blur averaged buffer pre-threshold
			BlurHFilter.instance(P.p).setBlurByPercent(postBlur, postBuffer.width);
			BlurHFilter.instance(P.p).applyTo(postBuffer);
			BlurVFilter.instance(P.p).setBlurByPercent(postBlur, postBuffer.height);
			BlurVFilter.instance(P.p).applyTo(postBuffer);
		}
	}
}
