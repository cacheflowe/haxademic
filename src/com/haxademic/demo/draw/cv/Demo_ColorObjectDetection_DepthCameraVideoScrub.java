package com.haxademic.demo.draw.cv;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.cv.ColorObjectDetection;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.depthcamera.DepthSilhouetteSmoothed;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.math.easing.EasingBoolean;
import com.haxademic.core.math.easing.EasingBoolean.IEasingBooleanCallback;
import com.haxademic.core.math.easing.FloatBuffer;
import com.haxademic.core.ui.UI;

import processing.core.PImage;
import processing.video.Movie;

public class Demo_ColorObjectDetection_DepthCameraVideoScrub 
extends PAppletHax
implements IEasingBooleanCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected DepthSilhouetteSmoothed depthSilhouetteSmoothed;
	protected FloatBuffer smoothedUserX = new FloatBuffer(30);
	protected EasingBoolean userActive;
	
	protected ColorObjectDetection colorObjectDetection;
	protected String COLOR_CLOSENESS_THRESHOLD = "COLOR_CLOSENESS_THRESHOLD";
	protected String COLOR_MIN_POINTS_DETECT_THRESHOLD = "COLOR_MIN_POINTS_DETECT_THRESHOLD";
	
	protected Movie movie;
	
	protected void firstFrame () {
		UI.addSlider(COLOR_CLOSENESS_THRESHOLD, 0.95f, 0.9f, 1f, 0.001f, false);
		UI.addSlider(COLOR_MIN_POINTS_DETECT_THRESHOLD, 10, 1, 100, 1, false);
		
		initDepthCamera();
		userActive = new EasingBoolean(false, 60, this);
		movie = new Movie(p, "D:\\workspace\\dbg-sci-py\\www\\apps\\_uploads\\pylon-2\\2Ageastrum-shortloop.mp4.crop.x642.y0.w762.h1080.mp4");
		movie.loop();
	}
	
	protected void initDepthCamera() {
		DepthCamera.instance(DepthCameraType.Realsense);
		IDepthCamera depthCamera = DepthCamera.instance().camera;
		depthSilhouetteSmoothed = new DepthSilhouetteSmoothed(depthCamera, 10);
		depthSilhouetteSmoothed.buildUI(false);
		UI.setValue(DepthSilhouetteSmoothed.SILHOUETTE_DEPTH_NEAR, 300);
		UI.setValue(DepthSilhouetteSmoothed.SILHOUETTE_DEPTH_FAR, 1000);
	}

	protected void drawApp() {
		// set up context
		p.background(0);
		p.noStroke();
		
		// update depth camera
		depthSilhouetteSmoothed.update();
		DebugView.setTexture("depthCamera", DepthCamera.instance().camera.getRgbImage());
		
		// update user detection w/camera
		updateUserDetection(depthSilhouetteSmoothed.image());
		
		// scrub video if user is active
		userActive.target(colorObjectDetection.isActive()).update();
		if(userActive.value() == true && colorObjectDetection.isActive()) {
			smoothedUserX.update(colorObjectDetection.x());
			float t = movie.duration() * smoothedUserX.average();
			movie.loop();
			movie.jump(t);
		}
		
		// draw camera & playhead
		ImageUtil.cropFillCopyImage(movie, p.g, true);
		p.rect(0, p.height - 10, p.width * (movie.time() / movie.duration()), 10);
		
		// draw debug
		drawDepthObjectJoystickDebug();
	}
	
	protected void updateUserDetection(PImage inputImage) {
		if(colorObjectDetection == null) {
			float detectionScaleDown = 1f;
			colorObjectDetection = new ColorObjectDetection(inputImage, detectionScaleDown);
		}
		colorObjectDetection.colorClosenessThreshold(UI.value(COLOR_CLOSENESS_THRESHOLD));
		colorObjectDetection.minPointsThreshold((int) UI.value(COLOR_MIN_POINTS_DETECT_THRESHOLD));
		colorObjectDetection.setColorCompare(1, 1, 1);
		colorObjectDetection.debugging(true);
		colorObjectDetection.update(inputImage);
	}
	
	protected void drawDepthObjectJoystickDebug() {
		// draw debug view to screen
		p.stroke(255);
		p.noFill();
		p.image(colorObjectDetection.sourceBuffer(), 0, 0);
		p.rect(0, 0, colorObjectDetection.sourceBuffer().width, colorObjectDetection.sourceBuffer().height);
		p.image(colorObjectDetection.analysisBuffer(), colorObjectDetection.sourceBuffer().width, 0);
		p.rect(colorObjectDetection.sourceBuffer().width, 0, colorObjectDetection.analysisBuffer().width, colorObjectDetection.analysisBuffer().height);
		
		// set debug values
		DebugView.setValue("BufferColorObjectDetection x", colorObjectDetection.x());
		DebugView.setValue("BufferColorObjectDetection y", colorObjectDetection.y());
	}

	@Override
	public void booleanSwitched(EasingBoolean booleanSwitch, boolean value) {
		if(value == true) {
			movie.pause();
		} else {
			movie.play();
		}
	}

}
