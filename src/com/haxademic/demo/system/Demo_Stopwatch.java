package com.haxademic.demo.system;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.system.Stopwatch;

import processing.core.PFont;

public class Demo_Stopwatch
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Stopwatch stopwatch = new Stopwatch();
	
	protected void drawApp() {
	    // draw time
		p.background(0);
		
		String fontFile = DemoAssets.fontOpenSansPath;
		PFont font = FontCacher.getFont(fontFile, 42);
		FontCacher.setFontOnContext(p.g, font, p.color(255), 1.3f, PTextAlign.LEFT, PTextAlign.TOP);

		p.text(
		        stopwatch.totalMs() + "ms\n" +
		        stopwatch.totalHours() + " hours"
		        , 30, 40);
		
		// key commands
		if(KeyboardState.keyTriggered('1')) stopwatch.start();
		if(KeyboardState.keyTriggered('2')) stopwatch.stop();
		if(KeyboardState.keyTriggered('3')) stopwatch.reset();
	}
}
