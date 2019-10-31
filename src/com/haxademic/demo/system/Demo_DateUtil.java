package com.haxademic.demo.system;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.system.DateUtil;

import processing.core.PFont;

public class Demo_DateUtil
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public void drawApp() {
		p.background(0);
		
		// set font
		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 18);
		FontCacher.setFontOnContext(p.g, font, p.color(255), 2f, PTextAlign.LEFT, PTextAlign.TOP);
				
		// draw debug output
		p.text(
				"Processing date components :: " + P.year() + "/" + P.month() + "/" + P.day() + FileUtil.NEWLINE + 
				"Processing time components :: " + P.hour() + ":" + P.minute() + ":" + P.second() + FileUtil.NEWLINE +
				"Uptime (timeFromSeconds()) :: " + DateUtil.timeFromSeconds((int) DateUtil.uptimeSeconds(), true) + FileUtil.NEWLINE + 
				"DateUtil.todaySeconds() :: " + DateUtil.todaySeconds() + FileUtil.NEWLINE + 
				"DateUtil.todayHours() :: " + DateUtil.todayHours() + FileUtil.NEWLINE + 
				"Current time (currentTime(true, false, true)) :: " + DateUtil.currentTime(false, false, true) + FileUtil.NEWLINE + 
				"Current time (secondsToFormattedTime()) :: " + DateUtil.secondsToFormattedTime((int) DateUtil.todaySeconds(), false, false) + FileUtil.NEWLINE + 
				"uptimeHours() :: " + DateUtil.uptimeHours() + FileUtil.NEWLINE + 
				"DateUtil.timeIsBetweenHours() :: " + DateUtil.timeIsBetweenHours(23f, 9f) + FileUtil.NEWLINE + 
				"Millis.fmtLong() :: " + DateUtil.fmtLong(p.millis()) + FileUtil.NEWLINE +
				"Millis.fmtShort() :: " + DateUtil.fmtShort(p.millis()) + FileUtil.NEWLINE +
				"Millis.parse('1 second') :: " + DateUtil.parse("1 second") + FileUtil.NEWLINE +
				"Millis.parse('1 minute') :: " + DateUtil.parse("1 minute") + FileUtil.NEWLINE +
				"Millis.parse('1 day') :: " + DateUtil.parse("1 day") + FileUtil.NEWLINE +
				""
		, 40, 40);
	}
		
}
