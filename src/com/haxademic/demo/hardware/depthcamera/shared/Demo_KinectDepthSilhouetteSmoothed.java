package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.depthcamera.KinectDepthSilhouetteSmoothed;


public class Demo_KinectDepthSilhouetteSmoothed 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected KinectDepthSilhouetteSmoothed kinectSilhouetteSmoothed;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
//		p.appConfig.setProperty( AppSettings.KINECT_V2_WIN_ACTIVE, true );
		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, true );
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 480 );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true );
	}
	
	
	public void setupFirstFrame() {
		kinectSilhouetteSmoothed = new KinectDepthSilhouetteSmoothed(p.kinectWrapper, 5);
		
		p.debugView.setTexture(kinectSilhouetteSmoothed.depthBuffer());
		p.debugView.setTexture(kinectSilhouetteSmoothed.avgBuffer());
		p.debugView.setTexture(kinectSilhouetteSmoothed.image());
	}
	public void drawApp() {
		p.background(0);

		KinectDepthSilhouetteSmoothed.KINECT_FAR = 600 + P.round(2000 * p.mousePercentX());
		KinectDepthSilhouetteSmoothed.KINECT_NEAR = 500;

		kinectSilhouetteSmoothed.update();
		ImageUtil.cropFillCopyImage(kinectSilhouetteSmoothed.image(), p.g, false);
	}
	
}
