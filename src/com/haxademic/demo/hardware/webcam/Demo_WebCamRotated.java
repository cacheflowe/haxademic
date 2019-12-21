package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_WebCamRotated 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics rotatedBuffer;
	
	public void setupFirstFrame () {
		WebCam.instance().setDelegate(this);
		rotatedBuffer = PG.newPG(360, 640);
	}

	public void drawApp() {
		// is the webcam loaded?
		boolean webcamIsGood = (WebCam.instance().image().width > 32);
		p.background((webcamIsGood) ? 50 : p.color(255,0,0));
		
		// crop fill rotate to buffer
		rotatedBuffer.beginDraw();
		rotatedBuffer.background(0);
		ImageUtil.drawImageCropFillRotated90deg(WebCam.instance().image(), rotatedBuffer, (Mouse.xNorm > 0.5f), (Mouse.yNorm > 0.5f), false);
		rotatedBuffer.endDraw();
		
		// draw rotated buffer to screen
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);
		p.image(rotatedBuffer, 0, 0);
	}

	@Override
	public void newFrame(PImage frame) {
		p.debugView.setValue("Last WebCam frame", p.frameCount);
	}

}
