package com.haxademic.demo.hardware.kinect.shared;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.hardware.kinect.KinectDepthSilhouetteSmoothed;


public class Demo_KinectDepthSilhouetteSmoothed 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected KinectDepthSilhouetteSmoothed kinectSilhouetteSmoothed;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.KINECT_V2_WIN_ACTIVE, true );
//		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, true );
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 480 );
	}
	
	
	public void setupFirstFrame() {
		kinectSilhouetteSmoothed = new KinectDepthSilhouetteSmoothed(p.kinectWrapper, 5);
		
		p.debugView.setTexture(kinectSilhouetteSmoothed.depthBuffer());
		p.debugView.setTexture(kinectSilhouetteSmoothed.avgBuffer());
		p.debugView.setTexture(kinectSilhouetteSmoothed.image());
	}
	public void drawApp() {
		p.background(0);
		kinectSilhouetteSmoothed.update();
		p.image(kinectSilhouetteSmoothed.image(), 0, 0, kinectSilhouetteSmoothed.image().width * 3, kinectSilhouetteSmoothed.image().height * 3);
	}
	
}
