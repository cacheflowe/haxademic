package com.haxademic.core.net;

import com.haxademic.core.app.P;
import com.haxademic.core.file.FileUtil;

import processing.data.JSONObject;

public class JsonUtil {

	public static String jsonToSingleLine(JSONObject jsonObj) {
		return jsonObj.toString().replaceAll("[\r\n]+", " ").replaceAll("\\s+", " ");
	}
	
	public static String jsonToSingleLine(String jsonString) {
		return jsonString.replaceAll("[\r\n]+", " ").replaceAll("\\s+", " ");
	}
	
	public static boolean isValid(String jsonString) {
		JSONObject jsonData = jsonFromString(jsonString);
		return (jsonData != null);
	}
	
	public static JSONObject jsonFromString(String jsonStr) {
		try {
			return JSONObject.parse(jsonStr);
		} catch (Exception e) {
			P.out("JSON.parse() failed"); 
			return null;
		}
	}
	
	public static String formatJsonString(String jsonStr) {
		try {
			return JSONObject.parse(jsonStr).format(2);
		} catch (Exception e) { P.out("JSON.parse() failed"); return null; }
	}
	
	public static String formatJsonObject(JSONObject jsonObj) {
		return jsonObj.format(2);
	}
	
	public static JSONObject jsonFromFile(String jsonPath) {
		String[] fileLines = FileUtil.readTextFromFile(jsonPath);
		String jsonStr = String.join("\n", fileLines);
		return jsonFromString(jsonStr);
	}

	public static void jsonToFile(JSONObject jsonData, String path) {
		jsonToFile(jsonData.toString(), path);
	}
	
	public static void jsonToFile(String jsonStr, String path) {
		FileUtil.writeTextToFile(path, jsonStr);
	}
	
	public enum Type {
		String,
		Number, 
		Boolean, 
		Unknown
	}
	
	public static Type getTypeForKey(JSONObject jsonData, String key) {
		Object val = jsonData.get(key);
		boolean isNull = jsonData.isNull(key);
		// check datatype & set proper values into AppStore
		if(val instanceof String) {
	    	return Type.String;
	    } else if (val instanceof Integer || val instanceof Long) {
	    	return Type.Number;
	    } else if(val instanceof Float || val instanceof Double) {
	    	return Type.Number;
	    } else if(val instanceof Boolean) {
	    	return Type.Boolean;
	    } else if(isNull) {
	    	return Type.String;
	    } else {
	    	return Type.Unknown;
	    }

	}
	
	// escape / unescape
	// from: https://stackoverflow.com/a/49831779

	public static String escape(String input) {
		StringBuilder output = new StringBuilder();

		for(int i=0; i<input.length(); i++) {
			char ch = input.charAt(i);
			int chx = (int) ch;

			// let's not put any nulls in our strings
			assert(chx != 0);

			if(ch == '\n') {
				output.append("\\n");
			} else if(ch == '\t') {
				output.append("\\t");
			} else if(ch == '\r') {
				output.append("\\r");
			} else if(ch == '\\') {
				output.append("\\\\");
			} else if(ch == '"') {
				output.append("\\\"");
			} else if(ch == '\b') {
				output.append("\\b");
			} else if(ch == '\f') {
				output.append("\\f");
			} else if(chx >= 0x10000) {
				assert false : "Java stores as u16, so it should never give us a character that's bigger than 2 bytes. It literally can't.";
			} else if(chx > 127) {
				output.append(String.format("\\u%04x", chx));
			} else {
				output.append(ch);
			}
		}

		return output.toString();
	}

	public static String unescape(String input, boolean trimOuterQuotes) {
		StringBuilder builder = new StringBuilder();

		if(trimOuterQuotes) input = input.substring(1, input.length() - 1);
		
		int i = 0;
		while (i < input.length()) {
			char delimiter = input.charAt(i); i++; // consume letter or backslash

			if(delimiter == '\\' && i < input.length()) {

				// consume first after backslash
				char ch = input.charAt(i); i++;

				if(ch == '\\' || ch == '/' || ch == '"' || ch == '\'') {
					builder.append(ch);
				}
				else if(ch == 'n') builder.append('\n');
				else if(ch == 'r') builder.append('\r');
				else if(ch == 't') builder.append('\t');
				else if(ch == 'b') builder.append('\b');
				else if(ch == 'f') builder.append('\f');
				else if(ch == 'u') {

					StringBuilder hex = new StringBuilder();

					// expect 4 digits
					if (i+4 > input.length()) {
						throw new RuntimeException("Not enough unicode digits! ");
					}
					for (char x : input.substring(i, i + 4).toCharArray()) {
						if(!Character.isLetterOrDigit(x)) {
							throw new RuntimeException("Bad character in unicode escape.");
						}
						hex.append(Character.toLowerCase(x));
					}
					i+=4; // consume those four digits.

					int code = Integer.parseInt(hex.toString(), 16);
					builder.append((char) code);
				} else {
					throw new RuntimeException("Illegal escape sequence: \\"+ch);
				}
			} else { // it's not a backslash, or it's the last character.
				builder.append(delimiter);
			}
		}

		return builder.toString();
	}
	
}
