package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.webcam.IWebCamCallback;

import processing.core.PImage;

public class Demo_WebCamWrapper 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WEBCAM_INDEX, 5 );
		p.appConfig.setProperty(AppSettings.WEBCAM_THREADED, false );
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true );
		p.appConfig.setProperty(AppSettings.FILLS_SCREEN, false );
	}
		
	public void setupFirstFrame () {
		p.webCamWrapper.setDelegate(this);
	}

	public void drawApp() {
		boolean webcamIsGood = (p.webCamWrapper.getImage().width > 32);
		
		p.background((webcamIsGood) ? 50 : p.color(255,0,0));
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);
		p.image(p.webCamWrapper.getImage(), 0, 0);
		
		p.debugView.setValue("webcam W", p.webCamWrapper.getImage().width);
		p.debugView.setValue("webcam H", p.webCamWrapper.getImage().height);
	}

	@Override
	public void newFrame(PImage frame) {
		p.debugView.setValue("Last WebCam frame", p.frameCount);
	}

}
