package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.depthcamera.DepthSilhouetteSmoothed;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.ui.UI;


public class Demo_DepthSilhouetteSmoothed 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected DepthSilhouetteSmoothed depthSilhouetteSmoothed;
	protected String DEPTH_NEAR = "DEPTH_NEAR";
	protected String DEPTH_FAR = "DEPTH_FAR";
	protected String SILHOUETTE_FRAME_BLEND = "SILHOUETTE_FRAME_BLEND";
	protected String SILHOUETTE_SMOOTH = "SILHOUETTE_SMOOTH";
	protected String SILHOUETTE_THRESHOLD_PRE_BRIGHTNESS = "SILHOUETTE_THRESHOLD_PRE_BRIGHTNESS";
	protected String SILHOUETTE_THRESHOLD_CUTOFF = "SILHOUETTE_THRESHOLD_CUTOFF";
	protected String SILHOUETTE_POST_BLUR = "SILHOUETTE_POST_BLUR";

	protected void config() {
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.WIDTH, 640 );
		Config.setProperty( AppSettings.HEIGHT, 480 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {
		// init depth cam
		DepthCamera.instance(DepthCameraType.Realsense);
		IDepthCamera depthCamera = DepthCamera.instance().camera;
		depthSilhouetteSmoothed = new DepthSilhouetteSmoothed(depthCamera, 5);
		
		// add camera images to debugview
		DebugView.setTexture("depthBuffer", depthSilhouetteSmoothed.depthBuffer());
		DebugView.setTexture("avgBuffer", depthSilhouetteSmoothed.avgBuffer());
		DebugView.setTexture("image", depthSilhouetteSmoothed.image());
		
		// add UI
		UI.addTitle("Depth Camera Config");
		UI.addSlider(DEPTH_NEAR, 300, 300, 3000, 10, false);
		UI.addSlider(DEPTH_FAR, 1500, 500, 6000, 10, false);
		UI.addSlider(SILHOUETTE_FRAME_BLEND, 0.25f, 0, 1, 0.01f, false);
		UI.addSlider(SILHOUETTE_SMOOTH, 0.25f, 0, 2, 0.01f, false);
		UI.addSlider(SILHOUETTE_THRESHOLD_PRE_BRIGHTNESS, 1.25f, 0, 3, 0.01f, false);
		UI.addSlider(SILHOUETTE_THRESHOLD_CUTOFF, 0.4f, 0, 1, 0.01f, false);
		UI.addSlider(SILHOUETTE_POST_BLUR, 0, 0, 4, 0.01f, false);
	}
	
	protected void drawApp() {
		p.background(0);

		// apply UI settings to silhouette object
		DepthSilhouetteSmoothed.DEPTH_NEAR = UI.valueInt(DEPTH_NEAR);
		DepthSilhouetteSmoothed.DEPTH_FAR = UI.valueInt(DEPTH_FAR);

		// do depth processing & draw to screen
		depthSilhouetteSmoothed.setFrameBlend(UI.value(SILHOUETTE_FRAME_BLEND));
		depthSilhouetteSmoothed.setSmoothing(UI.value(SILHOUETTE_SMOOTH));
		depthSilhouetteSmoothed.setThresholdPreBrightness(UI.value(SILHOUETTE_THRESHOLD_PRE_BRIGHTNESS));
		depthSilhouetteSmoothed.setThresholdCutoff(UI.value(SILHOUETTE_THRESHOLD_CUTOFF));
		depthSilhouetteSmoothed.setPostBlur(UI.value(SILHOUETTE_POST_BLUR));
		depthSilhouetteSmoothed.update();
		ImageUtil.cropFillCopyImage(depthSilhouetteSmoothed.image(), p.g, false);
	}
	
}
