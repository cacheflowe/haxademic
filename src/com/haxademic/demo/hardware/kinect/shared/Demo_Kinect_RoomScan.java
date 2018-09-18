package com.haxademic.demo.hardware.kinect.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PBlendModes;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.ErosionFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.kinect.KinectSize;

import processing.core.PGraphics;
import processing.opengl.PShader;

public class Demo_Kinect_RoomScan
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public int kinectNear = 300;
	public int kinectFar = 4000;
	
	protected PGraphics roomScanBuffer;
	protected PGraphics depthBuffer;
	protected PGraphics depthBufferSmoothed;

	protected PShader colorDistanceFilter;
	protected PGraphics depthDifference;
	
	protected int pixelSkip = 6;
	protected int roomMapCaptureFrames = 400;
	protected float distanceDiffThreshold = 0.05f;
	protected float diffSmoothBlur = 0.75f;
	protected float depthBufferSmoothLerp = 0.2f;

	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
//		p.appConfig.setProperty( AppSettings.KINECT_V2_WIN_ACTIVE, true );
		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, true );
	}

	public void setupFirstFrame() {
		roomScanBuffer = p.createGraphics(KinectSize.WIDTH / pixelSkip, KinectSize.HEIGHT / pixelSkip, PRenderers.P2D);
		roomScanBuffer.beginDraw();
		roomScanBuffer.background(0);
		roomScanBuffer.endDraw();

		depthBuffer = p.createGraphics(roomScanBuffer.width, roomScanBuffer.height, PRenderers.P2D);
		depthBufferSmoothed = p.createGraphics(roomScanBuffer.width, roomScanBuffer.height, PRenderers.P2D);
		depthDifference = p.createGraphics(roomScanBuffer.width, roomScanBuffer.height, PRenderers.P2D);
		
		colorDistanceFilter = P.p.loadShader(FileUtil.getFile("haxademic/shaders/filters/color-distance-two-textures.glsl"));
	}
	
	public void drawApp() {
		background(0, 127, 0);
		DrawUtil.setDrawCorner(p);
		p.noStroke();
		
		if(p.frameCount < roomMapCaptureFrames) {
			// store kinect depth map
			roomScanBuffer.beginDraw();
			roomScanBuffer.noStroke();
			roomScanBuffer.blendMode(PBlendModes.LIGHTEST);
			int numPixelsProcessed = 0;
			for ( int x = 0; x < roomScanBuffer.width; x++ ) {
				for ( int y = 0; y < roomScanBuffer.height; y++ ) {
					int pixelDepth = p.kinectWrapper.getMillimetersDepthForKinectPixel( x * pixelSkip, y * pixelSkip );
					if( pixelDepth != 0 && pixelDepth > kinectNear && pixelDepth < kinectFar ) {
						float depthToGray = P.map(pixelDepth, kinectNear, kinectFar, 255, 0);
						roomScanBuffer.fill(P.constrain(depthToGray, 0, 255));
						roomScanBuffer.rect(x, y, 1, 1);
						numPixelsProcessed++;
					} else {
						roomScanBuffer.fill(0);
						roomScanBuffer.rect(x, y, 1, 1);
					}
				}
			}
			roomScanBuffer.endDraw();
		}
		
		// draw current depth buffer
		depthBuffer.beginDraw();
		depthBuffer.clear();
		depthBuffer.noStroke();
		int numPixelsProcessed = 0;
		for ( int x = 0; x < depthBuffer.width; x++ ) {
			for ( int y = 0; y < depthBuffer.height; y++ ) {
				int pixelDepth = p.kinectWrapper.getMillimetersDepthForKinectPixel( x * pixelSkip, y * pixelSkip );
				if( pixelDepth != 0 && pixelDepth > kinectNear && pixelDepth < kinectFar ) {
					float depthToGray = P.map(pixelDepth, kinectNear, kinectFar, 255, 0);
					depthBuffer.fill(P.constrain(depthToGray, 0, 255));
					depthBuffer.rect(x, y, 1, 1);
					numPixelsProcessed++;
				}
			}
		}
		depthBuffer.endDraw();
		
		// lerp current depth buffer for smoothness (is this even needed?)
		BlendTowardsTexture.instance(p).setSourceTexture(depthBuffer);
		BlendTowardsTexture.instance(p).setBlendLerp(depthBufferSmoothLerp);
		BlendTowardsTexture.instance(p).applyTo(depthBufferSmoothed);
		
		// do room vs current depth buffer difference
		colorDistanceFilter.set("tex1", roomScanBuffer);
		colorDistanceFilter.set("tex2", depthBufferSmoothed);
		depthDifference.filter(colorDistanceFilter);
		
		// remove noise on diff
//		ErosionFilter.instance(p).applyTo(depthDifference);
		
		// smooth diff
		BlurHFilter.instance(P.p).setBlurByPercent(diffSmoothBlur, (float) depthDifference.width);
		BlurHFilter.instance(P.p).applyTo(depthDifference);
		BlurVFilter.instance(P.p).setBlurByPercent(diffSmoothBlur, (float) depthDifference.height);
		BlurVFilter.instance(P.p).applyTo(depthDifference);
		ThresholdFilter.instance(p).setCutoff(distanceDiffThreshold);
		ThresholdFilter.instance(p).applyTo(depthDifference);
		
		// draw all
		p.scale(2f);
		p.image(roomScanBuffer, 0, 0);
		p.image(depthBuffer, roomScanBuffer.width, 0);
		p.image(depthBufferSmoothed, roomScanBuffer.width * 2, 0);
		p.image(depthDifference, roomScanBuffer.width * 3, 0);
	}
	
}
