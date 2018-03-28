package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.BufferMotionDetectionMap;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.webcam.IWebCamCallback;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_WebCamBufferMotionDetectionMap 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics webcamBuffer;
	protected BufferMotionDetectionMap motionDetectionMap;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 720 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty(AppSettings.WEBCAM_INDEX, 3 );
	}
		
	public void setupFirstFrame () {
		p.webCamWrapper.setDelegate(this);
		webcamBuffer = p.createGraphics(720, 720, PRenderers.P2D);
	}

	public void drawApp() {
		// set up context
		p.background(0);
		DrawUtil.setDrawCenter(p);
		DrawUtil.setCenterScreen(p);
		
		if(motionDetectionMap != null) {
			// show detection buffer
			ImageUtil.cropFillCopyImage(motionDetectionMap.bwBuffer(), p.g, true);

			// test buffer motion detection pixel data
			motionDetectionMap.loadPixels();
			if(motionDetectionMap.pixelActive(P.round(webcamBuffer.width/2), P.round(webcamBuffer.height/2))) {
				p.fill(255,0,0);
//				p.rect(0, 0, 100, 100);
			}
		}
	}

	@Override
	public void newFrame(PImage frame) {
		// copy webcam and create motion detection at size of cropped webcam (and downscaling)
		ImageUtil.cropFillCopyImage(frame, webcamBuffer, true);
		webcamBuffer.copy(0, 0, webcamBuffer.width, webcamBuffer.height, webcamBuffer.width, 0, -webcamBuffer.width, webcamBuffer.height); // flip h
		
		// lazy init and update motion detection buffers/calcs
		if(motionDetectionMap == null) {
			motionDetectionMap = new BufferMotionDetectionMap(webcamBuffer, 0.15f);
		}
		// float mouseX = p.mousePercentX();
		// p.debugView.setValue("mouseX", mouseX);
		motionDetectionMap.setBlendLerp(0.05f);
		motionDetectionMap.setDiffThresh(0.03f);
		motionDetectionMap.setFalloffBW(0.2f);
		motionDetectionMap.setThresholdCutoff(0.5f);
		motionDetectionMap.setBlur(1f);
		motionDetectionMap.updateSource(webcamBuffer);
		
		// set textures for debug view
		p.debugView.setTexture(frame);
		p.debugView.setTexture(motionDetectionMap.backplate());
		p.debugView.setTexture(motionDetectionMap.differenceBuffer());
		p.debugView.setTexture(motionDetectionMap.bwBuffer());
	}

}
