package com.haxademic.core.net;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class URLUtil {

	// from: https://stackoverflow.com/a/4737898/352456
	public static String escapeURIPathParam(String input) {
		StringBuilder resultStr = new StringBuilder();
		for (char ch : input.toCharArray()) {
			if (isUnsafe(ch)) {
				resultStr.append('%');
				resultStr.append(toHex(ch / 16));
				resultStr.append(toHex(ch % 16));
			} else{
				resultStr.append(ch);
			}
		}
		return resultStr.toString();
	}

	private static char toHex(int ch) {
		return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
	}

	private static boolean isUnsafe(char ch) {
		if (ch > 128 || ch < 0)
			return true;
		return " %$&+,/:;=?@<>#%".indexOf(ch) >= 0;
	}
	
	public static String urlEncode(String queryParams) {
		try {
			return URLEncoder.encode(queryParams, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return queryParams;
	}
	
	public static String convertCommonUrlSpecialCharacters(String url) {
		return url.replaceAll(" ", "%20").replaceAll("\\(", "%28").replaceAll("\\)", "%29");
	}
}
