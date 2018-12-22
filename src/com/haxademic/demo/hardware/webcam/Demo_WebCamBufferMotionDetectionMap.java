package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.BufferMotionDetectionMap;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.webcam.IWebCamCallback;
import com.haxademic.core.math.MathUtil;

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
		webcamBuffer = p.createGraphics(p.width, p.height, PRenderers.P2D);
	}

	public void drawApp() {
		// set up context
		p.background(0);
		DrawUtil.setDrawCenter(p);
//		DrawUtil.setCenterScreen(p);
		
		if(motionDetectionMap != null) {
			// show detection buffer
			ImageUtil.cropFillCopyImage(motionDetectionMap.bwBuffer(), p.g, false);

			// test buffer motion detection pixel data
			// find random points
			drawRandomPoints();
		}
		
	}
	
	protected void drawRandomPoints() {
		// draw shapes - find launch points
		motionDetectionMap.loadPixels();
		p.fill(255, 0, 0);
		p.noStroke();
		
		int FRAME_LAUNCH_INTERVAL = 1;
		int MAX_LAUNCHED_PER_FRAME = 100;
		if(p.frameCount % FRAME_LAUNCH_INTERVAL == 0) {
			int numLaunched = 0;
			for (int i = 0; i < 1000; i++) {
				if(numLaunched < MAX_LAUNCHED_PER_FRAME) {
					int checkX = MathUtil.randRange(0, webcamBuffer.width);
					int checkY = MathUtil.randRange(0, webcamBuffer.height);
					if(motionDetectionMap.pixelActive(checkX, checkY)) {
						p.rect(checkX, checkY, 4, 4); // 4 because of 0.25 motion detection scale
						numLaunched++;
					}
				}
			}
		}

	}


	@Override
	public void newFrame(PImage frame) {
		// copy webcam and create motion detection at size of cropped webcam (and downscaling)
		ImageUtil.cropFillCopyImage(frame, webcamBuffer, true);
		ImageUtil.flipH(webcamBuffer);
		
		// lazy init and update motion detection buffers/calcs
		if(motionDetectionMap == null) {
			motionDetectionMap = new BufferMotionDetectionMap(webcamBuffer, 0.25f);
		}
		// float mouseX = p.mousePercentX();
		// p.debugView.setValue("mouseX", mouseX);
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
	}

}
