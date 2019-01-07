package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;

import processing.video.Capture;

public class Demo_WebCamTwoCameras 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public Capture webCam1 = null;
	public Capture webCam2 = null;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true );
	}
		
	public void setupFirstFrame () {
		P.println("getting cameras");
		String[] cameras = Capture.list();
		P.println("cameras: "+cameras);
		if (cameras.length == 0) {
			P.println("There are no cameras available for capture.");
		} else {
			P.println("Available cameras:");
			for (int i = 0; i < cameras.length; i++) {
				P.println("["+i+"] "+cameras[i]);
			}
			webCam1 = new Capture( P.p, cameras[22] );
			webCam1.start();
			webCam2 = new Capture( P.p, cameras[115] );
			webCam2.start();
		}      
	}

	public void drawApp() {
		p.background( 0 );
		DrawUtil.setDrawCenter(p);
		DrawUtil.setCenterScreen(p);
		
		// 
		if(webCam2.available() == true) webCam2.read();
		if(webCam1.available() == true) webCam1.read();

		p.translate(-p.width/2, 0);
		p.image(webCam2, 0, 0);
		p.translate(p.width, 0);
		p.image(webCam1, 0, 0);
	}

}
