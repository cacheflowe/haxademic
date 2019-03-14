package com.haxademic.demo.draw.text;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;

import processing.core.PFont;

public class Demo_FontCacher
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public void drawApp() {
		background(0);

		String fontFile = "haxademic/fonts/OpenSans-Regular.ttf";
		PFont font = FontCacher.getFont(fontFile, 42);
		FontCacher.setFontOnContext(p.g, font, p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		p.text(fontFile, 20, 20);
		
		String fontFile2 = "haxademic/fonts/DSEG7ClassicMini-Regular.ttf";
		font = FontCacher.getFont(fontFile2, 18);
		FontCacher.setFontOnContext(p.g, font, p.color(0, 255, 0), 1.8f, PTextAlign.LEFT, PTextAlign.TOP);
		p.text(fontFile2 + FileUtil.NEWLINE + fontFile2, 20, 100);
	}

}
