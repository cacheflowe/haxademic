
package com.haxademic.demo.draw.text;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.text.FitTextBuffer;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.text.RandomStringUtil;

import processing.core.PFont;

public class Demo_FitTextBuffer
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected FitTextBuffer fitText;
	protected int curLength = 4;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 640 );
		Config.setProperty( AppSettings.HEIGHT, 640 );
	}

	protected void firstFrame()	{
		int fontSize = 100;
//		PFont font = p.createFont( DemoAssets.fontBitlowPath, fontSize );
		PFont font = p.createFont( DemoAssets.fontOpenSansPath, fontSize );
		fitText = new FitTextBuffer(font, p.color(255));
		fitText.updateText("HEYO00");
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') newWord();
	}

	protected void drawApp() {
		background(0);
		
		if(p.frameCount % 100 == 0 || p.frameCount == 1) newWord();
		p.image(fitText.crop(), 100, 100);
		p.text(""+curLength, 10, 10);
	}
	
	protected void newWord() {
		curLength = MathUtil.randRange(3, 15);
		String str = RandomStringUtil.randomStringOfLength(curLength, RandomStringUtil.ALPHANUMERIC);
		fitText.updateText(str);
		DebugView.setTexture("croppedText", fitText.crop());
	}

}
