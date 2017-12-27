package com.haxademic.core.hardware.webcam;

import processing.core.PApplet;
import processing.core.PImage;
import processing.video.Capture;

import com.haxademic.core.app.P;

public class WebCamWrapper {

	public static Capture webCam;
	public static PImage lastFrame;

	public static boolean initWebCam( PApplet p, int cameraIndex ) {
		if( webCam == null ) {
			P.println("getting cameras");
			String[] cameras = Capture.list();
			P.println("cameras: "+cameras);
			if (cameras.length == 0) {
				P.println("There are no cameras available for capture.");
				return false;
			} else {
				P.println("Available cameras:");
				for (int i = 0; i < cameras.length; i++) {
					P.println("["+i+"] "+cameras[i]);
				}
				webCam = new Capture( p, cameras[cameraIndex] );
				webCam.start();
				return true;
			}      
		}
		return true;
	}

	public static PImage getImage() {
		if(webCam == null) return null;
		if( webCam.available() == true ) {
			webCam.read();
			lastFrame = webCam;
			return webCam;
		} else {
			// since webcam won't update as fast as 60fps, return the last frame
			return lastFrame;
		}
	}

	public static void drawImage( PApplet p, int x, int y ){
		p.image( webCam, x, y );
	}

	public static void dispose(){
		if( webCam != null ) webCam.stop();
	}

}
