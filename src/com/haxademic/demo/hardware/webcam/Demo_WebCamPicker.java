package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.FrozenImageMonitor;
import com.haxademic.core.hardware.webcam.WebCamPicker;

public class Demo_WebCamPicker 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public WebCamPicker camPicker1 = null;
	public WebCamPicker camPicker2 = null;
	public FrozenImageMonitor freezeMonitor;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, false );
		p.appConfig.setProperty(AppSettings.WIDTH, 800 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 960 );
	}
		
	public void setupFirstFrame() {
		camPicker1 = new WebCamPicker("cam_1");
		camPicker2 = new WebCamPicker("cam_2");
		freezeMonitor = new FrozenImageMonitor();
	}

	public void drawApp() {
		p.background(0);
		PG.setDrawCorner(p);
		
		// draw camera2
		p.image(camPicker1.image(), 0, 0);
		p.image(camPicker2.image(), 0, 400);

		// draw picker ui
		if(p.key == '1') camPicker1.drawMenu(p.g);
		if(p.key == '2') camPicker2.drawMenu(p.g);
		
		// check for frozen image
		if(p.frameCount % 600 == 1) 
			p.debugView.setValue("webcam1 frozen", freezeMonitor.isFrozen(camPicker1.image()));
	}
}
