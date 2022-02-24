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
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;

public class DepthSilhouetteSmoothed {

	protected IDepthCamera depthCamera;
	protected int pixelSkip = 5;
	protected int pixelsActive = 0;
	protected float frameBlend = 0.25f;
	protected float smoothing = 0.25f;
	protected float thresholdCutoff = 0.4f;
	protected float thresholdPreBrightness = 1.25f;
	protected float postBlur = 0;
	public static int DEPTH_NEAR = 500;
	public static int DEPTH_FAR = 1800;

	protected PGraphics depthBuffer;
	protected PGraphics avgBuffer;
	protected PGraphics postBuffer;
	
	public static String SILHOUETTE_DEPTH_NEAR = "SILHOUETTE_DEPTH_NEAR";
	public static String SILHOUETTE_DEPTH_FAR = "SILHOUETTE_DEPTH_FAR";
	public static String SILHOUETTE_FRAME_BLEND = "SILHOUETTE_FRAME_BLEND";
	public static String SILHOUETTE_SMOOTH = "SILHOUETTE_SMOOTH";
	public static String SILHOUETTE_THRESHOLD_PRE_BRIGHTNESS = "SILHOUETTE_THRESHOLD_PRE_BRIGHTNESS";
	public static String SILHOUETTE_THRESHOLD_CUTOFF = "SILHOUETTE_THRESHOLD_CUTOFF";
	public static String SILHOUETTE_POST_BLUR = "SILHOUETTE_POST_BLUR";
	protected boolean hasUI = false;

	public DepthSilhouetteSmoothed(IDepthCamera depthCamera, int pixelSkip) {
		this(depthCamera, pixelSkip, DEPTH_NEAR, DEPTH_FAR);
	}
	
	public DepthSilhouetteSmoothed(IDepthCamera depthCamera, int pixelSkip, int depthNear, int depthFar) {
		this.depthCamera = depthCamera;
		this.pixelSkip = pixelSkip;
		DEPTH_NEAR = depthNear;
		DEPTH_FAR = depthFar;
		
		depthBuffer = P.p.createGraphics(DepthCameraSize.WIDTH / pixelSkip, DepthCameraSize.HEIGHT / pixelSkip, PRenderers.P3D);
		avgBuffer = P.p.createGraphics(DepthCameraSize.WIDTH / pixelSkip, DepthCameraSize.HEIGHT / pixelSkip, PRenderers.P3D);
		postBuffer = P.p.createGraphics(DepthCameraSize.WIDTH / pixelSkip, DepthCameraSize.HEIGHT / pixelSkip, PRenderers.P3D);
	}
	
	public void buildUI(boolean saveValues) {
		UI.addTitle("DepthSilhouetteSmoothed");
		UI.addSlider(SILHOUETTE_DEPTH_NEAR, DEPTH_NEAR, 300, 3000, 10, saveValues);
		UI.addSlider(SILHOUETTE_DEPTH_FAR, DEPTH_FAR, 500, 6000, 10, saveValues);
		UI.addSlider(SILHOUETTE_FRAME_BLEND, 0.25f, 0, 1, 0.01f, saveValues);
		UI.addSlider(SILHOUETTE_SMOOTH, 0.25f, 0, 2, 0.01f, saveValues);
		UI.addSlider(SILHOUETTE_THRESHOLD_PRE_BRIGHTNESS, 1.25f, 0, 3, 0.01f, saveValues);
		UI.addSlider(SILHOUETTE_THRESHOLD_CUTOFF, 0.4f, 0, 1, 0.01f, saveValues);
		UI.addSlider(SILHOUETTE_POST_BLUR, 0, 0, 4, 0.01f, saveValues);
		hasUI = true;
	}
	
	protected void updateUI() {
		// apply UI settings to silhouette object
		DepthSilhouetteSmoothed.DEPTH_NEAR = UI.valueInt(SILHOUETTE_DEPTH_NEAR);
		DepthSilhouetteSmoothed.DEPTH_FAR = UI.valueInt(SILHOUETTE_DEPTH_FAR);

		// do depth processing & draw to screen
		setFrameBlend(UI.value(SILHOUETTE_FRAME_BLEND));
		setSmoothing(UI.value(SILHOUETTE_SMOOTH));
		setThresholdPreBrightness(UI.value(SILHOUETTE_THRESHOLD_PRE_BRIGHTNESS));
		setThresholdCutoff(UI.value(SILHOUETTE_THRESHOLD_CUTOFF));
		setPostBlur(UI.value(SILHOUETTE_POST_BLUR));
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
		if(hasUI) updateUI();
		
		// draw current depth to buffer
		depthBuffer.beginDraw();
		depthBuffer.noStroke();
		depthBuffer.background(0);
		depthBuffer.fill(255);
		float pixelDepth;
		pixelsActive = 0;
		for ( int x = 0; x < depthBuffer.width; x++ ) {
			for ( int y = 0; y < depthBuffer.height; y++ ) {
				pixelDepth = depthCamera.getDepthAt( x * pixelSkip, y * pixelSkip );
				if( pixelDepth != 0 && pixelDepth > DEPTH_NEAR && pixelDepth < DEPTH_FAR ) {
					depthBuffer.pushMatrix();
					depthBuffer.rect(x, y, 1, 1);
					depthBuffer.popMatrix();
					pixelsActive++;
				}
			}
		}
		depthBuffer.endDraw();
		
		// if we don't want a smoothed result, we can just do the depth buffer
		if(smoothing > 0) {
			
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
}
