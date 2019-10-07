package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.webcam.WebCam;

public class Demo_WebCamPicker_Basic 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public WebCam camPicker = null;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, false );
	}
		
	public void setupFirstFrame() {
		camPicker = new WebCam("cam_1");
	}

	public void drawApp() {
		p.background(0);
		PG.setDrawCorner(p);
		
		// draw camera2
		p.image(camPicker.image(), 0, 0);

		// draw picker ui
		if(p.key == '1') camPicker.drawMenu(p.g);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'r') camPicker.refreshCameraList();
	}
}
