package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.Millis;
import com.haxademic.core.text.StringFormatter;

import processing.core.PFont;

public class Demo_Millis
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public void drawApp() {
		p.background(0);
		
		// set font
		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 18);
		FontCacher.setFontOnContext(p.g, font, p.color(255), 2f, PTextAlign.LEFT, PTextAlign.TOP);
		
		// draw debug output
		p.text(
				"Uptime :: " + StringFormatter.timeFromSeconds(p.millis() / 1000, true) + FileUtil.NEWLINE + 
				"uptimeHours() :: " + uptimeHours() + FileUtil.NEWLINE + 
				"hour:minute:second :: " + P.hour() + ":" + P.minute() + ":" + P.second() + FileUtil.NEWLINE +
				"Millis.fmtLong() :: " + Millis.fmtLong(p.millis()) + FileUtil.NEWLINE +
				"Millis.fmtShort() :: " + Millis.fmtShort(p.millis()) + FileUtil.NEWLINE +
				"Millis.parse('1 second') :: " + Millis.parse("1 second") + FileUtil.NEWLINE +
				"Millis.parse('1 minute') :: " + Millis.parse("1 minute") + FileUtil.NEWLINE +
				"Millis.parse('1 day') :: " + Millis.parse("1 day") + FileUtil.NEWLINE +
				""
		, 40, 40);
	}
	
	protected float uptimeHours() {
		return Millis.msToHours(p.millis());
	}
}
