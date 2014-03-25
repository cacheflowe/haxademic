package com.haxademic.core.data;

public class ConvertUtil {

	public static int stringToInt( String str ) {
		return Integer.parseInt( str );
	}
	
	public static float stringToFloat( String str ) {
		return Float.valueOf( str );
	}
	
	public static String intToString( int number ) {
		return Integer.toString( number );
	}
}



