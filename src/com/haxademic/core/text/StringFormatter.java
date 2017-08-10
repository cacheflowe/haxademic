package com.haxademic.core.text;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.haxademic.core.app.P;

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
	}
	
	public static String timeFromMilliseconds( int millis, boolean showHours, boolean showMillis ) {
		int seconds = millis / 1000;
		int h  = (int) Math.floor(seconds / 3600f);
		int m  = (int) Math.floor(seconds % 3600f / 60f);
		int s  = (int) Math.floor(seconds % 3600f % 60);
		int ms = (int) Math.floor(millis % 100);
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

	public static String formatTimeFromSeconds( int seconds ) {
		int minutes = P.floor( seconds / 60f );
		int secondsOnly = seconds % 60;
		String secondsText = ( secondsOnly < 10 ) ? "0"+secondsOnly : ""+secondsOnly;
		String minutesText = ( minutes < 10 ) ? "0"+minutes : ""+minutes;
		return minutesText+":"+secondsText;
	}
	
	public static String timeFromMilliseconds( int millies, boolean showHours ) {
		return StringFormatter.timeFromSeconds( (int) Math.round( millies * 0.001f ), showHours );
	}
	
	public static String formattedDecimal(String number) {
		double amount = Double.parseDouble(number);
		DecimalFormat formatter = new DecimalFormat("#,###.00");
		return formatter.format(amount);
	}
	
	public static String formattedInteger(int number) {
		return NumberFormat.getInstance().format(number);
	}
	
	public static String toAlphaNumericChars(String str) {
		return str.replaceAll("[^A-Za-z0-9.]", "");
	}
	
	public static String toAlphaNumericCharsNoDecimal(String str) {
		return str.replaceAll("[^A-Za-z0-9]", "");
	}
	
	public static String toAlphaNumericCharsWithSpaces(String str) {
		return str.replaceAll("[^A-Za-z0-9\\s]", "");
	}
	
	public static String toNumericChars(String str) {
		return str.replaceAll("[^0-9.-]", "");
	}
	
	public static String paddedNumberString(int size, int val) {
		return String.format("%0"+size+"d", val);
	}
	
	public static String uniqueCharactersInString(String input) {
		String result = "";
		for (int i = 0; i < input.length(); i++) {
			char letter = input.charAt(i);
			if(result.indexOf(letter) == -1) result = result.concat(Character.toString(letter));
		}
		return result;
	}

}
