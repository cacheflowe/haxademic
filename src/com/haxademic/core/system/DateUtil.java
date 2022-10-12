package com.haxademic.core.system;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.haxademic.core.app.P;
import com.haxademic.core.text.StringUtil;

public class DateUtil {

	public static final String NOSTRING = "";
	public static final int secondInMS = 1000;
	public static final int minuteInSeconds = 60;
	public static final int minuteInMS = minuteInSeconds * 1000;
	public static final int hourInSeconds = minuteInSeconds * 60;
	public static final int hourInMS = hourInSeconds * 1000;
	public static final int dayInSeconds = hourInSeconds * 24;
	public static final int dayInMS = dayInSeconds * 1000;

	public static int secondsToMS(float seconds) { return P.round(seconds * secondInMS); }
	public static int minutesToMS(float minutes) { return P.round(minutes * minuteInMS); }
	public static int hoursToMS(float hours) { return P.round(hours * hourInMS); }
	public static int minutesToSeconds(float minutes) { return P.round(minutes * minuteInSeconds); }
	public static int hoursToSeconds(float hours) { return P.round(hours * hourInSeconds); }
	
	// TIME

	public static boolean timeIsBetweenHours(float startHour, float endHour) {
		float curHour = todayHours();
		if(startHour < endHour) {
			return curHour >= startHour && curHour < endHour;
		} else {
			return curHour >= startHour || curHour < endHour;
		}
	}
	
	// TIME
	
	public static float uptimeHours() {
		return DateUtil.msToHours(P.p.millis());
	}
	
	public static float uptimeSeconds() {
		return P.p.millis() / 1000;
	}
	
	public static float todaySeconds() {
		return (P.hour() * 60 * 60) + (P.minute() * 60) + P.second();
	}
	
	public static float todayHours() {
		return P.hour() + ((P.minute() + (P.second() / 60f)) / 60f);	
	}
	
	public static String currentTime(boolean showSeconds, boolean showMilliseconds, boolean showAmPm) {
		int hours = P.hour() % 12;
		return 	StringUtil.paddedNumberString(2, hours) + ":" + 
				StringUtil.paddedNumberString(2, P.minute()) +  
				((showSeconds) ? ":" + StringUtil.paddedNumberString(2, P.second()) : NOSTRING) +  
				((showMilliseconds) ? ":" + StringUtil.paddedNumberString(2, P.round((P.p.millis() % 1000) / 10)) : NOSTRING) + 
				((showAmPm) ? ((hours >= 12) ? "pm" : "am") : NOSTRING);
	}
	
	public static String currentDate(String delimiter) {
		String month = (P.month() < 10) ? "0"+P.month() : ""+P.month();
		String day = (P.day() < 10) ? "0"+P.day() : ""+P.day();
		String year = ""+P.round(P.year() - 2000);
		return month + delimiter + day + delimiter + year;
	}
	
	public static String secondsToFormattedTime(float seconds, boolean padHours, boolean shortTime) {
		return hoursToFormattedTime(seconds / 60f / 60f, padHours, shortTime);
	}
	
	public static String hoursToFormattedTime(float hours, boolean padHours, boolean shortTime) {
		hours = hours % 24; // keep in 24h range
		int hour = P.floor(hours) % 12;
		if(hour == 0) hour = 12;
		String hourPadded = (hour < 10 && padHours) ? "0"+hour : ""+hour;
		int minutesInt = P.floor((hours % 1) * 60f);
		String minutes = ":" + StringUtil.paddedNumberString(2, minutesInt);
		String amPm = (hours >= 12) ? "pm" : "am";
		if(shortTime) {
			amPm = "";
			if(minutesInt == 0) minutes = "";
		}
		return hourPadded + minutes + amPm;
	}
	
	public static String timeFromSeconds(int seconds, boolean showHours) {
		int h = (int) Math.floor(seconds / 3600f);
		int m = (int) Math.floor(seconds % 3600f / 60f);
		int s = (int) Math.floor(seconds % 3600f % 60);
		String hStr = (h < 10 ? "0" : "") + h;
		String mStr = (m < 10 ? "0" : "") + m;
		String sStr = (s < 10 ? "0" : "") + s;
		if( showHours == true ) {
			return hStr + ':' + mStr + ':' +sStr;
		} else {
			return mStr + ':' +sStr;
		}
	}
	
	public static String timeFromMilliseconds(int millis, boolean showHours, boolean showMillis) {
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

	public static String formatTimeFromSeconds(int seconds) {
		int minutes = P.floor( seconds / 60f );
		int secondsOnly = seconds % 60;
		String secondsText = ( secondsOnly < 10 ) ? "0"+secondsOnly : ""+secondsOnly;
		String minutesText = ( minutes < 10 ) ? "0"+minutes : ""+minutes;
		return minutesText+":"+secondsText;
	}
	
	public static String timeFromMilliseconds(int millies, boolean showHours) {
		return DateUtil.timeFromSeconds((int) Math.round( millies * 0.001f ), showHours);
	}
	
	public static long epochTime() {
		return System.currentTimeMillis();
	}
	
	// WEEK
	
	public static Calendar sharedCalendar;
	public static Calendar calendarInstance() {
		if(sharedCalendar == null) sharedCalendar = Calendar.getInstance(); 
		return sharedCalendar;
	}
	
	public static int dayOfWeek() {
		// Sunday == 1, Monday == 2, etc
		Date now = new Date();
		calendarInstance().setTime(now);
		return calendarInstance().get(Calendar.DAY_OF_WEEK);
	}

	public static int weekOfYear() {
		Date now = new Date();
		calendarInstance().setTime(now);
		return calendarInstance().get(Calendar.WEEK_OF_YEAR);
	}
	
	public static int getWeekInt(String year, String monthPadded, String dayPadded) { // , boolean isToday
		String input = year + monthPadded + dayPadded; // "20190507"
		String format = "yyyyMMdd";
		SimpleDateFormat df = new SimpleDateFormat(format);
		Date date;
		try {
			date = df.parse(input);
			Calendar cal = calendarInstance();
			cal.setTime(date);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
//			if(isToday && cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) week++; // project-specific hack: show upcoming week events if today is sunday
			return week;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return -1;
	}

	// MILLISECONDS
	// ported from: https://github.com/zeit/ms/blob/master/index.js
	// additions by @cacheflowe

	public static final int s = 1000;
	public static final int m = s * 60;
	public static final int h = m * 60;
	public static final int d = h * 24;
	public static final int w = d * 7;
	public static final float y = d * 365.25f;

	// millis to common time intervals
	
	public static float msToSeconds(float millis) {
		return millis / 1000f;
	}

	public static float msToMinutes(float millis) {
		return millis / 1000f / 60;
	}

	public static float msToHours(float millis) {
		return millis / 1000f / 60 / 60;
	}

	public static float msToDays(float millis) {
		return millis / 1000f / 60 / 60 / 24;
	}
	
	// english string to millis 
	
	public static String parse(String str) {
		if(str.length() > 100) {
			return "";
		}

		// do regex
		String pattern = "^(-?(?:\\d+)?\\.?\\d+) *(milliseconds?|msecs?|ms|seconds?|secs?|s|minutes?|mins?|m|hours?|hrs?|h|days?|d|weeks?|w|years?|yrs?|y)?$";
		Pattern r = Pattern.compile(pattern);
		Matcher matcher = r.matcher(str);
		if (matcher.find()) {
			int n = Integer.parseInt(matcher.group(1));
			String type = "ms";
			if(matcher.groupCount() >= 2) type = matcher.group(2).toLowerCase();
			switch (type) {
				case "years":
				case "year":
				case "yrs":
				case "yr":
				case "y":
					return "" + n * y;
				case "weeks":
				case "week":
				case "w":
					return "" + n * w;
				case "days":
				case "day":
				case "d":
					return "" + n * d;
				case "hours":
				case "hour":
				case "hrs":
				case "hr":
				case "h":
					return "" + n * h;
				case "minutes":
				case "minute":
				case "mins":
				case "min":
				case "m":
					return "" + n * m;
				case "seconds":
				case "second":
				case "secs":
				case "sec":
				case "s":
					return "" + n * s;
				case "milliseconds":
				case "millisecond":
				case "msecs":
				case "msec":
				case "ms":
					return "" + n;
				default:
					return "";
			}
		} else {
			return "";
		}
	}
	
	// millis to english

	public static String fmtShort(int ms) {
		float msAbs = P.abs(ms);
		if (msAbs >= d) {
			return P.round(ms / d) + "d";
		}
		if (msAbs >= h) {
			return P.round(ms / h) + "h";
		}
		if (msAbs >= m) {
			return P.round(ms / m) + "m";
		}
		if (msAbs >= s) {
			return P.round(ms / s) + "s";
		}
		return ms + "ms";
	}

	public static String fmtLong(int ms) {
		float msAbs = P.abs(ms);
		if (msAbs >= d) {
			return plural(ms, msAbs, d, "day");
		}
		if (msAbs >= h) {
			return plural(ms, msAbs, h, "hour");
		}
		if (msAbs >= m) {
			return plural(ms, msAbs, m, "minute");
		}
		if (msAbs >= s) {
			return plural(ms, msAbs, s, "second");
		}
		return ms + " ms";
	}

	// Pluralization helper

	public static String plural(int ms, float msAbs, float n, String name) {
		boolean isPlural = msAbs >= n * 1.5f;
		return P.round((float) ms / n) + " " + name + (isPlural ? "s" : "");
	}

}
