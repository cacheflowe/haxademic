package com.haxademic.demo.text;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.text.StringFormatter;

import processing.core.PFont;

public class Demo_StringFormatter_timeFromMilliseconds
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int endTime;
	protected int fakeLastDigit = 9;
	
	public void setup() {
		super.setup();		
		endTime = P.p.millis() + 45 * 60 * 1000;
		PFont daFont = p.createFont(FileUtil.getFile("fonts/coders_crux.ttf"), 100, true);
		p.textFont(daFont);
	}

	public void drawApp() {
		p.background(0);
		// update time 
		int curTime = endTime - P.p.millis();
		if(curTime < 35 * 60 * 1000) stop();
		String timeStr = StringFormatter.timeFromMilliseconds(curTime, false, true);
		fakeLastDigit = (fakeLastDigit > 0) ? fakeLastDigit - 1 : 9;
		timeStr = timeStr.substring(0, timeStr.length() - 1);
		timeStr += fakeLastDigit;
		
		// draw text
		p.noStroke();
		p.fill(255);
		p.textAlign(P.CENTER, P.CENTER);
		p.text(timeStr, 0, 0, p.width, p.height);
	}
}
