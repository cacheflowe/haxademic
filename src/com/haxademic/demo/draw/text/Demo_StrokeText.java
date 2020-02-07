package com.haxademic.demo.draw.text;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.draw.text.StrokeText;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.ui.UI;

import processing.core.PFont;

public class Demo_StrokeText
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected String thickness = "thickness";
	protected String resolution = "resolution";
	
	protected void firstFrame() {
		UI.addTitle("Stroke text config");
		UI.addSlider(thickness, 1, 0, 20, 0.1f, false);
		UI.addSlider(resolution, 12, 4, 36, 1, false);
		UI.active(true);
	}
	
	protected void drawApp() {
		p.background(0);
		
		// set font
		PFont font = FontCacher.getFont(DemoAssets.fontInterPath, 90);
		FontCacher.setFontOnContext(p.g, font, p.color(255), 1.4f, PTextAlign.LEFT, PTextAlign.TOP);
		
		// draw text
		StrokeText.draw(p.g, "Hello", 300, 100, p.color(0, 255, 0), p.color(0), UI.value(thickness), UI.valueInt(resolution));
	}
	
}
