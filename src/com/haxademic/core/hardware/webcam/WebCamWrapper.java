package com.haxademic.core.hardware.webcam;

import com.haxademic.core.app.P;

import processing.core.PImage;
import processing.video.Capture;

public class WebCamWrapper {

	public static Capture webCam;
	public static PImage lastFrame = new PImage(32, 32);
	public static IWebCamCallback delegate;

	public WebCamWrapper(int cameraIndex) {
		if(webCam == null) {
			new Thread(new Runnable() { public void run() {
				initCamera(cameraIndex);
			}}).start();	
			P.p.registerMethod("pre", this);
		}
	}
	
	protected void initCamera(int cameraIndex) {
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
			P.println("Selected camera:", cameras[cameraIndex]);
			webCam = new Capture( P.p, cameras[cameraIndex] );
			webCam.start();
		}      
	}

	public void pre() {
		if(webCam == null) return;
		if(webCam.available() == true) {
			webCam.read();
			lastFrame = webCam;
			if(delegate != null) delegate.newFrame(lastFrame);
		}
	}
		
	public PImage getImage() {
		return lastFrame;
	}
	
	public void setDelegate(IWebCamCallback obj) {
		delegate = obj;
	}

	public void dispose() {
		if(webCam != null) webCam.stop();
	}

}
