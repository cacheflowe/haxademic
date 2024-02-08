package com.haxademic.demo.text;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.text.StringRandomCharAnim;

import processing.core.PFont;

public class Demo_StringRandomCharAnim
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected StringRandomCharAnim textAnim;
	protected StringRandomCharAnim textAnim2;
	
	protected void firstFrame() {
		textAnim = new StringRandomCharAnim("Hello World");
		textAnim2 = new StringRandomCharAnim("Hello World", 2, 50);
	}

	protected void drawApp() {
		p.background(0);
		
		// key commands
		if(KeyboardState.instance().isKeyTriggered('2')) textAnim.resetText("Text Animation");
		if(KeyboardState.instance().isKeyTriggered('3')) textAnim.setAdvanceInterval(3);
		if(KeyboardState.instance().isKeyTriggered('4')) textAnim.setMaxFrames(40);
		if(KeyboardState.instance().isKeyTriggered('5')) textAnim2.resetText("           ");
		if(KeyboardState.instance().isKeyTriggered('6')) textAnim2.resetText("Hello World");
		
		// type out text
		textAnim.update();
		textAnim2.update();

		// print one-offs 
		PFont font = FontCacher.getFont(DemoAssets.fontMonospacePath, 36);
		FontCacher.setFontOnContext(p.g, font, P.p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		
		p.text(textAnim.curString(), 100, 100, 300, 600);
		p.text(textAnim2.curString(), 100, 200, 300, 600);
	}
	
}