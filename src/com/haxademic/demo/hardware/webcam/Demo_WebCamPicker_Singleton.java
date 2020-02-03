package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.system.AppUtil;

public class Demo_WebCamPicker_Singleton 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, false );
	}
		
	protected void firstFrame() {
		AppUtil.setResizable(p, true);
	}
	
	protected void drawApp() {
		p.background(0);
		PG.setDrawCorner(p);
		
		// draw camera via singleton
		p.image(WebCam.instance().image(), 0, 0);

		// draw camera picker ui
		if(p.key == '1') WebCam.instance().drawMenu(p.g);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'r') WebCam.instance().refreshCameraList();
	}
}
