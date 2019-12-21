package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.depthcamera.KinectDepthSilhouetteSmoothed;
import com.haxademic.core.hardware.mouse.Mouse;


public class Demo_KinectDepthSilhouetteSmoothed 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected KinectDepthSilhouetteSmoothed kinectSilhouetteSmoothed;

	protected void config() {
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
//		Config.setProperty( AppSettings.KINECT_V2_WIN_ACTIVE, true );
//		Config.setProperty( AppSettings.KINECT_ACTIVE, true );
		Config.setProperty( AppSettings.REALSENSE_ACTIVE, true );
		Config.setProperty( AppSettings.WIDTH, 640 );
		Config.setProperty( AppSettings.HEIGHT, 480 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
	}
	
	
	public void firstFrame() {
		kinectSilhouetteSmoothed = new KinectDepthSilhouetteSmoothed(p.depthCamera, 5);
		
		DebugView.setTexture("depthBuffer", kinectSilhouetteSmoothed.depthBuffer());
		DebugView.setTexture("avgBuffer", kinectSilhouetteSmoothed.avgBuffer());
		DebugView.setTexture("image", kinectSilhouetteSmoothed.image());
	}
	public void drawApp() {
		p.background(0);

		KinectDepthSilhouetteSmoothed.KINECT_FAR = 600 + P.round(2000 * Mouse.xNorm);
		KinectDepthSilhouetteSmoothed.KINECT_NEAR = 500;

		kinectSilhouetteSmoothed.update();
		ImageUtil.cropFillCopyImage(kinectSilhouetteSmoothed.image(), p.g, false);
	}
	
}
