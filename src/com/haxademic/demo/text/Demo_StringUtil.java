package com.haxademic.demo.text;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.StringBufferLog;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.text.StringUtil;

import processing.core.PFont;

public class Demo_StringUtil
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected StringBufferLog logOut = new StringBufferLog(30);

	protected void firstFrame() {
		logOut.update("formattedDecimal: " + StringUtil.formattedDecimal(""+1002.2333333f));
	}

	protected void drawApp() {
		p.background(0);
		
		logOut.printToScreen(p.g, 20, 20);

		// print one-offs 
		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 12);
		FontCacher.setFontOnContext(pg, font, P.p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		
		p.text(StringUtil.subStringByProgress("HELLOOOOOO", -1f + FrameLoop.count(0.02f) % 2f), 400, 20);
	}

}