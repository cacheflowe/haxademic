package com.haxademic.sketch.test;

import com.cage.zxing4p3.ZXING4P;
import com.haxademic.core.app.PAppletHax;

import processing.core.PImage;

public class XzingQRCodeTest
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PImage qrImage;
	
	protected void config() {
	}

	protected void firstFrame()	{
		ZXING4P qr = new ZXING4P();
		qrImage = qr.generateQRCode("https://cacheflowe.com", 256, 256);
	}

	protected void drawApp() {
		p.image(qrImage, 0, 0);
	}
}
