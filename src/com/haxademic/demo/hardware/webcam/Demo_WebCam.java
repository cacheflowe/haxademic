package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;

import processing.core.PImage;

public class Demo_WebCam 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true );
		Config.setProperty(AppSettings.FILLS_SCREEN, false );
	}
		
	protected void firstFrame () {
		WebCam.instance().setDelegate(this);
	}

	protected void drawApp() {
		PImage webcamImg = WebCam.instance().image();
		boolean webcamIsGood = (webcamImg.width > 400);
		
		p.background((webcamIsGood) ? 50 : p.color(255,0,0));
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);
		p.image(webcamImg, 0, 0);
		
		DebugView.setValue("webcam W", webcamImg.width);
		DebugView.setValue("webcam H", webcamImg.height);
		DebugView.setTexture("webcam", webcamImg);
	}

	@Override
	public void newFrame(PImage frame) {
		DebugView.setValue("Last WebCam frame", p.frameCount);
	}

}
