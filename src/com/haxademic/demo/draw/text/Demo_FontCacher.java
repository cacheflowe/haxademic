package com.haxademic.demo.draw.text;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PFont;

public class Demo_FontCacher
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void drawApp() {
		background(0);
		
		String fontFile = DemoAssets.fontOpenSansPath;
		PFont font = FontCacher.getFont(fontFile, 42);
		FontCacher.setFontOnContext(p.g, font, p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		p.text(fontFile, 20, 20);
		
		String fontFile2 = DemoAssets.fontMonospacePath;
		font = FontCacher.getFont(fontFile2, 18);
		FontCacher.setFontOnContext(p.g, font, p.color(0, 255, 0), 1.8f, PTextAlign.LEFT, PTextAlign.TOP);
		p.text(fontFile2 + FileUtil.NEWLINE + fontFile2, 20, 100);

		// pixel fonts
		
        String fontFile3 = "haxademic/fonts/MiniSet2.ttf";
		font = FontCacher.getFont(fontFile3, 8f);
		FontCacher.setFontOnContext(p.g, font, p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		p.text(fontFile3, 20, 200);
		
		font = FontCacher.getFont("haxademic/fonts/exepixelperfect.medium.ttf", 16f);
		FontCacher.setFontOnContext(p.g, font, p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		p.text(font.getName(), 20, 220);
		
		font = FontCacher.getFont("haxademic/fonts/Minecraftia-Regular.ttf", 8f);
		FontCacher.setFontOnContext(p.g, font, p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		p.text(font.getName(), 20, 245);

		// not-perfect pixel fonts
		font = FontCacher.getFont("haxademic/fonts/advanced_pixel-7.ttf", 19f);
		font = FontCacher.getFont("haxademic/fonts/neue-pixel-sans.regular.ttf", 12f);
	}

}
