package com.haxademic.demo.draw.image;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;

public class Demo_ImageUtil_drawImageCropFill 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void drawApp() {
		background(0);
		boolean cropFill = Mouse.xNorm < 0.5f;
		ImageUtil.drawImageCropFill(DemoAssets.justin(), p.g, cropFill, Mouse.yNorm < 0.5f, false);
	}
}
