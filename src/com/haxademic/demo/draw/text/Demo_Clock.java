package com.haxademic.demo.draw.text;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.system.DateUtil;

import processing.core.PFont;

public class Demo_Clock
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void drawApp() {
		background(0);
		drawClock();
	}

	protected void drawClock() {
		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 100);
		FontCacher.setFontOnContext(p.g, font, p.color(0, 255, 0), 2f, PTextAlign.CENTER, PTextAlign.CENTER);

		String dateString = DateUtil.currentDate(" - ");
		String timeString = DateUtil.currentTime(true, false, false);
		p.text(dateString + FileUtil.NEWLINE + timeString, 0, 0, p.width, p.height);
	}
	
}
