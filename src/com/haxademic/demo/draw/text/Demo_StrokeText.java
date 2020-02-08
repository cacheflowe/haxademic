package com.haxademic.demo.draw.text;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.draw.text.StrokeText;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;
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

		// draw text that wraps
		font = FontCacher.getFont(DemoAssets.fontInterPath, 20);
		FontCacher.setFontOnContext(p.g, font, p.color(255), 1.4f, PTextAlign.LEFT, PTextAlign.TOP);
		StrokeText.draw(p.g, "Hello this will wrap if we have enough text", 300, 300, 150 + FrameLoop.osc(0.02f, 0, 150), 1000, p.color(0, 255, 255), p.color(0), UI.value(thickness)/2f, UI.valueInt(resolution));
	}
	
}
