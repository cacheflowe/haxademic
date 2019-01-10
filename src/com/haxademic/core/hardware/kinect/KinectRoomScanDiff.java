package com.haxademic.core.hardware.kinect;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.ErosionFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.opengl.PShader;

public class KinectRoomScanDiff {

	protected PGraphics roomScanBuffer;
	protected PGraphics depthBuffer;

	protected PShader colorDistanceFilter;
	protected PGraphics depthDifference;
	
	protected int roomMapCaptureStartFrame;
	protected int roomMapCaptureFrames = 400;
	
	protected int kinectNear = 300;
	protected int kinectFar = 5000;
	protected int pixelSkip = 6;
	protected float distanceDiffThreshold = 0.05f;
	protected float diffSmoothBlur = 0.75f;
	protected float depthBufferSmoothLerp = 0.2f;
	
	public KinectRoomScanDiff() {
		roomScanBuffer = P.p.createGraphics(KinectSize.WIDTH / pixelSkip, KinectSize.HEIGHT / pixelSkip, PRenderers.P2D);
		roomScanBuffer.noSmooth();

		depthBuffer = P.p.createGraphics(roomScanBuffer.width, roomScanBuffer.height, PRenderers.P2D);
		depthDifference = P.p.createGraphics(roomScanBuffer.width, roomScanBuffer.height, PRenderers.P2D);
		depthBuffer.noSmooth();
		depthDifference.noSmooth();
		
		colorDistanceFilter = P.p.loadShader(FileUtil.getFile("haxademic/shaders/filters/color-distance-two-textures.glsl"));
		
		reset();
	}
	
	public void reset() {
		roomScanBuffer.beginDraw();
		roomScanBuffer.background(0);
		roomScanBuffer.endDraw();

		roomMapCaptureStartFrame = P.p.frameCount;
	}
	
	public PGraphics roomScanBuffer() {
		return roomScanBuffer;
	}
	
	public PGraphics depthBuffer() {
		return depthBuffer;
	}
	
	public PGraphics depthDifference() {
		return depthDifference;
	}
	
	public void update() {
		if(P.p.kinectWrapper != null) {
			storeRoomScan();
			drawCurrentDepthBuffer();
		}
		processDepthDifference();
	}
	
	protected void storeRoomScan() {
		int scanFrameCount = P.p.frameCount - roomMapCaptureStartFrame;
		if(scanFrameCount < roomMapCaptureFrames) {
			// store kinect depth map
			roomScanBuffer.beginDraw();
			roomScanBuffer.noStroke();
			roomScanBuffer.blendMode(PBlendModes.LIGHTEST);
			for ( int x = 0; x < roomScanBuffer.width; x++ ) {
				for ( int y = 0; y < roomScanBuffer.height; y++ ) {
					int pixelDepth = P.p.kinectWrapper.getMillimetersDepthForKinectPixel( x * pixelSkip, y * pixelSkip );
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
			roomScanBuffer.endDraw();
		}
	}
	
	protected void drawCurrentDepthBuffer() {
		depthBuffer.beginDraw();
		depthBuffer.clear();
		depthBuffer.noStroke();
		int numPixelsProcessed = 0;
		for ( int x = 0; x < depthBuffer.width; x++ ) {
			for ( int y = 0; y < depthBuffer.height; y++ ) {
				int pixelDepth = P.p.kinectWrapper.getMillimetersDepthForKinectPixel( x * pixelSkip, y * pixelSkip );
				if( pixelDepth != 0 && pixelDepth > kinectNear && pixelDepth < kinectFar ) {
					float depthToGray = P.map(pixelDepth, kinectNear, kinectFar, 255, 0);
					depthBuffer.fill(P.constrain(depthToGray, 0, 255));
					depthBuffer.rect(x, y, 1, 1);
					numPixelsProcessed++;
				}
			}
		}
		depthBuffer.endDraw();
		P.p.debugView.setValue("numPixelsProcessed", numPixelsProcessed);
	}
	
	protected void processDepthDifference() {
		// do room vs current depth buffer difference
		colorDistanceFilter.set("tex1", roomScanBuffer);
		colorDistanceFilter.set("tex2", depthBuffer);
		depthDifference.filter(colorDistanceFilter);
		
		// remove noise on diff
		ErosionFilter.instance(P.p).applyTo(depthDifference);
		
		// smooth diff
		BlurHFilter.instance(P.p).setBlurByPercent(diffSmoothBlur, (float) depthDifference.width);
		BlurHFilter.instance(P.p).applyTo(depthDifference);
		BlurVFilter.instance(P.p).setBlurByPercent(diffSmoothBlur, (float) depthDifference.height);
		BlurVFilter.instance(P.p).applyTo(depthDifference);
		ThresholdFilter.instance(P.p).setCutoff(distanceDiffThreshold);
		ThresholdFilter.instance(P.p).applyTo(depthDifference);
	}
}
