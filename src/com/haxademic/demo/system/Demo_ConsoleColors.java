package com.haxademic.demo.system;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.system.Console;
import com.haxademic.core.system.DateUtil;

public class Demo_ConsoleColors
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void drawApp() {
		p.background(0);
		
		// set font
		DemoAssets.setDemoFont(p.g);
				
		// draw debug output
		p.text(
				"Epoch time :: " + DateUtil.epochTime() + FileUtil.NEWLINE + 
				"Processing date components :: " + P.year() + "/" + P.month() + "/" + P.day() + FileUtil.NEWLINE + 
				"Processing time components :: " + P.hour() + ":" + P.minute() + ":" + P.second() + FileUtil.NEWLINE +
				"Uptime (timeFromSeconds()) :: " + DateUtil.timeFromSeconds((int) DateUtil.uptimeSeconds(), true) + FileUtil.NEWLINE + 
				"DateUtil.todaySeconds() :: " + DateUtil.todaySeconds() + FileUtil.NEWLINE + 
				"DateUtil.todayHours() :: " + DateUtil.todayHours() + FileUtil.NEWLINE + 
				"DateUtil.dayOfWeek() :: " + DateUtil.dayOfWeek() + FileUtil.NEWLINE +
				"DateUtil.weekOfYear() :: " + DateUtil.weekOfYear() + FileUtil.NEWLINE +
				"Current date (currentDate()) :: " + DateUtil.currentDate(" / ") + FileUtil.NEWLINE + 
				"Current time (currentTime(true, false, true)) :: " + DateUtil.currentTime(true, false, true) + FileUtil.NEWLINE + 
				"Current time (secondsToFormattedTime()) :: " + DateUtil.secondsToFormattedTime((int) DateUtil.todaySeconds(), false, false) + FileUtil.NEWLINE + 
				"uptimeHours() :: " + DateUtil.uptimeHours() + FileUtil.NEWLINE + 
				"DateUtil.timeIsBetweenHours() :: " + DateUtil.timeIsBetweenHours(23f, 9f) + FileUtil.NEWLINE + 
				"Millis.fmtLong() :: " + DateUtil.fmtLong(p.millis()) + FileUtil.NEWLINE +
				"Millis.fmtShort() :: " + DateUtil.fmtShort(p.millis()) + FileUtil.NEWLINE +
				"Millis.parse('1 second') :: " + DateUtil.parse("1 second") + FileUtil.NEWLINE +
				"Millis.parse('1 minute') :: " + DateUtil.parse("1 minute") + FileUtil.NEWLINE +
				"Millis.parse('1 day') :: " + DateUtil.parse("1 day") + FileUtil.NEWLINE +
				"secondsToMS(1) :: " + DateUtil.secondsToMS(1) + FileUtil.NEWLINE +
				"minutesToMS(1) :: " + DateUtil.minutesToMS(1) + FileUtil.NEWLINE +
				"hoursToMS(1) :: " + DateUtil.hoursToMS(1) + FileUtil.NEWLINE +
				"minutesToSeconds(1) :: " + DateUtil.minutesToSeconds(1) + FileUtil.NEWLINE +
				"hoursToSeconds(1) :: " + DateUtil.hoursToSeconds(1) + FileUtil.NEWLINE +
				""
		, 40, 40);
		
			if(FrameLoop.frameModLooped(120)) {
				String time = DateUtil.currentTime(true, false, true);
				P.out(Console.YELLOW + "The time is: " + Console.GREEN_BACKGROUND_BRIGHT + time + Console.RESET);
				P.outColor(Console.CYAN, time);
				P.outColor(Console.RED_BOLD, time);
				P.outColor(Console.GREEN_UNDERLINED, time);
				P.outColor(Console.BLUE_BACKGROUND, time);
				P.outColor(Console.PURPLE_BRIGHT, time);
				P.outColor(Console.YELLOW_BOLD_BRIGHT, time);
				P.outColor(Console.WHITE_BACKGROUND_BRIGHT, time);
				P.warn("This is warning", "yes");
				P.outColor(Console.WHITE_BACKGROUND_BRIGHT, "yes", "no");
			}
	}
		
}
