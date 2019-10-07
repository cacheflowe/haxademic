package com.haxademic.demo.hardware.depthcamera.kinectpv2;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.image.BufferMotionDetectionMap;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.depthcamera.KinectDepthSilhouetteSmoothed;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_KinectV2_DepthRgbOverlap
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected BufferMotionDetectionMap motionDetectionMap;
	protected KinectDepthSilhouetteSmoothed kinectSilhouetteSmoothed;
	protected PGraphics motionBuffer;	// copy kinect silhouette to match the size of the RGB camera

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true );
		p.appConfig.setProperty( AppSettings.KINECT_V2_WIN_ACTIVE, true );
//		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, true );
	}
	
	public void setupFirstFrame() {
		p.ui.addSlider("STRETCH_DEPTH_X", 1f, 1f, 3f, 0.01f);
		kinectSilhouetteSmoothed = new KinectDepthSilhouetteSmoothed(p.depthCamera, 5);
	}

	public void drawApp() {
		p.background(0);
		
		// update silhouette
		kinectSilhouetteSmoothed.update();
//		p.image(kinectSilhouetteSmoothed.image(), 0, 0, kinectSilhouetteSmoothed.image().width * 3, kinectSilhouetteSmoothed.image().height * 3);

		PImage depthImage = depthCamera.getDepthImage();
		PImage cameraImage = depthCamera.getRgbImage();
		
		// draw images
		p.image(cameraImage, 0, 0);
		PG.setPImageAlpha(p, 0.5f);
		if(motionBuffer != null) p.image(motionBuffer, 0, 0);
		PG.setPImageAlpha(p, 1f);
		
		// lazy-init motion detection to pass Kinect into
		if(motionDetectionMap == null && depthImage != null) {
			motionBuffer = P.p.createGraphics(cameraImage.width, cameraImage.height, P.P2D);
			motionDetectionMap = new BufferMotionDetectionMap(motionBuffer, 1f);
		}
		
		if(motionDetectionMap != null) {
			ImageUtil.cropFillCopyImage(kinectSilhouetteSmoothed.image(), motionBuffer, false);
			BlurHFilter.instance(P.p).setBlurByPercent(0.5f, motionBuffer.width);
			BlurHFilter.instance(P.p).applyTo(motionBuffer);
			BlurVFilter.instance(P.p).setBlurByPercent(0.5f, motionBuffer.height);
			BlurVFilter.instance(P.p).applyTo(motionBuffer);

			
			motionDetectionMap.setBlendLerp(0.25f);
			motionDetectionMap.setDiffThresh(0.025f);
			motionDetectionMap.setFalloffBW(0.25f);
			motionDetectionMap.setThresholdCutoff(0.5f);
			motionDetectionMap.setBlur(1f);
			motionDetectionMap.updateSource(motionBuffer);
			
			p.debugView.setTexture("kinectSilhouetteSmoothed", kinectSilhouetteSmoothed.image());
			p.debugView.setTexture("motionDetectionMap.backplate", motionDetectionMap.backplate());
			p.debugView.setTexture("motionDetectionMap.differenceBuffer", motionDetectionMap.differenceBuffer());
			p.debugView.setTexture("motionDetectionMap.bwBuffer", motionDetectionMap.bwBuffer());
		}
	}

}
