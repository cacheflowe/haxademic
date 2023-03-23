package com.haxademic.core.hardware.depthcamera;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
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
	protected int depthNear = 500;
	protected int depthFar = 1800;

	protected PGraphics depthBuffer;
	protected PGraphics avgBuffer;
	protected PGraphics postBuffer;
	
	protected String uiID = null;
	protected boolean hasUI = false;
	protected String SILHOUETTE_DEPTH_NEAR = "SILHOUETTE_DEPTH_NEAR";
	protected String SILHOUETTE_DEPTH_FAR = "SILHOUETTE_DEPTH_FAR";
	protected String SILHOUETTE_FRAME_BLEND = "SILHOUETTE_FRAME_BLEND";
	protected String SILHOUETTE_SMOOTH = "SILHOUETTE_SMOOTH";
	protected String SILHOUETTE_THRESHOLD_PRE_BRIGHTNESS = "SILHOUETTE_THRESHOLD_PRE_BRIGHTNESS";
	protected String SILHOUETTE_THRESHOLD_CUTOFF = "SILHOUETTE_THRESHOLD_CUTOFF";
	protected String SILHOUETTE_POST_BLUR = "SILHOUETTE_POST_BLUR";

	public DepthSilhouetteSmoothed(IDepthCamera depthCamera, int pixelSkip) {
		this(depthCamera, pixelSkip, 500, 1800);
	}
	
	public DepthSilhouetteSmoothed(IDepthCamera depthCamera, int pixelSkip, int depthNear, int depthFar) {
		this.depthCamera = depthCamera;
		this.pixelSkip = pixelSkip;
		this.depthNear = depthNear;
		this.depthFar = depthFar;
		buildBuffers();
	}
	
	protected void buildBuffers() {
        int bufferW = DepthCameraSize.WIDTH / pixelSkip;
        int bufferH = DepthCameraSize.HEIGHT / pixelSkip;
        depthBuffer = PG.newPG2DFast(bufferW, bufferH);
        avgBuffer = PG.newPG2DFast(bufferW, bufferH);
        postBuffer = PG.newPG2DFast(bufferW, bufferH);
	}
	
	public void buildUI(boolean saveValues) {
	    buildUI(null, saveValues);
	}
	
	public void buildUI(String uiID, boolean saveValues) {
	    // set UI keys to be unique in case of multiple cameras. needs testing
	    String uiTitle = "DepthSilhouetteSmoothed";
	    if(uiID != null) {
            uiTitle += " | " + uiID;
            SILHOUETTE_DEPTH_NEAR = SILHOUETTE_DEPTH_NEAR.replace("SILHOUETTE_", "SILHOUETTE_"+uiID+"_");
            SILHOUETTE_DEPTH_FAR = SILHOUETTE_DEPTH_FAR.replace("SILHOUETTE_", "SILHOUETTE_"+uiID+"_");
            SILHOUETTE_FRAME_BLEND = SILHOUETTE_FRAME_BLEND.replace("SILHOUETTE_", "SILHOUETTE_"+uiID+"_");
            SILHOUETTE_SMOOTH = SILHOUETTE_SMOOTH.replace("SILHOUETTE_", "SILHOUETTE_"+uiID+"_");
            SILHOUETTE_THRESHOLD_PRE_BRIGHTNESS = SILHOUETTE_THRESHOLD_PRE_BRIGHTNESS.replace("SILHOUETTE_", "SILHOUETTE_"+uiID+"_");
            SILHOUETTE_THRESHOLD_CUTOFF = SILHOUETTE_THRESHOLD_CUTOFF.replace("SILHOUETTE_", "SILHOUETTE_"+uiID+"_");
            SILHOUETTE_POST_BLUR = SILHOUETTE_POST_BLUR.replace("SILHOUETTE_", "SILHOUETTE_"+uiID+"_");
	    }

	    // create UI controls
		UI.addTitle(uiTitle);
		UI.addSlider(SILHOUETTE_DEPTH_NEAR, depthNear, 0, 3000, 10, saveValues);
		UI.addSlider(SILHOUETTE_DEPTH_FAR, depthFar, 0, 6000, 10, saveValues);
		UI.addSlider(SILHOUETTE_FRAME_BLEND, 0.25f, 0, 1, 0.01f, saveValues);
		UI.addSlider(SILHOUETTE_SMOOTH, 0.25f, 0, 2, 0.01f, saveValues);
		UI.addSlider(SILHOUETTE_THRESHOLD_PRE_BRIGHTNESS, 1.25f, 0, 3, 0.01f, saveValues);
		UI.addSlider(SILHOUETTE_THRESHOLD_CUTOFF, 0.4f, 0, 1, 0.01f, saveValues);
		UI.addSlider(SILHOUETTE_POST_BLUR, 0, 0, 4, 0.01f, saveValues);
	    this.uiID = uiID;
		hasUI = true;
		updatePropsFromUI();
	}
	
	public void updatePropsFromUI() {
	    if(!hasUI) return; 
	    
		// apply UI settings to silhouette object
	    setDepthNear(UI.valueInt(SILHOUETTE_DEPTH_NEAR));
	    setDepthFar(UI.valueInt(SILHOUETTE_DEPTH_FAR));

		// do depth processing & draw to screen
		setFrameBlend(UI.value(SILHOUETTE_FRAME_BLEND));
		setSmoothing(UI.value(SILHOUETTE_SMOOTH));
		setThresholdPreBrightness(UI.value(SILHOUETTE_THRESHOLD_PRE_BRIGHTNESS));
		setThresholdCutoff(UI.value(SILHOUETTE_THRESHOLD_CUTOFF));
		setPostBlur(UI.value(SILHOUETTE_POST_BLUR));
	}
	
	public void setDepthNear(int depthNear) {
	    this.depthNear = depthNear;
	    UI.setValue(SILHOUETTE_DEPTH_NEAR, depthNear);
	}
	
	public void setDepthFar(int depthFar) {
	    this.depthFar = depthFar;
	    UI.setValue(SILHOUETTE_DEPTH_FAR, depthFar);
	}
	
	public void setFrameBlend(float frameBlend) {
		this.frameBlend = frameBlend;
		UI.setValue(SILHOUETTE_FRAME_BLEND, frameBlend);
	}
	
	public void setSmoothing(float smoothing) {
		this.smoothing = smoothing;
		UI.setValue(SILHOUETTE_SMOOTH, smoothing);
	}
	
	public void setThresholdPreBrightness(float thresholdPreBrightness) {
		this.thresholdPreBrightness = thresholdPreBrightness;
		UI.setValue(SILHOUETTE_THRESHOLD_PRE_BRIGHTNESS, thresholdPreBrightness);
	}
	
	public void setThresholdCutoff(float thresholdCutoff) {
		this.thresholdCutoff = thresholdCutoff;
		UI.setValue(SILHOUETTE_THRESHOLD_CUTOFF, thresholdCutoff);
	}
	
	public void setPostBlur(float postBlur) {
		this.postBlur = postBlur;
		UI.setValue(SILHOUETTE_POST_BLUR, postBlur);
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
		if(hasUI) updatePropsFromUI();
		
		// draw current depth to buffer
		depthBuffer.beginDraw();
		depthBuffer.noStroke();
		depthBuffer.background(0);
	    depthBuffer.loadPixels();
	    pixelsActive = 0;
	    float pixelDepth;
	    for ( int x = 0; x < depthBuffer.width; x++ ) {
            for ( int y = 0; y < depthBuffer.height; y++ ) {
                pixelDepth = depthCamera.getDepthAt( x * pixelSkip, y * pixelSkip );
                if( pixelDepth != 0 && pixelDepth > depthNear && pixelDepth < depthFar ) {
                    ImageUtil.setPixelColor(depthBuffer, x, y, 0xffffffff);
                    pixelsActive++;
                }
            }
        }
	    depthBuffer.updatePixels();
		depthBuffer.endDraw();
		
		// if we don't want a smoothed result, we can just do the depth buffer
		if(smoothing > 0) {
			
			// lerp texture
			BlendTowardsTexture.instance().setBlendLerp(frameBlend);
			BlendTowardsTexture.instance().setSourceTexture(depthBuffer);
			BlendTowardsTexture.instance().applyTo(avgBuffer);
	
			// blur averaged buffer pre-threshold
			BlurHFilter.instance().setBlurByPercent(smoothing, avgBuffer.width);
			BlurHFilter.instance().applyTo(avgBuffer);
			BlurVFilter.instance().setBlurByPercent(smoothing, avgBuffer.height);
			BlurVFilter.instance().applyTo(avgBuffer);
			
			// clean up post copy blobs
			ImageUtil.copyImage(avgBuffer, postBuffer);
			BrightnessFilter.instance().setBrightness(thresholdPreBrightness);
			BrightnessFilter.instance().applyTo(postBuffer);
			ThresholdFilter.instance().setCutoff(thresholdCutoff);
			ThresholdFilter.instance().applyTo(postBuffer);
			
			// do post blur if applicable
			if(postBlur > 0) {
				// blur averaged buffer pre-threshold
				BlurHFilter.instance().setBlurByPercent(postBlur, postBuffer.width);
				BlurHFilter.instance().applyTo(postBuffer);
				BlurVFilter.instance().setBlurByPercent(postBlur, postBuffer.height);
				BlurVFilter.instance().applyTo(postBuffer);
			}
		}
	}
}
