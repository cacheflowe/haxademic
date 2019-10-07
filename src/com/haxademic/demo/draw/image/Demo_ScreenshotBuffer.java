package com.haxademic.demo.draw.image;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.ScreenshotBuffer;

public class Demo_ScreenshotBuffer
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected ScreenshotBuffer screenshotBuffer;

	public void setupFirstFrame() {
		screenshotBuffer = new ScreenshotBuffer();
		screenshotBuffer.addScaledImage(0.5f);
	}

	public void drawApp() {
		background(0);
		
		// update screenshot occasionally
		if(p.frameCount % 60 == 1) screenshotBuffer.needsUpdate(true);
		
		// draw 2 versions of screenshot
		ImageUtil.cropFillCopyImage(screenshotBuffer.image(), p.g, false);
		p.g.image(screenshotBuffer.scaledImg(), 0, 0);
	}

}
