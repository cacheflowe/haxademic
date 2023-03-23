
package com.haxademic.demo.draw.text;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.draw.text.FitTextBuffer;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.text.RandomStringUtil;

import processing.core.PFont;

public class Demo_FitTextBuffer
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected FitTextBuffer fitText;
	protected FitTextBuffer fitTextBig;
	protected int curLength = 4;
	
	protected void config() {
//		Config.setProperty( AppSettings.WIDTH, 640 );
//		Config.setProperty( AppSettings.HEIGHT, 640 );
	}

	protected void firstFrame()	{
		int fontSize = 100;
//		PFont font = p.createFont( DemoAssets.fontBitlowPath, fontSize );
		PFont font = p.createFont( DemoAssets.fontOpenSansPath, fontSize );
		fitText = new FitTextBuffer(font, p.color(255));
		fitText.updateText("HEYO00");

		font = p.createFont( DemoAssets.fontOpenSansPath, fontSize * 3 );
		fitTextBig = new FitTextBuffer(font, p.color(255));
		fitTextBig.updateText("9");
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

		PG.setDrawCenter(p);
		for(int i=0; i < 10; i++) {
			float scaleOffset = 1f + 0.1f * P.sin(p.frameCount * 0.1f + i/1f);
			p.image(fitTextBig.crop(), 300 + i * 20, 400 + i * 20, fitTextBig.crop().width * scaleOffset * FrameLoop.osc(0.03f, 0.6f, 1f), fitTextBig.crop().height * scaleOffset * FrameLoop.osc(0.04f, 0.6f, 1f));
		}
		PG.setDrawCorner(p);
		
		// draw in a row with a fixed width
		float drawX = 100;
		float targetDrawW = 600;
		float totalDrawW = 0;
		for(int i=0; i < 20; i++) {
			float letterW = 0 + p.noise(i * 0.05f, p.frameCount * 0.0075f) * 10;
			totalDrawW += letterW;
		}
		float drawScale = targetDrawW / totalDrawW;
		for(int i=0; i < 20; i++) {
			float letterW = 0 + p.noise(i * 0.05f, p.frameCount * 0.0075f) * 10;
			letterW *= drawScale;
			p.image(fitTextBig.crop(), drawX, 220, letterW, 30);
			drawX += letterW + 2;
		}
		
		ThresholdFilter.instance().setCrossfade(0.5f);
		ThresholdFilter.instance().applyTo(p);
	}
	
	protected void newWord() {
		// make sentence
		curLength = MathUtil.randRange(3, 15);
		String str = RandomStringUtil.randomStringOfLength(curLength, RandomStringUtil.ALPHANUMERIC);
		fitText.updateText(str);
		DebugView.setTexture("fitText", fitText.crop());
		
		// one letter
		curLength = 1;
		str = RandomStringUtil.randomStringOfLength(curLength, RandomStringUtil.ALPHANUMERIC);
		fitTextBig.updateText(str);
		DebugView.setTexture("fitTextBig", fitTextBig.crop());
	}

}
