package com.haxademic.core.text;

import processing.core.PApplet;
import processing.core.PFont;

import com.haxademic.core.app.P;

public class FontUtil {
	
	/*
	From original Hangul code: http://processing.org/discourse/yabb2/YaBB.pl?num=1235526464/4#4
	You must call "size()" before creating any instance of this class.
	*/
	public static PFont FontLoad(PApplet p, String fontName, int fontSize) {
		char[]   mCharset;
		// PFont    mFont;

		int index = 0;
		int count = 0;
		
		// calculate the number of characters
		count += (0x007F - 0x0000 + 1);    // basic Latin
		
		// allocate memory
		mCharset = new char[count];

		// loading basic Latin characters
		for (int code = 0x0000; code <= 0x007F; code++) {
			mCharset[index] = Character.toChars(code)[0];
			index++;
		}

		// creating font
		P.print("Creating font " + fontName + "  " + fontSize + "... please wait... ");
		return p.createFont(fontName, fontSize, true, mCharset);
	}
	
	public static void listfonts( PApplet p ) {
		P.println( PFont.list() );
	}
	
}
