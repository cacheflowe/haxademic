package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.RealSenseWrapper;
import com.haxademic.core.ui.UI;


public class Demo_DepthSilhouette_Realsense_HiRes 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected String NEAR = "NEAR";
	protected String FAR = "FAR";

	protected void config() {
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
		Config.setProperty( AppSettings.SHOW_UI, true );
	}

	protected void firstFrame() {
		UI.addSlider(NEAR, 0, 0, 5, 0.01f, false);
		UI.addSlider(FAR, 2, 0, 16, 0.01f, false);

		// init depth cam
		RealSenseWrapper.setMidStreamFast();
		RealSenseWrapper.FIXED_COLOR_SCHEME_GRADIENT = true;
		DepthCamera.instance(DepthCameraType.Realsense);
	}
	
	protected void drawApp() {
		// get realsense for better native near/far controls
		IDepthCamera depthCamera = DepthCamera.instance().camera;
		RealSenseWrapper realsense = (RealSenseWrapper) depthCamera;

		// use new threshold filter
		realsense.setNearFar(UI.value(NEAR), UI.value(FAR));

		pg.beginDraw();
		pg.clear();
		ImageUtil.cropFillCopyImage(realsense.getDepthImage(), p.g, true);

		float smoothing = 0.9f;
		BlurHFilter.instance().setBlurByPercent(smoothing, pg.width);
		BlurHFilter.instance().applyTo(pg);
		BlurVFilter.instance().setBlurByPercent(smoothing, pg.height);
		BlurVFilter.instance().applyTo(pg);
		
		// clean up post copy blobs
		float thresholdPreBrightness = 2.3f;
		float thresholdCutoff = 0.2f;
		BrightnessFilter.instance().setBrightness(thresholdPreBrightness);
		BrightnessFilter.instance().applyTo(pg);
		ThresholdFilter.instance().setCutoff(thresholdCutoff);
		ThresholdFilter.instance().applyTo(pg);

		pg.endDraw();

		p.background(0);
		p.image(pg, 0, 0);
	}
	
}
