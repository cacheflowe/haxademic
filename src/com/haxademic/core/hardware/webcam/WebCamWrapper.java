package com.haxademic.core.hardware.webcam;

import com.haxademic.core.app.P;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.text.StringFormatter;

import processing.core.PImage;
import processing.video.Capture;

public class WebCamWrapper {

	public static Capture webCam;
	public static PImage lastFrame = new PImage(32, 32);
	public static IWebCamCallback delegate;

	public WebCamWrapper(int cameraIndex, boolean threaded) {
		if(webCam == null) {
			if(threaded) {
				new Thread(new Runnable() { public void run() {
					initCamera(cameraIndex);
				}}).start();	
			} else {
				initCamera(cameraIndex);
			}
			P.p.registerMethod("pre", this);
		}
	}
	
	public WebCamWrapper(int cameraIndex) {
		this(cameraIndex, true);
	}
	
	protected void initCamera(int cameraIndex) {
		P.println("WebCamWrapper: listing cameras");
		String[] cameras = Capture.list();
		P.println("cameras: "+cameras);
		if (cameras.length == 0) {
			P.println("There are no cameras available for capture.");
		} else {
			P.println("Available cameras:");
			for (int i = 0; i < cameras.length; i++) {
				// parse webcam string
				// String[] cameraNameParts = cameras[i].split(",");
				// get fps
				// String fpsStr = cameraNameParts[2].split("=")[1];
				// int fps = ConvertUtil.stringToInt(StringFormatter.toAlphaNumericChars(fpsStr)); 
				// get size
				// String sizeStr = cameraNameParts[1].split("=")[1];
				// String widthStr = sizeStr.split("x")[0];
				// int width = ConvertUtil.stringToInt(widthStr);
				// ditch cameras that are very not-awesome
				// if(fps > 20 && width > 600) {
					P.println("["+i+"] " + cameras[i]);
				// }
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
