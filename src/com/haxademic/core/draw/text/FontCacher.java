package com.haxademic.core.draw.text;

import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.file.FileUtil;

import processing.core.PFont;
import processing.core.PGraphics;

public class FontCacher {

	public static HashMap<String, PFont> fonts = new HashMap<String, PFont>();
	
	public static PFont getFont(String fontPath, float fontSize) {
		String key = fontPath + "-" + fontSize;
		if(fonts.containsKey(key) == false) {
			fonts.put(key, P.p.createFont(FileUtil.getFile(fontPath), fontSize));
		}
		return fonts.get(key);
	}
	
	public static void setFontOnContext(PGraphics pg, PFont font, int color, float leadingMult, int alignX, int alignY) {
		pg.fill(color);
		pg.textFont(font);
		pg.textSize(font.getSize());
		pg.textLeading(font.getSize() * leadingMult);
		pg.textAlign(alignX, alignY);
	}
}
