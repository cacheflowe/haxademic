package com.haxademic.core.hardware.depthcamera;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.opengl.PShader;

public class DepthCameraRoomScanDiff {

	protected IDepthCamera depthCamera;
	protected PGraphics roomScanBuffer;
	protected PGraphics depthBuffer;
	protected PGraphics lerpedDepthBuffer;
	protected PGraphics resultSmoothed;
	protected PGraphics resultLerped;

	protected PShader colorDistanceFilter;
	protected PGraphics depthDifference;
	
	protected int roomMapCaptureStartFrame;
	protected int roomMapCaptureFrames = 600;
	
	protected boolean depthImageMode = true;
	protected int kinectNear = 300;
	protected int kinectFar = 8000;
	protected int pixelSkip = 10;
	
	protected float colorDiffThresh = 0.005f;
	protected float smoothThresh = 0.5f;
	protected float smoothBlur = 0.43f;
	protected float smoothLerp = 0.15f;
	protected boolean erodes = true;
	
	public DepthCameraRoomScanDiff(IDepthCamera kinectWrapper) {
		this(kinectWrapper, 3, true);
	}
	
	public DepthCameraRoomScanDiff(IDepthCamera kinectWrapper, int pixelSkip, boolean depthImageMode) {
		this.depthCamera = kinectWrapper;
		this.pixelSkip = pixelSkip;
		this.depthImageMode = depthImageMode;
		
		roomScanBuffer = PG.newPG2DFast(DepthCameraSize.WIDTH / pixelSkip, DepthCameraSize.HEIGHT / pixelSkip);
		depthBuffer = PG.newPG2DFast(roomScanBuffer.width, roomScanBuffer.height);
		depthDifference = PG.newPG2DFast(roomScanBuffer.width, roomScanBuffer.height);
		lerpedDepthBuffer = PG.newPG2DFast(roomScanBuffer.width, roomScanBuffer.height);

		resultLerped = PG.newPG(roomScanBuffer.width, roomScanBuffer.height);
		resultSmoothed = PG.newPG(roomScanBuffer.width, roomScanBuffer.height);
		PG.setTextureRepeat(resultLerped, false);
		PG.setTextureRepeat(resultSmoothed, false);

		colorDistanceFilter = P.p.loadShader(FileUtil.getPath("haxademic/shaders/filters/color-distance-two-textures.glsl"));
		
		reset();
	}
	
	public void reset() {
		roomScanBuffer.beginDraw();
		roomScanBuffer.background(0);
		roomScanBuffer.endDraw();
		roomMapCaptureStartFrame = P.p.frameCount;
	}
	
	// setters
	
	public void colorDiffThresh(float colorDiffThresh) {
		this.colorDiffThresh = colorDiffThresh;
	}
	public void smoothThresh(float smoothThresh) {
		this.smoothThresh = smoothThresh;
	}
	public void smoothBlur(float smoothBlur) {
		this.smoothBlur = smoothBlur;
	}
	public void smoothLerp(float smoothLerp) {
		this.smoothLerp = smoothLerp;
	}
	public void erodes(boolean erodes) {
		this.erodes = erodes;
	}
	
	// getters
	
	public PGraphics roomScanBuffer() {
		return roomScanBuffer;
	}
	
	public PGraphics depthBuffer() {
		return depthBuffer;
	}
	
	public PGraphics depthDifference() {
		return depthDifference;
	}
	
	public PGraphics resultLerped() {
		return resultLerped;
	}
	
	public PGraphics resultSmoothed() {
		return resultSmoothed;
	}
	
	public void update() {
		storeRoomScan();
		drawCurrentDepthBuffer();
		processDepthDifference();
		smoothOutput();
	}
	
	public float scanProgress() {
		int scanFrameCount = P.p.frameCount - roomMapCaptureStartFrame;
		float progress = (float) scanFrameCount / (float) roomMapCaptureFrames;
		return P.min(1, progress);
	}
	
	protected void storeRoomScan() {
		int scanFrameCount = P.p.frameCount - roomMapCaptureStartFrame;
		if(scanFrameCount < roomMapCaptureFrames) {
			// store kinect depth map
			roomScanBuffer.beginDraw();
			roomScanBuffer.noStroke();
			// lightest ensures that valid depth data closer to the camera will be stored
			roomScanBuffer.blendMode(PBlendModes.LIGHTEST);
			if(depthImageMode) {
				roomScanBuffer.image(depthCamera.getDepthImage(), 0, 0, roomScanBuffer.width, roomScanBuffer.height);
			} else {
				for ( int x = 0; x < roomScanBuffer.width; x++ ) {
					for ( int y = 0; y < roomScanBuffer.height; y++ ) {
						int pixelDepth = depthCamera.getDepthAt( x * pixelSkip, y * pixelSkip );
						if( pixelDepth != 0 && pixelDepth > kinectNear && pixelDepth < kinectFar ) {
							float depthToGray = P.map(pixelDepth, kinectNear, kinectFar, 255, 0);
							roomScanBuffer.fill(P.constrain(depthToGray, 0, 255));
							roomScanBuffer.rect(x, y, 1, 1);
						} else {
							roomScanBuffer.fill(0);
							roomScanBuffer.rect(x, y, 1, 1);
						}
					}
				}
			}
			roomScanBuffer.blendMode(PBlendModes.BLEND);
			roomScanBuffer.endDraw();
		}
	}
	
	protected void drawCurrentDepthBuffer() {
		if(depthImageMode) {
			lerpedDepthBuffer.beginDraw();
			lerpedDepthBuffer.image(depthCamera.getDepthImage(), 0, 0, lerpedDepthBuffer.width, lerpedDepthBuffer.height);
			lerpedDepthBuffer.endDraw();
		} else {
			int numPixelsProcessed = 0;
			lerpedDepthBuffer.loadPixels();
			for ( int x = 0; x < lerpedDepthBuffer.width; x++ ) {
				for ( int y = 0; y < lerpedDepthBuffer.height; y++ ) {
					int pixelDepth = depthCamera.getDepthAt( x * pixelSkip, y * pixelSkip );
					if( pixelDepth > kinectNear && pixelDepth < kinectFar ) {
						float depthToGray = P.map(pixelDepth, kinectNear, kinectFar, 255, 0);
						int col = P.p.color(P.constrain(depthToGray, 0, 255));
						lerpedDepthBuffer.pixels[MathUtil.gridIndexFromXY(x, y, lerpedDepthBuffer.width)] = col;
						numPixelsProcessed++;
					} else {
						lerpedDepthBuffer.pixels[MathUtil.gridIndexFromXY(x, y, lerpedDepthBuffer.width)] = P.p.color(0);
					}
				}
			}
			lerpedDepthBuffer.updatePixels();
			DebugView.setValue("KinectRoomScanDiff.numPixelsProcessed", numPixelsProcessed);

			// rect() method
//			lerpedDepthBuffer.beginDraw();
//			int numPixelsProcessed = 0;
//			for ( int x = 0; x < lerpedDepthBuffer.width; x++ ) {
//				for ( int y = 0; y < lerpedDepthBuffer.height; y++ ) {
//					int pixelDepth = depthCamera.getDepthAt( x * pixelSkip, y * pixelSkip );
//					if( pixelDepth > kinectNear && pixelDepth < kinectFar ) {	// allow black pixels by removing: pixelDepth != 0 && 
//						float depthToGray = P.map(pixelDepth, kinectNear, kinectFar, 255, 0);
//						lerpedDepthBuffer.fill(P.constrain(depthToGray, 0, 255));
//						lerpedDepthBuffer.rect(x, y, 1, 1);
//						numPixelsProcessed++;
//					} else {
//						lerpedDepthBuffer.fill(0, 255);
//						lerpedDepthBuffer.rect(x, y, 1, 1);
//					}
//				}
//			}
//			lerpedDepthBuffer.endDraw();
//			DebugView.setValue("KinectRoomScanDiff.numPixelsProcessed", numPixelsProcessed);
		}
		
		// composite room scan with current depth buffer on top
		// this helps fill in holes that might exist in current depth buffer
		depthBuffer.beginDraw();
		depthBuffer.noStroke();
		depthBuffer.background(0);
		depthBuffer.image(roomScanBuffer, 0, 0);	// lay down cached scan and draw on top
		depthBuffer.blendMode(PBlendModes.LIGHTEST);
		depthBuffer.image(lerpedDepthBuffer, 0, 0, depthBuffer.width, depthBuffer.height);
		depthBuffer.blendMode(PBlendModes.BLEND);
		depthBuffer.endDraw();
	}
	
	protected void processDepthDifference() {
		// do room vs current depth buffer difference
		colorDistanceFilter.set("tex1", roomScanBuffer);
		colorDistanceFilter.set("tex2", depthBuffer);
		depthDifference.filter(colorDistanceFilter);
		
		ThresholdFilter.instance(P.p).setCutoff(colorDiffThresh);
		ThresholdFilter.instance(P.p).applyTo(depthDifference);
	}
	
	protected void smoothOutput() {
		// lerp & blur to get rid of noise
		BlendTowardsTexture.instance(P.p).setBlendLerp(smoothLerp);
		BlendTowardsTexture.instance(P.p).setSourceTexture(depthDifference);
		BlendTowardsTexture.instance(P.p).applyTo(resultLerped);

		BlurHFilter.instance(P.p).setBlurByPercent(smoothBlur, (float) resultSmoothed.width);
		BlurHFilter.instance(P.p).applyTo(resultLerped);
		BlurVFilter.instance(P.p).setBlurByPercent(smoothBlur, (float) resultSmoothed.height);
		BlurVFilter.instance(P.p).applyTo(resultLerped);
		
		// copy lerped to smoothed
		ImageUtil.copyImage(resultLerped, resultSmoothed);
		
		// bring edges back in
		ThresholdFilter.instance(P.p).setCutoff(smoothThresh);
		ThresholdFilter.instance(P.p).applyTo(resultSmoothed);
	}


/*
 * OLD DRAW VIA RECT METHOD
	protected void storeRoomScan() {
		int scanFrameCount = P.p.frameCount - roomMapCaptureStartFrame;
		if(scanFrameCount < roomMapCaptureFrames) {
			// store kinect depth map
			roomScanBuffer.beginDraw();
			roomScanBuffer.noStroke();
			// lightest ensures that valid depth data closer to the camera will be stored
			roomScanBuffer.blendMode(PBlendModes.LIGHTEST);
			if(depthImageMode) {
				roomScanBuffer.image(depthCamera.getDepthImage(), 0, 0, roomScanBuffer.width, roomScanBuffer.height);
			} else {
				roomScanBuffer.loadPixels();
				for ( int x = 0; x < roomScanBuffer.width; x++ ) {
					for ( int y = 0; y < roomScanBuffer.height; y++ ) {
						int pixelDepth = depthCamera.getDepthAt( x * pixelSkip, y * pixelSkip );
						if( pixelDepth != 0 && pixelDepth > kinectNear && pixelDepth < kinectFar ) {
							float depthToGray = P.map(pixelDepth, kinectNear, kinectFar, 255, 0);
							int col = P.p.color(P.constrain(depthToGray, 0, 255));
							roomScanBuffer.pixels[MathUtil.gridIndexFromXY(x, y, roomScanBuffer.width)] = col;
						} else {
							roomScanBuffer.pixels[MathUtil.gridIndexFromXY(x, y, roomScanBuffer.width)] = P.p.color(0);
						}
					}
				}
				roomScanBuffer.updatePixels();
			}
			roomScanBuffer.blendMode(PBlendModes.BLEND);
			roomScanBuffer.endDraw();
		}
	}
	
	protected void drawCurrentDepthBuffer() {
		if(depthImageMode) {
			lerpedDepthBuffer.beginDraw();
			lerpedDepthBuffer.image(depthCamera.getDepthImage(), 0, 0, lerpedDepthBuffer.width, lerpedDepthBuffer.height);
			lerpedDepthBuffer.endDraw();
		} else {
			int numPixelsProcessed = 0;
			lerpedDepthBuffer.loadPixels();
			for ( int x = 0; x < lerpedDepthBuffer.width; x++ ) {
				for ( int y = 0; y < lerpedDepthBuffer.height; y++ ) {
					int pixelDepth = depthCamera.getDepthAt( x * pixelSkip, y * pixelSkip );
					if( pixelDepth > kinectNear && pixelDepth < kinectFar ) {
						float depthToGray = P.map(pixelDepth, kinectNear, kinectFar, 255, 0);
						int col = P.p.color(P.constrain(depthToGray, 0, 255));
						lerpedDepthBuffer.pixels[MathUtil.gridIndexFromXY(x, y, lerpedDepthBuffer.width)] = col;
						numPixelsProcessed++;
					} else {
						lerpedDepthBuffer.pixels[MathUtil.gridIndexFromXY(x, y, lerpedDepthBuffer.width)] = P.p.color(0);
					}
				}
			}
			lerpedDepthBuffer.updatePixels();
			DebugView.setValue("KinectRoomScanDiff.numPixelsProcessed", numPixelsProcessed);
		}
		
		// composite room scan with current depth buffer on top
		// this helps fill in holes that might exist in current depth buffer
		depthBuffer.beginDraw();
		depthBuffer.noStroke();
		depthBuffer.background(0);
		depthBuffer.image(roomScanBuffer, 0, 0);	// lay down cached scan and draw on top
		depthBuffer.blendMode(PBlendModes.LIGHTEST);
		depthBuffer.image(lerpedDepthBuffer, 0, 0, depthBuffer.width, depthBuffer.height);
		depthBuffer.blendMode(PBlendModes.BLEND);
		depthBuffer.endDraw();
	}

	
 * */
}