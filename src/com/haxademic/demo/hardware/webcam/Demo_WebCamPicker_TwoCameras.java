package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.FrozenImageMonitor;
import com.haxademic.core.hardware.webcam.WebCam;

public class Demo_WebCamPicker_TwoCameras 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public WebCam cam1 = null;
	public WebCam cam2 = null;
	public FrozenImageMonitor freezeMonitor;

	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, false );
		Config.setProperty(AppSettings.WIDTH, 800 );
		Config.setProperty(AppSettings.HEIGHT, 960 );
	}
		
	protected void firstFrame() {
		cam1 = new WebCam("cam_1");
		freezeMonitor = new FrozenImageMonitor();
	}

	protected void drawApp() {
		p.background(0);
		PG.setDrawCorner(p);
		
		// draw cam1, and wait until it's initialized, then init camera 2. 
		// this is necessary because both trying to init at the same time doesn't work inside WebCam
		p.image(cam1.image(), 0, 0);
		if(cam2 != null) p.image(cam2.image(), 0, 400);
		else if(cam1.isReady()) cam2 = new WebCam("cam_2");

		// draw picker ui
		if(p.key == '1') cam1.drawMenu(p.g);
		if(p.key == '2' && cam2 != null) cam2.drawMenu(p.g);
		
		// check for frozen image
		if(p.frameCount % 60 == 1) {
			DebugView.setValue("webcam1 frozen", freezeMonitor.isFrozen(cam1.image()));
			if(cam2 != null) DebugView.setValue("webcam2 frozen", freezeMonitor.isFrozen(cam2.image()));
		}
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'r') cam1.refreshCameraList();
	}
}
