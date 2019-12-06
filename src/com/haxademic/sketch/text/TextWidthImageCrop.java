
package com.haxademic.sketch.text;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.text.RandomStringUtil;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;

public class TextWidthImageCrop
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected FitTextBuffer fitText;
	protected int curLength = 4;
	protected PImage croppedText = null;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 );
	}

	public void setup()	{

		
		croppedText = p.createImage(128, 128, P.ARGB);
		
		int fontSize = 100;
//		PFont font = p.createFont( FileUtil.getFile("fonts/bitlow.ttf"), fontSize );
		PFont font = p.createFont( FileUtil.getFile("fonts/HelveticaNeueLTStd-Blk.ttf"), fontSize );
		fitText = new FitTextBuffer(font, p.color(127));
		fitText.updateText("HEYO00");
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') newWord();
	}

	public void drawApp() {
		background(0);
		
//		if(p.frameCount % 100 == 0 || p.frameCount == 1) newWord();
		p.image(fitText.buffer(), 100, 100);
		p.text(""+curLength, 10, 10);
		if(croppedText != null) p.image(croppedText, 20, 500);
	}
	
	protected void newWord() {
		curLength = MathUtil.randRange(3, 10);
		String str = RandomStringUtil.randomStringOfLength(curLength, RandomStringUtil.ALPHANUMERIC);
		fitText.updateText(str);
		ImageUtil.imageCroppedEmptySpace(fitText.buffer(), croppedText, ImageUtil.EMPTY_INT, true);
	}

	public class FitTextBuffer {
		
		protected PFont font;
		protected int color;
		protected PGraphics buffer;
		
		public FitTextBuffer(PFont font, int color) {
			this.font = font;
			this.color = color;
			buffer = p.createGraphics(512, P.ceil(font.getSize() * 1.1f), PRenderers.P2D);
			buffer.smooth(8);
		}
		
		public PGraphics buffer() {
			return buffer;
		}
		
		public void updateText(String text) {
			// set text size
			buffer.textFont(font);
			int textW = P.ceil(buffer.textWidth(text) * 1.001f);
//			buffer.setSize(textW, buffer.height);
//			buffer.resize(textW, buffer.height);
			
			// draw
			buffer.beginDraw();
			buffer.clear();
			// buffer.background(255);
			buffer.noStroke();
			buffer.fill(color);
			buffer.textAlign(P.CENTER, P.TOP);
			buffer.textFont(font);
			// buffer.textLeading(font.getSize() * 0.75f);
			buffer.text(text, 0, 0, buffer.width, buffer.height);
			buffer.endDraw();
		}
		
	}

}
