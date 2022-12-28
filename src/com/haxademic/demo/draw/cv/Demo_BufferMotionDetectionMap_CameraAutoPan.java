package com.haxademic.demo.draw.cv;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.cv.BufferMotionDetectionMap;
import com.haxademic.core.draw.cv.ColorObjectDetection;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PGraphics;
import processing.core.PImage;


public class Demo_BufferMotionDetectionMap_CameraAutoPan 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

    protected BufferMotionDetectionMap motionDetectionMap;
	protected ColorObjectDetection colorObjectDetection;
	protected PGraphics webcamBuffer;
	protected EasingFloat panX = new EasingFloat(0, 0.1f);

	protected void config() {
	    Config.setAppSize(768, 1024);
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
		Config.setProperty( AppSettings.SHOW_UI, true );
	}

	protected void firstFrame() {
        // load webcam
        WebCam.instance().setDelegate(this);
        webcamBuffer = PG.newPG(640, 480);
		
		// cv buffer: color detection
        colorObjectDetection = new ColorObjectDetection(pg, 64, 64);
        DebugView.setTexture("analysisBuffer", colorObjectDetection.analysisBuffer());
	}
	
	protected void drawApp() {
		p.background(0);
		updateUserDetection();
		drawCamera();
	}
	
	protected void updateUserDetection() {
	    if(motionDetectionMap == null) return;
	    
        colorObjectDetection.setColorCompare(1, 1, 1);
        colorObjectDetection.minPointsThreshold(70);
        colorObjectDetection.debugging(true);
        colorObjectDetection.update(motionDetectionMap.bwBuffer());
        
        DebugView.setValue("BufferColorObjectDetection x", colorObjectDetection.x());
        DebugView.setValue("BufferColorObjectDetection y", colorObjectDetection.y());
	}
	
	protected void drawCamera() {
	    // get camera dimensions
	    float rgbW = webcamBuffer.width;
	    float rgbH = webcamBuffer.height;
	    float cameraScale = MathUtil.scaleToTarget(rgbH, p.height);
	    float outW = rgbW * cameraScale;
	    float outH = rgbH * cameraScale;
	    
	    // update panning
	    boolean hasUser = colorObjectDetection.isActive();
	    float panDist = (outW - p.width) / 2;
	    float inputX = colorObjectDetection.x();
	    if(hasUser) panX.setTarget(P.map(inputX, 0, 1, 1f, -1f));
//        panX.setEaseFactor(P.map(lerpAmp.value(), 0, 1, 0.3f, 0.05f));
	    panX.update(true);
	    
	    // crop to screen
	    p.push();
	    PG.setCenterScreen(p.g);
	    PG.setDrawCenter(p.g);
	    p.image(webcamBuffer, panDist * panX.value(), 0, outW, outH);
	    p.pop();
	}
	
	public void newFrame(PImage frame) {
	    // copy webcam and create motion detection at size of cropped webcam (and downscaling)
	    ImageUtil.cropFillCopyImage(frame, webcamBuffer, true);
	    ImageUtil.flipH(webcamBuffer);

	    // lazy init and update motion detection buffers/calcs
	    if(motionDetectionMap == null) {
	        motionDetectionMap = new BufferMotionDetectionMap(webcamBuffer, 0.1f);
	    }
	    // float mouseX = Mouse.xNorm;
	    motionDetectionMap.setBlendLerp(0.25f);
	    motionDetectionMap.setDiffThresh(0.03f);
	    motionDetectionMap.setFalloffBW(0.75f);
	    motionDetectionMap.setThresholdCutoff(0.2f);
	    motionDetectionMap.setBlur(1f);
	    motionDetectionMap.updateSource(webcamBuffer);

	    // set textures for debug view
	    DebugView.setTexture("webcam", frame);
	    DebugView.setTexture("motionDetectionMap.backplate", motionDetectionMap.backplate());
	    DebugView.setTexture("motionDetectionMap.differenceBuffer", motionDetectionMap.differenceBuffer());
	    DebugView.setTexture("motionDetectionMap.bwBuffer", motionDetectionMap.bwBuffer());
	}

}
