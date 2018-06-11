package com.haxademic.demo.hardware.kinect.openni;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.filters.shaders.BlendTowardsTexture;
import com.haxademic.core.draw.filters.shaders.BlurHFilter;
import com.haxademic.core.draw.filters.shaders.BlurVFilter;
import com.haxademic.core.draw.filters.shaders.BrightnessFilter;
import com.haxademic.core.draw.filters.shaders.ThresholdFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.kinect.KinectSize;

import processing.core.PGraphics;
import processing.opengl.PShader;


public class Demo_KinectDenoised 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public static final int PIXEL_SIZE = 3;
	public static final int KINECT_CLOSE = 500;
	public static final int KINECT_FAR = 2500;
	
	protected PGraphics depthBuffer;
	protected PGraphics avgBuffer;
	protected PGraphics postBuffer;
	
	public void setupFirstFrame() {
		depthBuffer = p.createGraphics(KinectSize.WIDTH / PIXEL_SIZE, KinectSize.HEIGHT / PIXEL_SIZE, PRenderers.P3D);
		avgBuffer = p.createGraphics(KinectSize.WIDTH / PIXEL_SIZE, KinectSize.HEIGHT / PIXEL_SIZE, PRenderers.P3D);
		postBuffer = p.createGraphics(KinectSize.WIDTH / PIXEL_SIZE, KinectSize.HEIGHT / PIXEL_SIZE, PRenderers.P3D);
		
		p.debugView.setTexture(depthBuffer);
		p.debugView.setTexture(avgBuffer);
		p.debugView.setTexture(postBuffer);
	}

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, true );
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 480 );
	}
	
	public void drawApp() {
		p.background(0);
		
		// draw current depth to buffer
		depthBuffer.beginDraw();
		depthBuffer.noStroke();
		depthBuffer.clear();
		depthBuffer.background(0);
		depthBuffer.fill(255);
		float pixelDepth;
		for ( int x = 0; x < depthBuffer.width; x++ ) {
			for ( int y = 0; y < depthBuffer.height; y++ ) {
				pixelDepth = p.kinectWrapper.getMillimetersDepthForKinectPixel( x * PIXEL_SIZE, y * PIXEL_SIZE );
				if( pixelDepth != 0 && pixelDepth > KINECT_CLOSE && pixelDepth < KINECT_FAR ) {
					depthBuffer.pushMatrix();
					depthBuffer.rect(x, y, 1, 1);
					depthBuffer.popMatrix();
				}
			}
		}
		depthBuffer.endDraw();
		
		// lerp texture
		BlendTowardsTexture.instance(p).setBlendLerp(0.25f);
		BlendTowardsTexture.instance(p).setSourceTexture(depthBuffer);
		BlendTowardsTexture.instance(p).applyTo(avgBuffer);

		// blur averaged buffer		
		BlurHFilter.instance(p).setBlurByPercent(0.35f, avgBuffer.width);
		BlurHFilter.instance(p).applyTo(avgBuffer);
		BlurVFilter.instance(p).setBlurByPercent(0.35f, avgBuffer.height);
		BlurVFilter.instance(p).applyTo(avgBuffer);
		
		// clean up post copy
		ImageUtil.copyImage(avgBuffer, postBuffer);
		BrightnessFilter.instance(p).setBrightness(1.25f);
		BrightnessFilter.instance(p).applyTo(postBuffer);
		ThresholdFilter.instance(p).setCutoff(0.4f);
		ThresholdFilter.instance(p).applyTo(postBuffer);

		p.image(postBuffer, 0, 0, p.width, p.height);
	}
	
}
