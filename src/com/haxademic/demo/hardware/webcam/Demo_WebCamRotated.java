package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.webcam.IWebCamCallback;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_WebCamRotated 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics rotatedBuffer;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WEBCAM_INDEX, 5 );
		p.appConfig.setProperty(AppSettings.WEBCAM_THREADED, false );
	}
		
	public void setupFirstFrame () {
		p.webCamWrapper.setDelegate(this);
		rotatedBuffer = PG.newPG(360, 640);
	}

	public void drawApp() {
		// is the webcam loaded?
		boolean webcamIsGood = (p.webCamWrapper.getImage().width > 32);
		p.background((webcamIsGood) ? 50 : p.color(255,0,0));
		
		// crop fill rotate to buffer
		rotatedBuffer.beginDraw();
		rotatedBuffer.background(0);
		ImageUtil.drawImageCropFillRotated(p.webCamWrapper.getImage(), rotatedBuffer, (p.mousePercentX() > 0.5f), (p.mousePercentY() > 0.5f), false);
		rotatedBuffer.endDraw();
		
		// draw rotated buffer to screen
		DrawUtil.setDrawCenter(p);
		DrawUtil.setCenterScreen(p);
		p.image(rotatedBuffer, 0, 0);
	}

	@Override
	public void newFrame(PImage frame) {
		p.debugView.setValue("Last WebCam frame", p.frameCount);
	}

}
