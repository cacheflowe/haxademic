package com.haxademic.demo.hardware.webcam;

import java.awt.Rectangle;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.filters.pshader.LeaveWhiteFilter;
import com.haxademic.core.draw.image.BufferMotionDetectionMap;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_WebCamMotionDetectionRectZoom 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics webcamBuffer;
	protected BufferMotionDetectionMap motionDetectionMap;
	protected float motionBufferScale = 0.2f;
	protected EasingFloat[] rectSize;
	
	protected String BLEND_LERP = "BLEND_LERP";
	protected String DIFF_THRESH = "DIFF_THRESH";
	protected String FALLOFF_BW = "FALLOFF_BW";
	protected String THRESHOLD_CUTOFF = "THRESHOLD_CUTOFF";
	protected String MOTION_DETECT_BLUR = "MOTION_DETECT_BLUR";
	protected String RECT_LERP = "RECT_LERP";

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 640 + 320 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 480 );
		p.appConfig.setProperty(AppSettings.SMOOTHING, AppSettings.SMOOTH_NONE );
	}

	public void setupFirstFrame () {
		WebCam.instance().setDelegate(this);
		webcamBuffer = p.createGraphics(640, 480, PRenderers.P2D);
		webcamBuffer.noSmooth();

		// lerping rect size
		float lerpSpeed = 0.06f;
		rectSize = new EasingFloat[] {
				new EasingFloat(0, lerpSpeed),
				new EasingFloat(0, lerpSpeed),
				new EasingFloat(640, lerpSpeed),
				new EasingFloat(480, lerpSpeed),
		};

		// add sliders
		UI.addSlider(BLEND_LERP, 0.25f, 0.01f, 1f, 0.01f);
		UI.addSlider(DIFF_THRESH, 0.05f, 0.005f, 1f, 0.001f);
		UI.addSlider(FALLOFF_BW, 0.7f, 0.01f, 1f, 0.01f);
		UI.addSlider(THRESHOLD_CUTOFF, 0.5f, 0.01f, 1f, 0.01f);
		UI.addSlider(MOTION_DETECT_BLUR, 1f, 0.01f, 2f, 0.01f);
		UI.addSlider(RECT_LERP, lerpSpeed, 0.001f, 0.5f, 0.001f);
	}

	public void drawApp() {
		// set up context
		p.background(0);

		// find rectangle extents
		if(motionDetectionMap != null) {
			Rectangle rect = null;

			// check motion buffer grid for active pixels, and create a rectangle 
			motionDetectionMap.loadPixels();
			int pixelSkip = 2;
			for (int x = 0; x < webcamBuffer.width; x+=pixelSkip) {
				for (int y = 0; y < webcamBuffer.height; y+=pixelSkip) {
					if(motionDetectionMap.pixelActive(x, y)) {
						if(rect == null) {
							rect = new Rectangle(x, y, 1, 1);
						} else {
							rect.add(x, y);
						}
					}
				}				
			}
			
			if(rect != null) {
				
				// fix/lock aspect ratio
				float targetRatio = 640f / 480f;
				float rectRatio = (float) rect.width / (float) rect.height;
				if(targetRatio > rectRatio) { 	// rect is too skinny
					float addWidth = (rect.height * targetRatio) - rect.width;
					rect.x -= addWidth / 2f;
					rect.width += addWidth;
				} else {							 // rect is too short
					float addHeight = (rect.width / targetRatio) - rect.height;
					rect.y -= addHeight / 2f;
					rect.height += addHeight;
				}
				
				// make rectangle a little bigger to cover the subject
				float inflateAmp = 0.2f;
				float rectAddW = rect.width * inflateAmp;
				float rectAddH = rect.height * inflateAmp;
				rect.x -= rectAddW / 2;
				rect.y -= rectAddH / 2;
				rect.width += rectAddW;
				rect.height += rectAddH;
				
				// minimum rect size
				if(rect.width < 80) {
					float addWidth = 80 - rect.width;
					rect.x -= addWidth / 2;
					float addHeight = 60 - rect.height;
					rect.y -= addHeight / 2;
					rect.width = 80;
					rect.height = 60;
				}
				
				// keep in bounds
				if(rect.x < 0) rect.x = 0;
				if(rect.y < 0) rect.y = 0;
				if(rect.x + rect.width > webcamBuffer.width) rect.x = webcamBuffer.width - rect.width;
				if(rect.y + rect.height > webcamBuffer.height) rect.y = webcamBuffer.height - rect.height;
			}
			
			// update rect size
			if(rect != null) {
				// lerp rect
				rectSize[0].setTarget(rect.x);
				rectSize[1].setTarget(rect.y);
				rectSize[2].setTarget(rect.width);
				rectSize[3].setTarget(rect.height);
			} else {
				// reset rect
				rectSize[0].setTarget(0);
				rectSize[1].setTarget(0);
				rectSize[2].setTarget(webcamBuffer.width);
				rectSize[3].setTarget(webcamBuffer.height);
			}
			// lerp rect
			for (int i = 0; i < rectSize.length; i++) rectSize[i].setEaseFactor(UI.value(RECT_LERP));
			for (int i = 0; i < rectSize.length; i++) rectSize[i].update(true);

			// debug draw
			float debugW = 640f/2f;
			float debugH = 480f/2f;
			
			// copy rect to display zoomed portion
			float[] imageCropSize = ImageUtil.getOffsetAndSizeToCrop(p.width, p.height, rectSize[2].value(), rectSize[3].value(), false);
			p.copy(webcamBuffer, (int) rectSize[0].value(), (int) rectSize[1].value(), (int) rectSize[2].value(), (int) rectSize[3].value(), 320/2 + (int) imageCropSize[0], (int) imageCropSize[1], (int) imageCropSize[2], (int) imageCropSize[3]);

			// draw webcam & motion map
			p.g.fill(255);
			p.g.noStroke();
			p.g.image(webcamBuffer, 0, 0, debugW, debugH);
			
			LeaveWhiteFilter.instance(p).applyTo(motionDetectionMap.bwBuffer());
			p.blendMode(PBlendModes.EXCLUSION);
			p.g.image(motionDetectionMap.bwBuffer(), 0, 0, debugW, debugH);
			p.blendMode(PBlendModes.BLEND);

			// draw rect
			p.g.noFill();
			p.g.strokeWeight(4);
			p.g.stroke(255,0,0);
			p.g.rect(rectSize[0].value() / 2f, rectSize[1].value() / 2f, rectSize[2].value() / 2f, rectSize[3].value() / 2f);
			
			// draw more textures
			p.g.image(motionDetectionMap.differenceBuffer(), 0, debugH, debugW, debugH);
		}
	}

	@Override
	public void newFrame(PImage frame) {
		// copy webcam and create motion detection at size of cropped webcam (and downscaling)
		// ImageUtil.cropFillCopyImage(frame, webcamBuffer, true);
		ImageUtil.copyImageFlipH(frame, webcamBuffer);
//		ImageUtil.copyImage(frame, webcamBuffer);
//		ImageUtil.flipH(webcamBuffer);

		// lazy init and update motion detection buffers/calcs
		if(motionDetectionMap == null) {
			motionDetectionMap = new BufferMotionDetectionMap(webcamBuffer, motionBufferScale);
		}
		motionDetectionMap.setBlendLerp(UI.value(BLEND_LERP));
		motionDetectionMap.setDiffThresh(UI.value(DIFF_THRESH));
		motionDetectionMap.setFalloffBW(UI.value(FALLOFF_BW));
		motionDetectionMap.setThresholdCutoff(UI.value(THRESHOLD_CUTOFF));
		motionDetectionMap.setBlur(UI.value(MOTION_DETECT_BLUR));
		motionDetectionMap.updateSource(webcamBuffer);

		// set textures for debug view
		//		p.debugView.setTexture(frame);
		//		p.debugView.setTexture(motionDetectionMap.backplate());
		//		p.debugView.setTexture(motionDetectionMap.differenceBuffer());
		//		p.debugView.setTexture(motionDetectionMap.bwBuffer());
	}

}
