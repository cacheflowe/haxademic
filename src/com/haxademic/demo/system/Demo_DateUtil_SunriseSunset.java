package com.haxademic.demo.system;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.vendor.SunriseSunset;

import processing.core.PFont;

public class Demo_DateUtil_SunriseSunset
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public Calendar sharedCalendar = Calendar.getInstance();

	
	protected void drawApp() {
		p.background(0);
		
		// calc sunrise and sunset for today
		Date now = new Date();
		sharedCalendar.setTime(now);
		Calendar[] sunRiseAndSet = SunriseSunset.getSunriseSunset(sharedCalendar, 27.217730, -80.261923);
		Calendar rise = sunRiseAndSet[0];
		Calendar set = sunRiseAndSet[1];
		
		// set font
		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 18);
		FontCacher.setFontOnContext(p.g, font, p.color(255), 1.2f, PTextAlign.LEFT, PTextAlign.TOP);
				
		// draw debug output
		p.text("Sunrise :: " + (new SimpleDateFormat("HH:mm")).format(rise.getTime()), 40, 40);
		p.text("Sunset  :: " + (new SimpleDateFormat("HH:mm")).format(set.getTime()), 40, 80);
	}
		
}
