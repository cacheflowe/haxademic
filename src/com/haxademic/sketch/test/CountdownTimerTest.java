package com.haxademic.sketch.test;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.file.FileUtil;

import processing.core.PFont;

public class CountdownTimerTest
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

	public String timeFromMilliseconds( int millis, boolean showHours, boolean showMillis ) {
		int seconds = millis / 1000;
		int h  = (int) Math.floor(seconds / 3600f);
		int m  = (int) Math.floor(seconds % 3600f / 60f);
		int s  = (int) Math.floor(seconds % 3600f % 60);
		int ms = (int) Math.floor((millis % 1000)/10);
		String hStr = (h < 10 ? "0" : "") + h;
		String mStr = (m < 10 ? "0" : "") + m;
		String sStr = (s < 10 ? "0" : "") + s;
		String msStr = (ms < 10 ? "0" : "") + ms;
		String timeStr = "";
		if( showHours == true ) timeStr += hStr + ':';
		timeStr += mStr + ':' +sStr;
		if( showMillis == true ) timeStr += ':' + msStr;
		return timeStr;
	}

	public void drawApp() {
		p.background(0);
		// update time 
		int curTime = endTime - P.p.millis();
		if(curTime < 35 * 60 * 1000) stop();
		String timeStr = timeFromMilliseconds(curTime, false, true);
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
