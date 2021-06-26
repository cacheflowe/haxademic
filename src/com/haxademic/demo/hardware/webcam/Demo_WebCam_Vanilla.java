package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;

import processing.video.Capture;

public class Demo_WebCam_Vanilla 
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
		} else if (cameras.length == 0) {
			println("There are no cameras available for capture.");
			exit();
		} else {
			println("Available cameras:");
			printArray(cameras);

			// The camera can be initialized directly using an element
			// from the array returned by list():
			cam = new Capture(this, cameras[1]);
			// Or, the settings can be defined based on the text in the list
			//cam = new Capture(this, 640, 480, "Built-in iSight", 30);

			// Start capturing the images from the camera
			cam.start();
		}	}

	protected void drawApp() {
		if (cam.available() == true) {
			cam.read();
		}
		image(cam, 0, 0, width, height);
		// The following does the same as the above image() line, but 
		// is faster when just drawing the image without any additional 
		// resizing, transformations, or tint.
		//set(0, 0, cam);
	}

}
