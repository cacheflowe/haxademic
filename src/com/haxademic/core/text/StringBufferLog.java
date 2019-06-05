package com.haxademic.core.text;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.media.DemoAssets;

import processing.core.PFont;
import processing.core.PGraphics;

public class StringBufferLog {

	protected String[] lines;
	protected int curIndex = 0;
	protected String EMPTY_STR = "";
	
	public StringBufferLog(int size) {
		lines = new String[size];
		for (int i = 0; i < lines.length; i++) {
			lines[i] = EMPTY_STR;
		}
	}
	
	public void update(String newStr) {
		lines[curIndex] = newStr;
		
		curIndex++;
		if(curIndex >= lines.length) curIndex = 0;
	}
	
	public void printToScreen(PGraphics pg, float x, float y) {
		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 14);
		FontCacher.setFontOnContext(pg, font, P.p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		
		String outputStr = "";
		for (int i = 0; i < lines.length; i++) {
			int loopedIndx = (curIndex + i) % lines.length;
			outputStr += lines[loopedIndx] + "\n";
		}
		
		pg.text(outputStr, x, y);
	}
}
