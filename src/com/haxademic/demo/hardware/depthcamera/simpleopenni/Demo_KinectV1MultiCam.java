package com.haxademic.demo.hardware.depthcamera.simpleopenni;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;

import SimpleOpenNI.SimpleOpenNI;

public class Demo_KinectV1MultiCam
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected SimpleOpenNI  cam1;
	protected SimpleOpenNI  cam2;

	protected KinectUpdater _loader;
	protected Thread _updateThread;
	protected Boolean _updateComplete = true;


	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 480 );
		//		p.appConfig.setProperty(AppSettings.KINECT_ACTIVE, true );
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true );
	}

	public void setupFirstFrame() {
		// init the cameras
		cam1 = new SimpleOpenNI( p, SimpleOpenNI.RUN_MODE_MULTI_THREADED );
		cam2 = new SimpleOpenNI( SimpleOpenNI.RUN_MODE_MULTI_THREADED, p, 1);

		// set the camera generators
		// enable depthMap generation 
//		cam1.enableRGB();
		cam1.enableDepth();
		cam1.enableIR();

		// enable depthMap generation 
//		cam2.enableRGB();
		cam2.enableDepth();
		cam2.enableIR();

	}

	public void drawApp() {
		// update all cams
		// SimpleOpenNI.updateAll();
		update();

		// draw depthImageMap
		// cam1.update();
//		image(cam1.rgbImage(), 0, 0);
		image(cam1.depthImage(), 0, 0);
//		image(cam1.irImage(), 0, 480);

		// cam2.update();
//		image(cam2.rgbImage(), 640, 0);
		image(cam2.depthImage(), 640, 0);
//		image(cam2.irImage(), 640, 480);

	}

	// threaded updating

	class KinectUpdater implements Runnable {
		public KinectUpdater() {}    

		public void run() {
			if(cam1 != null && cam1.isInit() == true) {
				cam1.update();
				cam2.update();
				//				_depthArray = cam1.depthMap();
				//				if(_flipped == true) reverse(_depthArray);
				//				_realWorldMap = cam1.depthMapRealWorld();
				_updateComplete = true;
			}
		} 
	}

	public void update() {
		if(_updateComplete == true) {
			_updateComplete = false;
			if(_loader == null) _loader = new KinectUpdater();
			_updateThread = new Thread( _loader );
			_updateThread.start();
		}
	}


}
