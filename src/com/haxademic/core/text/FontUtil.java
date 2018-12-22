package com.haxademic.core.text;

import com.haxademic.core.app.P;

import processing.core.PApplet;
import processing.core.PFont;

public class FontUtil {
		
	public static void listfonts( PApplet p ) {
		P.println( (Object[]) PFont.list() );
	}
	
}
