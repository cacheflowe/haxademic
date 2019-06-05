package com.haxademic.core.text;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class StringFormatter {

	public static String formattedDecimal(String number) {
		double amount = Double.parseDouble(number);
		DecimalFormat formatter = new DecimalFormat("#,###.00");
		return formatter.format(amount);
	}

	public String roundToPrecision( float value, int numDecimalPlaces ) {
		String decimalPlaces = "";
		for (int i = 0; i < numDecimalPlaces; i++) decimalPlaces += "#";
		DecimalFormat df = new DecimalFormat("#."+decimalPlaces);
		String output = df.format(value);
		if(output.length() < numDecimalPlaces + 2) output += ".0";
		while(output.length() < numDecimalPlaces + 2) output += "0";
		return output;
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
