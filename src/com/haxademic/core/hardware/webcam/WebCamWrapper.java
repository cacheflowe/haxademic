package com.haxademic.core.hardware.webcam;

import processing.core.PApplet;
import processing.core.PImage;
import processing.video.Capture;

import com.haxademic.core.app.P;

public class WebCamWrapper {

	public static Capture webCam;
	public static PImage lastFrame = new PImage(32, 32);
	public static IWebCamCallback callbacks;

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
				P.println("Selected camera:", cameras[cameraIndex]);
				webCam = new Capture( p, cameras[cameraIndex] );
				webCam.start();
				return true;
			}      
		}
		return true;
	}

	public static void update() {
		if( webCam.available() == true ) {
			webCam.read();
			lastFrame = webCam;
			if(callbacks != null) callbacks.newFrame(lastFrame);
		}
	}
	
	public static PImage getImage() {
		return lastFrame;
	}
	
	public static void addWebCamCallback(IWebCamCallback obj) {
		callbacks = obj;
	}

	public static void drawImage( PApplet p, int x, int y ){
		p.image( webCam, x, y );
	}

	public static void dispose(){
		if( webCam != null ) webCam.stop();
	}

}
