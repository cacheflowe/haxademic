package com.haxademic.demo.text;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.text.RandomStringUtil;
import com.haxademic.core.text.StringTypewriterAnim;

import processing.core.PFont;

public class Demo_StringTypewriterAnim
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected StringTypewriterAnim textAnim;
	
	protected void firstFrame() {
	    textAnim = new StringTypewriterAnim("A lot of text potentially here", 10);
	}

	protected void drawApp() {
		p.background(0);
		
		// key commands
		if(KeyboardState.instance().isKeyTriggered('1')) textAnim.addText(RandomStringUtil.randomString());
		if(KeyboardState.instance().isKeyTriggered('2')) textAnim.resetText("Reset with some new text");
		if(KeyboardState.instance().isKeyTriggered('3')) textAnim.setAdvanceInterval(4);
		
		// type out text
		textAnim.update();

		// print one-offs 
		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 36);
		FontCacher.setFontOnContext(p.g, font, P.p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		
		p.text(textAnim.curString(), 100, 100, 300, 600);
	}
	
}