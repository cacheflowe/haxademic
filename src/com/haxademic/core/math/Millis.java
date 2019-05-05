package com.haxademic.core.math;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.haxademic.core.app.P;

public class Millis {

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
