package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.system.DateUtil;

import processing.video.Capture;

class Demo_WebCam_VanillaGStreamer 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Capture cam;

	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true );
		Config.setProperty(AppSettings.FILLS_SCREEN, false );
	}

	protected void firstFrame () {
		String[] cameras = Capture.list();

		if (cameras == null) {
			println("Failed to retrieve the list of available cameras, will try the default...");
			cam = new Capture(this, 640, 480);
//		} else if (cameras.length == 0) {
//			println("There are no cameras available for capture.");
//			exit();
		} else {
			println("Available cameras:");
			printArray(cameras);
//			cam = new Capture(this, cameras[0]);
//			cam = new Capture(this, 1280, 720, "pipeline: ksvideosrc device-index=0 ! image/jpeg, width=1280, height=720, framerate=30/1 ! jpegdec ! videoconvert");
//			cam = new Capture(this, 1920, 1080, "pipeline: ksvideosrc device-index=0 ! image/jpeg, framerate=30/1 ! jpegdec ! videoconvert");
			cam = new Capture(this, 1920, 1080, "pipeline: ksvideosrc device-index=0 ! image/jpeg, width=1920, height=1080, framerate=30/1 ! jpegdec ! videoconvert");
			cam.start();
		}	
	}

	protected void drawApp() {
		if (cam != null) {
			if(cam.available() == true) {
				cam.read();
			}
			DebugView.setTexture("cam", cam);
			image(cam, 0, 0, cam.width, cam.height);
		}
		if(FrameLoop.frameModMinutes(1)) P.out("Running: ", DateUtil.uptimeHours());
	}

}
