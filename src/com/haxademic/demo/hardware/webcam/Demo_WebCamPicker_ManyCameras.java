package com.haxademic.demo.hardware.webcam;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.FrozenImageMonitor;
import com.haxademic.core.hardware.webcam.WebCam;

public class Demo_WebCamPicker_ManyCameras 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public ArrayList<WebCam> cams = new ArrayList<WebCam>();
	protected int webcamIndex = 0;
	protected int maxWebcams = 6;
	public FrozenImageMonitor freezeMonitor;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, false );
		p.appConfig.setProperty(AppSettings.WIDTH, 1500 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 960 );
	}
		
	public void setupFirstFrame() {
		addWebCam();
		freezeMonitor = new FrozenImageMonitor();
	}
	
	protected void addWebCam() {
		cams.add(new WebCam("cam_"+(cams.size() + 1)));
	}
	
	protected WebCam lastWebCam() {
		return cams.get(cams.size() - 1);
	}

	public void drawApp() {
		p.background(0);
		PG.setDrawCorner(p);
		
		// draw existing cams, and wait until it's initialized, then init camera 2. 
		// this is necessary because both trying to init at the same time doesn't work inside WebCam
		int camDestW = 500;
		int camDestH = 280;
		for (int i = 0; i < cams.size(); i++) {
			WebCam cam = cams.get(i);
			int x = (camDestW * (i % 3));
			int y = camDestH * P.floor(i/3f);
			p.fill(255);
			p.noStroke();
			p.image(cam.image(), x, y, camDestW, camDestH);
			p.stroke(0, 255, 0);
			p.noFill();
			p.rect(x, y, camDestW, camDestH);
		}
		
		// add webcams when the last is initialized
		if(cams.size() < maxWebcams) {
			if(lastWebCam().isReady()) {
				addWebCam();
			}
		}
		
		// draw UI webcam picker per camera
		if(p.key == '1' && cams.size() > 0) cams.get(0).drawMenu(p.g);
		if(p.key == '2' && cams.size() > 1) cams.get(1).drawMenu(p.g);
		if(p.key == '3' && cams.size() > 2) cams.get(2).drawMenu(p.g);
		if(p.key == '4' && cams.size() > 3) cams.get(3).drawMenu(p.g);
		if(p.key == '5' && cams.size() > 4) cams.get(4).drawMenu(p.g);
		if(p.key == '6' && cams.size() > 5) cams.get(5).drawMenu(p.g);
		
		// check for frozen image
//		if(p.frameCount % 60 == 1) {
//			p.debugView.setValue("webcam1 frozen", freezeMonitor.isFrozen(cam1.image()));
//			if(cam2 != null) p.debugView.setValue("webcam2 frozen", freezeMonitor.isFrozen(cam2.image()));
//		}
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') addWebCam();
//		if(p.key == 'r') cam1.refreshCameraList();
	}
}
