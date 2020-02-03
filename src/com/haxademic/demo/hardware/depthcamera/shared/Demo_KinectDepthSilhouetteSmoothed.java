package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.depthcamera.KinectDepthSilhouetteSmoothed;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.ui.UI;


public class Demo_KinectDepthSilhouetteSmoothed 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected KinectDepthSilhouetteSmoothed kinectSilhouetteSmoothed;
	protected String KINECT_NEAR = "KINECT_NEAR";
	protected String KINECT_FAR = "KINECT_FAR";

	protected void config() {
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.WIDTH, 640 );
		Config.setProperty( AppSettings.HEIGHT, 480 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {
		// init depth cam
		DepthCamera.instance(DepthCameraType.KinectV1);
		IDepthCamera depthCamera = DepthCamera.instance().camera;
		kinectSilhouetteSmoothed = new KinectDepthSilhouetteSmoothed(depthCamera, 5);
		
		// add camera images to debugview
		DebugView.setTexture("depthBuffer", kinectSilhouetteSmoothed.depthBuffer());
		DebugView.setTexture("avgBuffer", kinectSilhouetteSmoothed.avgBuffer());
		DebugView.setTexture("image", kinectSilhouetteSmoothed.image());
		
		// add UI
		UI.addTitle("Depth Camera Config");
		UI.addSlider(KINECT_NEAR, 300, 300, 3000, 10, false);
		UI.addSlider(KINECT_FAR, 1500, 500, 6000, 10, false);
	}
	
	protected void drawApp() {
		p.background(0);

		// apply UI settings to silhouette object
		KinectDepthSilhouetteSmoothed.KINECT_NEAR = UI.valueInt(KINECT_NEAR);
		KinectDepthSilhouetteSmoothed.KINECT_FAR = UI.valueInt(KINECT_FAR);

		// do depth processing & draw to screen
		kinectSilhouetteSmoothed.update();
		ImageUtil.cropFillCopyImage(kinectSilhouetteSmoothed.image(), p.g, false);
	}
	
}
