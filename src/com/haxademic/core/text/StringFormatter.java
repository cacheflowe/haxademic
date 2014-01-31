package com.haxademic.core.text;

public class StringFormatter {

	public static String timeFromSeconds( int seconds, boolean showHours ) {
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
	};

	public static String timeFromMilliseconds( int millies, boolean showHours ) {
		return StringFormatter.timeFromSeconds( (int) Math.round( millies * 0.001f ), showHours );
	}
}
