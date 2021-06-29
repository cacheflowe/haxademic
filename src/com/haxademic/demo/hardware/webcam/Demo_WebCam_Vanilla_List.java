package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;

import processing.video.Capture;

public class Demo_WebCam_Vanilla_List 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Capture webCam;
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true );
		Config.setProperty(AppSettings.FILLS_SCREEN, false );
	}
		
	protected void firstFrame () {
		P.out("CameraConfig :: getting cameras");
		String[] camerasList = Capture.list();

		for (int i = 0; i < camerasList.length; i++) {
			// get camera name & components
			String camera = camerasList[i];
			P.out(i, camera);
		}
		
//		webCam = new Capture(P.p, camerasList[1]);
		webCam = new Capture(P.p, 640, 480);
		webCam.start();
	}

	protected void drawApp() {
		if(webCam.available() == true) webCam.read();
		p.image(webCam, 0, 0);

		DebugView.setValue("webCam.pipeline.toString()", webCam.pipeline.toString());
		DebugView.setValue("webCam.pipeline.getName()", webCam.pipeline.getName());
		DebugView.setValue("webCam.pipeline.listPropertyNames()", webCam.pipeline.listPropertyNames().toString());
		DebugView.setValue("webCam.pipeline.isPlaying()", webCam.pipeline.isPlaying());
		DebugView.setValue("webCam.pixelWidth", webCam.pixelWidth);
		DebugView.setValue("webCam.sourceWidth", webCam.sourceWidth);
		DebugView.setValue("webCam.width", webCam.width);
		DebugView.setValue("webCam.frameRate", webCam.frameRate);
	}

}
