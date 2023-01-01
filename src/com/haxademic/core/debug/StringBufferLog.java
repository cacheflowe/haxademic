package com.haxademic.core.debug;

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
	protected int fontSize;
	
	public StringBufferLog(int size) {
		this(size, 12);
	}
	
	public StringBufferLog(int size, int fontSize) {
		this.fontSize = fontSize;
		lines = new String[size];
		for (int i = 0; i < lines.length; i++) {
			lines[i] = EMPTY_STR;
		}
	}
	
	public String itemAt(int index) {
	    int localIndex = curIndex + index;
	    while(localIndex < 0) localIndex += lines.length;
	    localIndex = localIndex % lines.length;
	    return lines[localIndex];
	}
	
	public void update(String newStr) {
		lines[curIndex] = newStr;
		curIndex++;
		if(curIndex >= lines.length) curIndex = 0;
	}
	
	public void printToScreen(PGraphics pg, float x, float y) {
		printToScreen(pg, x, y, true);
	}

	public void printToScreen(PGraphics pg, float x, float y, boolean useDefaultFont) {
        printToScreen(pg, x, y, true, 0);
	}
	
	public void printToScreen(PGraphics pg, float x, float y, boolean useDefaultFont, float maxW) {
		if(useDefaultFont) {
			PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, fontSize);
			FontCacher.setFontOnContext(pg, font, P.p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		}
		
		String outputStr = "";
		for (int i = 0; i < lines.length; i++) {
			int loopedIndx = (curIndex + i) % lines.length;
			outputStr += lines[loopedIndx] + "\n";
		}
		
		if(maxW > 0) {
		    pg.text(outputStr, x, y, maxW, 9999);
		} else {
		    pg.text(outputStr, x, y);
		}
	}
}
