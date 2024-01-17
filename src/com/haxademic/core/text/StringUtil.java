package com.haxademic.core.text;

import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

import com.haxademic.core.app.P;

public class StringUtil {
	
	public static final String EMPTY_STRING = "";

	public static String removeLineBreaks(String str) {
		return str.replaceAll("[\r\n]+", " ").replaceAll("\\s+", " ");
	}

	public static String formattedDecimal(String number) {
		double amount = Double.parseDouble(number);
		DecimalFormat formatter = new DecimalFormat("#,###.00");
		return formatter.format(amount);
	}

	public static String roundToPrecision(float value, int numDecimalPlaces) {
		return roundToPrecision(value, numDecimalPlaces, false);
	}

	public static String roundToPrecision(float value, int numDecimalPlaces, boolean padRight) {
		String decimalPlaces = stringOfLengthWithChar(numDecimalPlaces, '#');
		DecimalFormat df = new DecimalFormat(" #."+decimalPlaces+";-#"); // https://stackoverflow.com/a/5243412
		df.setRoundingMode(RoundingMode.FLOOR);
		String output = df.format(value);
		// always fill to padded size
		if(padRight) {
				if(output.contains(".") == false) {
					output += "." + stringOfLengthWithChar(numDecimalPlaces, '0');
				} else {
					int curDecimalPlaces = output.length() - output.indexOf(".") - 1;
					int numZerosToFill = numDecimalPlaces - curDecimalPlaces;
					if(numZerosToFill > 0) {
						output += stringOfLengthWithChar(numZerosToFill, '0');
					}
				}
		}
		return output;
	}

	public static String formattedInteger(int number) {
		return NumberFormat.getInstance().format(number);
	}
	
	public static String stringOfLengthWithChar(int length, char charToFill) {
		if (length > 0) {
			char[] array = new char[length];
			Arrays.fill(array, charToFill);
			return new String(array);
		}
		return "";
	}
	
	public static String safeString(String str) {
		return str.replaceAll("[^A-Za-z0-9._\\-]", EMPTY_STRING);
	}
	
	public static String toAlphaNumericChars(String str) {
		return str.replaceAll("[^A-Za-z0-9.]", EMPTY_STRING);
	}
	
	public static String toAlphaNumericCharsNoDecimal(String str) {
		return str.replaceAll("[^A-Za-z0-9]", EMPTY_STRING);
	}
	
	public static String toAlphaNumericCharsWithSpaces(String str) {
		return str.replaceAll("[^A-Za-z0-9\\s]", EMPTY_STRING);
	}
	
	public static String toNumericChars(String str) {
		return str.replaceAll("[^0-9.-]", EMPTY_STRING);
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
	
	public static String urlEncode(String value) {
			try {
					return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
			} catch (UnsupportedEncodingException ex) {
					throw new RuntimeException(ex.getCause());
			}
	}
	
	public static String subStringByProgress(String str, float progress) {
		if(P.abs(progress) == 1) return str;
		if(progress == 0) return EMPTY_STRING;
		progress = P.constrain(progress, -1, 1);
		int index = P.round(progress * str.length());
		if(progress > 0) {
			return str.substring(0, index);
		} else {
			int startIndex = str.length() - P.abs(index);
			String pad = "";
			for (int i = 0; i < startIndex; i++) pad += "  "; 
			return pad + str.substring(startIndex, str.length());
		}
	}

}
