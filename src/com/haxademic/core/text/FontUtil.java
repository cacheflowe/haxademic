package com.haxademic.core.text;

import processing.core.PApplet;
import processing.core.PFont;

import com.haxademic.core.app.P;

public class FontUtil {
		
	public static void listfonts( PApplet p ) {
		P.println( (Object[]) PFont.list() );
	}
	
}
