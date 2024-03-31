package com.haxademic.demo.debug;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.StringBufferLog;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PFont;

public class Demo_StringBufferLog 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected StringBufferLog logOut = new StringBufferLog(10);

	protected void drawApp() {
		background(0);
		if(MathUtil.randBooleanWeighted(0.015f)) logOut.update("FRAME " + p.frameCount);

		// print with normal debug font
		logOut.printToScreen(p.g, 20, 20);
		
		// print with custom font
		FontCacher.setFontOnContext(p.g, DemoAssets.font8px(), 0xffffffff, 1.5f, PTextAlign.LEFT, PTextAlign.TOP);
		logOut.printToScreen(p.g, 300, 20, false);

		// print with custom font
		FontCacher.setFontOnContext(p.g, FontCacher.getFont(DemoAssets.fontBitlowPath, 42), 0xffffffff, 1, PTextAlign.LEFT, PTextAlign.TOP);
		logOut.printToScreen(p.g, 500, 20, false);
		
		// show oldest item
		p.text(logOut.itemAt(0), 20, 320);
		p.text(logOut.newestValue(), 20, 360);
		p.text(logOut.oldestValue(), 20, 400);
	}

	public void keyPressed() {
		super.keyPressed();
		logOut.update("Key pressed!".toUpperCase());
	}

}