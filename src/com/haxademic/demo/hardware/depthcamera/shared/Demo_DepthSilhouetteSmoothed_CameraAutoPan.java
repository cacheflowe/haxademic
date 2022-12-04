package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.cv.ColorObjectDetection;
import com.haxademic.core.hardware.depthcamera.DepthSilhouetteSmoothed;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.LinearFloat;


public class Demo_DepthSilhouetteSmoothed_CameraAutoPan 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected DepthSilhouetteSmoothed depthSilhouetteSmoothed;
	protected ColorObjectDetection colorObjectDetection;
	protected EasingFloat panX = new EasingFloat(0, 0.1f);
	protected LinearFloat lerpAmp = new LinearFloat(0, 0.05f);

	protected void config() {
	    Config.setAppSize(768, 1024);
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
		Config.setProperty( AppSettings.SHOW_UI, true );
	}

	protected void firstFrame() {
		// init depth cam
//	    RealSenseWrapper.setSmallStream();
		DepthCamera.instance(DepthCameraType.Realsense);
		IDepthCamera depthCamera = DepthCamera.instance().camera;
		
		// cv buffer: silhouette
		depthSilhouetteSmoothed = new DepthSilhouetteSmoothed(depthCamera, 5);
		depthSilhouetteSmoothed.buildUI(false);

		// cv buffer: color detection
        float detectionScaleDown = 0.25f;
        colorObjectDetection = new ColorObjectDetection(pg, detectionScaleDown);

		// add camera images to debugview
		DebugView.setTexture("depthBuffer", depthSilhouetteSmoothed.depthBuffer());
		DebugView.setTexture("avgBuffer", depthSilhouetteSmoothed.avgBuffer());
		DebugView.setTexture("image", depthSilhouetteSmoothed.image());
		DebugView.setTexture("analysisBuffer", colorObjectDetection.analysisBuffer());
	}
	
	protected void drawApp() {
		p.background(0);
		updateSilhouette();
		updateUserDetection();
		drawCamera();
	}
	
	protected void updateSilhouette() {
	    depthSilhouetteSmoothed.update();
	    
	    DebugView.setValue("pixelsActive()", depthSilhouetteSmoothed.pixelsActive());
	}
	
	protected void updateUserDetection() {
        colorObjectDetection.setColorCompare(1, 1, 1);
        colorObjectDetection.minPointsThreshold(100);
        colorObjectDetection.debugging(true);
        colorObjectDetection.update(depthSilhouetteSmoothed.depthBuffer());
        
        DebugView.setValue("BufferColorObjectDetection x", colorObjectDetection.x());
        DebugView.setValue("BufferColorObjectDetection y", colorObjectDetection.y());
	}
	
	protected void drawCamera() {
	    // get camera dimensions
	    IDepthCamera depthCamera = DepthCamera.instance().camera;
	    float rgbW = depthCamera.getRgbImage().width;
	    float rgbH = depthCamera.getRgbImage().height;
	    float cameraScale = MathUtil.scaleToTarget(rgbH, p.height);
	    float outW = rgbW * cameraScale;
	    float outH = rgbH * cameraScale;
	    
	    // update panning
	    boolean hasUser = colorObjectDetection.isActive();
	    float panDist = (outW - p.width) / 2;
	    float inputX = (hasUser) ? colorObjectDetection.x() : 0;
	    lerpAmp.setTarget((hasUser) ? 1 : 0);
	    lerpAmp.setInc(0.025f);
	    lerpAmp.update(true);
        panX.setTarget(P.map(inputX, 0, 1, 1f, -1f));
        panX.setEaseFactor(P.map(lerpAmp.value(), 0, 1, 0.3f, 0.05f));
	    panX.update(true);
	    
	    // crop to screen
	    p.push();
	    PG.setCenterScreen(p.g);
	    PG.setDrawCenter(p.g);
	    p.image(depthCamera.getRgbImage(), panDist * panX.value(), 0, outW, outH);
	    p.pop();
	}
	
}
